package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Event;

public class LeaveAirspaceHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {
		final Event removeFromAnimation = new Event(Event.REMOVE_FROM_ANIMATION, e.getTimeStamp(), null,
				e.getAirCraft());
		scheduler.scheduleEvent(removeFromAnimation);
		final Event enterAirspace = new Event(Event.ENTER_AIRSPACE, e.getTimeStamp(), null, e.getAirCraft());
		scheduler.scheduleEvent(enterAirspace);

	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		final Event removeFromAnimation = new Event(Event.REMOVE_FROM_ANIMATION, e.getTimeStamp(), null,
				e.getAirCraft());
		removeFromAnimation.setAntiMessage(true);
		scheduler.scheduleEvent(removeFromAnimation);
		final Event enterAirspace = new Event(Event.ENTER_AIRSPACE, e.getTimeStamp(), null, e.getAirCraft());
		enterAirspace.setAntiMessage(true);
		scheduler.scheduleEvent(enterAirspace);
	}

}
