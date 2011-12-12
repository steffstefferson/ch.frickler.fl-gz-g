package simulation.model;


public class Flight {
	private long timeGap;
	private Airport destination;

	public Flight(long timeGap, Airport destination ){
		this.timeGap = timeGap; 
		this.destination = destination;
	}
	
	/**
	 * @return the timeGap between this flight and the last flight (landing)
	 */
	public long getTimeGap() {
		return timeGap;
	}
	
	public void setTimeGap(long timeGap) {
		this.timeGap = timeGap;
	}
	
	public Airport getDestination() {
		return destination;
	}
	
	public void setDestination(Airport destination) {
		this.destination = destination;
	}

}
