/*
 * Copyright 2008 Niclas Hedhman. All rights Reserved.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package org.qi4j.bootstrap;

import java.io.IOException;
import java.util.Iterator;
import org.qi4j.api.structure.Application;
import org.qi4j.bootstrap.internal.ServiceLoader;
import org.qi4j.spi.Qi4jSPI;

/**
 * Main bootstrap class for starting Qi4j and creating new applications. Instantiate this
 * and call one of the factory methods to get started.
 *
 * This class will use the Service Loader mechanism in Java to try to locate a runtime that implements
 * the ApplicationFactory interface. This avoids a direct dependency from the bootstrap to the runtime.
 */
public class Energy4Java
{
    private static ServiceLoader serviceLoader;

    private ApplicationFactory factory;

    static
    {
        serviceLoader = new ServiceLoader();
    }

    public static ServiceLoader getServiceLoader()
    {
        return serviceLoader;
    }

    public static void setServiceLoader( ServiceLoader aServiceLoader )
    {
        serviceLoader = aServiceLoader;
    }

    public Energy4Java()
    {
        this( findQi4jApplicationFactory() );
    }

    public Energy4Java( ApplicationFactory factory )
    {
        this.factory = factory;
    }

    public Application newApplication( Assembler assembler )
        throws AssemblyException
    {
        return factory.newApplication( assembler );
    }

    public Application newApplication( Assembler[][][] assemblers )
        throws AssemblyException
    {
        return factory.newApplication( assemblers );
    }

    public Application newApplication( ApplicationAssembly applicationAssembly )
        throws AssemblyException
    {
        return factory.newApplication( applicationAssembly );
    }

    public ApplicationAssembly newApplicationAssembly()
    {
        return factory.newApplicationAssembly();
    }

    public Qi4jSPI runtime()
    {
        return factory.runtime();
    }
    
    private static ApplicationFactory findQi4jApplicationFactory()
        throws BootstrapException
    {
        try
        {
            Iterator<? extends ApplicationFactory> providers = serviceLoader.providers();
            if( providers.hasNext() )
            {
                return providers.next();
            }
        }
        catch( IOException e )
        {
            throw new BootstrapException( "Unable to load a ApplicationFactory provider.", e );
        }
        throw new BootstrapException( "No Application Factory providers found." );
    }

    public static void notifyRuntimeShutdown()
    {
        // TODO What????
    }
}
