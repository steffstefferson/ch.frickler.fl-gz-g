package simulation.definition;

import simulation.model.Event;

public interface EventScheduler {
	public void scheduleEvent(Event e);

	public void processNextEvent();

	public int getNumberOfPendingEvents();
	// public long getCurrentSimulationTime();
}
