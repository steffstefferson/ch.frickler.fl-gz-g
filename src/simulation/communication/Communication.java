package simulation.communication;

import simulation.model.Aircraft;
import simulation.model.Event;

/**
 * events that don't belong to the current lp have to be sent to the right one.
 * classes implementing this interface handle the send and receive process.
 */
public interface Communication {

	/**
	 * sends an event to another logical process
	 * 
	 * @param event
	 * @param aircraft
	 */
	void send(Event event, Aircraft aircraft);

	/**
	 * 
	 * @return a received message or null if none was available
	 */
	Message receive();
}
