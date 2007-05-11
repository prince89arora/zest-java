/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
 * Copyright (c) 2007, Niclas Hedhman. All Rights Reserved.
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
package iop.runtime.persistence;

import org.qi4j.api.ObjectFactory;
import org.qi4j.api.annotation.Dependency;
import org.qi4j.api.annotation.Modifies;
import org.qi4j.api.annotation.Uses;
import org.qi4j.api.persistence.binding.PersistenceBinding;

public final class PeristentCloneableModifier<T extends PersistenceBinding>
    implements org.qi4j.api.persistence.Cloneable<T>
{
    @Modifies
    org.qi4j.api.persistence.Cloneable<T> cloneable;
    @Uses
    PersistenceBinding persistent;
    @Dependency
    ObjectFactory factory;

    public T clone()
    {
        T cloned = cloneable.clone();
        cloned.setIdentity( persistent.getIdentity() + "cloned" );
        persistent.getPersistentRepository().create( factory.getThat( cloned ) );
        return cloned;
    }
}
