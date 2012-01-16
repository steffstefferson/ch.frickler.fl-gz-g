package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;
import simulation.model.RollBackVariables;

public class ProcessQueuesHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {
		final Airport ap = e.getAirPort();
		if (ap.isRunWayFree()) {
			// priority for landing
			if (ap.getWaitingForLandingQueue().size() > 0) {
				final Aircraft ac = ap.removeNextFromHoldingQueue();
				// when is the exact time (ac is on holding loop)
				// period of the loop:
				double period = ap.getRunwayLength() * 2 * Math.PI / Aircraft.MAX_SPEED;
				double m = (e.getTimeStamp() - ac.getLastTime()) / period;
				int n = (int) Math.ceil(m);
				long time = (long) (ac.getLastTime() + n * period);
				Event eNew = new Event(Event.START_LANDING, time, ap, ac);
				scheduler.scheduleEvent(eNew);
				ap.setRunWayFree(false);
				// tell this event, that we created a start_landing event
				e.setRollBackVariable(new RollBackVariables<Object[]>(new Object[] { true, time }));
				e.setAirCraft(ac);

			} else if (ap.getWaitingForTakeOffQueue().size() > 0) {
				final Aircraft ac = ap.removeFirstFromStartQueue();
				Event eNew = new Event(Event.START_TAKE_OFF, e.getTimeStamp(), ap, ac);
				scheduler.scheduleEvent(eNew);
				ap.setRunWayFree(false);
				e.setRollBackVariable(new RollBackVariables<Object[]>(new Object[] { false, e.getTimeStamp() }));
				e.setAirCraft(ac);
			}
		}
	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		RollBackVariables<Boolean> vars = e.getRollBackVariable();

		// if no vars were stored, no event has been created
		if (vars == null)
			return;

		final Airport ap = e.getAirPort();

		if (vars.getValue()[0]) { // rollback the landing

		} else { // rollback the takeoff
			final Aircraft ac = e.getAirCraft();
			Event eNew = new Event(Event.START_TAKE_OFF, e.getTimeStamp(), ap, ac);
			scheduler.scheduleEvent(eNew);
			ap.setRunWayFree(false);
			e.setRollBackVariable(new RollBackVariables<Boolean>(false));
			e.setAirCraft(ac);
		}
	}

}
