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

package org.apache.zest.runtime.service;

import org.junit.Test;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.service.ServiceReference;
import org.apache.zest.bootstrap.AssemblyException;
import org.apache.zest.bootstrap.ModuleAssembly;
import org.apache.zest.test.AbstractZestTest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ServiceFinderTest extends AbstractZestTest
{

    @Override
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.services( CompileTimeService.class );
        module.services( RuntimeService.class )
            .withTypes( MyRuntimeService.class )
            .withMixins( MyRuntimeServiceMixin.class );
    }

    @Test
    public void givenServiceCompileTimeWeavingWhenFindingServiceBySuperTypeExceptServiceToBeFound()
    {
        ServiceReference<MyCompileTimeService> service = serviceFinder.findService( MyCompileTimeService.class );
        assertThat( service, notNullValue() );
        assertThat( service.get(), notNullValue() );
        assertThat( service.get().doSomething(), equalTo( "Niclas" ) );
    }

    @Test
    public void givenServiceRuntimeWeavingWhenFindingServiceBySuperTypeExceptServiceToBeFound()
    {
        ServiceReference<MyRuntimeService> service = serviceFinder.findService( MyRuntimeService.class );
        assertThat( service, notNullValue() );
        assertThat( service.get(), notNullValue() );
        assertThat( service.get().doSomething(), equalTo( "Niclas" ) );
    }

    @Mixins( MyCompileTimeServiceMixin.class )
    public interface MyCompileTimeService
    {
        String doSomething();
    }

    public interface MyRuntimeService
    {
        String doSomething();
    }

    public class MyCompileTimeServiceMixin
        implements MyCompileTimeService
    {

        @Override
        public String doSomething()
        {
            return "Niclas";
        }
    }

    public class MyRuntimeServiceMixin
        implements MyRuntimeService
    {

        @Override
        public String doSomething()
        {
            return "Niclas";
        }
    }

    @Mixins( CompileTimeServiceMixin.class )
    public interface CompileTimeService extends MyCompileTimeService
    {
        String anotherMethod();
    }

    @Mixins( RuntimeServiceMixin.class )
    public interface RuntimeService
    {
        String anotherMethod();
    }

    public abstract class CompileTimeServiceMixin
        implements CompileTimeService
    {
        @Override
        public String anotherMethod()
        {
            return "Hedhman";
        }
    }

    public class RuntimeServiceMixin
        implements RuntimeService
    {
        @Override
        public String anotherMethod()
        {
            return "Hedhman";
        }
    }
}
