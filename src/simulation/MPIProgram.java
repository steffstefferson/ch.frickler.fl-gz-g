package simulation;

import p2pmpi.mpi.MPI;
import simulation.communication.MPICommunication;
import simulation.model.SimWorld;

public class MPIProgram {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MPI.Init(args);

		int idofProcessor = MPI.COMM_WORLD.Rank();
		System.out.println("rank (idofProcessor): " + idofProcessor);
		System.out.println("Size " + MPI.COMM_WORLD.Size());
		int totalProcessors = MPI.COMM_WORLD.SizeTotal();
		System.out.println("SizeTotal " + totalProcessors);
		System.out.println("Get_processor_name " + MPI.Get_processor_name());

		StartSimulation((idofProcessor == 0), idofProcessor, totalProcessors);

		MPI.Finalize();
		System.exit(0);

	}

	private static void StartSimulation(boolean bMasterProcess, int idofProcessor, int totalProcessors) {
		Simulator sim = new Simulator(SimWorld.getInstance(), bMasterProcess, idofProcessor, totalProcessors);
		sim.setCommunication(new MPICommunication());
		sim.initGui();
		sim.initWorld(100);
		sim.runSimulation();
	}

}
