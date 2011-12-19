package simulation.model;


import java.util.HashMap;



/**
 * SimWorld is a container class with airports and aircrafts whith unique name 
 * The class allows to access the simulation objects by name 
 * @author ps
 *
 */
public class SimWorld {
	private HashMap<String,Airport> airports = new HashMap<String,Airport>();
	private HashMap<String,Aircraft> aircrafts = new HashMap<String,Aircraft>();
	static private SimWorld instance = new SimWorld(); 
	public SimWorld(){}
	
	public void addAirport(Airport ap){
		if (airports.containsKey(ap.getName())) throw new RuntimeException("Duplicate airport name");
		airports.put(ap.getName(), ap);
	}
	public void addAircraft(Aircraft ac){
		if (aircrafts.containsKey(ac.getName())) throw new RuntimeException("Duplicate aircraft name");
		aircrafts.put(ac.getName(),ac);
	}
	public Airport getAirport(String name){
		return airports.get(name);
	}
	
	public Aircraft getAircraft(String name){
		return aircrafts.get(name);
	}
	
	public static SimWorld getInstance(){
		return instance;
	}

	@Override
	public String toString() {
		return "SimWorld [airports=" + airports + ", aircrafts=" + aircrafts
				+ "]";
	}

	public HashMap<String,Airport> getAirports() {
		return this.airports;
	}

	public HashMap<String, Aircraft> getAircrafts() {
		return this.aircrafts;
	}
	
}
