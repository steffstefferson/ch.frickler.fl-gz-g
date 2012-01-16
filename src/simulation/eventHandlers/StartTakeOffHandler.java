package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;

public class StartTakeOffHandler implements TransactionalEventHandler {

	
	
	@Override
	public void process(Event e, EventScheduler scheduler) {
		final Aircraft ac = e.getAirCraft();
		final Airport ap = e.getAirPort();
		ac.setState(Aircraft.TAKING_OFF);
		ac.setLastTime(e.getTimeStamp());
		// we assume a constant acceleration maxAcceleration
		long takeOffDuration = Aircraft.MAX_SPEED / Aircraft.MAX_ACCEL;
		// distance for the accelerating part:
		double dist = Aircraft.MAX_ACCEL * takeOffDuration * takeOffDuration
				/ 2.0;
		// the remaining part
		double remainingDist = ap.getRunwayLength() - dist;
		// the remaining distance of the runway we have constant speed
		if (remainingDist > 0) {
			takeOffDuration += remainingDist / Aircraft.MAX_SPEED;
		} else {
			throw new RuntimeException("runway too short!!");
		}
		// schedule next event
		Event eNew = new Event(Event.END_TAKE_OFF, e.getTimeStamp()
				+ takeOffDuration, ap, ac); // to do!
		scheduler.scheduleEvent(eNew);
	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		Aircraft ac = e.getAirCraft();
		Airport ap = e.getAirPort();
		
		//rollback airplane state
		ac.setState(Event.READY_FOR_DEPARTURE);
		ac.setLastTime(e.getRollBackVariable().getLongValue());
		
		//remove event from event List
		Event endTakeOffEvent = new Event(Event.END_TAKE_OFF, e.getTimeStamp(), ap, ac);
		endTakeOffEvent.setAntiMessage(true);
		scheduler.scheduleEvent(endTakeOffEvent);
	}

}
