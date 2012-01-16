package simulation.model;

public class Event {
	// Event - Types
	public static final int READY_FOR_DEPARTURE = 0;
	public static final int START_TAKE_OFF = 1;
	public static final int END_TAKE_OFF = 2;
	public static final int ARRIVAL = 3;
	public static final int START_LANDING = 4;
	public static final int END_LANDING = 5;
	// never used
	// public static final int ENTER_START_QUEUE = 6;
	// public static final int ENTER_LANDING_QUEUE = 7;
	public static final int PROCESS_QUEUES = 8;
	public static final int ADD_TO_ANIMATION = 9;
	public static final int REMOVE_FROM_ANIMATION = 10;
	public static final int REPAINT_ANIMATION = 11;
	public static final int LEAVE_AIRSPACE = 12;
	public static final int ENTER_AIRSPACE = 13;

	public String[] typeStrings = { "READY_FOR_DEPARTURE", "START_TAKE_OFF", "END_TAKE_OFF", "ARRIVAL",
			"START_LANDING", "END_LANDING", "ENTER_START_QUEUE", "ENTER_LANDING_QUEUE", "PROCESS_QUEUES",
			"ADD_TO_ANIMATION", "REMOVE_FROM_ANIMATION", "REPAINT_ANIMATION", "LEAVE_AIRSPACE", "ENTER_AIRSPACE" };

	private long timeStamp;
	private int type;
	private Airport airPort;
	private Aircraft airCraft;
	private boolean isAntiMessage;

	private RollBackVariables rollBackVariable;
	
	
	
	public Airport getAirPort() {
		return airPort;
	}

	public void setAirPort(Airport airPort) {
		this.airPort = airPort;
	}

	public Aircraft getAirCraft() {
		return airCraft;
	}

	public void setAirCraft(Aircraft airCraft) {
		this.airCraft = airCraft;
	}

	public Event(int type, long time, Airport ap, Aircraft ac) {
		this.type = type;
		timeStamp = time;
		airPort = ap;
		airCraft = ac;
	}

	public int getType() {
		return type;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean isAntiMessage() {
		return isAntiMessage;
	}

	public void setAntiMessage(boolean isAntiMessage) {
		this.isAntiMessage = isAntiMessage;
	}

	public void testType() {
		if (type == READY_FOR_DEPARTURE)
			return;
		else if (type == START_TAKE_OFF)
			return;
		else if (type == END_TAKE_OFF)
			return;
		else if (type == ARRIVAL)
			return;
		else if (type == START_LANDING)
			return;
		else if (type == END_LANDING)
			return;
		// else if (type == ENTER_START_QUEUE)
		// return;
		// else if (type == ENTER_LANDING_QUEUE)
		// return;
		else if (type == PROCESS_QUEUES)
			return;
		else
			throw new RuntimeException("invalid event type: " + type);
	}

	@Override
	public String toString() {

		String s = "Event " + "T=" + timeStamp + " " + typeStrings[type];
		s = s + ", ap: ";
		if (airPort != null)
			s = s + airPort.getName();
		else
			s = s + "null";
		s = s + ", ac: ";
		if (airCraft != null)
			s = s + airCraft.getName();
		else
			s = s + "null";

		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((airCraft == null) ? 0 : airCraft.hashCode());
		result = prime * result + ((airPort == null) ? 0 : airPort.hashCode());
		result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
		result = prime * result + type;
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
		Event other = (Event) obj;
		if (airCraft == null) {
			if (other.airCraft != null)
				return false;
		} else if (!airCraft.equals(other.airCraft))
			return false;
		if (airPort == null) {
			if (other.airPort != null)
				return false;
		} else if (!airPort.equals(other.airPort))
			return false;
		if (timeStamp != other.timeStamp)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public RollBackVariables getRollBackVariable() {
		return rollBackVariable;
	}

	public void setRollBackVariable(RollBackVariables rollBackVariable) {
		this.rollBackVariable = rollBackVariable;
	}

}
