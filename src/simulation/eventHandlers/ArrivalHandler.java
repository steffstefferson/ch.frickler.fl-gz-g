package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;

public class ArrivalHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {
		final Aircraft ac = e.getAirCraft();
		final Airport ap = e.getAirPort();
		ap.subscribeAircraft(ac);
		ac.setState(Aircraft.ON_HOLDING_LOOP);
		ac.setCurrentAirPort(ap);
		ac.setLastX(ap.getX2());
		ac.setLastY(ap.getY2());
		ac.setLastTime(e.getTimeStamp());
		ap.addToHoldingQueue(ac);
		Event eNew = new Event(Event.PROCESS_QUEUES, e.getTimeStamp(), ap, ac);
		scheduler.scheduleEvent(eNew);
	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		// TODO Auto-generated method stub

	}

}
