package se.citerus.dddsample.domain.model.handling;

import static java.util.Arrays.asList;
import java.util.Date;
import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.SampleCarrierMovements;
import static se.citerus.dddsample.domain.model.carrier.SampleCarrierMovements.CM003;
import static se.citerus.dddsample.domain.model.carrier.SampleCarrierMovements.CM004;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.CLAIM;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.CUSTOMS;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.LOAD;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.RECEIVE;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.UNLOAD;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.valueOf;
import static se.citerus.dddsample.domain.model.location.SampleLocations.CHICAGO;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HAMBURG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HELSINKI;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.NEWYORK;

public class HandlingEventTest extends TestCase {
  private final Cargo cargo = new Cargo(new TrackingId("XYZ"), HONGKONG, NEWYORK);

  public void testNewWithCarrierMovement() throws Exception {

    HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), LOAD, HONGKONG, CM003);
    assertEquals(HONGKONG, e1.location());

    HandlingEvent e2 = new HandlingEvent(cargo, new Date(), new Date(), UNLOAD, NEWYORK, CM003);
    assertEquals(NEWYORK, e2.location());

      // These event types prohibit a carrier movement association
    for (HandlingEvent.Type type : asList(CLAIM, RECEIVE, CUSTOMS)) {
      try {
        new HandlingEvent(cargo, new Date(), new Date(), type, HONGKONG, CM003);
        fail("Handling event type " + type + " prohibits carrier movement");
      } catch (IllegalArgumentException expected) {}
    }

      // These event types requires a carrier movement association
    for (HandlingEvent.Type type : asList(LOAD, UNLOAD)) {
        try {
          new HandlingEvent(cargo, new Date(), new Date(), type, HONGKONG, null);
            fail("Handling event type " + type + " requires carrier movement");
        } catch (IllegalArgumentException expected) {}
    }
  }

  public void testNewWithLocation() throws Exception {
    HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, HELSINKI);
    assertEquals(HELSINKI, e1.location());
  }

  public void testCurrentLocationLoadEvent() throws Exception {

    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), LOAD, CHICAGO, CM004);
    
    assertEquals(CHICAGO, ev.location());
  }
  
  public void testCurrentLocationUnloadEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), UNLOAD, HAMBURG, CM004);
    
    assertEquals(HAMBURG, ev.location());
  }
  
  public void testCurrentLocationReceivedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), RECEIVE, CHICAGO);

    assertEquals(CHICAGO, ev.location());
  }
  public void testCurrentLocationClaimedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), CLAIM, CHICAGO);

    assertEquals(CHICAGO, ev.location());
  }
  
  public void testParseType() throws Exception {
    assertEquals(CLAIM, valueOf("CLAIM"));
    assertEquals(LOAD, valueOf("LOAD"));
    assertEquals(UNLOAD, valueOf("UNLOAD"));
    assertEquals(RECEIVE, valueOf("RECEIVE"));
  }
  
  public void testParseTypeIllegal() throws Exception {
    try {
      valueOf("NOT_A_HANDLING_EVENT_TYPE");
      assertTrue("Expected IllegaArgumentException to be thrown", false);
    } catch (IllegalArgumentException e) {
      // All's well
    }
  }
  
  public void testEqualsAndSameAs() throws Exception {
    Date timeOccured = new Date();
    Date timeRegistered = new Date();

    HandlingEvent ev1 = new HandlingEvent(cargo, timeOccured, timeRegistered, LOAD, CHICAGO, SampleCarrierMovements.CM005);
    HandlingEvent ev2 = new HandlingEvent(cargo, timeOccured, timeRegistered, LOAD, CHICAGO, SampleCarrierMovements.CM005);

    // Two handling events are not equal() even if all non-uuid fields are identical
    assertTrue(ev1.equals(ev2));
    assertTrue(ev2.equals(ev1));

    assertTrue(ev1.equals(ev1));

    assertFalse(ev2.equals(null));
    assertFalse(ev2.equals(new Object()));
  }

}
