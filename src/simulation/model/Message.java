package simulation.model;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {

	/**
	 * version id for Serialization; increment this when changing the class
	 */
	private static final long serialVersionUID = 1L;

	private int eventType;
	private long timeStamp;
	private String aircraftName;
	private String origin;
	private String destination;
	private long[] fpTimegaps;
	private String[] fpDestinations;

	public Message(Event event, Aircraft aircraft) {
		eventType = event.getType();
		timeStamp = event.getTimeStamp();
		aircraftName = aircraft.getName();
		origin = aircraft.getOrigin().getName();
		destination = aircraft.getDestination().getName();
		final List<Flight> flights = aircraft.getFlightPlan().getFlights();
		fpTimegaps = new long[flights.size()];
		fpDestinations = new String[flights.size()];
		for (int i = 0; i < flights.size(); i++) {
			fpTimegaps[i] = flights.get(i).getTimeGap();
			fpDestinations[i] = flights.get(i).getDestination().getName();
		}
	}

	public Event getEvent(SimWorld world) {
		Airport ap = world.getAirport(destination);
		return new Event(eventType, ap, timeStamp, ap, getAircraft(world));
	}

	public Aircraft getAircraft(SimWorld world) {
		Aircraft aircraft = world.getAircraft(aircraftName);
		if (aircraft == null)
			aircraft = new Aircraft(aircraftName, world.getAirport(destination));
		aircraft.setOrigin(world.getAirport(origin));
		aircraft.setFlightPlan(getFlightPlan(world));
		return aircraft;
	}

	private FlightPlan getFlightPlan(SimWorld world) {
		FlightPlan flightPlan = new FlightPlan();
		for (int i = 0; i < fpTimegaps.length; i++) {
			flightPlan.addFlight(new Flight(fpTimegaps[i], world
					.getAirport(fpDestinations[i])));
		}
		return flightPlan;
	}

}
