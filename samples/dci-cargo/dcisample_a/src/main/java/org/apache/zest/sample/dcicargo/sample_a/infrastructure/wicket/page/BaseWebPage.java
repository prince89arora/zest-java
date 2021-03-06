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
package org.apache.zest.sample.dcicargo.sample_a.infrastructure.wicket.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.zest.api.composite.TransientBuilderFactory;
import org.apache.zest.api.composite.TransientComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic Wicket base page
 *
 * Convenience access to common resources
 */
public class BaseWebPage extends WebPage
{
    public Logger logger = LoggerFactory.getLogger( getClass() );

    static protected TransientBuilderFactory tbf;

    public BaseWebPage( PageParameters pageParameters )
    {
        super( pageParameters );
    }

    public BaseWebPage()
    {
    }

    public static <T extends TransientComposite> T query( Class<T> queryClass )
    {
        return tbf.newTransient( queryClass );
    }

    public static void prepareBaseWebPageClass( TransientBuilderFactory transientBuilderFactory )
    {
        tbf = transientBuilderFactory;
    }
}