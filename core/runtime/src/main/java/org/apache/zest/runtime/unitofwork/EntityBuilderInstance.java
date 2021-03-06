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
package org.apache.zest.runtime.unitofwork;

import org.apache.zest.api.common.QualifiedName;
import org.apache.zest.api.entity.EntityBuilder;
import org.apache.zest.api.entity.EntityDescriptor;
import org.apache.zest.api.entity.EntityReference;
import org.apache.zest.api.entity.Identity;
import org.apache.zest.api.entity.LifecycleException;
import org.apache.zest.runtime.composite.FunctionStateResolver;
import org.apache.zest.runtime.entity.EntityInstance;
import org.apache.zest.runtime.entity.EntityModel;
import org.apache.zest.spi.entity.EntityState;
import org.apache.zest.spi.entitystore.EntityStoreUnitOfWork;
import org.apache.zest.spi.module.ModuleSpi;

/**
 * Implementation of EntityBuilder. Maintains an instance of the entity which
 * will not have its state validated until it is created by calling newInstance().
 */
public final class EntityBuilderInstance<T>
    implements EntityBuilder<T>
{
    private static final QualifiedName IDENTITY_STATE_NAME;

    private final EntityModel model;
    private final ModuleUnitOfWork uow;
    private final EntityStoreUnitOfWork store;
    private String identity;

    private final BuilderEntityState entityState;
    private final EntityInstance prototypeInstance;

    static
    {
        try
        {
            IDENTITY_STATE_NAME = QualifiedName.fromAccessor( Identity.class.getMethod( "identity" ) );
        }
        catch( NoSuchMethodException e )
        {
            throw new InternalError( "Zest Core Runtime codebase is corrupted. Contact Zest team: EntityBuilderInstance" );
        }
    }

    public EntityBuilderInstance(
        EntityDescriptor model,
        ModuleUnitOfWork uow,
        EntityStoreUnitOfWork store,
        String identity
    )
    {
        this( model, uow, store, identity, null );
    }

    public EntityBuilderInstance(
        EntityDescriptor model,
        ModuleUnitOfWork uow,
        EntityStoreUnitOfWork store,
        String identity,
        FunctionStateResolver stateResolver
    )
    {
        this.model = (EntityModel) model;
        this.uow = uow;
        this.store = store;
        this.identity = identity;
        EntityReference reference = new EntityReference( identity );
        entityState = new BuilderEntityState( model, reference );
        this.model.initState( model.module(), entityState );
        if( stateResolver != null )
        {
            stateResolver.populateState( this.model, entityState );
        }
        entityState.setPropertyValue( IDENTITY_STATE_NAME, identity );
        prototypeInstance = this.model.newInstance( uow, (ModuleSpi) model.module().instance(), entityState );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public T instance()
    {
        checkValid();
        return prototypeInstance.<T>proxy();
    }

    @Override
    public <K> K instanceFor( Class<K> mixinType )
    {
        checkValid();
        return prototypeInstance.newProxy( mixinType );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T newInstance()
        throws LifecycleException
    {
        checkValid();

        // Figure out whether to use given or generated identity
        String identity = (String) entityState.propertyValueOf( IDENTITY_STATE_NAME );
        EntityReference entityReference = EntityReference.parseEntityReference( identity );
        EntityState newEntityState = model.newEntityState( store, entityReference );

        prototypeInstance.invokeCreate();

        // Check constraints
        prototypeInstance.checkConstraints();

        entityState.copyTo( newEntityState );

        EntityInstance instance = model.newInstance( uow, (ModuleSpi) model.module().instance(), newEntityState );

        Object proxy = instance.proxy();

        // Add entity in UOW
        uow.addEntity( instance );

        // Invalidate builder
        this.identity = null;

        return (T) proxy;
    }

    private void checkValid()
        throws IllegalStateException
    {
        if( identity == null )
        {
            throw new IllegalStateException( "EntityBuilder is not valid after call to newInstance()" );
        }
    }
}
