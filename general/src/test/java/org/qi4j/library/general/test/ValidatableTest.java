/*
 * Copyright (c) 2007, Sianny Halim. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.library.general.test;

import org.qi4j.api.annotation.ImplementedBy;
import org.qi4j.api.annotation.ModifiedBy;
import org.qi4j.api.persistence.EntityComposite;
import org.qi4j.library.framework.properties.PropertiesMixin;
import org.qi4j.library.general.model.AbstractTest;
import org.qi4j.library.general.model.HasName;
import org.qi4j.library.general.model.Validatable;
import org.qi4j.library.general.model.modifiers.LifecycleValidationModifier;
import org.qi4j.library.general.test.model.DummyValidationModifier;

public class ValidatableTest extends AbstractTest
{
    public void testValidatableSuccessful() throws Exception
    {
        DummyComposite composite = builderFactory.newCompositeBuilder( DummyComposite.class ).newInstance();
//        composite.setEntityRepository( new DummyEntityRepository() );
//        composite.setEntityRepository( null );
//        composite.create();

        assertTrue( DummyValidationModifier.validateIsCalled);
    }

    @ModifiedBy( { LifecycleValidationModifier.class, DummyValidationModifier.class } )
    @ImplementedBy( { PropertiesMixin.class } )
    private interface DummyComposite extends HasName, Validatable, EntityComposite
    {
    }
}
