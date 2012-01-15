package simulation.eventHandlers;

import simulation.definition.EventScheduler;
import simulation.definition.TransactionalEventHandler;
import simulation.gui.Animation;
import simulation.logic.Clock;
import simulation.model.Event;

public class RepaintAnimationHandler implements TransactionalEventHandler {

	@Override
	public void process(Event e, EventScheduler scheduler) {
		Animation.getInstance().repaint();

		if (scheduler.getEventList().size() > 0) {
			Event eNew = new Event(Event.REPAINT_ANIMATION, e.getTimeStamp() + Clock.REPAINT_GAP, null, null);
			scheduler.scheduleEvent(eNew);
		}

	}

	@Override
	public void rollback(Event e, EventScheduler scheduler) {
		// TODO Auto-generated method stub

	}

}
