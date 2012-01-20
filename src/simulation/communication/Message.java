package simulation.communication;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;
import simulation.model.Flight;
import simulation.model.FlightPlan;
import simulation.model.SimWorld;

public class Message implements Serializable {

	/**
	 * version id for Serialization; increment this when changing the class
	 */
	private static final long serialVersionUID = 3L;

	private int eventType;
	private boolean antiMessage;
	private long timeStamp;
	private long takeOffTime;
	private String aircraftName;
	private String origin;
	private String destination;
	private long[] fpTimegaps;
	private String[] fpDestinations;

	public Message(Event event, Aircraft aircraft) {
		eventType = event.getType();
		antiMessage = event.isAntiMessage();
		timeStamp = event.getTimeStamp();
		if (eventType == Event.START_GVT)
			return;
		takeOffTime = aircraft.getLastTime();
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
		final Aircraft aircraft = eventType == Event.START_GVT ? null : getAircraft(world);
		final Event event = new Event(eventType, timeStamp, ap, aircraft);
		event.setAntiMessage(antiMessage);
		return event;
	}

	private Aircraft getAircraft(SimWorld world) {
		Aircraft aircraft = world.getAircraft(aircraftName);
		final Airport destinationAp = world.getAirport(destination);
		if (aircraft == null)
			aircraft = new Aircraft(aircraftName, destinationAp);
		aircraft.setOrigin(world.getAirport(origin));
		aircraft.setDestination(destinationAp);
		aircraft.setCurrentAirPort(destinationAp);
		aircraft.setState(Aircraft.ON_FLIGHT);
		aircraft.setLastTime(takeOffTime);
		aircraft.calcPosition(timeStamp);
		aircraft.setFlightPlan(getFlightPlan(world));
		return aircraft;
	}

	private FlightPlan getFlightPlan(SimWorld world) {
		FlightPlan flightPlan = new FlightPlan();
		for (int i = 0; i < fpTimegaps.length; i++) {
			flightPlan.addFlight(new Flight(fpTimegaps[i], world.getAirport(fpDestinations[i])));
		}
		return flightPlan;
	}

	@Override
	public String toString() {
		return "Message [eventType=" + eventType + ", antiMessage=" + antiMessage + ", timeStamp=" + timeStamp
				+ ", takeOffTime=" + takeOffTime + ", aircraftName=" + aircraftName + ", origin=" + origin
				+ ", destination=" + destination + ", fpTimegaps=" + Arrays.toString(fpTimegaps) + ", fpDestinations="
				+ Arrays.toString(fpDestinations) + "]";
	}

	

}
