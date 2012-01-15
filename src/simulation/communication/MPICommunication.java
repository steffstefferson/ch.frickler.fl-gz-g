package simulation.communication;

import p2pmpi.mpi.MPI;
import p2pmpi.mpi.Request;
import simulation.model.Aircraft;
import simulation.model.Event;
import simulation.model.SimWorld;

public class MPICommunication implements Communication {

	Request[] requests = new Request[4];

	@Override
	public void send(Event event, Aircraft aircraft) {
		System.out.println("sending Event " + event.toString());
		int n = MPI.COMM_WORLD.SizeTotal();
		final int dest = aircraft.getDestination().getAirportId() % n;
		System.out.println("n = " + n + ",dest = " + dest);
		// MPI.COMM_WORLD.Ssend(new Message[] { new Message(event, aircraft) },
		// 0, 1, MPI.OBJECT,
		// dest, 1);

		// if (MPI.COMM_WORLD.Rank() == 0)
		MPI.COMM_WORLD.Send(new int[] { event.getType() }, 0, 1, MPI.INT, dest, 1);
	}

	@Override
	public Message receive() {
		// Message[] messages = new Message[1];
		int[] messages = new int[1];
		int n = MPI.COMM_WORLD.SizeTotal();
		Request request = null;
		for (int i = 0; i < n; i++) {
			if (i == MPI.COMM_WORLD.Rank())
				continue;
			if (requests[i] == null)
				requests[i] = MPI.COMM_WORLD.Irecv(messages, 0, 1, MPI.INT, i, 1);

			// if (MPI.COMM_WORLD.Rank() != 0) {
			// request = MPI.COMM_WORLD.Irecv(messages, 0, 1, MPI.INT, 0, 1);
			// System.out.println("Received Request " + request.toString() +
			// "from " + 0);
			if (requests[i].Test() != null) {
				System.out.println("Receiving...");
				requests[i].Wait();
//				MPI.COMM_WORLD.Recv(messages, 0, 1, MPI.INT, i, 1);
				requests[i] = null;
			}
			// request.Wait();
			if (messages[0] != 0)
				System.out.println("Message is " + messages[0]);
			// System.out.println("Received Event "+ messages[0] + " from " +i);
			// break;

		}
		return null;
		// return messages[0];
	}

}
