package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;

public class ReadyForDepartureHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {
		final Aircraft airCraft = e.getAirCraft();
		final Airport airPort = e.getAirPort();
		if (airCraft.getDestination() == airPort)
			throw new RuntimeException("destination = origin");
		airCraft.setState(Aircraft.WAITING_FOR_TAKE_OFF);
		airPort.addToStartQueue(airCraft);
		final Event eNew = new Event(Event.PROCESS_QUEUES, e.getTimeStamp(),
				airPort, airCraft);
		scheduler.scheduleEvent(eNew);
	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		final Aircraft airCraft = e.getAirCraft();
		final Airport airPort = e.getAirPort();
		airCraft.setState(Aircraft.ON_GROUND);
		airPort.removeFromStartQueue(airCraft);
		final Event eNew = new Event(Event.PROCESS_QUEUES, e.getTimeStamp(),
				airPort, airCraft);
		eNew.setAntiMessage(true);
		scheduler.scheduleEvent(eNew);
	}

}
