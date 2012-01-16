package simulation.model;

import javax.management.RuntimeErrorException;

public class RollBackVariables {

	private long lastEventTimeStamp;

	public RollBackVariables(long timeStamp) {
		this.lastEventTimeStamp = timeStamp;
	}

	public long getLastEventTimeStamp() {
		if(lastEventTimeStamp == 0) throw new RuntimeException("lastEventTimeStamp not set");
		return lastEventTimeStamp;
	}

	public void setLastEventTimeStamp(long lastEventTimeStamp) {
		this.lastEventTimeStamp = lastEventTimeStamp;
	}
	
}
