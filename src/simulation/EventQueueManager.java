package simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import simulation.model.Event;

/**
 * This class handles all queues. There are three queues: One for all upcoming
 * events, one for all processed events and one for all anti-messages
 * 
 */
public class EventQueueManager {

	private List<Event> eventList; // time ordered list
	private Set<Event> antiMessages;
	private List<Event> processedEvents;

	/**
	 * Default constructor
	 */
	public EventQueueManager() {
		eventList = new ArrayList<Event>();
		antiMessages = new HashSet<Event>();
		processedEvents = new ArrayList<Event>();
	}

	/**
	 * Inserts an event to the event-queue. If the message is an anti-message
	 * the event removes the according event from the queue.
	 * 
	 * @param e
	 *            Event to insert into queue
	 */
	public void insertEvent(Event e) {
		// antimessage and normal message cancel each other out
		if (e.isAntiMessage()) {
			if (eventList.contains(e))
				eventList.remove(e);
			else
				antiMessages.add(e);
		} else if (!e.isAntiMessage()) {
			if (antiMessages.contains(e))
				antiMessages.remove(e);
			else {
				insertAtCorrectTime(e);
			}
		}
	}

	/**
	 * Inserts an event at the correct place in the event-queue.
	 * 
	 * @param e
	 *            Event to insert into queue
	 */
	private void insertAtCorrectTime(Event e) {
		int pos = 0;
		while (pos < eventList.size()) {
			Event n = eventList.get(pos);
			if (n.getTimeStamp() > e.getTimeStamp())
				break;
			pos++;
		}

		eventList.add(pos, e);
	}

	/**
	 * 
	 * @return List of all processed events
	 */
	public List<Event> getProcessedEvents() {
		return processedEvents;
	}

	/**
	 * After a rollback an event can be re-inserted into the queue
	 * 
	 * @param e
	 */
	public void reInsertEvent(final Event e) {
		if (e.isAntiMessage())
			throw new IllegalArgumentException("Cannot re-insert antiMessage, as it should never have been processed");
		processedEvents.remove(e);
		insertEvent(e);
	}

	/**
	 * Insert an event to the processed queue. REPAINT_ANIMATION-Events are not
	 * added the the processed events, because they are not reprocessed later.
	 * 
	 * @param e
	 *            Processed event
	 */
	public void moveToProcessedQueue(final Event e) {
		// repaint event will automatically regenerated
		if (e.getType() != Event.REPAINT_ANIMATION)
			processedEvents.add(e);
		eventList.remove(e);
	}

	/**
	 * 
	 * @return The next event from the queue
	 */
	public Event getNextEvent() {
		return eventList.get(0);
	}

	/**
	 * 
	 * @return Amount of unprocessed events
	 */
	public int getNumberOfPendingEvents() {
		return eventList.size();
	}
}
