package simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import simulation.model.Event;

/**
 * This class manages the event queues (messages to be processed, processed
 * messages, and anti-messages).
 * 
 */
public class EventQueueManager {

	private List<Event> eventList; // time ordered list
	private Set<Event> antiMessages;
	private List<Event> processedEvents;
	private long gvt;

	public EventQueueManager() {
		eventList = new ArrayList<Event>();
		antiMessages = new HashSet<Event>();
		processedEvents = new ArrayList<Event>();
	}

	/**
	 * Insert the event, checking for antimessages. If a pair of
	 * message/anti-message is found, perform message annihilation. Otherwise,
	 * insert the event at the correct position.
	 * 
	 * @param e
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
			else
				insertAtCorrectTime(e);
		}
	}

	/**
	 * Walk the eventQueue and insert the event at the correct position,
	 * maintaining the ascending timestamp order.
	 * 
	 * @param e
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

	public List<Event> getProcessedEvents() {
		return processedEvents;
	}

	/**
	 * Re-insert the Event into the event queue after a rollback
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
	 * Move the Event to the processed queue.
	 * 
	 * @param e
	 */
	public void moveToProcessedQueue(final Event e) {
		switch (e.getType()) {
		// repaint and start_gvt events cant't be rolled back, so we don't need
		// to keep them in the processed queue
		case Event.REPAINT_ANIMATION:
		case Event.START_GVT:
			break;
		default:
			processedEvents.add(e);
			break;
		}
		eventList.remove(e);
	}

	/**
	 * Get the next event to be processed, but don't remove it from the queue
	 * yet.
	 * 
	 * @return
	 */
	public Event getNextEvent() {
		return eventList.get(0);
	}

	public int getNumberOfPendingEvents() {
		return eventList.size();
	}

	/**
	 * Remove all processed events with a timestamp earlier than the gvt.
	 * 
	 * @param gvt
	 */
	public void cleanup(long gvt) {
		this.gvt = gvt;
		int before = processedEvents.size();
		if (gvt == Simulator.EMPTY_QUEUE)
			processedEvents.clear();
		else {
			for (Iterator<Event> iterator = processedEvents.iterator(); iterator.hasNext();) {
				Event event = iterator.next();
				if (event.getTimeStamp() < gvt)
					iterator.remove();
			}
		}
		System.out.println("Deleted " + (before - processedEvents.size())
				+ " events from processed queue; new size is " + processedEvents.size());
	}

	public long getGVT() {
		return gvt;
	}

}
