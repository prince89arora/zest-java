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
package org.apache.zest.tutorials.composites.tutorial4;

import org.apache.zest.api.composite.TransientComposite;

// START SNIPPET: solution

/**
 * This Composite interface declares all the Fragments
 * of the HelloWorld composite.
 * <p>
 * The Mixins annotation has been moved to the respective sub-interfaces.
 * The sub-interfaces therefore declare themselves what mixin implementation
 * is preferred. This interface could still have its own Mixins annotation
 * with overrides of those defaults however.
 * </p>
 */
public interface HelloWorldComposite
    extends HelloWorld, TransientComposite
{
}
// END SNIPPET: solution
