package simulation;

import java.util.Random;
import java.util.Vector;

/**
 * @author ps Manages simulation clock, a event queue and world consisting of
 *         aircrafts and airports
 */
public class Simulator implements EventScheduler {

	private SimWorld world;
	private long now; // simulation time

	private Gui gui;

	private Vector<Event> evList; // time ordered list

	public Simulator(SimWorld world) {
		this.world = world;
		evList = new Vector<Event>();
	}

	@Override
	public long getCurrentSimulationTime() {
		return now;
	}

	/*
	 * New events which lay in the past cause a causality error
	 * 
	 * @see EventScheduler#scheduleEvent(Event)
	 */
	public void scheduleEvent(Event e) {
		long tim = e.getTimeStamp();
		if (tim < now)
			throw new RuntimeException("Causality error: " + this);
		int pos = 0;
		while (pos < evList.size()) {
			Event n = evList.get(pos);
			if (n.getTimeStamp() > tim)
				break;
			pos++;
		}
		evList.add(pos, e);
	}

	/**
	 * Advances the time to the time of the oldest event in the event queue and
	 * processes the event
	 */
	public void processNextEvent() {

		// System.out.println("processing "+e);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		gui.repaint();

		Event e = evList.remove(0);
		now = e.getTimeStamp();
		gui.println(e.toString()); // log the event
		EventHandler eh = e.getEventHandler();
		eh.processEvent(e, this);
		// log the state of the object which is the target of this
		gui.println(e.getEventHandler().toString());

	}

	/**
	 * This is the main simulation loop
	 */
	public void runSimulation() {
		int evCnt = 0;
		while (evList.size() > 0) {
			processNextEvent();
			evCnt++;
		}
		System.out.println("Processed " + evCnt + " events.");
	}

	public void initWorld() {
		initWorld(100, 100);
	}

	public void initWorld(int intAmountOfFlights, int intAirCrafts) {

		// Random Generator:
		Random rand = new Random(1234);
		// create airports
		String[] airportNames = { "ZURICH", "GENF", "BASEL" };
		Airport ap = new Airport("ZURICH", 684000, 256000, 683000, 259000);
		world.addAirport(ap);
		ap = new Airport("GENF", 497000, 120000, 499000, 122000);
		world.addAirport(ap);
		ap = new Airport("BASEL", 599000, 287000, 601000, 288000);
		world.addAirport(ap);

		// create 100 aircrafts and choose an arbitrary airport
		for (int i = 0; i < intAirCrafts; i++) {
			// Random Airport:
			ap = world.getAirport(airportNames[rand
					.nextInt(airportNames.length)]);
			Aircraft ac = new Aircraft("X" + 1000 + i, ap);
			world.addAircraft(ac);
		}
		// create FlightPlans for all aircrafts
		for (int i = 0; i < intAmountOfFlights; i++) {
			// Random Airport:
			Aircraft ac = world.getAircraft("X" + 1000 + i);
			// first Flight:
			ap = world.getAirport(airportNames[rand.nextInt(3)]);
			while (ap == ac.getCurrentAirPort()) {
				ap = world.getAirport(airportNames[rand.nextInt(3)]);
			}
			Flight f = new Flight(rand.nextInt(1000), ap);
			ac.getFlightPlan().addFlight(f);
			// Return flight
			f = new Flight(rand.nextInt(1000), ac.getCurrentAirPort());
		}

		// System.out.println(world);
		// schedule initial events
		for (int i = 0; i < intAirCrafts; i++) {
			Aircraft ac = world.getAircraft("X" + 1000 + i);
			Flight f = ac.getFlightPlan().removeNextFlight();
			ac.setDestination(f.getDestination());
			ap = ac.getCurrentAirPort();
			Event e = new Event(Event.READY_FOR_DEPARTURE, ap, f.getTimeGap(),
					ap, ac);
			scheduleEvent(e);
		}
	}

	public void InitGui() {
		gui = new Gui();
		gui.init(this);
		
		Animation a = new Animation(this);
		a.setVisible(true);
	}

	public SimWorld getSimWorld() {
		return world;
	}

}
