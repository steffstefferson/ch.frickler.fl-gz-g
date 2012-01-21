package simulation.logic;

/**
 * This class is responsible for managing the simulation time.
 */
public class Clock {

	private long currentSimulationTime = 0;

	/**
	 * Scale factor for the simulation time; larger values mean the simulation
	 * runs faster.
	 */
	private static final int SCALE_FACTOR = 500;

	/**
	 * Gap between to repaints event.
	 */
	public static final long REPAINT_GAP = 30;

	/**
	 * Gap between GVT synch events.
	 */
	public static final long GVT_TIME_GAP = 1000;

	/**
	 * 
	 * @return The current simulation time
	 */

	public long currentSimulationTime() {
		return currentSimulationTime;
	}

	/**
	 * Sleeps until the targetSimulationTime
	 * 
	 * @param targetSimulationTime
	 */
	public void sleepUntil(long targetSimulationTime) {
		final long simulationTimeDelta = targetSimulationTime - currentSimulationTime;
		try {
			final long sleepTime = simulationTimeDelta * 1000 / SCALE_FACTOR;
			Thread.sleep(sleepTime);
			currentSimulationTime = targetSimulationTime;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param timeEvent
	 * @return true if the parameter is smaller than the current simulation time
	 */
	public boolean isInPast(long timeEvent) {
		return timeEvent < currentSimulationTime;
	}

	/**
	 * Resets the current simulation time
	 * 
	 * @param timeStamp
	 *            Sets the currentSimulationTime to the given timestamp
	 */
	public void rollbackTo(long timeStamp) {
		currentSimulationTime = timeStamp;
	}
}
