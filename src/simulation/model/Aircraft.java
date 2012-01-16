package simulation.model;


public class Aircraft {
	// states
	public static final int ON_GROUND = 0;
	public static final int WAITING_FOR_TAKE_OFF = 1;
	public static final int TAKING_OFF = 2;
	public static final int ON_FLIGHT = 3;
	public static final int ARRIVING = 4;
	public static final int ON_HOLDING_LOOP = 5;
	public static final int LANDING = 6;
	public static final String[] stateStrings = { "ON_GROUND", "WAITING_FOR_TAKE_OFF", "TAKING_OFF", "ON_FLIGHT",
			"ARRIVING", "ON_HOLDING_LOOP", "LANDING" };

	private String name;
	private Airport currentAirPort;
	private double lastX;
	private double lastY;
	private long lastTime;
	private int state;
	private Airport origin;
	private Airport destination;
	private FlightPlan flightPlan = new FlightPlan();
	private long remainingFuel;
	public static long MAX_SPEED = 100;
	public static long MAX_ACCEL = 5;

	public Aircraft(String name, Airport ap) {
		this.name = name;
		origin = ap;
		currentAirPort = ap;
		ap.subscribeAircraft(this);
		lastX = ap.getX1();
		lastY = ap.getY1();
		state = ON_GROUND;
	}

	public Airport getCurrentAirPort() {
		return currentAirPort;
	}

	public void setCurrentAirPort(Airport currentAirPort) {
		this.currentAirPort = currentAirPort;
	}

	public double getLastX() {
		return lastX;
	}

	public void setLastX(double lastX) {
		this.lastX = lastX;
	}

	public double getLastY() {
		return lastY;
	}

	public void setLastY(double lastY) {
		this.lastY = lastY;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Airport getOrigin() {
		return origin;
	}

	public void setOrigin(Airport origin) {
		this.origin = origin;
	}

	public Airport getDestination() {
		return destination;
	}

	public void setDestination(Airport destination) {
		this.destination = destination;
	}

	public long getRemainingFuel() {
		return remainingFuel;
	}

	public void setRemainingFuel(long remainingFuel) {
		this.remainingFuel = remainingFuel;
	}

	public void setFlightPlan(FlightPlan flightPlan) {
		this.flightPlan = flightPlan;
	}

	public FlightPlan getFlightPlan() {
		return flightPlan;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		String dest = null;
		if (destination != null)
			dest = destination.getName();
		else
			dest = null;
		return "Aircraft name=" + name + ", state=" + stateStrings[state] + "\n  lastX=" + lastX + ", lastY=" + lastY
				+ ", lastTime=" + lastTime + "\n  currentAirPort=" + currentAirPort.getName() + "\n  origin="
				+ origin.getName() + "\n  destination=" + dest + "\n  maxSpeed=" + MAX_SPEED + "\n  maxAcceleration="
				+ MAX_ACCEL;
	}

	public void calcPosition(long currentSimulationTime) {
		if (getState() == ON_HOLDING_LOOP) {
			calcHoldingPosition(currentSimulationTime);
		} else {
			calcFlightPosition(currentSimulationTime);
		}

	}

	private void calcHoldingPosition(long currentSimulationTime) {
		double beginRunwayX = destination.getX1();
		double beginRunwayY = destination.getY1();

		double endRunwayX = destination.getX2();
		double endRunwayY = destination.getY2();

		double runwayX = endRunwayX - beginRunwayX;
		double runwayY = endRunwayY - beginRunwayY;

		double runwayXorthogonal = -runwayY;
		double runwayYorthogonal = runwayX;

		double timeDelta = currentSimulationTime - getLastTime();

		// skip calculation if aircraft has moved on in the meantime
		if (timeDelta < 0)
			return;

		double angularVelocity = 1; // (2 * Math.PI) / 1;

		double newX = beginRunwayX + Math.cos(angularVelocity * timeDelta) * runwayX
				+ Math.sin(angularVelocity * timeDelta) * runwayXorthogonal;

		double newY = beginRunwayY + Math.cos(angularVelocity * timeDelta) * runwayY
				+ Math.sin(angularVelocity * timeDelta) * runwayYorthogonal;

		this.setLastX(newX);
		this.setLastY(newY);

	}

	private void calcFlightPosition(long currentSimulationTime) {
		double targetX = destination.getX1();
		double targetY = destination.getY1();

		double orignX = origin.getX1();
		double orignY = origin.getY1();

		double distanceX = targetX - orignX;
		double distanceY = targetY - orignY;

		double betrag = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

		double nX = distanceX / betrag;
		double nY = distanceY / betrag;

		double timeDelta = currentSimulationTime - getLastTime();

		// skip calculation if aircraft has moved on in the meantime
		if (timeDelta < 0)
			return;

		double newX = orignX + timeDelta * MAX_SPEED * nX;
		double newY = orignY + timeDelta * MAX_SPEED * nY;

		this.setLastX(newX);
		this.setLastY(newY);
	}
}
