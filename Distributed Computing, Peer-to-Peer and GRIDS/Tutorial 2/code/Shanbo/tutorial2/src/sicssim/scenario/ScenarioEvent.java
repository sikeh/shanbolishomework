package sicssim.scenario;

import sicssim.types.EventType;

/**
 * An event in a scenario. This is internal to the simulator.
 * 
 * @author tallat
 *
 */
public abstract class ScenarioEvent {

//----------------------------------------------------------------------------------
	public static class Event {
		public final EventType type;
		public final int time;
		public String info;
		public int[] data;
		public Class nodeType;
		public Class linkType;

//----------------------------------------------------------------------------------
		/**
		 * 
		 * @param type event type
		 * @param time time of the event
		 */
		public Event(EventType type, int time) {
			this.type = type;
			this.time = time;
		}
		
//----------------------------------------------------------------------------------
		public Event(EventType type, int time, String info, int[] data, Class nodeType, Class linkType) {
			this.type = type;
			this.time = time;
			this.info = info;
			this.data = data;
			this.nodeType = nodeType;
			this.linkType = linkType;
		}
	}

//----------------------------------------------------------------------------------
	/**
	 * Does the scenario have more events?
	 * @return true if events exist, false otherwise
	 */
	public abstract boolean hasNext();
 
//----------------------------------------------------------------------------------
	/**
	 * Get the next event in the scenario
	 * @return next event
	 */
	public abstract Event nextEvent();

//----------------------------------------------------------------------------------	
	/**
	 * Push back the event. TODO: check if this documentation is OK
	 *
	 */
	public abstract void undo();
}
