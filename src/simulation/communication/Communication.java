package simulation.communication;

import simulation.model.Aircraft;
import simulation.model.Event;

public interface Communication {
	void send(Event event, Aircraft aircraft);
	Message receive();
	long calculateGVT(long localMinimum);
	void startGVT(Event event);
	long broadcastGVT(long localMinimum);
}
