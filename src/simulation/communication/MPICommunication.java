package simulation.communication;

import p2pmpi.mpi.MPI;
import p2pmpi.mpi.Request;
import simulation.model.Aircraft;
import simulation.model.Event;

public class MPICommunication implements Communication {

	Request[] requests = new Request[MPI.COMM_WORLD.SizeTotal()];
	Message[] messages = new Message[MPI.COMM_WORLD.SizeTotal()];

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
		for (int i = 0; i < MPI.COMM_WORLD.SizeTotal(); i++) {
			if (i == MPI.COMM_WORLD.Rank())
				continue;
			if (requests[i] == null)
				requests[i] = MPI.COMM_WORLD.Irecv(messages, i, 1, MPI.OBJECT, i, 1);

			if (requests[i].Test() != null) {
				System.out.println("Receiving...");
				requests[i].Wait();
				requests[i] = null;
			}
			if (messages[i] != null) {
				System.out.println("Received message is " + messages[i]);
				Message retMessage = messages[i];
				messages[i] = null;
				return retMessage;
			}
		}
		return null;
	}

}
