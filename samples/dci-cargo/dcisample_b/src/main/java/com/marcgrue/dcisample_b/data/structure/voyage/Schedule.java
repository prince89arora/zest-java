package com.marcgrue.dcisample_b.data.structure.voyage;

import java.util.List;
import org.qi4j.api.property.Property;
import org.qi4j.api.value.ValueComposite;

/**
 * Schedule
 *
 * A schedule is a series of {@link CarrierMovement}s.
 *
 * List of carrier movements is mandatory and immutable.
 */
public interface Schedule
    extends ValueComposite
{
    Property<List<CarrierMovement>> carrierMovements();
}
