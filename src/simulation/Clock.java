package simulation;

public class Clock {
	
	private long currentSimulationTime = 0;

	public static final long REPAINT_GAP = 50;
	
	private static final int SCALE_FACTOR = 1000;

	public long currentSimulationTime() {
		return currentSimulationTime;
	}

	public void sleepUntil(long targetSimulationTime) {
		final long simulationTimeDelta = targetSimulationTime - currentSimulationTime;
		try {
			final long sleepTime = simulationTimeDelta * 1000 / SCALE_FACTOR;
			System.out.println("Sleep for " + sleepTime);
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
}
