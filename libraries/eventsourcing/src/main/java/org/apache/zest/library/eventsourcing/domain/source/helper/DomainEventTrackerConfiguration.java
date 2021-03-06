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

package org.apache.zest.library.eventsourcing.domain.source.helper;

import org.apache.zest.api.common.UseDefaults;
import org.apache.zest.api.configuration.ConfigurationComposite;
import org.apache.zest.api.configuration.Enabled;
import org.apache.zest.api.property.Property;

/**
 * Configuration that a service doing event tracking must have. Let the configuration
 * of the service extend this one.
 */
public interface DomainEventTrackerConfiguration
        extends ConfigurationComposite, Enabled
{
    /**
     * A count of how many events have been read already. Call EventStore.events(lastOffset,{limit}) to get
     * the next set of events.
     *
     * @return count of how many events have been read already.
     */
    @UseDefaults
    Property<Long> lastOffset();
}
