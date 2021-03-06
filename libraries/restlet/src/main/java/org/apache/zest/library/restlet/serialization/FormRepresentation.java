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

package org.apache.zest.library.restlet.serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;
import org.apache.zest.api.association.AssociationStateHolder;
import org.apache.zest.api.common.Optional;
import org.apache.zest.api.injection.scope.Structure;
import org.apache.zest.api.injection.scope.Uses;
import org.apache.zest.api.property.PropertyDescriptor;
import org.apache.zest.api.value.ValueBuilder;
import org.apache.zest.api.value.ValueBuilderFactory;
import org.apache.zest.api.value.ValueComposite;
import org.apache.zest.api.value.ValueDescriptor;
import org.apache.zest.spi.ZestSPI;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

/**
 * Representation based of Zest ValueComposites. It can serialize and deserialize
 * automatically in JSON only.<br>
 * <br>
 */
public class FormRepresentation<T> extends OutputRepresentation
{
    @Structure
    private ZestSPI spi;

    @Structure
    private ValueBuilderFactory vbf;

    /**
     * The (parsed) object to format.
     */
    @Optional
    private volatile T object;

    /**
     * The object class to instantiate.
     */
    @Optional
    @Uses
    private volatile Class<T> objectClass;

    /**
     * The representation to parse.
     */
    @Optional
    @Uses
    private volatile Representation representation;

    public FormRepresentation()
    {
        super( MediaType.APPLICATION_WWW_FORM );
    }

    private T createObject()
    {
        //noinspection MismatchedQueryAndUpdateOfCollection
        final Form form = new Form( representation );
        ValueBuilder<T> builder = this.vbf.newValueBuilderWithState(
            objectClass,
            new Function<PropertyDescriptor, Object>()
            {
                @Override
                public Object apply( PropertyDescriptor descriptor )
                {
                    return form.getFirstValue( descriptor.qualifiedName().name() );
                }
            },
            descriptor -> null,
            descriptor -> null,
            descriptor -> null
        );
        return builder.newInstance();
    }

    /**
     * Returns the wrapped object, deserializing the representation with Zest
     * if necessary.
     *
     * @return The wrapped object.
     *
     * @throws IOException
     */
    public T getObject()
        throws IOException
    {
        if( object == null )
        {
            object = createObject();
        }
        return object;
    }

    /**
     * Returns the object class to instantiate.
     *
     * @return The object class to instantiate.
     */
    public Class<T> getObjectClass()
    {
        return objectClass;
    }

    @Override
    public void write( OutputStream outputStream )
        throws IOException
    {
        if( representation != null )
        {
            representation.write( outputStream );
        }
        else if( object != null )
        {
            AssociationStateHolder state = spi.stateOf( (ValueComposite) object );
            ValueDescriptor descriptor = (ValueDescriptor) spi.modelDescriptorFor( object );
            descriptor.state().properties().forEach( property -> {
                String name = property.qualifiedName().name();
                String value = state.propertyFor( property.accessor() ).get().toString();
                try
                {
                    outputStream.write( name.getBytes( "UTF-8" ) );
                    outputStream.write( "=".getBytes( "UTF-8" ) );
                    outputStream.write( value.getBytes( "UTF-8" ) );
                    outputStream.write( '\n' );
                }
                catch( IOException e )
                {
                    // TODO; not sure what could happen here.
                }
            } );
        }
    }
}
