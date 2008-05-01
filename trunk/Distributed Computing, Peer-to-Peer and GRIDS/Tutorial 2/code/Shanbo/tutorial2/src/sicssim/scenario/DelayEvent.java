package sicssim.scenario;

import sicssim.types.EventType;


public class DelayEvent extends ScenarioEvent {
	private int eventCount = 1;
	private int time;
	
//----------------------------------------------------------------------------------
	public DelayEvent(int time) {
		this.time = time;
	}
	
//----------------------------------------------------------------------------------
	public boolean hasNext() { 
		return (eventCount-- == 1); 
	}

//----------------------------------------------------------------------------------
	public Event nextEvent() {
		return new Event(EventType.DELAY, time);
	}
	
//----------------------------------------------------------------------------------
	public void undo() {
		
	}
}

