package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;

public class EnterAirspaceHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {
		final Aircraft ac = e.getAirCraft();
		scheduler.scheduleEvent(new Event(Event.ADD_TO_ANIMATION, e.getTimeStamp(), null, ac));
		Airport origin = ac.getOrigin();
		Airport dest = ac.getDestination();
		long duration = (long) (origin.getDistanceTo(dest) / Aircraft.MAX_SPEED);
		scheduler.scheduleEvent(new Event(Event.ARRIVAL, e.getTimeStamp() + duration / 2, dest, ac));
	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		final Aircraft ac = e.getAirCraft();
		final Event addToAnimation = new Event(Event.ADD_TO_ANIMATION, e.getTimeStamp(), null, ac);
		addToAnimation.setAntiMessage(true);
		scheduler.scheduleEvent(addToAnimation);
		Airport origin = ac.getOrigin();
		Airport dest = ac.getDestination();
		long duration = (long) (origin.getDistanceTo(dest) / Aircraft.MAX_SPEED);
		final Event arrival = new Event(Event.ARRIVAL, e.getTimeStamp() + duration / 2, dest, ac);
		arrival.setAntiMessage(true);
		scheduler.scheduleEvent(arrival);
	}

}
