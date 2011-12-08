package simulation;

public interface EventScheduler {
	public void scheduleEvent(Event e);
	public void processNextEvent();
	//public long getCurrentSimulationTime();
}
