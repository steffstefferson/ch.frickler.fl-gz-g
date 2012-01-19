package simulation.definition;

import simulation.model.Event;

/**
 * This interface provides a forward and a backward process handler. All Events
 * have to implement this interface to allow the time warp mechanism. The
 * rollback-method is used when a struggler message has arrived to rollback all
 * falsely processed events.
 * 
 * 
 */
public interface TransactionalEventHandler {

	/**
	 * Processes an event. Forward in time
	 * 
	 * @param e
	 * @param scheduler
	 */
	public void process(Event e, EventScheduler scheduler);

	/**
	 * Rollbacks an event after a struggler message has arrived.
	 * 
	 * @param e
	 *            Event to rollback
	 * @param scheduler
	 */
	public void rollback(Event e, EventScheduler scheduler);
}
