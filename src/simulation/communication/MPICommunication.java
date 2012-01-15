package simulation.communication;

import p2pmpi.mpi.MPI;
import p2pmpi.mpi.Request;
import simulation.model.Aircraft;
import simulation.model.Event;

public class MPICommunication implements Communication {

	Request request;
	Message[] messages = new Message[1];

	@Override
	public void send(Event event, Aircraft aircraft) {
		Message[]  m= new Message[] { new Message(event, aircraft) };
		System.out.println("sending Message " +m[0]);
		int n = MPI.COMM_WORLD.SizeTotal();
		final int dest = aircraft.getDestination().getAirportId() % n;
		System.out.println("n = " + n + ",dest = " + dest);
		MPI.COMM_WORLD.Send(m, 0, 1, MPI.OBJECT, dest, 1);
	}

	@Override
	public Message receive() {
			if (request == null)
				request = MPI.COMM_WORLD.Irecv(messages, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, 1);

			if (request.Test() != null) {
				System.out.println("Receiving...");
				request = null;
			}
			if (messages[0] != null) {
				System.out.println("Received message is " + messages[0]);
				Message retMessage = messages[0];
				messages[0] = null;
				return retMessage;
			}
		return null;
	}

}
