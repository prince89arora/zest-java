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
 *
 *
 */
package org.apache.zest.library.spring.bootstrap.internal.service;

import org.apache.zest.api.object.ObjectDescriptor;
import org.apache.zest.api.service.ImportedServiceDescriptor;
import org.apache.zest.api.service.ServiceDescriptor;
import org.apache.zest.api.service.ServiceReference;
import org.apache.zest.api.structure.Application;
import org.apache.zest.api.structure.ApplicationDescriptor;
import org.apache.zest.api.structure.LayerDescriptor;
import org.apache.zest.api.structure.Module;
import org.apache.zest.api.structure.ModuleDescriptor;
import org.apache.zest.functional.HierarchicalVisitor;

final class ServiceLocator
    implements HierarchicalVisitor<Object, Object, RuntimeException>
{
    private final String serviceId;
    private Class serviceType;
    private String moduleName;
    private String layerName;

    private String tempLayerName;
    private String tempModuleName;

    ServiceLocator( String serviceId )
    {
        this.serviceId = serviceId;
    }

    @Override
    public boolean visitEnter( Object visited )
        throws RuntimeException
    {
        if( visited instanceof ApplicationDescriptor )
        {
            return true;
        }
        else if( visited instanceof LayerDescriptor )
        {
            tempLayerName = ( (LayerDescriptor) visited ).name();
            return true;
        }
        else if( visited instanceof ModuleDescriptor )
        {
            tempModuleName = ( (ModuleDescriptor) visited ).name();
            return true;
        }
        else if( visited instanceof ServiceDescriptor )
        {
            ServiceDescriptor aDescriptor = (ServiceDescriptor) visited;
            String identity = aDescriptor.identity();
            if( serviceId.equals( identity ) )
            {
                layerName = tempLayerName;
                moduleName = tempModuleName;
                serviceType = aDescriptor.types().findFirst().orElse( null );
            }
        }
        else if( visited instanceof ObjectDescriptor )
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean visitLeave( Object visited )
        throws RuntimeException
    {
        return true;
    }

    @Override
    public boolean visit( Object visited )
        throws RuntimeException
    {
        if( visited instanceof ImportedServiceDescriptor )
        {
            ImportedServiceDescriptor aDescriptor = (ImportedServiceDescriptor) visited;
            String identity = aDescriptor.identity();
            if( serviceId.equals( identity ) )
            {
                layerName = tempLayerName;
                moduleName = tempModuleName;
                serviceType = aDescriptor.type();
            }
        }

        return true;
    }

    @SuppressWarnings( "unchecked" )
    ServiceReference locateService( Application anApplication )
    {
        if( layerName != null )
        {
            Module module = anApplication.findModule( layerName, moduleName );
            Iterable<ServiceReference<Object>> serviceRefs = module.findServices( serviceType );
            for( ServiceReference<Object> serviceRef : serviceRefs )
            {
                if( serviceId.equals( serviceRef.identity() ) )
                {
                    return serviceRef;
                }
            }
        }

        return null;
    }
}
