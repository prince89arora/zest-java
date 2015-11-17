/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
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

package org.apache.zest.api.unitofwork;

import java.util.Map;
import java.util.stream.Collectors;
import org.apache.zest.api.entity.EntityComposite;
import org.apache.zest.api.type.HasTypes;
import org.apache.zest.api.usecase.Usecase;

/**
 * This exception is thrown by UnitOfWork.complete() if any entities that are being committed
 * had been changed while the UnitOfWork was being executed.
 */
public class ConcurrentEntityModificationException
    extends UnitOfWorkCompletionException
{
    private static final long serialVersionUID = 3872723845064767689L;

    private final Map<EntityComposite, HasTypes> concurrentlyModifiedEntities;

    public ConcurrentEntityModificationException( Map<EntityComposite, HasTypes> concurrentlyModifiedEntities,
                                                  Usecase usecase
    )
    {
        super( "Entities changed concurrently, and detected in usecase '" + usecase + "'\nModified entities : " + format( concurrentlyModifiedEntities ) );
        this.concurrentlyModifiedEntities = concurrentlyModifiedEntities;
    }

    private static String format( Map<EntityComposite, HasTypes> concurrentlyModifiedEntities )
    {
        return concurrentlyModifiedEntities.entrySet().stream()
            .map( entry ->
                      entry.getKey()
                      + " : "
                      + entry.getValue().types().map( Class::getSimpleName )
                          .collect( Collectors.joining( "," ) )
            )
            .collect( Collectors.joining( "\n" ) );
    }

    public Map<EntityComposite, HasTypes> concurrentlyModifiedEntities()
    {
        return concurrentlyModifiedEntities;
    }
}