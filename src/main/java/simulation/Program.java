package simulation;

import simulation.model.SimWorld;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Simulator sim = new Simulator(SimWorld.getInstance());
		sim.initGui();
		sim.initWorld(1000);
		sim.runSimulation();
	}

}
