package se.citerus.dddsample.domain.service;

import java.util.List;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;

/**
 * Routing service.
 *
 */
public interface RoutingService {

  /**
   * @param routeSpecification route specification
   * @return A list of itineraries that satisfy the specification. May be an empty list if no route is found.
   */
  List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification);

}
