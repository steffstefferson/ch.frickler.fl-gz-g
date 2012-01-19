package simulation.communication;

import java.util.Arrays;

import p2pmpi.mpi.MPI;
import p2pmpi.mpi.Request;
import simulation.model.Aircraft;
import simulation.model.Event;

public class MPICommunication implements Communication {

	Request request;
	Message[] messages = new Message[1];
	private static final int flagEvent = 1;
	private static final int flagLocalMinimum = 2;
	private static final int flagGVT = 3;

	@Override
	public void send(Event event, Aircraft aircraft) {
		int id = MPI.COMM_WORLD.Rank();
		Message[] m = new Message[] { new Message(event, aircraft) };
		System.out.println("[" + id + "]: sending Message " + m[0]);
		int n = MPI.COMM_WORLD.SizeTotal();
		final int dest = aircraft.getDestination().getAirportId() % n;
		System.out.println("n = " + n + ",dest = " + dest);
		MPI.COMM_WORLD.Send(m, 0, 1, MPI.OBJECT, dest, flagEvent);
	}

	@Override
	public Message receive() {
		int id = MPI.COMM_WORLD.Rank();
		if (request == null)
			request = MPI.COMM_WORLD.Irecv(messages, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, flagEvent);

		if (request.Test() != null) {
			System.out.println("[" + id + "]: Received message is " + messages[0]);
			Message retMessage = messages[0];
			request = null;
			messages[0] = null;
			return retMessage;
		}
		return null;
	}

	@Override
	public long calculateGVT(long localMinimum) {
		int id = MPI.COMM_WORLD.Rank();
		System.out.println("[" + id + "]: Sending local minimum:" + localMinimum);
		MPI.COMM_WORLD.Send(new long[] { localMinimum }, 0, 1, MPI.LONG, 0, flagLocalMinimum);
		System.out.println("[" + id + "]: Sent local minimum:" + localMinimum);
		long[] gvt = new long[1];
		MPI.COMM_WORLD.Recv(gvt, 0, 1, MPI.LONG, 0, flagGVT);
		System.out.println("[" + id + "]: Received new GVT:" + gvt[0]);
		return gvt[0];
	}

	@Override
	public void startGVT(Event event) {
		int numberOfProcessors = MPI.COMM_WORLD.Size();
		Message[] m = new Message[] { new Message(event, null) };
		for (int i = 1; i < numberOfProcessors; i++) {
			System.out.println("sending Start GVT message to " + i);
			MPI.COMM_WORLD.Send(m, 0, 1, MPI.OBJECT, i, flagEvent);
		}

	}

	@Override
	public long broadcastGVT(long localMinimum) {
		int numberOfProcessors = MPI.COMM_WORLD.Size();
		long[] localMinima = new long[numberOfProcessors];
		localMinima[0] = localMinimum;
		for (int i = 1; i < numberOfProcessors; i++) {
			System.out.println("trying to receive local minimum from " + i);
			MPI.COMM_WORLD.Recv(localMinima, i, 1, MPI.LONG, i, flagLocalMinimum);
			System.out.println("Received local minimum " + localMinima[i] + " from " + i);
		}
		Arrays.sort(localMinima);
		System.out.println("New global minimum is: " + localMinima[0]);
		for (int i = 1; i < numberOfProcessors; i++) {
			System.out.println("sending new GVT to " + i);
			MPI.COMM_WORLD.Send(localMinima, 0, 1, MPI.LONG, i, flagGVT);
		}
		return localMinima[0];
	}
}
