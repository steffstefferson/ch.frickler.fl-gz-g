package simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import simulation.model.Event;

public class EventQueueManager {

	private List<Event> eventList; // time ordered list
	private Set<Event> antiMessages;
	private List<Event> processedEvents;

	public EventQueueManager() {
		eventList = new ArrayList<Event>();
		antiMessages = new HashSet<Event>();
		processedEvents = new ArrayList<Event>();
	}

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

	public void reInsertEvent(final Event e) {
		if (e.isAntiMessage())
			throw new IllegalArgumentException("Cannot re-insert antiMessage, as it should never have been processed");
		processedEvents.remove(e);
		insertEvent(e);
	}

	public void moveToProcessedQueue(final Event e) {
		switch (e.getType()) {
		case Event.REPAINT_ANIMATION:
		case Event.START_GVT:
			break;
		default:
			processedEvents.add(e);
			break;
		}
		eventList.remove(e);
	}

	public Event getNextEvent() {
		return eventList.get(0);
	}

	public int getNumberOfPendingEvents() {
		return eventList.size();
	}

	public void cleanup(long gvt) {
		int before = processedEvents.size();
		for (Iterator<Event> iterator = processedEvents.iterator(); iterator.hasNext();) {
			Event event = iterator.next();
			if (event.getTimeStamp() < gvt)
				iterator.remove();
		}
		System.out.println("Deleted " + (before - processedEvents.size())
				+ " events from processed queue; new size is " + processedEvents.size());

	}
}
