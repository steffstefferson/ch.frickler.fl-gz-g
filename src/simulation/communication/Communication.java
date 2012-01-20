package simulation.communication;

import simulation.model.Aircraft;
import simulation.model.Event;

/**
 * events that don't belong to the current LP have to be sent to the right one.
 * classes implementing this interface handle the send and receive process.
 */
public interface Communication {

	/**
	 * Sends an event to another logical process
	 * 
	 * @param event
	 * @param aircraft
	 */
	void send(Event event, Aircraft aircraft);

	/**
	 * Try to receive a message from another LP. Non-blocking.
	 * 
	 * @return a received message or null if none was available
	 */
	Message receive();

	/**
	 * Send the local minimum to the controller process and wait for the
	 * controller to return the GVT
	 * 
	 * @param localMinimum
	 * @return the newly calculated GVT
	 */
	long calculateGVT(long localMinimum);

	/**
	 * Broadcast START_GVT event to all LPs. Must only be called by the master
	 * process (controller)
	 * 
	 * @param event
	 */
	void startGVT(Event event);

	/**
	 * Wait for all LPs to send their local minimum, then calculate the new GVT
	 * and broadcast it to all LPs. Must only be called by the master process
	 * (controller)
	 * 
	 * @param localMinimum
	 * @return the newly calculated GVT
	 */
	long broadcastGVT(long localMinimum);
}
