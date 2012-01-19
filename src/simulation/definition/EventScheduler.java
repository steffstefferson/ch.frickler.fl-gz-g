package simulation.definition;

import simulation.model.Event;

/**
 * defines an event scheduler
 */
public interface EventScheduler {

	/**
	 * Schedules the next event. If the event is of type ENTER_AIRSPACE-Event
	 * then the event gets sent to the affected airport.
	 * 
	 * @param e
	 *            Next event to schedule
	 * @throws
	 * @see EventScheduler#scheduleEvent(Event)
	 * 
	 * 
	 */
	public void scheduleEvent(Event e);

	/**
	 * Advances the time to the time of the oldest event in the event queue and
	 * processes the event
	 */
	public void processNextEvent();

	/**
	 * 
	 * @return the number of events that are not yet processed
	 */
	public int getNumberOfPendingEvents();

}
