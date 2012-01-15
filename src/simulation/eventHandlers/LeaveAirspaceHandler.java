package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Event;

public class LeaveAirspaceHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {
		scheduler.scheduleEvent(new Event(Event.REMOVE_FROM_ANIMATION, e.getTimeStamp(), null, e.getAirCraft()));
		scheduler.scheduleEvent(new Event(Event.ENTER_AIRSPACE, e.getTimeStamp(), null, e.getAirCraft()));

	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		// TODO Auto-generated method stub

	}

}
