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
package org.apache.zest.index.elasticsearch;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.spi.query.IndexExporter;

@Mixins( ElasticSearchIndexExporter.Mixin.class )
public interface ElasticSearchIndexExporter
        extends IndexExporter
{

    class Mixin
            implements ElasticSearchIndexExporter
    {

        @Override
        public void exportReadableToStream( PrintStream out )
                throws IOException, UnsupportedOperationException
        {
            exportFormalToWriter( new PrintWriter( out ) );
        }

        @Override
        public void exportFormalToWriter( PrintWriter out )
                throws IOException, UnsupportedOperationException
        {
            // TODO
        }

    }

}
