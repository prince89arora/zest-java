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
package org.apache.zest.library.logging.log;

import java.io.Serializable;
import org.apache.zest.api.concern.Concerns;

@Concerns( { SimpleLogConcern.class } )
public interface SimpleLog
{
    void info( String message );

    void info( String message, Serializable param1 );

    void info( String message, Serializable param1, Serializable param2 );

    void info( String message, Serializable... params );

    void warning( String message );

    void warning( String message, Serializable param1 );

    void warning( String message, Serializable param1, Serializable param2 );

    void warning( String message, Serializable... params );

    void error( String message );

    void error( String message, Serializable param1 );

    void error( String message, Serializable param1, Serializable param2 );

    void error( String message, Serializable... params );


}
