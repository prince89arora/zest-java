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
package org.apache.zest.tutorials.cargo.step1.internal;

import org.apache.zest.tutorials.cargo.step1.BookingPolicy;
import org.apache.zest.tutorials.cargo.step1.Cargo;
import org.apache.zest.tutorials.cargo.step1.ShippingService;
import org.apache.zest.tutorials.cargo.step1.Voyage;

public class ShippingServiceImpl
    implements ShippingService
{
    private BookingPolicy bookingPolicy;
    private SimpleSequenceImpl orderConfirmationSequence;

    public ShippingServiceImpl( BookingPolicy bookingPolicy )
    {
        this.bookingPolicy = bookingPolicy;
        orderConfirmationSequence = new SimpleSequenceImpl();
    }

    @Override
    public int makeBooking( Cargo cargo, Voyage voyage )
    {
        if( !bookingPolicy.isAllowed( cargo, voyage ) )
        {
            return -1;
        }
        int confirmation = orderConfirmationSequence.next();
        if( voyage instanceof VoyageImpl )
        {
            ( (VoyageImpl) voyage ).addCargo( cargo, confirmation );
        }
        else
        {
            return -1;
        }
        return confirmation;
    }
}
