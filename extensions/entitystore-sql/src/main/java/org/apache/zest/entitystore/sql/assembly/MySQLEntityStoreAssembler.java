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
package org.apache.zest.entitystore.sql.assembly;

import java.io.IOException;
import org.apache.zest.entitystore.sql.internal.MySQLDatabaseSQLServiceMixin;
import org.sql.generation.api.vendor.MySQLVendor;
import org.sql.generation.api.vendor.SQLVendor;
import org.sql.generation.api.vendor.SQLVendorProvider;

/**
 * MySQL EntityStore assembly.
 */
public class MySQLEntityStoreAssembler
        extends AbstractSQLEntityStoreAssembler<MySQLEntityStoreAssembler>
{

    @Override
    protected Class<?> getDatabaseSQLServiceSpecializationMixin()
    {
        return MySQLDatabaseSQLServiceMixin.class;
    }

    @Override
    protected SQLVendor getSQLVendor()
            throws IOException
    {
        return SQLVendorProvider.createVendor( MySQLVendor.class );
    }

}
