package simulation.definition;

import simulation.model.Event;

public interface TransactionalEventHandler {
	public void process(Event e, EventScheduler scheduler);
	public void rollback(Event e, EventScheduler scheduler);
}
