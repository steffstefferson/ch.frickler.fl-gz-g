package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;
import simulation.model.RollBackVariables;

public class StartLandingHandler implements TransactionalEventHandler {

	private static final String ROLLBACK_VAR_KEY = "LAST_TIME";
	
	@Override
	public void process(Event e, EventScheduler scheduler) {
		final Aircraft ac = e.getAirCraft();
		final Airport ap = e.getAirPort();
		ac.setState(Aircraft.LANDING);
		ac.setLastX(ap.getX2());
		ac.setLastY(ap.getY2());
		e.setRollBackVariable(new RollBackVariables(StartLandingHandler.ROLLBACK_VAR_KEY, ac.getLastTime()));
		ac.setLastTime(e.getTimeStamp());
		// we assume that the aircraft is landing with a constant negative
		// acceleration
		long landingDuration = (long) (2 * ap.getRunwayLength() / Aircraft.MAX_SPEED);
		
		Event eNew = new Event(Event.END_LANDING, e.getTimeStamp() + landingDuration, ap, ac);
		scheduler.scheduleEvent(eNew);
		Event e3 = new Event(Event.REMOVE_FROM_ANIMATION, e.getTimeStamp(), null, ac);
		scheduler.scheduleEvent(e3);
	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		final Aircraft ac = e.getAirCraft();
		final Airport ap = e.getAirPort();
		ac.setState(Aircraft.ON_HOLDING_LOOP);
		ac.setLastX(ap.getX2());
		ac.setLastY(ap.getY2());
		ac.setLastTime(e.getRollBackVariable().getLongValue(StartLandingHandler.ROLLBACK_VAR_KEY));

		
		long landingDuration = (long) (2 * ap.getRunwayLength() / Aircraft.MAX_SPEED);
		Event eNew = new Event(Event.END_LANDING, e.getTimeStamp() + landingDuration, ap, ac);
		eNew.setAntiMessage(true);
		scheduler.scheduleEvent(eNew);
		Event e3 = new Event(Event.REMOVE_FROM_ANIMATION, e.getTimeStamp(), null, ac);
		e3.setAntiMessage(true);
		scheduler.scheduleEvent(e3);

	}

}
