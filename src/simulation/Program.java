package simulation;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Simulator sim = new Simulator(SimWorld.getInstance());
		sim.initGui();
		sim.initWorld(100, 100);
		sim.runSimulation();
	}

}
