package sicssim.scheduler;

import sicssim.network.NodeId;
import sicssim.types.EventType;

/**
 * The future event class
 * 
 * @author Tallat
 *
 */
public class FutureEvent implements Comparable<FutureEvent> {

	public NodeId srcId;
	public NodeId destId;
	public long time; 
	public EventType type;
	public Object data;
	
	public Class nodeType;
	public Class linkType;
	
//----------------------------------------------------------------------------------
	/**
	 * Create an Event
	 * @param time time of event to trigger
	 * @param fromId source
	 * @param Id if event type is PERIODIC, then Id is the identifier for this event
	 * 			 else, Id is the id of the destination node
	 * @param type the event type
	 * @param data the event data
	 */
	FutureEvent(long time, NodeId srcId, NodeId destId, EventType type, Object data) {
		this.time = time;
		this.srcId = srcId;
		this.destId = destId;
		this.type = type;
		this.data = data;
	}

//----------------------------------------------------------------------------------
	/**
	 * Create an Event
	 * @param time time of event to trigger
	 * @param fromId source
	 * @param Id if event type is PERIODIC, then Id is the identifier for this event
	 * 			 else, Id is the id of the destination node
	 * @param type the event type
	 * @param data the event data
	 */
	FutureEvent(long time, NodeId srcId, NodeId destId, EventType type, Object data, Class nodeType, Class linkType) {
		this.time = time;
		this.srcId = srcId;
		this.destId = destId;
		this.type = type;
		this.data = data;
		this.nodeType = nodeType;
		this.linkType = linkType;
	}
	
	
//----------------------------------------------------------------------------------
	FutureEvent(EventType type, long time) {
		this.type = type;
		this.time = time;
	}
	
//----------------------------------------------------------------------------------
	public int compareTo(FutureEvent event) {
		if (this.time < event.time)
			return -1;
		else if (this.time > event.time)
			return 1;
		else 
			return 0;
	}
	
//----------------------------------------------------------------------------------
	public String toString() {
		String newLine = System.getProperty("line.separator");

		String str = newLine + "Event: " + newLine;
		str += newLine + "Type: \t" + this.type.ordinal();
		str += newLine + "Time: \t" + this.time;
		str += newLine + "From: \t" + this.srcId;
		str += newLine + "To: \t" + this.destId;
		str += newLine + "Data: \t" + this.data;
		str += newLine + newLine + "---" + newLine;
		
		return str;
	}
}
