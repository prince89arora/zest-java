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

package org.apache.zest.api.value;

import org.apache.zest.api.association.AssociationMixin;
import org.apache.zest.api.association.ManyAssociationMixin;
import org.apache.zest.api.association.NamedAssociationMixin;
import org.apache.zest.api.composite.Composite;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.property.Immutable;

/**
 * ValueComposites are Composites that has state, and equality is defined from its values and not any identity nor
 * instance references.
 *
 * <ul>
 * <li>No Identity</li>
 * <li>No Lifecycle</li>
 * <li>Immutable</li>
 * <li>equals()/hashCode() operates on the Properties</li>
 * <li>Can have property and associations methods.</li>
 * <li>Can not reference Services</li>
 * <li>Can not have @Uses</li>
 * </ul>
 */
@Immutable
@Mixins( { AssociationMixin.class, ManyAssociationMixin.class, NamedAssociationMixin.class } )
public interface ValueComposite
    extends Composite
{
}
