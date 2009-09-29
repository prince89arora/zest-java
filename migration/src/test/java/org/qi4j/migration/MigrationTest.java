/*
 * Copyright (c) 2009, Rickard Öberg. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.qi4j.migration;

import static org.junit.Assert.*;
import org.junit.Test;
import org.hamcrest.CoreMatchers;
import org.qi4j.test.AbstractQi4jTest;
import org.qi4j.test.EntityTestAssembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.bootstrap.SingletonAssembler;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.entitystore.memory.TestData;
import org.qi4j.entitystore.map.StateStore;
import org.qi4j.entitystore.map.MapEntityStore;
import org.qi4j.migration.assembly.EntityMigrationOperation;
import org.qi4j.migration.assembly.MigrationBuilder;
import org.qi4j.migration.assembly.MigrationOperation;
import org.qi4j.spi.service.importer.NewObjectImporter;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;

/**
 * JAVADOC
 */
public class MigrationTest
    extends AbstractQi4jTest
{
    public void assemble( ModuleAssembly module ) throws AssemblyException
    {
        new EntityTestAssembler().assemble( module );

        module.addObjects( MigrationEventLogger.class );
        module.importServices( MigrationEventLogger.class ).importedBy( NewObjectImporter.class );

        module.addEntities( TestEntity1_0.class,
                            TestEntity1_1.class,
                            TestEntity2_0.class);

        MigrationBuilder migration = new MigrationBuilder("1.0");
        migration.
            toVersion("1.1").
                renameEntity(TestEntity1_0.class.getName(), TestEntity1_1.class.getName()).
                atStartup( new CustomFixOperation("Fix for 1.1") ).
                forEntities(TestEntity1_1.class.getName()).
                    renameProperty( "foo", "newFoo").
                    renameManyAssociation( "fooManyAssoc", "newFooManyAssoc" ).
                    renameAssociation( "fooAssoc", "newFooAssoc" ).

            toVersion( "2.0" ).
                renameEntity(TestEntity1_1.class.getName(), TestEntity2_0.class.getName()).
                atStartup( new CustomFixOperation("Fix for 2.0, 1") ).
                atStartup( new CustomFixOperation("Fix for 2.0, 2") ).
                forEntities( TestEntity2_0.class.getName() ).
                    addProperty("bar", "Some value").
                    removeProperty( "newFoo", "Some value" ).
                    custom( new CustomBarOperation() );

        module.addServices( MigrationService.class ).setMetaInfo( migration );
        module.addEntities( MigrationConfiguration.class );
        module.forMixin( MigrationConfiguration.class ).declareDefaults().lastStartupVersion().set( "1.0" );
    }

    @Test
    public void testMigration() throws UnitOfWorkCompletionException, IOException
    {
        // Set up version 1
        String id;
        String data_v1;
        {
            SingletonAssembler v1 = new SingletonAssembler()
                    {
                        public void assemble( ModuleAssembly module ) throws AssemblyException
                        {
                            MigrationTest.this.assemble( module );
                            module.layerAssembly().applicationAssembly().setVersion( "1.0" );
                        }
                    };

            UnitOfWork uow = v1.unitOfWorkFactory().newUnitOfWork();
            TestEntity1_0 entity = uow.newEntity( TestEntity1_0.class );
            entity.foo().set( "Some value" );
            entity.fooManyAssoc().add( entity );
            entity.fooAssoc().set( entity );
            id = entity.identity().get();
            uow.complete();

            TestData testData = (TestData) v1.module().serviceFinder().findService( TestData.class ).get();
            data_v1 = testData.exportData();
        }

        // Set up version 1.1
        String data_v1_1;
        {
            SingletonAssembler v1_1 = new SingletonAssembler()
                    {
                        public void assemble( ModuleAssembly module ) throws AssemblyException
                        {
                            MigrationTest.this.assemble( module );
                            module.layerAssembly().applicationAssembly().setVersion( "1.1" );
                        }
                    };

            TestData testData = (TestData) v1_1.serviceFinder().findService( TestData.class ).get();
            testData.importData( data_v1 );

            UnitOfWork uow = v1_1.unitOfWorkFactory().newUnitOfWork();
            TestEntity1_1 entity = uow.get( TestEntity1_1.class, id );
            assertThat( "Property has been renamed", entity.newFoo().get(), CoreMatchers.equalTo("Some value" ));
            assertThat( "ManyAssociation has been renamed", entity.newFooManyAssoc().count(), CoreMatchers.equalTo(1 ));
            assertThat( "Association has been renamed", entity.newFooAssoc().get(), CoreMatchers.equalTo(entity ));
            uow.complete();

            data_v1_1 = testData.exportData();
        }

        // Set up version 2.0
        {
            SingletonAssembler v2_0 = new SingletonAssembler()
                    {
                        public void assemble( ModuleAssembly module ) throws AssemblyException
                        {
                            MigrationTest.this.assemble( module );
                            module.layerAssembly().applicationAssembly().setVersion( "2.0" );
                        }
                    };

            TestData testData = (TestData) v2_0.serviceFinder().findService( TestData.class ).get();
            testData.importData( data_v1_1 );

            // Test migration from 1.0 -> 2.0
            {
                testData.importData( data_v1 );
                UnitOfWork uow = v2_0.unitOfWorkFactory().newUnitOfWork();
                TestEntity2_0 entity = uow.get( TestEntity2_0.class, id );
                assertThat( "Property has been created", entity.bar().get(), CoreMatchers.equalTo("Some value" ));
                assertThat( "Custom Property has been created", entity.customBar().get(), CoreMatchers.equalTo("Hello Some value" ));
                assertThat( "ManyAssociation has been renamed", entity.newFooManyAssoc().count(), CoreMatchers.equalTo(1 ));
                assertThat( "Association has been renamed", entity.newFooAssoc().get(), CoreMatchers.equalTo(entity ));
                uow.complete();
            }
        }

    }

    private static class CustomBarOperation implements EntityMigrationOperation
    {
        public boolean upgrade( JSONObject state, StateStore stateStore, Migrator migrator ) throws JSONException
        {
            JSONObject properties = (JSONObject) state.get( MapEntityStore.JSONKeys.properties.name() );

            return migrator.addProperty( state, "customBar", "Hello "+ properties.getString("bar" ));
        }

        public boolean downgrade( JSONObject state, StateStore stateStore, Migrator migrator ) throws JSONException
        {
            return migrator.removeProperty( state, "customBar" );
        }
    }

    private static class CustomFixOperation implements MigrationOperation
    {
        String msg;

        private CustomFixOperation( String msg )
        {
            this.msg = msg;
        }

        public void upgrade( StateStore stateStore, Migrator migrator ) throws IOException
        {
            System.out.println(msg);
        }

        public void downgrade( StateStore stateStore, Migrator migrator ) throws IOException
        {
            System.out.println(msg);
        }
    }
}
