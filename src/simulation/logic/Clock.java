package simulation.logic;

public class Clock {

	private long currentSimulationTime = 0;

	public static final long REPAINT_GAP = 80;

	private static final int SCALE_FACTOR = 300;

	public long currentSimulationTime() {
		return currentSimulationTime;
	}

	public void sleepUntil(long targetSimulationTime) {
		final long simulationTimeDelta = targetSimulationTime - currentSimulationTime;
		try {
			final long sleepTime = simulationTimeDelta * 1000 / SCALE_FACTOR;
			Thread.sleep(sleepTime);
			currentSimulationTime = targetSimulationTime;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isInPast(long timeEvent) {
		return timeEvent < currentSimulationTime;
	}

	public void rollbackTo(long timeStamp) {
		currentSimulationTime = timeStamp;
	}
}
