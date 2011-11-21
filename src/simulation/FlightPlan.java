package simulation;


import java.util.LinkedList;


/**
 * @author ps
 * A simple linked list based class to store a sequence of Flights
 * belonging to a aircraft.   
 */
public class FlightPlan {
	private LinkedList<Flight> flightPlan = new LinkedList<Flight>();

	public int size(){
		return flightPlan.size();
	}
	
	public Flight removeNextFlight(){
		return flightPlan.removeFirst(); 
	}
	
	public void addFlight(Flight f){
		flightPlan.addLast(f);		 
	}

	@Override
	public String toString() {
		return "FlightPlan [flightPlan=" + flightPlan + "]";
	}
	
}
