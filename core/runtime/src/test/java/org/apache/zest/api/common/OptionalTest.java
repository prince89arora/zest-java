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

package org.apache.zest.api.common;

import org.junit.Test;
import org.apache.zest.api.association.Association;
import org.apache.zest.api.composite.TransientBuilder;
import org.apache.zest.api.composite.TransientComposite;
import org.apache.zest.api.constraint.ConstraintViolationException;
import org.apache.zest.api.entity.EntityBuilder;
import org.apache.zest.api.entity.EntityComposite;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.property.Property;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.bootstrap.AssemblyException;
import org.apache.zest.bootstrap.ModuleAssembly;
import org.apache.zest.test.AbstractZestTest;
import org.apache.zest.test.EntityTestAssembler;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for @Optional
 */
public class OptionalTest
    extends AbstractZestTest
{
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.transients( TestComposite.class );
        module.transients( TestComposite2.class );
        module.entities( TestComposite3.class, TestComposite4.class );
        new EntityTestAssembler().assemble( module );
    }

    @Test
    public void givenOptionalMethodWhenCorrectInvokeThenNoException()
    {
        TestComposite instance = transientBuilderFactory.newTransient( TestComposite.class );
        instance.doStuff( "Hello WOrld", "Hello World" );
    }

    @Test( expected = ConstraintViolationException.class )
    public void givenOptionalMethodWhenMandatoryMissingThenException()
    {
        TestComposite instance = transientBuilderFactory.newTransient( TestComposite.class );
        instance.doStuff( "Hello World", null );
    }

    @Test
    public void givenOptionalMethodWhenOptionalMissingThenNoException()
    {
        TestComposite instance = transientBuilderFactory.newTransient( TestComposite.class );
        instance.doStuff( null, "Hello World" );
    }

    @Test
    public void givenOptionalPropertyWhenOptionalMissingThenNoException()
    {
        TransientBuilder<TestComposite2> builder = transientBuilderFactory.newTransientBuilder( TestComposite2.class );
        builder.prototype().mandatoryProperty().set( "Hello World" );
        TestComposite2 testComposite2 = builder.newInstance();
    }

    @Test
    public void givenOptionalPropertyWhenOptionalSetThenNoException()
    {
        TransientBuilder<TestComposite2> builder = transientBuilderFactory.newTransientBuilder( TestComposite2.class );
        builder.prototype().mandatoryProperty().set( "Hello World" );
        builder.prototype().optionalProperty().set( "Hello World" );
        TestComposite2 testComposite2 = builder.newInstance();
    }

    @Test( expected = ConstraintViolationException.class )
    public void givenMandatoryPropertyWhenMandatoryMissingThenException()
    {
        TestComposite2 testComposite2 = transientBuilderFactory.newTransient( TestComposite2.class );
    }

    @Test
    public void givenOptionalAssociationWhenOptionalMissingThenNoException()
        throws Exception
    {
        UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
        try
        {
            TestComposite4 ref = unitOfWork.newEntity( TestComposite4.class );

            EntityBuilder<TestComposite3> builder = unitOfWork.newEntityBuilder( TestComposite3.class );
            builder.instance().mandatoryAssociation().set( ref );
            TestComposite3 testComposite3 = builder.newInstance();

            unitOfWork.complete();
        }
        finally
        {
            unitOfWork.discard();
        }
    }

    @Test
    public void givenOptionalAssociationWhenOptionalSetThenNoException()
        throws Exception
    {
        UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
        try
        {
            TestComposite4 ref = unitOfWork.newEntity( TestComposite4.class );

            EntityBuilder<TestComposite3> builder = unitOfWork.newEntityBuilder( TestComposite3.class );
            builder.instance().mandatoryAssociation().set( ref );
            builder.instance().optionalAssociation().set( ref );
            TestComposite3 testComposite3 = builder.newInstance();

            unitOfWork.complete();
        }
        finally
        {
            unitOfWork.discard();
        }
    }

    @Test( expected = ConstraintViolationException.class )
    public void givenMandatoryAssociationWhenMandatoryMissingThenException()
        throws Exception
    {
        UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
        try
        {
            TestComposite4 ref = unitOfWork.newEntity( TestComposite4.class );

            EntityBuilder<TestComposite3> builder = unitOfWork.newEntityBuilder( TestComposite3.class );
            builder.instance().optionalAssociation().set( ref );
            TestComposite3 testComposite3 = builder.newInstance();

            unitOfWork.complete();
        }
        finally
        {
            unitOfWork.discard();
        }
    }

    @Mixins( TestComposite.TestMixin.class )
    public interface TestComposite
        extends TransientComposite
    {
        void doStuff( @Optional String optional, String mandatory );

        abstract class TestMixin
            implements TestComposite
        {
            public void doStuff( @Optional String optional, String mandatory )
            {
                assertThat( "Mandatory is not null", mandatory, notNullValue() );
            }
        }
    }

    public interface TestComposite2
        extends TransientComposite
    {
        @Optional
        Property<String> optionalProperty();

        Property<String> mandatoryProperty();
    }

    public interface TestComposite3
        extends EntityComposite
    {
        @Optional
        Association<TestComposite4> optionalAssociation();

        Association<TestComposite4> mandatoryAssociation();
    }

    public interface TestComposite4
        extends EntityComposite
    {
    }
}
