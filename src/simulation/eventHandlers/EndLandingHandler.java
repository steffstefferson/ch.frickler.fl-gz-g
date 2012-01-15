package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;
import simulation.model.Flight;

public class EndLandingHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {
		final Aircraft ac = e.getAirCraft();
		final Airport ap = e.getAirPort();
		ac.setState(Aircraft.ON_GROUND);
		ac.setLastX(ap.getX1());
		ac.setLastY(ap.getY1());
		ac.setLastTime(e.getTimeStamp());
		ap.setRunWayFree(true);
		// do we have another flight?
		if (ac.getFlightPlan().size() > 0) {
			Flight f = ac.getFlightPlan().removeNextFlight();
			ac.setDestination(f.getDestination());
			Event e2 = new Event(Event.READY_FOR_DEPARTURE, e.getTimeStamp() + f.getTimeGap(), ap, ac);
			scheduler.scheduleEvent(e2);
		}
		Event e2 = new Event(Event.PROCESS_QUEUES, e.getTimeStamp(), ap, null);
		scheduler.scheduleEvent(e2);
	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		// TODO Auto-generated method stub

	}

}
