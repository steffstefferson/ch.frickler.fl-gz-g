package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import simulation.communication.Communication;
import simulation.communication.Message;
import simulation.definition.EventHandler;
import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.eventHandlers.AddToAnimationHandler;
import simulation.eventHandlers.ArrivalHandler;
import simulation.eventHandlers.EndLandingHandler;
import simulation.eventHandlers.EndTakeOffHandler;
import simulation.eventHandlers.EnterAirspaceHandler;
import simulation.eventHandlers.LeaveAirspaceHandler;
import simulation.eventHandlers.ProcessQueuesHandler;
import simulation.eventHandlers.ReadyForDepartureHandler;
import simulation.eventHandlers.RemoveFromAnimationHandler;
import simulation.eventHandlers.RepaintAnimationHandler;
import simulation.eventHandlers.StartLandingHandler;
import simulation.eventHandlers.StartTakeOffHandler;
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
	private List<Event> evList; // time ordered list
	private Animation animation;
	private int totalProcessors = 1;
	private String[] airportNames = { "ZURICH", "GENF", "BASEL", "BERNE" };
	private Communication communication;
	private Map<Integer, TransactionalEventHandler> eventHandlers = new HashMap<Integer, TransactionalEventHandler>();
	private Vector<Event> processedEvents;

	public Simulator(SimWorld world) {
		this.world = world;
		evList = new ArrayList<Event>();
		processedEvents = new Vector<Event>();
		eventHandlers.put(Event.READY_FOR_DEPARTURE, new ReadyForDepartureHandler());
		eventHandlers.put(Event.START_TAKE_OFF, new StartTakeOffHandler());
		eventHandlers.put(Event.END_TAKE_OFF, new EndTakeOffHandler());
		eventHandlers.put(Event.ARRIVAL, new ArrivalHandler());
		eventHandlers.put(Event.START_LANDING, new StartLandingHandler());
		eventHandlers.put(Event.END_LANDING, new EndLandingHandler());
		eventHandlers.put(Event.PROCESS_QUEUES, new ProcessQueuesHandler());
		eventHandlers.put(Event.ADD_TO_ANIMATION, new AddToAnimationHandler());
		eventHandlers.put(Event.REMOVE_FROM_ANIMATION, new RemoveFromAnimationHandler());
		eventHandlers.put(Event.REPAINT_ANIMATION, new RepaintAnimationHandler());
		eventHandlers.put(Event.LEAVE_AIRSPACE, new LeaveAirspaceHandler());
		eventHandlers.put(Event.ENTER_AIRSPACE, new EnterAirspaceHandler());

	}

	public Simulator(SimWorld world, boolean bMasterProcess, int idofProcessor, int totalProcessors) {
		this(world);
		this.idofProcessor = idofProcessor;
		this.isMaster = bMasterProcess;
		this.totalProcessors = totalProcessors;
	}

	public int getIdOfProcessor() {
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
				Event eNew = new Event(Event.REPAINT_ANIMATION, this, e.getTimeStamp() + Clock.REPAINT_GAP, null, null);
				scheduleEvent(eNew);
			}

		} else if (e.getType() == Event.LEAVE_AIRSPACE) {
			scheduleEvent(new Event(Event.REMOVE_FROM_ANIMATION, this, e.getTimeStamp(), null, e.getAirCraft()));
			scheduleEvent(new Event(Event.ENTER_AIRSPACE, this, e.getTimeStamp(), null, e.getAirCraft()));
		} else if (e.getType() == Event.ENTER_AIRSPACE) {
			scheduleEvent(new Event(Event.ADD_TO_ANIMATION, this, e.getTimeStamp(), null, e.getAirCraft()));
			Aircraft ac = e.getAirCraft();
			Airport origin = ac.getOrigin();
			Airport dest = ac.getDestination();
			long duration = (long) (origin.getDistanceTo(dest) / Aircraft.MAX_SPEED);
			scheduleEvent(new Event(Event.ARRIVAL, dest, e.getTimeStamp() + duration / 2, dest, ac));
		} else
			throw new RuntimeException("Scheduler can handle only QUERY events");

	}

	/*
	 * get the next lower bound of future sending event
	 */

	private long getLowerFutureTimeStampBoundary() {
		for (Event ev : evList) {
			if (ev.getType() == Event.ENTER_AIRSPACE) {
				return ev.getTimeStamp();
			}
		}
		return -1;
	}

	/*
	 * New events which lay in the past cause a causality error
	 * 
	 * @see EventScheduler#scheduleEvent(Event)
	 */
	public void scheduleEvent(Event e) {

		if (e.getType() == Event.ENTER_AIRSPACE) {
			communication.send(e, e.getAirCraft());
			return;
		}

		// long timeEvent = e.getTimeStamp();

		// if (clock.isInPast(timeEvent)) {
		// throw new RuntimeException("Causality error: " + e + "tim: "
		// + timeEvent + " currentSimulationTime"
		// + clock.currentSimulationTime());
		//
		// }
		insertEvent(e);

		// If list is empty, start painting (again)
		if (evList.size() <= 1) {
			Event eNew = new Event(Event.REPAINT_ANIMATION, this, e.getTimeStamp() + Clock.REPAINT_GAP, null, null);
			scheduleEvent(eNew);

			logGui.println("Start paint animation" + e.toString());
		}

	}

	private void insertEvent(Event e) {
		if (e.isAntiMessage() && evList.contains(e))
			evList.remove(e);
		int pos = 0;
		while (pos < evList.size()) {
			Event n = evList.get(pos);
			if (n.getTimeStamp() > e.getTimeStamp())
				break;
			pos++;
		}

		evList.add(pos, e);
	}

	private Event removeEvent(Event e) {
		int pos = 0;
		while (pos < evList.size()) {
			Event n = evList.get(pos);
			if (e.equals(n))
				break;
			pos++;
		}

		return evList.remove(pos);
	}

	private void handleAntiMessage(Event msgEvent) {
		if (!clock.isInPast(msgEvent.getTimeStamp())) { // has not yet been
														// proccssed

			if (removeEvent(msgEvent) == null) { // If Message has been
													// recieved, remove event
				// Event has not yet been recived
				insertEvent(msgEvent);
			}

		} else { // Message has been processed
			doRollback(msgEvent);
		}
	}

	private void doRollback(Event msgEvent) {

		eventHandlers.get(msgEvent.getType()).rollback(msgEvent, this);

	}

	private void addMPIEvent(Event msgEvent) {
		if (!evList.contains(msgEvent)) { // There is no Anti-Message in
											// the queue
			insertEvent(msgEvent); // Insert event into queue
		} else {
			removeEvent(msgEvent); // Remove anti-Message
		}
	}

	/**
	 * Advances the time to the time of the oldest event in the event queue and
	 * processes the event
	 */
	public void processNextEvent() {
		final Event e;
		final Message message = communication.receive();
		if (message != null) {
			Event msgEvent = message.getEvent(world, this);

			// Page 30
			if (msgEvent.isAntiMessage()) {
				handleAntiMessage(msgEvent);
			}

			if (clock.isInPast(msgEvent.getTimeStamp())) {
				doRollback(msgEvent);

			} else {
				addMPIEvent(msgEvent);
			}
			e = message.getEvent(world, this);
		} else {
			e = evList.remove(0); // TODO only remove if we are sure
									// we
									// are allowed to process
		}
		logGui.println("Process next event:" + e);
		if (e.getTimeStamp() > clock.currentSimulationTime()) {
			clock.sleepUntil(e.getTimeStamp());
		}

		// EventHandler eh = e.getEventHandler();
		// eh.processEvent(e, this);
		eventHandlers.get(e.getType()).process(e, this);
		// Add to history
		processedEvents.add(e);
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

		initAirports();

		// create 100 aircrafts and choose an arbitrary airport
		for (int i = 0; i < amountOfFlights; i++) {
			// Random Airport:
			Airport ap = world.getAirport(airportNames[rand.nextInt(airportNames.length)]);
			Aircraft ac = new Aircraft("X" + 1000 + i, ap);

			// add the aircraft only to the wolrd where its base airport is.
			if (ap.getAirportId() % getTotalProcessors() == getIdOfProcessor()) {
				world.addAircraft(ac);
				System.out.println("aircraft " + ac.getName() + " for processor " + getIdofProcessor() + " located at "
						+ ap.getName());
			}
		}
		// create FlightPlans for all aircrafts
		// for (int i = 0; i < world.getAircrafts().size(); i++) {
		// Random Airport:
		// Aircraft ac = world.getAircraft("X"+1000+i);

		for (String key : world.getAircrafts().keySet()) {
			Aircraft ac = world.getAircrafts().get(key);

			// first Flight:
			int amountOfAps = world.getAirports().size();
			Airport ap = world.getAirport(airportNames[rand.nextInt(amountOfAps)]);
			while (ap == ac.getCurrentAirPort()) {
				ap = world.getAirport(airportNames[rand.nextInt(amountOfAps)]);
			}
			int scheduleTime = rand.nextInt(10000);
			logGui.println("SchedulTime for Aircraft: :" + key + " is " + scheduleTime);
			Flight f = new Flight(scheduleTime, ap);
			ac.getFlightPlan().addFlight(f);
			// Return flight
			// f = new Flight(rand.nextInt(1000), ac.getCurrentAirPort());
		}

		// schedule initial events
		// for (int i = 0; i < amountOfFlights; i++) {

		for (String key : world.getAircrafts().keySet()) {
			Aircraft ac = world.getAircrafts().get(key);
			// Aircraft ac = world.getAircraft("X"+1000+i);
			Flight f = ac.getFlightPlan().removeNextFlight();
			if (f != null) {
				ac.setDestination(f.getDestination());
				Airport ap = ac.getCurrentAirPort();
				Event e = new Event(Event.READY_FOR_DEPARTURE, ap, f.getTimeGap(), ap, ac);
				scheduleEvent(e);
			}
		}
	}

	private void initAirports() {
		// create airports

		Airport ap = new Airport("ZURICH", 684000, 256000, 683000, 259000, 60000, 45000);
		world.addAirport(ap);
		ap = new Airport("GENF", 497000, 120000, 499000, 122000, 50000, 100000);
		world.addAirport(ap);
		ap = new Airport("BASEL", 599000, 287000, 601000, 288000, 40000, 45000);
		world.addAirport(ap);

		ap = new Airport("BERNE", 550000, 207000, 552000, 208000, 40000, 100000);
		world.addAirport(ap);

	}

	private int getTotalProcessors() {
		return this.totalProcessors;
	}

	public void initGui() {
		logGui = new LogGui();
		logGui.init(this);

		animation = Animation.init(this, clock);
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

	public void setCommunication(Communication communication) {
		this.communication = communication;
	}

	@Override
	public List<Event> getEventList() {
		return evList;
	}

}
