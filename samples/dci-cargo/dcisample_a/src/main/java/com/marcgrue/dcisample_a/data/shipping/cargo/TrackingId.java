package com.marcgrue.dcisample_a.data.shipping.cargo;

import org.qi4j.api.property.Property;
import org.qi4j.api.value.ValueComposite;
import org.qi4j.library.constraints.annotation.NotEmpty;

/**
 * A TrackingId uniquely identifies a particular cargo.
 * Automatically generated by the application.
 */
public interface TrackingId
    extends ValueComposite
{
    @NotEmpty
    Property<String> id();
}
