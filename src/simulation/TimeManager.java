package simulation;

import javax.xml.stream.events.StartDocument;


public class TimeManager {

	private long startTime =0;
	private long scalFactor = 5;
	

/*
	public long getWallClockTime(){	
		return System.currentTimeMillis();
	}
*/
	public long getScalFactor() {
		return scalFactor;
	}

	public void setScalFactor(long scalFactor) {
		this.scalFactor = scalFactor;
	}
	
	
	public long getSimulationTime(){
							 //scal passed time
		return   convertToSimulationTime(getWallClockTime());
	}

	public long getWallClockTime(){
		return System.currentTimeMillis()-getStartTime();
	}
	
		
	public boolean isTimeInPast(long wallClockTime) {
		return getWallClockTime() > wallClockTime;

	}

	public long getTimeDeltaForNextEvent(long wallClockTime){
		return convertToSimulationTime(wallClockTime)-getSimulationTime();
	}
	
	public long convertToSimulationTime(long wallClockTime){
		long value = scalFactor * wallClockTime;
		return value;
	}
	
	public long convertToWallClockTime(long simulationTime){
		long value =  simulationTime / scalFactor;
		return value;
	}

	long getStartTime() {
		return this.startTime == 0 ? System.currentTimeMillis() : startTime;
	}

	
	
	public long getSimTimeDelta(long wallClock) {
		
		long event2 = convertToSimulationTime((System.currentTimeMillis() -  (startTime+wallClock)));
		return event2;
	}

	public void initTime() {
		this.startTime = System.currentTimeMillis();

		
	}

	public boolean isStarted() {
		return (this.startTime  > 0);
	}


	public long getEventSchedulTime(long wallclockTime) {
		return convertToSimulationTime(wallclockTime);
	}
	
	
	
}
