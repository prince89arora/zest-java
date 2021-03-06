/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.zest.entitystore.geode;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionFactory;
import com.gemstone.gemfire.cache.RegionShortcut;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import org.apache.zest.api.configuration.Configuration;
import org.apache.zest.api.entity.EntityDescriptor;
import org.apache.zest.api.entity.EntityReference;
import org.apache.zest.api.injection.scope.This;
import org.apache.zest.api.service.ServiceActivation;
import org.apache.zest.io.Input;
import org.apache.zest.io.Output;
import org.apache.zest.io.Receiver;
import org.apache.zest.io.Sender;
import org.apache.zest.spi.entitystore.EntityNotFoundException;
import org.apache.zest.spi.entitystore.EntityStoreException;
import org.apache.zest.spi.entitystore.helpers.MapEntityStore;

/**
 * Geode EntityStore Mixin.
 */
public class GeodeEntityStoreMixin
        implements ServiceActivation, MapEntityStore
{
    @This
    private Configuration<GeodeConfiguration> config;

    private AutoCloseable closeable;
    private Region<String, String> region;

    @Override
    public void activateService()
            throws Exception
    {
        config.refresh();
        GeodeConfiguration configuration = config.get();
        switch( configuration.topology().get() )
        {
            case EMBEDDED:
                activateEmbedded( configuration );
                break;
            case CLIENT_SERVER:
                activateClientServer( configuration );
                break;
            default:
                throw new IllegalStateException( "Invalid/Unsupported Geode Topology: "
                                                 + configuration.topology().get() );
        }
    }

    private void activateEmbedded( GeodeConfiguration configuration )
            throws IOException
    {
        Properties cacheProperties = buildCacheProperties( configuration );
        String regionShortcutName = configuration.regionShortcut().get();
        RegionShortcut regionShortcut = regionShortcutName == null
                                        ? RegionShortcut.LOCAL
                                        : RegionShortcut.valueOf( regionShortcutName );
        String regionName = configuration.regionName().get();

        CacheFactory cacheFactory = new CacheFactory( cacheProperties );
        Cache cache = cacheFactory.create();
        RegionFactory<String, String> regionFactory = cache.createRegionFactory( regionShortcut );
        region = regionFactory.create( regionName );
        closeable = cache;
    }

    private void activateClientServer( GeodeConfiguration configuration )
            throws IOException
    {
        Properties cacheProperties = buildCacheProperties( configuration );
        String regionShortcutName = configuration.regionShortcut().get();
        ClientRegionShortcut regionShortcut = regionShortcutName == null
                                              ? ClientRegionShortcut.PROXY
                                              : ClientRegionShortcut.valueOf( regionShortcutName );
        String regionName = configuration.regionName().get();

        ClientCacheFactory cacheFactory = new ClientCacheFactory( cacheProperties );
        ClientCache cache = cacheFactory.create();
        ClientRegionFactory<String, String> regionFactory = cache.createClientRegionFactory( regionShortcut );
        region = regionFactory.create( regionName );
        closeable = cache;
    }

    private Properties buildCacheProperties( GeodeConfiguration config )
            throws IOException
    {
        Properties properties = new Properties();
        String cachePropertiesPath = config.cachePropertiesPath().get();
        if( cachePropertiesPath != null )
        {
            try( InputStream input = getClass().getResourceAsStream( cachePropertiesPath ) )
            {
                if( input == null )
                {
                    throw new IllegalStateException( "Geode Cache Properties could not be found: "
                                                     + cachePropertiesPath );
                }
                properties.load( input );
            }
        }
        properties.setProperty( "name", config.cacheName().get() );
        return properties;
    }

    @Override
    public void passivateService()
            throws Exception
    {
        region = null;
        if( closeable != null )
        {
            closeable.close();
            closeable = null;
        }
    }

    @Override
    public Reader get( EntityReference entityReference ) throws EntityStoreException
    {
        String serializedState = region.get( entityReference.identity() );
        if( serializedState == null )
        {
            throw new EntityNotFoundException( entityReference );
        }
        return new StringReader( serializedState );
    }

    @Override
    public void applyChanges( MapChanges changes ) throws IOException
    {
        changes.visitMap( new MapChanger()
        {

            @Override
            public Writer newEntity( final EntityReference ref, EntityDescriptor entityDescriptor )
                    throws IOException
            {
                return new StringWriter( 1000 )
                {

                    @Override
                    public void close()
                            throws IOException
                    {
                        super.close();
                        region.put( ref.identity(), toString() );
                    }
                };
            }

            @Override
            public Writer updateEntity( EntityReference ref, EntityDescriptor entityDescriptor )
                    throws IOException
            {
                return newEntity( ref, entityDescriptor );
            }

            @Override
            public void removeEntity( EntityReference ref, EntityDescriptor entityDescriptor )
                    throws EntityNotFoundException
            {
                region.remove( ref.identity() );
            }
        } );
    }

    @Override
    public Input<Reader, IOException> entityStates()
    {
        return new Input<Reader, IOException>()
        {
            @Override
            public <ReceiverThrowableType extends Throwable> void transferTo( Output<? super Reader, ReceiverThrowableType> output )
                    throws IOException, ReceiverThrowableType
            {
                output.receiveFrom( new Sender<Reader, IOException>()
                {
                    @Override
                    public <RTT extends Throwable> void sendTo( Receiver<? super Reader, RTT> receiver )
                            throws RTT, IOException
                    {
                        for( Map.Entry<String, String> eachEntry : region.entrySet() )
                        {
                            receiver.receive( new StringReader( eachEntry.getValue() ) );
                        }
                    }
                } );
            }
        };
    }
}
