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
package org.apache.zest.runtime.value;

import org.apache.zest.valueserialization.orgjson.OrgJsonValueSerializationService;
import org.junit.Test;
import org.apache.zest.api.injection.scope.This;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.property.Property;
import org.apache.zest.api.value.ValueBuilder;
import org.apache.zest.bootstrap.AssemblyException;
import org.apache.zest.bootstrap.ModuleAssembly;
import org.apache.zest.test.AbstractZestTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ValueCompositeBasicsTest
    extends AbstractZestTest
{
    @Override
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.services( OrgJsonValueSerializationService.class );
        module.values( SomeValue.class );
    }

    @Test
    public void testEqualsForValueComposite()
    {
        ValueBuilder<SomeValue> builder = valueBuilderFactory.newValueBuilder( SomeValue.class );
        builder.prototypeFor( SomeInternalState.class ).name().set( "Niclas" );
        assertEquals("Niclas", builder.prototype().name());
        SomeValue value1 = builder.newInstance();
        SomeValue value2 = builder.newInstance();
        builder.prototypeFor( SomeInternalState.class ).name().set( "Niclas2" );
        SomeValue value3 = builder.newInstance();
        assertEquals( value1, value2 );
        assertFalse( value1.equals( value3 ) );
    }

    @Test
    public void testToStringForValueComposite()
    {
        ValueBuilder<SomeValue> builder = valueBuilderFactory.newValueBuilder( SomeValue.class );
        builder.prototypeFor( SomeInternalState.class ).name().set( "Niclas" );
        SomeValue underTest = builder.newInstance();
        assertEquals( "{\"name\":\"Niclas\"}", underTest.toString() );
    }

    @Test
    public void testToJSonForValueComposite()
    {
        ValueBuilder<SomeValue> builder = valueBuilderFactory.newValueBuilder( SomeValue.class );
        builder.prototypeFor( SomeInternalState.class ).name().set( "Niclas" );
        SomeValue underTest = builder.newInstance();
        assertEquals( "{\"name\":\"Niclas\"}", underTest.toString() );
    }

    public abstract static class SomeMixin
        implements SomeValue
    {
        @This
        private SomeInternalState state;

        @Override
        public String name()
        {
            return state.name().get();
        }
    }

    @Mixins( SomeMixin.class )
    public interface SomeValue
        //extends ValueComposite
    {
        String name();
    }

    public interface SomeInternalState
    {
        Property<String> name();
    }
}
