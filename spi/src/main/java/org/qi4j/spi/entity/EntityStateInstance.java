/*  Copyright 2007 Niclas Hedhman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.spi.entity;

import java.lang.reflect.Method;
import java.util.Map;
import org.qi4j.association.AbstractAssociation;
import org.qi4j.property.Property;
import org.qi4j.spi.composite.CompositeBinding;

/**
 * Standard implementation of EntityState.
 */
public class EntityStateInstance
    implements EntityState
{
    private final String identity;
    private final CompositeBinding compositeBinding;
    private EntityStatus status;

    protected final Map<Method, Property> properties;
    protected final Map<Method, AbstractAssociation> associations;

    public EntityStateInstance( String identity,
                                CompositeBinding compositeBinding,
                                EntityStatus status,
                                Map<Method, Property> properties,
                                Map<Method, AbstractAssociation> associations )
    {
        this.identity = identity;
        this.compositeBinding = compositeBinding;
        this.status = status;
        this.properties = properties;
        this.associations = associations;
    }

    // EntityState implementation
    public String getIdentity()
    {
        return identity;
    }

    public CompositeBinding getCompositeBinding()
    {
        return compositeBinding;
    }

    public Property getProperty( Method propertyMethod )
    {
        return properties.get( propertyMethod );
    }

    public AbstractAssociation getAssociation( Method associationMethod )
    {
        return associations.get( associationMethod );
    }

    public void remove()
    {
        status = EntityStatus.REMOVED;
    }

    public EntityStatus getStatus()
    {
        return status;
    }

    public Map<Method, Property> getProperties()
    {
        return properties;
    }

    public Map<Method, AbstractAssociation> getAssociations()
    {
        return associations;
    }
}