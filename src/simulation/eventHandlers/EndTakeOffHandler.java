package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;

public class EndTakeOffHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {
		final Aircraft ac = e.getAirCraft();
		final Airport ap = e.getAirPort();
		ac.setState(Aircraft.ON_FLIGHT);
		ac.setLastX(ap.getX2());
		ac.setLastY(ap.getY2());
		ac.setLastTime(e.getTimeStamp());
		ap.setRunWayFree(true);
		ap.unscribeAircraft(ac);
		long duration = (long) (ap.getDistanceTo(ac.getDestination()) / Aircraft.MAX_SPEED);

		// TODO does this still work without the hack?
		// Event e1 = new Event(Event.LEAVE_AIRSPACE, (EventHandler) sched //
		// HACK!
		// , e.getTimeStamp() + duration / 2, ac.getOrigin(), ac);
		Event e1 = new Event(Event.LEAVE_AIRSPACE, null, e.getTimeStamp() + duration / 2, ac.getOrigin(), ac);
		scheduler.scheduleEvent(e1);
		Event e2 = new Event(Event.PROCESS_QUEUES, ap, e.getTimeStamp(), ap, null);
		scheduler.scheduleEvent(e2);
		Event e3 = new Event(Event.ADD_TO_ANIMATION, null, e.getTimeStamp(), null, ac);
		scheduler.scheduleEvent(e3);
	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		// TODO Auto-generated method stub

	}

}
