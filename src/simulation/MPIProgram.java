package simulation;
import javax.swing.JOptionPane;

import p2pmpi.mpi.MPI;
import simulation.model.SimWorld;


public class MPIProgram {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MPI.Init(args);
		
		int idofProcessor = MPI.COMM_WORLD.Rank();
				System.out.println("rank (idofProcessor): "+idofProcessor);
		System.out.println("Size "+MPI.COMM_WORLD.Size());
		System.out.println("SizeTotal "+MPI.COMM_WORLD.SizeTotal());
		System.out.println("Get_processor_name "+MPI.Get_processor_name());
		
		JOptionPane.showInputDialog("sdfasdfd");
		
		StartSimulation((idofProcessor == 0),idofProcessor);

		
		MPI.Finalize();
		
	}

	private static void StartSimulation(boolean bMasterProcess, int idofProcessor) {
		Simulator sim = new Simulator(SimWorld.getInstance(),bMasterProcess,idofProcessor);
		sim.initGui();
		sim.initWorld(100);
		sim.runSimulation();		
	}

}
