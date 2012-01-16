package simulation.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ps A simple linked list based class to store a sequence of Flights
 *         belonging to a aircraft.
 */
public class FlightPlan {
	private LinkedList<Flight> flightPlan = new LinkedList<Flight>();
	private List<Flight> pastFlights = new ArrayList<Flight>();

	public int size() {
		return flightPlan.size();
	}

	public Flight removeNextFlight() {
		final Flight flight = flightPlan.removeFirst();
		pastFlights.add(flight);
		return flight;
	}

	public void addFlight(Flight f) {
		flightPlan.addLast(f);
	}

	public List<Flight> getFlights() {
		return flightPlan;
	}

	public Flight getPreviousFlight() {
		if (pastFlights.isEmpty())
			return null;
		return pastFlights.get(pastFlights.size() - 1);
	}

	@Override
	public String toString() {
		return "FlightPlan [flightPlan=" + flightPlan + "]";
	}

}
