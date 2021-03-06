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

package org.apache.zest.test.composite;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.zest.api.concern.Concerns;
import org.apache.zest.api.concern.GenericConcern;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.bootstrap.AssemblyException;
import org.apache.zest.bootstrap.ModuleAssembly;
import org.apache.zest.test.AbstractZestTest;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

/**
 * Test if the stacktrace is cleaned up properly.
 * <p>
 * NOTE: This satisfiedBy MUST NOT be inside package org.apache.zest.runtime, or it will fail.
 * </p>
 */
public class CleanStackTraceTest
    extends AbstractZestTest
{

    @BeforeClass
    public static void beforeClass_IBMJDK()
    {   
        assumeTrue( !( System.getProperty( "java.vendor" ).contains( "IBM" ) ) );
    }   

    @Override
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.transients( CleanStackTraceTest.TestComposite.class );
    }

    /**
     * Tests that stack trace is cleaned up on an application exception.
     */
    @Test
    public void cleanStackTraceOnApplicationException()
    {
        // Don't run the satisfiedBy if compacttrace is set to anything else but proxy
        String compactTracePropertyValue = System.getProperty( "zest.compacttrace" );
        if( compactTracePropertyValue != null && !"proxy".equals( compactTracePropertyValue ) )
        {
            return;
        }
        TestComposite composite = transientBuilderFactory.newTransient( TestComposite.class );
        try
        {
            composite.doStuff();
        }
        catch( RuntimeException e )
        {
            String separator = System.getProperty( "line.separator" );
            String correctTrace1 = "java.lang.RuntimeException: level 2" + separator +
                                   "\tat org.apache.zest.test.composite.CleanStackTraceTest$DoStuffMixin.doStuff(CleanStackTraceTest.java:128)" + separator +
                                   "\tat org.apache.zest.test.composite.CleanStackTraceTest$NillyWilly.invoke(CleanStackTraceTest.java:141)" + separator +
                                   "\tat org.apache.zest.test.composite.CleanStackTraceTest.cleanStackTraceOnApplicationException(CleanStackTraceTest.java:79)";
            assertEquality( e, correctTrace1 );
            String correctTrace2 = "java.lang.RuntimeException: level 1" + separator +
                                   "\tat org.apache.zest.test.composite.CleanStackTraceTest$DoStuffMixin.doStuff(CleanStackTraceTest.java:124)" + separator +
                                   "\tat org.apache.zest.test.composite.CleanStackTraceTest$NillyWilly.invoke(CleanStackTraceTest.java:141)" + separator +
                                   "\tat org.apache.zest.test.composite.CleanStackTraceTest.cleanStackTraceOnApplicationException(CleanStackTraceTest.java:79)";
            assertThat( e.getCause(), notNullValue() );
            assertEquality( e.getCause(), correctTrace2 );
        }
    }

    private void assertEquality( Throwable e, String correctTrace )
    {
        StringWriter actualTrace = new StringWriter();
        e.printStackTrace( new PrintWriter( actualTrace ) );

        String actual = actualTrace.toString();
        actual = actual.substring( 0, correctTrace.length() );
        assertEquals( correctTrace, actual );
    }

    @Concerns( NillyWilly.class )
    @Mixins(DoStuffMixin.class)
    public interface TestComposite
    {
        void doStuff();
    }

    public static class DoStuffMixin
        implements TestComposite
    {

        @Override
        public void doStuff()
        {
            try
            {
                throw new RuntimeException( "level 1" );
            }
            catch( RuntimeException e )
            {
                throw new RuntimeException( "level 2", e );
            }
        }
    }

    static class NillyWilly extends GenericConcern
        implements InvocationHandler
    {

        @Override
        public Object invoke( Object proxy, Method method, Object[] args )
            throws Throwable
        {
            return next.invoke( proxy, method, args );
        }
    }
}
