package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;

public class StartLandingHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {
		final Aircraft ac = e.getAirCraft();
		final Airport ap = e.getAirPort();
		ac.setState(Aircraft.LANDING);
		ac.setLastX(ap.getX2());
		ac.setLastY(ap.getY2());
		ac.setLastTime(e.getTimeStamp());
		// we assume that the aircraft is landing with a constant negative
		// acceleration
		long landingDuration = (long) (2 * ap.getRunwayLength() / Aircraft.MAX_SPEED);
		Event eNew = new Event(Event.END_LANDING, ac, e.getTimeStamp() + landingDuration, ap, ac);
		scheduler.scheduleEvent(eNew);
		Event e3 = new Event(Event.REMOVE_FROM_ANIMATION, null, e.getTimeStamp(), null, ac);
		scheduler.scheduleEvent(e3);
	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		// TODO Auto-generated method stub

	}

}
