package simulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import simulation.communication.Communication;
import simulation.communication.Message;
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
 * Manages simulation clock, a event queue and world consisting of aircrafts and
 * airports
 */
public class Simulator implements EventScheduler {

	/**
	 * Special GVT value indicating that event processing is finished.
	 */
	public static final int EMPTY_QUEUE = -1;
	private Clock clock = new Clock();
	private SimWorld world;
	private LogGui logGui;
	private int idofProcessor = 0;
	private boolean isMaster = false;
	private Animation animation;
	private int totalProcessors = 1;
	private String[] airportNames = { "ZURICH", "GENF", "BASEL", "BERNE" };
	private Communication communication;
	private Map<Integer, TransactionalEventHandler> eventHandlers = new HashMap<Integer, TransactionalEventHandler>();
	private EventQueueManager eventQueueManager;

	public Simulator(SimWorld world) {
		this.world = world;

		eventQueueManager = new EventQueueManager();
		// Each event has its own handler
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

	/**
	 * 
	 * @param world
	 * @param bMasterProcess
	 *            Indicates if this LP
	 * @param idofProcessor
	 *            ID of this LP
	 * @param totalProcessors
	 *            Total amount of all LP
	 */
	public Simulator(SimWorld world, boolean bMasterProcess, int idofProcessor, int totalProcessors) {
		this(world);
		this.idofProcessor = idofProcessor;
		this.isMaster = bMasterProcess;
		this.totalProcessors = totalProcessors;
	}

	/**
	 * 
	 * @return Id of the LP
	 */
	public int getIdOfProcessor() {
		return idofProcessor;
	}

	/**
	 * Schedules the next event. If the event is of type ENTER_AIRSPACE-Event
	 * then the event gets sent to the affected airport via MPI
	 * 
	 * @param e
	 *            Next event to schedule
	 * @throws
	 * @see EventScheduler#scheduleEvent(Event)
	 * 
	 * 
	 */
	public void scheduleEvent(Event e) {

		if (e.getType() == Event.ENTER_AIRSPACE) {
			// pass aircraft on to next airport
			communication.send(e, e.getAirCraft());
		} else if (e.getType() == Event.START_GVT) {
			// master process only: calculate & broadcast GVT
			communication.startGVT(e);
			long gvt = communication.broadcastGVT(getLocalMinimum());
			eventQueueManager.cleanup(gvt);
			eventQueueManager.insertEvent(new Event(Event.START_GVT,
					clock.currentSimulationTime() + Clock.GVT_TIME_GAP, null, null));
		} else {
			// handle event locally
			eventQueueManager.insertEvent(e);

			if (clock.isInPast(e.getTimeStamp())) {
				throw new RuntimeException("Causality error: " + e + "tim: " + e.getTimeStamp()
						+ " currentSimulationTime" + clock.currentSimulationTime());
			}

		}

	}

	/**
	 * Creates the first REPAINT_EVENT to force the repaint of the animation GUI
	 */
	private void startAnimation() {
		Event eNew = new Event(Event.REPAINT_ANIMATION, clock.currentSimulationTime() + Clock.REPAINT_GAP, null, null);
		scheduleEvent(eNew);

		logGui.println("Start paint animation" + eNew.toString());
	}

	/**
	 * Roll back all processed events that are newer than the stragglerEvent
	 * 
	 * @param stragglerEvent
	 */
	private void doRollback(Event stragglerEvent) {
		clock.rollbackTo(stragglerEvent.getTimeStamp());
		final List<Event> processedEvents = eventQueueManager.getProcessedEvents();
		for (int i = processedEvents.size() - 1; i >= 0; i--) {
			Event event = processedEvents.get(i);
			if (!clock.isInPast(event.getTimeStamp()))
				rollbackEvent(event);
		}
	}

	/**
	 * Roll back a single event
	 * 
	 * @param event
	 *            Event to roll back
	 */
	private void rollbackEvent(Event event) {
		System.out.println("[" + getIdOfProcessor() + "]: Rolling back event " + event);
		eventHandlers.get(event.getType()).rollback(event, this);
		eventQueueManager.reInsertEvent(event);
	}

	/**
	 * Advances the time to the time of the oldest event in the event queue and
	 * processes the event
	 */
	public void processNextEvent() {
		final Event e = eventQueueManager.getNextEvent();
		// master process only: GVT broadcast is handled explicitly
		if (e.getType() == Event.START_GVT) {
			eventQueueManager.moveToProcessedQueue(e);
			scheduleEvent(e);
			return;
		}
		// local causality constraint violation, should never happen
		if (clock.isInPast(e.getTimeStamp()))
			throw new RuntimeException("event is in past");
		// event is in the future. Sleep until this time
		if (e.getTimeStamp() > clock.currentSimulationTime()) {
			clock.sleepUntil(e.getTimeStamp());
		}
		processEvent(e);
	}

	/**
	 * Try to receive a message via MPI. If a message is received, insert it
	 * into the event queue.
	 */
	private void receiveMessage() {
		final Message message = communication.receive();
		if (message == null)
			return; // nothing received
		final Event e = message.getEvent(world);
		if (e.getType() == Event.START_GVT) {
			long gvt = communication.calculateGVT(getLocalMinimum());
			eventQueueManager.cleanup(gvt);
		} else {
			if (clock.isInPast(e.getTimeStamp())) {
				System.out.println("[" + getIdOfProcessor() + "]: Received straggler message, starting rollback: " + e);
				doRollback(e);
			}
			eventQueueManager.insertEvent(e);
		}
	}

	/**
	 * Calculate the minimal local timestamp.
	 * 
	 * @return the lowest timestamp of the queued events, or -1 if no more
	 *         events are in the queue
	 */
	private long getLocalMinimum() {
		// there will always be a repaint event in the queue, so we check for
		// <=1
		if (eventQueueManager.getNumberOfPendingEvents() <= 1)
			return EMPTY_QUEUE;
		return eventQueueManager.getNextEvent().getTimeStamp();
	}

	/**
	 * Process a single event by calling the appropriate EventHandler, then
	 * moving the event to the processed queue.
	 * 
	 * @param e
	 *            the event to be processed
	 * 
	 */
	private void processEvent(final Event e) {
		System.out.println("[" + getIdOfProcessor() + "]: Processing event " + e);
		logGui.println("Process next event:" + e);
		eventHandlers.get(e.getType()).process(e, this);
		eventQueueManager.moveToProcessedQueue(e);
	}

	/**
	 * This is the main simulation loop
	 */
	public void runSimulation() {
		int evCnt = 0;
		while (eventQueueManager.getNextEvent() != null && eventQueueManager.getGVT() != EMPTY_QUEUE) {
			receiveMessage();
			processNextEvent();
			evCnt++;
		}
		logGui.println("Processed " + evCnt + " events.");
		System.out.println("[" + getIdOfProcessor() + "]: Processed " + evCnt + " events.");
	}

	public void initWorld(int amountOfFlights) {

		// Random Generator:
		Random rand = new Random(1234);

		initAirports();

		// create 100 aircrafts and choose an arbitrary airport
		for (int i = 0; i < amountOfFlights; i++) {
			// Random Airport:
			Airport ap = world.getAirport(airportNames[rand.nextInt(airportNames.length)]);
			Aircraft ac = new Aircraft("X" + getIdOfProcessor() + "000" + i, ap);

			// add the aircraft only to the wolrd where its base airport is.
			if (ap.getAirportId() % getTotalProcessors() == getIdOfProcessor()) {
				world.addAircraft(ac);
				System.out.println("aircraft " + ac.getName() + " for processor " + getIdOfProcessor() + " located at "
						+ ap.getName());
			}
		}

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
		// Start
		for (String key : world.getAircrafts().keySet()) {
			Aircraft ac = world.getAircrafts().get(key);
			// Aircraft ac = world.getAircraft("X"+1000+i);
			Flight f = ac.getFlightPlan().removeNextFlight();
			if (f != null) {
				ac.setDestination(f.getDestination());
				Airport ap = ac.getCurrentAirPort();
				Event e = new Event(Event.READY_FOR_DEPARTURE, f.getTimeGap(), ap, ac);
				scheduleEvent(e);
			}
		}
		startAnimation();
		startGVT();
	}

	private void startGVT() {
		if (isMaster() || getTotalProcessors() == 1) {
			scheduleEvent(new Event(Event.START_GVT, clock.currentSimulationTime() + Clock.GVT_TIME_GAP, null, null));
		}

	}

	private void initAirports() {
		// create airports
		Airport ap = new Airport("ZURICH", 664000, 266000, 663000, 269000, 60000, 45000);
		world.addAirport(ap);
		ap = new Airport("GENF", 487000, 120000, 489000, 122000, 50000, 100000);
		world.addAirport(ap);
		ap = new Airport("BASEL", 592000, 287000, 593000, 288000, 40000, 45000);
		world.addAirport(ap);
		ap = new Airport("BERNE", 585000, 207000, 587000, 208000, 40000, 100000);
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

	public boolean isMaster() {
		return isMaster;
	}

	public void setCommunication(Communication communication) {
		this.communication = communication;
	}

	@Override
	public int getNumberOfPendingEvents() {
		return eventQueueManager.getNumberOfPendingEvents();
	}
}
