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
package org.apache.zest.library.shiro.web;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.junit.Test;
import org.apache.zest.api.common.Visibility;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.service.ServiceActivation;
import org.apache.zest.api.service.ServiceComposite;
import org.apache.zest.bootstrap.AssemblyException;
import org.apache.zest.bootstrap.ModuleAssembly;
import org.apache.zest.library.http.JettyConfiguration;
import org.apache.zest.library.http.JettyServiceAssembler;
import org.apache.zest.library.shiro.ini.ShiroIniConfiguration;
import org.apache.zest.library.shiro.web.assembly.HttpShiroAssembler;
import org.apache.zest.test.AbstractZestTest;
import org.apache.zest.test.EntityTestAssembler;
import org.apache.zest.test.util.FreePortFinder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.apache.zest.library.http.Servlets.addServlets;
import static org.apache.zest.library.http.Servlets.serve;

public class WebRealmServiceTest
    extends AbstractZestTest
{

    private int port;

    @Mixins( MyRealmMixin.class )
    public interface MyRealmService
        extends Realm, ServiceComposite, ServiceActivation
    {
    }

    public class MyRealmMixin
        extends SimpleAccountRealm
        implements ServiceActivation
    {

        private final PasswordService passwordService;

        public MyRealmMixin()
        {
            super();
            passwordService = new DefaultPasswordService();
            PasswordMatcher matcher = new PasswordMatcher();
            matcher.setPasswordService( passwordService );
            setCredentialsMatcher( matcher );
        }

        @Override
        public void activateService()
            throws Exception
        {
            // Create a test account
            addAccount( "foo", passwordService.encryptPassword( "bar" ) );
        }

        @Override
        public void passivateService()
            throws Exception
        {
        }

    }

    @Mixins( MyServlet.class )
    public interface MyServletService
        extends Servlet, ServiceComposite
    {
    }

    public static class MyServlet
        extends HttpServlet
    {

        @Override
        protected void doGet( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException
        {
            resp.getWriter().println( "FOO" );
        }

    }

    @Override
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        try
        {
            ModuleAssembly configModule = module;
            new EntityTestAssembler().assemble( configModule );
            // START SNIPPET: assembly
            new JettyServiceAssembler().withConfig( configModule, Visibility.layer ).assemble( module );
            // END SNIPPET: assembly

            port = FreePortFinder.findFreePortOnLoopback();
            JettyConfiguration config = module.forMixin( JettyConfiguration.class ).declareDefaults();
            config.hostName().set( "127.0.0.1" );
            config.port().set( port );

            // START SNIPPET: assembly
            new HttpShiroAssembler().
                withConfig( configModule, Visibility.layer ).
                assemble( module );
            module.services( MyRealmService.class );
            // END SNIPPET: assembly

            configModule.forMixin( ShiroIniConfiguration.class ).
                declareDefaults().
                iniResourcePath().set( "classpath:web-shiro.ini" );

            addServlets( serve( "/*" ).with( MyServletService.class ) ).to( module );
        }
        catch( IOException ex )
        {
            throw new AssemblyException( "Unable to find free port to bind to", ex );
        }
    }

    @Test
    public void test()
        throws IOException
    {
        DefaultHttpClient client = new DefaultHttpClient();

        // Our request method
        HttpGet get = new HttpGet( "http://127.0.0.1:" + port + "/" );

        HttpResponse response = client.execute( get );
        int status = response.getStatusLine().getStatusCode();

        assertThat( String.valueOf( status ).substring( 0, 1 ) + "xx", is( "4xx" ) );

        EntityUtils.consume( response.getEntity() );

        // Simple interceptor set as first interceptor in the protocol chain
        HttpRequestInterceptor preemptiveAuth = new BasicAuthRequestInterceptor();
        client.addRequestInterceptor( preemptiveAuth, 0 );

        // Set credentials
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials( "foo", "bar" );
        client.getCredentialsProvider().setCredentials( AuthScope.ANY, creds );

        response = client.execute( get );
        status = response.getStatusLine().getStatusCode();

        assertThat( status, is( 200 ) );

        String result = new BasicResponseHandler().handleResponse( response ).trim();
        assertThat( result, is( "FOO" ) );
    }

    /**
     * HttpClient Basic Auth Request Interceptor.
     * Needed for HTTP Basic Authentication.
     */
    public class BasicAuthRequestInterceptor
        implements HttpRequestInterceptor
    {
        @Override
        public void process( final HttpRequest request, final HttpContext context )
            throws HttpException, IOException
        {
            AuthState authState = (AuthState) context.getAttribute( ClientContext.TARGET_AUTH_STATE );
            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute( ClientContext.CREDS_PROVIDER );
            HttpHost targetHost = (HttpHost) context.getAttribute( ExecutionContext.HTTP_TARGET_HOST );

            // If not auth scheme has been initialized yet
            if( authState.getAuthScheme() == null )
            {
                AuthScope authScope = new AuthScope( targetHost.getHostName(),
                                                     targetHost.getPort() );
                // Obtain credentials matching the target host
                Credentials creds = credsProvider.getCredentials( authScope );
                // If found, generate BasicScheme preemptively
                if( creds != null )
                {
                    authState.setAuthScheme( new BasicScheme() );
                    authState.setCredentials( creds );
                }
            }
        }
    }

}
