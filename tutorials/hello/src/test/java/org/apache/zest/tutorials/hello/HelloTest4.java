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
package org.apache.zest.tutorials.hello;

import org.junit.Test;
import org.apache.zest.api.entity.EntityBuilder;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.bootstrap.AssemblyException;
import org.apache.zest.bootstrap.ModuleAssembly;
import org.apache.zest.entitystore.memory.MemoryEntityStoreService;
import org.apache.zest.test.AbstractZestTest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class HelloTest4 extends AbstractZestTest
{
    @Override
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.entities( Hello.class );
        module.services( MemoryEntityStoreService.class );
    }

    @Test
    public void givenHelloValueInitializedToHelloWorldWhenCallingSayExpectHelloWorld()
    {
        UnitOfWork uow = unitOfWorkFactory.newUnitOfWork();
        try
        {
            EntityBuilder<Hello> builder = uow.newEntityBuilder( Hello.class, "123" );
            builder.instanceFor( Hello.State.class ).phrase().set( "Hello" );
            builder.instanceFor( Hello.State.class ).name().set( "World" );
            builder.newInstance();
            uow.complete();
            uow = unitOfWorkFactory.newUnitOfWork();
            Hello underTest = uow.get( Hello.class, "123" );
            String result = underTest.say();
            uow.complete();
            assertThat( result, equalTo( "Hello World" ) );
        }
        catch( Exception e )
        {
            uow.discard();
        }
    }
}
