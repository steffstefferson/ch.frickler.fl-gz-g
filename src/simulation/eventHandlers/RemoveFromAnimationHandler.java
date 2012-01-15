package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.gui.Animation;
import simulation.model.Event;

public class RemoveFromAnimationHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {
		Animation.getInstance().removeFromQuery(e.getAirCraft());
	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		// TODO Auto-generated method stub

	}

}
