package simulation.definition;

import java.util.List;

import simulation.model.Event;

public interface EventScheduler {
	public void scheduleEvent(Event e);

	public void processNextEvent();

	public List<Event> getEventList();
	// public long getCurrentSimulationTime();
}
