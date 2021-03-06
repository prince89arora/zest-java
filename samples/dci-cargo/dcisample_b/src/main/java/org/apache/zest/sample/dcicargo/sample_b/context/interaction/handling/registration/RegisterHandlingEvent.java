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
package org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.registration;

import org.apache.zest.api.injection.scope.This;
import org.apache.zest.api.mixin.Mixins;
import org.apache.zest.api.query.Query;
import org.apache.zest.api.query.QueryBuilder;
import org.apache.zest.api.unitofwork.NoSuchEntityException;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.ProcessHandlingEvent;
import org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.parsing.dto.ParsedHandlingEventData;
import org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.registration.exception.AlreadyClaimedException;
import org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.registration.exception.CannotRegisterHandlingEventException;
import org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.registration.exception.DuplicateEventException;
import org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.registration.exception.MissingVoyageNumberException;
import org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.registration.exception.UnknownCargoException;
import org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.registration.exception.UnknownLocationException;
import org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.registration.exception.UnknownVoyageException;
import org.apache.zest.sample.dcicargo.sample_b.data.aggregateroot.HandlingEventAggregateRoot;
import org.apache.zest.sample.dcicargo.sample_b.data.factory.HandlingEventFactory;
import org.apache.zest.sample.dcicargo.sample_b.data.factory.exception.CannotCreateHandlingEventException;
import org.apache.zest.sample.dcicargo.sample_b.data.structure.cargo.Cargo;
import org.apache.zest.sample.dcicargo.sample_b.data.structure.handling.HandlingEvent;
import org.apache.zest.sample.dcicargo.sample_b.data.structure.handling.HandlingEventType;
import org.apache.zest.sample.dcicargo.sample_b.data.structure.location.Location;
import org.apache.zest.sample.dcicargo.sample_b.data.structure.tracking.TrackingId;
import org.apache.zest.sample.dcicargo.sample_b.data.structure.voyage.Voyage;
import org.apache.zest.sample.dcicargo.sample_b.infrastructure.dci.Context;
import org.apache.zest.sample.dcicargo.sample_b.infrastructure.dci.RoleMixin;

import static org.apache.zest.api.query.QueryExpressions.*;
import static org.apache.zest.sample.dcicargo.sample_b.data.aggregateroot.HandlingEventAggregateRoot.HANDLING_EVENTS_ID;
import static org.apache.zest.sample.dcicargo.sample_b.data.structure.handling.HandlingEventType.*;

/**
 * Register Handling Event (subfunction use case)
 *
 * Second step in the ProcessHandlingEvent use case.
 *
 * Verifies handling event data received from ParseHandlingEventData against the database in
 * order to create a valid HandlingEvent.
 *
 * If only the enclosing {@link ProcessHandlingEvent} summary use case is allowed to call this
 * subfunction use case, we could draw an interesting parallel to the responsibilities of the
 * DDD aggregate of enforcing invariants between its member objects. Whereas here it would be
 * {@link ProcessHandlingEvent} enforcing its subfunction use cases to process correctly and
 * ultimately abort the interactions chain when any of the subfunction interactions fail...
 *
 * As an example of "process integrity enforcement" we here validate that a cargo with the
 * given TrackingId exists in our system. When we then inspect the cargo in the third and last
 * step of the ProcessHandlingEvent summary use case, we presume having a valid cargo.
 *
 * How can we ensure that RegisterHandlingEvent is not mistakenly called directly out of the
 * larger context of ProcessHandlingEvent?
 *
 * IMPORTANT:
 * Compared to the DDD sample, we don't save a Cargo object with the HandlingEvent, but only
 * the TrackingId! HandlingEvent never needs the full Cargo graph (not in the DDD sample either),
 * so by saving only the TrackingId we get a much slimmer HandlingEvent object.
 */
public class RegisterHandlingEvent extends Context
{
    private EventRegistrarRole eventRegistrar;

    private ParsedHandlingEventData eventData;
    private HandlingEventType eventType;
    private String trackingIdString;
    private String unLocodeString;
    private String voyageNumberString;

    public RegisterHandlingEvent( ParsedHandlingEventData parsedEventData )
    {
        eventRegistrar = rolePlayer( EventRegistrarRole.class, HandlingEventAggregateRoot.class, HANDLING_EVENTS_ID );

        this.eventData = parsedEventData;
        eventType = parsedEventData.handlingEventType().get();
        trackingIdString = parsedEventData.trackingIdString().get();
        unLocodeString = parsedEventData.unLocodeString().get();
        voyageNumberString = parsedEventData.voyageNumberString().get();
    }

    public HandlingEvent getEvent()
        throws CannotRegisterHandlingEventException
    {
        return eventRegistrar.registerAndGetHandlingEvent();
    }

    @Mixins( EventRegistrarRole.Mixin.class )
    public interface EventRegistrarRole
    {
        void setContext( RegisterHandlingEvent context );

        HandlingEvent registerAndGetHandlingEvent()
            throws CannotRegisterHandlingEventException;

        class Mixin
            extends RoleMixin<RegisterHandlingEvent>
            implements EventRegistrarRole
        {
            @This
            HandlingEventFactory eventFactory;

            public HandlingEvent registerAndGetHandlingEvent()
                throws CannotRegisterHandlingEventException
            {
                UnitOfWork uow = uowf.currentUnitOfWork();
                TrackingId trackingId;
                Location location;
                Voyage voyage = null;

                // Step 1 - Find Cargo from tracking id string
                try
                {
                    trackingId = uow.get( Cargo.class, c.trackingIdString ).trackingId().get();
                }
                catch( NoSuchEntityException e )
                {
                    throw new UnknownCargoException( c.eventData );
                }

                // Step 2 - Find Location from UnLocode string

                try
                {
                    location = uow.get( Location.class, c.unLocodeString );
                }
                catch( NoSuchEntityException e )
                {
                    throw new UnknownLocationException( c.eventData );
                }

                // Step 3 - Find Voyage from voyage number string

                if( c.eventType.requiresVoyage() )
                {
                    if( c.voyageNumberString == null )
                    {
                        throw new MissingVoyageNumberException( c.eventData );
                    }

                    try
                    {
                        voyage = uow.get( Voyage.class, c.voyageNumberString );
                    }
                    catch( NoSuchEntityException e )
                    {
                        throw new UnknownVoyageException( c.eventData );
                    }
                }

                // Step 4 - Verify that cargo is not received, in customs or claimed more than once

                if( c.eventType.equals( RECEIVE ) || c.eventType.equals( CUSTOMS ) || c.eventType.equals( CLAIM ) )
                {
                    QueryBuilder<HandlingEvent> qb = qbf.newQueryBuilder( HandlingEvent.class )
                        .where(
                            and(
                                eq( templateFor( HandlingEvent.class ).trackingId().get().id(), c.trackingIdString ),
                                eq( templateFor( HandlingEvent.class ).handlingEventType(), c.eventType )
                            )
                        );
                    Query<HandlingEvent> duplicates = uowf.currentUnitOfWork().newQuery( qb );
                    if( duplicates.count() > 0 )
                    {
                        throw new DuplicateEventException( c.eventData );
                    }
                }

                // Step 5 - Verify that cargo is not handled after being claimed

                if( !c.eventType.equals( CLAIM ) )
                {
                    HandlingEvent eventTemplate = templateFor( HandlingEvent.class );
                    QueryBuilder<HandlingEvent> qb = qbf.newQueryBuilder( HandlingEvent.class )
                        .where(
                            and(
                                eq( eventTemplate.trackingId().get().id(), c.trackingIdString ),
                                eq( eventTemplate.handlingEventType(), CLAIM )
                            )
                        );
                    Query<HandlingEvent> alreadyClaimed = uowf.currentUnitOfWork().newQuery( qb );
                    if( alreadyClaimed.count() > 0 )
                    {
                        throw new AlreadyClaimedException( c.eventData );
                    }
                }

                // Step 6 - Create Handling Event in the system

                try
                {
                    return eventFactory.createHandlingEvent( c.eventData.registrationDate().get(),
                                                             c.eventData.completionDate().get(),
                                                             trackingId,
                                                             c.eventData.handlingEventType().get(),
                                                             location,
                                                             voyage );
                }
                catch( CannotCreateHandlingEventException e )
                {
                    throw new CannotRegisterHandlingEventException( c.eventData );
                }
            }
        }
    }
}
