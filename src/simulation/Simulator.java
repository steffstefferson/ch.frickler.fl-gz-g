package simulation;

import java.util.Random;
import java.util.Vector;

/**
 * @author ps Manages simulation clock, a event queue and world consisting of
 *         aircrafts and airports
 */
public class Simulator implements EventScheduler {

	private static final int TIME_SCALE = 100000;
	private SimWorld world;
	private long startTimeMillis;

	private Gui gui;

	private Vector<Event> evList; // time ordered list

	public Simulator(SimWorld world) {
		this.world = world;
		evList = new Vector<Event>();
		startTimeMillis = System.currentTimeMillis();
	}

	@Override
	public long getCurrentSimulationTime() {
		return (System.currentTimeMillis() - startTimeMillis) * 100 / TIME_SCALE;
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
	}

	/**
	 * Advances the time to the time of the oldest event in the event queue and
	 * processes the event
	 */
	public void processNextEvent() {
		final Event e = evList.remove(0);
		final long eventTime = e.getTimeStamp();
		final long currentSimulationTime = getCurrentSimulationTime();
		if (currentSimulationTime < eventTime)
			try {
				Thread.sleep(eventTime - currentSimulationTime);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

		gui.println(e.toString()); // log the event
		e.getEventHandler().processEvent(e, this);
		// log the state of the object which is the target of this
		gui.println(e.getEventHandler().toString());

	}

	// TODO
	//
	// Event e = evList.remove(0)
	// now = e.getTimestamp();
	// lont rt = System.currentTimeMillis - startClockMillies
	// long soll = (long) (now*100/timescale);
	// if (rt<soll){
	// try
	// fred.sleep(soll-rt)
	// }
	// catch{InterruptedExcption}
	//
	// if (e.getType()!= EVENT.query gui.println(e.toString()=;
	// e.getEventHandeler.processEvent(e,this);
	// if (e.getType!=Event.Query) gui.println(e.getEventHandler().toString();
	//
	// }
	//
	// @Override
	// public void processEvent(Event e, EventScheduler s){
	// // we handle only query events!
	// if (e.getType == Event.ADD_TO_QUERY){
	// animation.addToQuery(e.getAircraft());
	// } else if (e.getType() == Event.REMOVE_FROM_QUERY){
	// animation.removeFromQuery(e.getAircracft());
	// }
	// else if (e.getType == Event.QUERY){
	// animation.repaint();
	//
	// if (evList.size() > )}{
	// Event eNew = new Event(Event.QUERY, this, now + viewGap, null, null);
	// scheduleEvent(eNew);
	// }
	// }
	// else throw new
	// RuntimeException("Scheduler can handle only QUERY events");
	// }

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
		int n = 100;
		// Random Generator:
		Random rand = new Random(1234);
		// create airports
		String[] airportNames = { "ZÜRICH", "GENF", "BASEL" };
		Airport ap = new Airport("ZÜRICH", 684000, 256000, 683000, 259000);
		world.addAirport(ap);
		ap = new Airport("GENF", 497000, 120000, 499000, 122000);
		world.addAirport(ap);
		ap = new Airport("BASEL", 599000, 287000, 601000, 288000);
		world.addAirport(ap);

		// create 100 aircrafts and choose an arbitrary airport
		for (int i = 0; i < n; i++) {
			// Random Airport:
			ap = world.getAirport(airportNames[rand
					.nextInt(airportNames.length)]);
			Aircraft ac = new Aircraft("X" + 1000 + i, ap);
			world.addAircraft(ac);
		}
		// create FlightPlans for all aircrafts
		for (int i = 0; i < n; i++) {
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
		for (int i = 0; i < n; i++) {
			Aircraft ac = world.getAircraft("X" + 1000 + i);
			Flight f = ac.getFlightPlan().removeNextFlight();
			ac.setDestination(f.getDestination());
			ap = ac.getCurrentAirPort();
			Event e = new Event(Event.READY_FOR_DEPARTURE, ap, f.getTimeGap(),
					ap, ac);
			scheduleEvent(e);
		}
	}

	static public void main(String[] argv) {
		Simulator sim = new Simulator(SimWorld.getInstance());
		sim.initWorld();
		sim.gui = new Gui();
		sim.gui.init();
		sim.runSimulation(); // main simulation loop
	}

}
