package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Event;

public class RepaintAnimationHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {

	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {

	}

}
