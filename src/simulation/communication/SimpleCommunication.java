package simulation.communication;

import simulation.model.Aircraft;
import simulation.model.Event;

/**
 * Simple Communication implementation for single-process program.
 * 
 */
public class SimpleCommunication implements Communication {

	private Message message = null;

	@Override
	public void send(Event event, Aircraft aircraft) {
		if (message != null)
			throw new IllegalStateException("Call receive before sending another message!");
		message = new Message(event, aircraft);
	}

	@Override
	public Message receive() {
		Message retMessage = message;
		message = null;
		return retMessage;
	}

	@Override
	public long calculateGVT(long localMinimum) {
		return localMinimum;
	}

	@Override
	public long broadcastGVT(long localMinimum) {
		return localMinimum;
	}

	@Override
	public void startGVT(Event event) {
	}
}
