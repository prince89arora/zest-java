/*  Copyright 2008 Edward Yakop.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
* implied.
*
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.qi4j.samples.dddsample.domain.service.assembly;

import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;

import static org.qi4j.api.common.Visibility.*;

/**
 * Note: BookingService is handled by the model itself.
 *
 * @author edward.yakop@gmail.com
 * @since 0.5
 */
public final class HandlingServiceAssembler
    implements Assembler
{
    public final void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.addServices(
            HandlingEventService.class
        ).visibleIn( application );
    }
}