<<<<<<< HEAD
package simulation;

import java.util.Random;
import java.util.Vector;

/**
 * @author ps Manages simulation clock, a event queue and world consisting of
 *         aircrafts and airports
 */
public class Simulator implements EventScheduler, EventHandler {

	private static final int TIME_SCALE = 10000;
	private static final long REPAINT_GAP = 100;
	private SimWorld world;
	private long startTimeMillis;

	private Gui gui;

	private Vector<Event> evList; // time ordered list
	private Animation animation;

	public Simulator(SimWorld world) {
		this.world = world;
		evList = new Vector<Event>();
		startTimeMillis = System.currentTimeMillis();
	}

	@Override
	public long getCurrentSimulationTime() {
		return (System.currentTimeMillis() - startTimeMillis) * 100
				/ TIME_SCALE;
	}

	@Override
	public void processEvent(Event e, EventScheduler s) {
		// we handle only query events!
		if (e.getType() == Event.ADD_TO_ANIMATION) {
			animation.addToQuery(e.getAirCraft());
		} else if (e.getType() == Event.REMOVE_FROM_ANIMATION) {
			animation.removeFromQuery(e.getAirCraft());
		} else if (e.getType() == Event.REPAINT_ANIMATION) {
			animation.repaint();

			if (evList.size() > 0) {
				Event eNew = new Event(Event.REPAINT_ANIMATION, this,
						e.getTimeStamp() + REPAINT_GAP, null, null);
				scheduleEvent(eNew);
			}
		} else
			throw new RuntimeException("Scheduler can handle only QUERY events");

	}

	/*
	 * New events which lay in the past cause a causality error
	 * 
	 * @see EventScheduler#scheduleEvent(Event)
	 */
	public void scheduleEvent(Event e) {
		long tim = e.getTimeStamp();
		final long currentSimulationTime = getCurrentSimulationTime();
		if (tim < currentSimulationTime)
			throw new RuntimeException("Causality error: " + this);
		int pos = 0;
		while (pos < evList.size()) {
			Event n = evList.get(pos);
			if (n.getTimeStamp() > tim)
				break;
			pos++;
		}

		evList.add(pos, e);

		// If list is empty, start painting (again)
		if (evList.size() <= 1) {
			Event eNew = new Event(Event.REPAINT_ANIMATION, this,
					e.getTimeStamp() + REPAINT_GAP, null, null);
			scheduleEvent(eNew);
			
			gui.println("Start paint animation" + e.toString());
		}

	}

	/**
	 * Advances the time to the time of the oldest event in the event queue and
	 * processes the event
	 */
	public void processNextEvent() {
		final Event e = evList.remove(0);
		final long eventTime = e.getTimeStamp();
		final long currentSimulationTime = getCurrentSimulationTime();
		if (currentSimulationTime < eventTime) {
			try {
				Thread.sleep(eventTime - currentSimulationTime);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		gui.repaint();
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
		String[] airportNames = { "ZURICH", "GENF", "BASEL","BERNE" };
		Airport ap;
		ap = new Airport("ZURICH", 684000, 256000, 683000, 259000);
		world.addAirport(ap);
		ap = new Airport("GENF", 497000, 120000, 499000, 122000);
		world.addAirport(ap);
		ap = new Airport("BASEL", 599000, 287000, 601000, 288000);
		world.addAirport(ap);

			ap = new Airport("BERNE", 550000, 207000, 552000, 208000);
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

	public void initGui() {
		gui = new Gui();
		gui.init(this);

		animation = new Animation(this);
		animation.setVisible(true);
	}

	public SimWorld getSimWorld() {
		return world;
	}

}
=======
package simulation;

import java.util.Random;
import java.util.Vector;

/**
 * @author ps Manages simulation clock, a event queue and world consisting of
 *         aircrafts and airports
 */
public class Simulator implements EventScheduler, EventHandler {

	private static final int TIME_SCALE = 1000;
	private static final long REPAINT_GAP = 50;
	private SimWorld world;
	private long startTimeMillis = 0;

	private Gui gui;

	private Vector<Event> evList; // time ordered list
	private Animation animation;

	public Simulator(SimWorld world) {
		this.world = world;
		evList = new Vector<Event>();
	}

	private long converToSimulationTime(long eventTime) {
		return eventTime * 100 / TIME_SCALE;
	}

	@Override
	public long getCurrentSimulationTime() {
		return converToSimulationTime((System.currentTimeMillis() - startTimeMillis));
	}

	@Override
	public void processEvent(Event e, EventScheduler s) {
		// we handle only query events!
		if (e.getType() == Event.ADD_TO_ANIMATION) {
			animation.addToQuery(e.getAirCraft());
		} else if (e.getType() == Event.REMOVE_FROM_ANIMATION) {
			animation.removeFromQuery(e.getAirCraft());
		} else if (e.getType() == Event.REPAINT_ANIMATION) {
			animation.setCurrentTime(e.getTimeStamp());
			animation.repaint();

			if (evList.size() > 0) {
				Event eNew = new Event(Event.REPAINT_ANIMATION, this,
						e.getTimeStamp() + REPAINT_GAP, null, null);
				scheduleEvent(eNew);
			}
		} else
			throw new RuntimeException("Scheduler can handle only QUERY events");

	}

	/*
	 * New events which lay in the past cause a causality error
	 * 
	 * @see EventScheduler#scheduleEvent(Event)
	 */
	public void scheduleEvent(Event e) {

		long time = e.getTimeStamp();
		final long currentSimulationTime = getCurrentSimulationTime();
		System.out.println("Schedule event: " + e + " currentSimulationTime"
				+ currentSimulationTime);
		// Check if simulation has started
		if (startTimeMillis > 0) {
			if (converToSimulationTime(time) < currentSimulationTime) {
				throw new RuntimeException("Causality error: " + e + "tim: "
						+ time + " currentSimulationTime"
						+ currentSimulationTime);
			}
		}
		int pos = 0;
		while (pos < evList.size()) {
			Event n = evList.get(pos);
			if (n.getTimeStamp() > time)
				break;
			pos++;
		}

		evList.add(pos, e);

		// If list is empty, start painting (again)
		if (evList.size() <= 1) {
			Event eNew = new Event(Event.REPAINT_ANIMATION, this,
					e.getTimeStamp() + REPAINT_GAP, null, null);
			scheduleEvent(eNew);

			gui.println("Start paint animation" + e.toString());
		}

	}

	/**
	 * Advances the time to the time of the oldest event in the event queue and
	 * processes the event
	 */
	public void processNextEvent() {

		final Event e = evList.remove(0);
		System.out.println("Process next event:" + e);
		final long eventTimeInSimTime = converToSimulationTime(e.getTimeStamp());
		final long currentSimulationTime = getCurrentSimulationTime();
		if (currentSimulationTime < eventTimeInSimTime) {
			try {

				long sleepTime = eventTimeInSimTime - currentSimulationTime;
				Thread.sleep(sleepTime);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		// gui.repaint();
		// gui.println(e.toString()); // log the event

		EventHandler eh = e.getEventHandler();
		eh.processEvent(e, this);
		// log the state of the object which is the target of this
		// gui.println(e.getEventHandler().toString());

	}

	/**
	 * This is the main simulation loop
	 */
	public void runSimulation() {
		startTimeMillis = System.currentTimeMillis();
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

	public void initGui() {
		gui = new Gui();
		gui.init(this);

		animation = new Animation(this);
		animation.setVisible(true);
	}

	public SimWorld getSimWorld() {
		return world;
	}

}
>>>>>>> d38f04f814e86ffb4b330ae8875378962069f327
