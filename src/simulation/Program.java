package simulation;

import simulation.communication.SimpleCommunication;
import simulation.model.SimWorld;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Simulator sim = new Simulator(SimWorld.getInstance());
		sim.setCommunication(new SimpleCommunication());
		sim.initGui();
		sim.initWorld(1000);
		sim.runSimulation();
	}

}
