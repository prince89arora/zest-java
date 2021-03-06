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
package org.apache.zest.sample.dcicargo.sample_b.context.test.handling.parsing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.apache.zest.api.constraint.ConstraintViolationException;
import org.apache.zest.api.service.ServiceReference;
import org.apache.zest.api.unitofwork.UnitOfWork;
import org.apache.zest.sample.dcicargo.sample_b.bootstrap.test.TestApplication;
import org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.parsing.ParseHandlingEventData;
import org.apache.zest.sample.dcicargo.sample_b.context.interaction.handling.parsing.exception.InvalidHandlingEventDataException;
import org.apache.zest.sample.dcicargo.sample_b.data.aggregateroot.CargoAggregateRoot;

import static org.apache.zest.sample.dcicargo.sample_b.data.structure.delivery.RoutingStatus.ROUTED;
import static org.apache.zest.sample.dcicargo.sample_b.data.structure.delivery.TransportStatus.NOT_RECEIVED;

/**
 * {@link ParseHandlingEventData} tests
 */
public class ParseHandlingEventDataTest extends TestApplication
{
    private static ParseHandlingEventData handlingEventParser;
    private static String completionDate;

    @Before
    public void prepareTest()
        throws Exception
    {
        super.prepareTest();
        TestApplication.setup();
        UnitOfWork uow = uowf.currentUnitOfWork();
        CargoAggregateRoot CARGOS = uow.get( CargoAggregateRoot.class, CargoAggregateRoot.CARGOS_ID );

        // Create new cargo
        routeSpec = routeSpecFactory.build( HONGKONG, STOCKHOLM, LocalDate.now(), deadline = DAY24 );
        delivery = delivery( TODAY, NOT_RECEIVED, ROUTED, unknownLeg );
        cargo = CARGOS.createCargo( routeSpec, delivery, "ABC" );
        trackingId = cargo.trackingId().get();
        trackingIdString = trackingId.id().get();
        cargo.itinerary().set( itinerary );
        completionDate = LocalDate.now().toString();

        // Start ParseHandlingEventData service
        ServiceReference<ParseHandlingEventData> ParseHandlingEventDataRef =
            serviceFinder.findService( ParseHandlingEventData.class );
        handlingEventParser = ParseHandlingEventDataRef.get();
    }

    // Null

    @Test
    public void deviation_2a_Null_CompletionTimeString()
        throws Exception
    {
        thrown.expect( ConstraintViolationException.class, "constraint \"not optional(param1)\", for value 'null'" );
        handlingEventParser.parse( null, trackingIdString, "RECEIVE", "CNHKG", null );
    }

    @Test
    public void deviation_2a_Null_TrackingIdString()
        throws Exception
    {
        thrown.expect( ConstraintViolationException.class, "constraint \"not optional(param2)\", for value 'null'" );
        handlingEventParser.parse( completionDate, null, "RECEIVE", "CNHKG", null );
    }

    // etc...

    @Test
    public void step_2_Null_VoyageNumberString()
        throws Exception
    {
        // No voyage number string is ok
        handlingEventParser.parse( completionDate, trackingIdString, "RECEIVE", "CNHKG", null );
    }

    // Empty

    @Test
    public void deviation_2a_Empty_CompletionTimeString()
        throws Exception
    {
        thrown.expect( ConstraintViolationException.class, "NotEmpty()(param1)\", for value ' '" );
        handlingEventParser.parse( " ", trackingIdString, "RECEIVE", "CNHKG", null );
    }

    @Test
    public void step_2_Empty_VoyageNumberString()
        throws Exception
    {
        // Empty voyage number string is ok
        handlingEventParser.parse( completionDate, trackingIdString, "RECEIVE", "CNHKG", " " );
    }

    // Basic type conversion

    @Test
    public void deviation_3a_TypeConversion_CompletionTimeString()
        throws Exception
    {
        thrown.expect( InvalidHandlingEventDataException.class,
                       "Invalid date format: '5/27/2011' must be on ISO 8601 format yyyy-MM-dd HH:mm" );
        handlingEventParser.parse( "5/27/2011", trackingIdString, "RECEIVE", "CNHKG", null );
    }

    @Test
    public void deviation_3a_TypeConversion_HandlingEventTypeString()
        throws Exception
    {
        thrown.expect( InvalidHandlingEventDataException.class, "No enum const" );
        handlingEventParser.parse( completionDate, trackingIdString, "HAND_OVER", "CNHKG", null );
    }

    // Successful parsing

    @Test
    public void success_Parsing()
        throws Exception
    {
        handlingEventParser.parse( completionDate, trackingIdString, "RECEIVE", "CNHKG", null );
    }
}