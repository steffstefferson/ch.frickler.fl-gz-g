package simulation.model;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author ps Simple airport with a single runway used for landing and starting
 */
public class Airport {

	private String name;

	// Start goes from (x1,y1) to (x2,y2)
	// landing goes from (x2,y2) to (x1,y1)
	private double x1;
	private double y1;
	private double x2;
	private double y2;
	private Dimension controlArea;
	private double runwayLength;
	private boolean runWayFree = true;
	HashSet<Aircraft> aircrafts = new HashSet<Aircraft>();
	LinkedList<Aircraft> waitingForTakeOffQueue = new LinkedList<Aircraft>();
	LinkedList<Aircraft> waitingForLandingQueue = new LinkedList<Aircraft>();

	private static int airportCount = 0;
	private int airportId = 0;

	public Airport(String name, double x1, double y1, double x2, double y2, int lRH, int lrW) {
		this.name = name;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.airportId = airportCount++;
		runwayLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		int luftRaumHeight = lRH;
		int luftRaumWidth = lrW;

		controlArea = new Dimension(luftRaumWidth, luftRaumHeight);
	}

	public Airport(String name, double x1, double y1, double x2, double y2) {
		this(name, x1, y1, x2, y2, 40000, 60000);

	}

	/**
	 * All aircrafts which are on ground or taking off or in the holding loop
	 * 
	 * @param the
	 *            aircraft arriving at this airport
	 */
	public void subscribeAircraft(Aircraft a) {
		aircrafts.add(a);
	}

	public int getAirportId() {
		return this.airportId;
	}

	/**
	 * Aircrafts leaving this airport (after take-off)
	 * 
	 * @param a
	 *            the aircraft after taking off
	 */
	public void unsubscribeAircraft(Aircraft a) {
		aircrafts.remove(a);
	}

	public void addToStartQueue(Aircraft ac) {
		waitingForTakeOffQueue.addLast(ac);
	}

	public void addFirstToStartQueue(Aircraft ac) {
		waitingForTakeOffQueue.addFirst(ac);
	}
	
	public void removeFromStartQueue(Aircraft ac) {
		waitingForTakeOffQueue.remove(ac);
	}

	public Aircraft removeFirstFromStartQueue() {
		if (waitingForTakeOffQueue.size() < 1)
			throw new RuntimeException("No aircrafts in waiting queue");
		return waitingForTakeOffQueue.removeFirst();
	}

	public void addToHoldingQueue(Aircraft ac) {
		waitingForLandingQueue.addLast(ac);
	}

	public void addFirstToHoldingQueue(Aircraft ac) {
		waitingForLandingQueue.addFirst(ac);
	}
	
	public Aircraft removeNextFromHoldingQueue() {
		if (waitingForLandingQueue.size() < 1)
			throw new RuntimeException("No aircrafts in waiting queue");
		return waitingForLandingQueue.removeFirst();
	}

	public void removeFromHoldingQueue(Aircraft ac) {
		waitingForLandingQueue.remove(ac);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getX1() {
		return x1;
	}

	public void setX1(double x1) {
		this.x1 = x1;
	}

	public double getY1() {
		return y1;
	}

	public void setY1(double y1) {
		this.y1 = y1;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getY2() {
		return y2;
	}

	public void setY2(double y2) {
		this.y2 = y2;
	}

	public boolean isRunWayFree() {
		return runWayFree;
	}

	public void setRunWayFree(boolean runWayFree) {
		this.runWayFree = runWayFree;
	}

	public HashSet<Aircraft> getAircrafts() {
		return aircrafts;
	}

	public void setAircrafts(HashSet<Aircraft> aircrafts) {
		this.aircrafts = aircrafts;
	}

	public double getRunwayLength() {
		return runwayLength;
	}

	public LinkedList<Aircraft> getWaitingForTakeOffQueue() {
		return waitingForTakeOffQueue;
	}

	public void setWaitingForTakeOffQueue(LinkedList<Aircraft> waitingForTakeOffQueue) {
		this.waitingForTakeOffQueue = waitingForTakeOffQueue;
	}

	public LinkedList<Aircraft> getWaitingForLandingQueue() {
		return waitingForLandingQueue;
	}

	public void setWaitingForLandingQueue(LinkedList<Aircraft> waitingForLandingQueue) {
		this.waitingForLandingQueue = waitingForLandingQueue;
	}

	@Override
	public String toString() {
		return "Airport name=" + name + ", x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + "\n  runWayFree="
				+ runWayFree + ", waitingForTakeOff=" + waitingForTakeOffQueue.size() + ", waitingForLanding="
				+ waitingForLandingQueue.size();
	}

	public double getDistanceTo(Airport destination) {
		return Math.sqrt((x1 - destination.getX2()) * (x1 - destination.getX2()) + (y1 - destination.getY2())
				* (y1 - destination.getY2()));
	}

	public Dimension getControlarea() {
		return controlArea;
	}

	public Color getColor() {
		Color[] colors = { Color.ORANGE, Color.BLUE, Color.CYAN, Color.GREEN, Color.PINK };
		return colors[getAirportId() % colors.length];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + airportId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Airport other = (Airport) obj;
		if (airportId != other.airportId)
			return false;
		return true;
	}
}
