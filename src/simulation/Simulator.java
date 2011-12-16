package simulation;

import java.util.Random;
import java.util.Vector;

import simulation.definition.EventHandler;
import simulation.definition.EventScheduler;
import simulation.gui.Animation;
import simulation.gui.LogGui;
import simulation.logic.Clock;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.Event;
import simulation.model.Flight;
import simulation.model.SimWorld;

/**
 * @author ps Manages simulation clock, a event queue and world consisting of
 *         aircrafts and airports
 */
public class Simulator implements EventScheduler, EventHandler {

	private Clock clock = new Clock();
	private SimWorld world;
	private LogGui logGui;
	private int idofProcessor = 0;
	private boolean isMaster = false;
	private Vector<Event> evList; // time ordered list
	private Animation animation;
	private int totalProcessors = 1;

	public Simulator(SimWorld world) {
		this.world = world;
		evList = new Vector<Event>();
	}

	public Simulator(SimWorld world, boolean bMasterProcess,
			int idofProcessor, int totalProcessors) {
		this(world);
		this.idofProcessor = idofProcessor;
		this.isMaster = bMasterProcess;
		this.totalProcessors  = totalProcessors;
	}

	public int getIdOfProcessor(){
		return idofProcessor;
	}
	
	@Override
	public void processEvent(Event e, EventScheduler s) {
		// we handle only query events!
		if (e.getType() == Event.ADD_TO_ANIMATION) {
			animation.addToQuery(e.getAirCraft());
		} else if (e.getType() == Event.REMOVE_FROM_ANIMATION) {
			animation.removeFromQuery(e.getAirCraft());
		} else if (e.getType() == Event.REPAINT_ANIMATION) {
			// animation.setCurrentTime(e.getTimeStamp());
			animation.repaint();

			if (evList.size() > 0) {
				Event eNew = new Event(Event.REPAINT_ANIMATION, this,
						e.getTimeStamp() + Clock.REPAINT_GAP, null, null);
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

		long timeEvent = e.getTimeStamp();

		if (clock.isInPast(timeEvent)) {
			throw new RuntimeException("Causality error: " + e + "tim: "
					+ timeEvent + " currentSimulationTime"
					+ clock.currentSimulationTime());

		}
		insertEvent(e);

		// If list is empty, start painting (again)
		if (evList.size() <= 1) {
			Event eNew = new Event(Event.REPAINT_ANIMATION, this,
					e.getTimeStamp() + Clock.REPAINT_GAP, null, null);
			scheduleEvent(eNew);

			logGui.println("Start paint animation" + e.toString());
		}

	}

	private void insertEvent(Event e) {
		int pos = 0;
		while (pos < evList.size()) {
			Event n = evList.get(pos);
			if (n.getTimeStamp() > e.getTimeStamp())
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

		final Event e = evList.remove(0); // TODO only remove if we are sure we
											// are allowed to process
		logGui.println("Process next event:" + e);
		if (e.getTimeStamp() > clock.currentSimulationTime()) {
			clock.sleepUntil(e.getTimeStamp());
		}

		EventHandler eh = e.getEventHandler();
		eh.processEvent(e, this);

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
		logGui.println("Processed " + evCnt + " events.");
	}

	public void initWorld() {
		initWorld(100);
	}

	public void initWorld(int amountOfFlights) {

		// Random Generator:
		Random rand = new Random(1234);

		// create airports
		String[] airportNames = { "ZURICH", "GENF", "BASEL", "BERNE" };
		Airport ap = new Airport("ZURICH", 684000, 256000, 683000, 259000);
		world.addAirport(ap);
		ap = new Airport("GENF", 497000, 120000, 499000, 122000);
		world.addAirport(ap);
		ap = new Airport("BASEL", 599000, 287000, 601000, 288000);
		world.addAirport(ap);

		ap = new Airport("BERNE", 550000, 207000, 552000, 208000);
		world.addAirport(ap);

		// create 100 aircrafts and choose an arbitrary airport
		for (int i = 0; i < amountOfFlights; i++) {
			// Random Airport:
			ap = world.getAirport(airportNames[rand
					.nextInt(airportNames.length)]);
			Aircraft ac = new Aircraft("X"+1000+i,ap);
			
			//add the aircraft only to the wolrd where its base airport is.
			if(ap.getAirportId() % getTotalProcessors() == getIdOfProcessor()){
				world.addAircraft(ac);
				System.out.println("aircraft "+ac.getName()+" for processor "+getIdofProcessor()+ " located at "+ap.getName());
			}
		}
		// create FlightPlans for all aircrafts
		//for (int i = 0; i < world.getAircrafts().size(); i++) {
			// Random Airport:
			//Aircraft ac = world.getAircraft("X"+1000+i);	
			
		for(String key : world.getAircrafts().keySet()){
			Aircraft ac = world.getAircrafts().get(key);
			
			
			// first Flight:
			int amountOfAps = world.getAirports().size();
			ap = world.getAirport(airportNames[rand.nextInt(amountOfAps)]);
			while (ap == ac.getCurrentAirPort()) {
				ap = world.getAirport(airportNames[rand.nextInt(amountOfAps)]);
			}
			int scheduleTime = rand.nextInt(10000);
			logGui.println("SchedulTime for Aircraft: :" + key + " is "
					+ scheduleTime);
			Flight f = new Flight(scheduleTime, ap);
			ac.getFlightPlan().addFlight(f);
			// Return flight
			// f = new Flight(rand.nextInt(1000), ac.getCurrentAirPort());
		}

		// schedule initial events
		//for (int i = 0; i < amountOfFlights; i++) {
		
		for(String key : world.getAircrafts().keySet()){
			Aircraft ac = world.getAircrafts().get(key);
			//Aircraft ac = world.getAircraft("X"+1000+i);
			Flight f = ac.getFlightPlan().removeNextFlight();
			if(f != null){
			ac.setDestination(f.getDestination());
			ap = ac.getCurrentAirPort();
			Event e = new Event(Event.READY_FOR_DEPARTURE, ap, f.getTimeGap(),
					ap, ac);
			scheduleEvent(e);
			}
		}
	}

	private int getTotalProcessors() {
		return this.totalProcessors;
	}

	public void initGui() {
		logGui = new LogGui();
		logGui.init(this);

		animation = new Animation(this, clock);
		animation.setVisible(true);
	}

	public SimWorld getSimWorld() {
		return world;
	}

	public int getIdofProcessor() {
		return idofProcessor;
	}

	public boolean isMaster() {
		return isMaster;
	}


}
