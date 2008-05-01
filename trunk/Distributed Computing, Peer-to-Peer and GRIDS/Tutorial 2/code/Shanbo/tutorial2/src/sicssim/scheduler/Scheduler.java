package sicssim.scheduler;

import sicssim.config.SicsSimConfig;
import sicssim.network.NodeId;
import sicssim.scenario.Scenario;
import sicssim.scenario.ScenarioEvent.Event;
import sicssim.types.EventType;

public class Scheduler {

	private Scenario scenario;
	private FEL futureEventList;
	private long clock = 0;
	private boolean firstTime = true;
	
//----------------------------------------------------------------------------------
	public Scheduler(Scenario scenario, FEL futureEventList) {
		this.scenario = scenario;
		this.futureEventList = futureEventList;
	}
	
//----------------------------------------------------------------------------------
	private boolean generateScenarioEvent() {
		if (this.scenario.hasNextEvent()) {
			Event event = this.scenario.nextEvent();
			this.futureEventList.addEvent(new FutureEvent(this.clock + event.time, new NodeId(-7,-7), new NodeId(-7,-7), event.type, null, event.nodeType, event.linkType));
			return true;
		}
		
		return false;
	}

//----------------------------------------------------------------------------------
	public FutureEvent nextStep() {
		if (this.firstTime) {
			this.generateScenarioEvent();
			this.firstTime = false;
		}				

		if (this.futureEventList.peekLastEvent() != null) {
			FutureEvent currentEvent = this.futureEventList.getLastEvent();
			long eventTime = currentEvent.time;

			if (this.clock != eventTime) 
				this.clock = eventTime;

			if (currentEvent.type == EventType.JOIN || currentEvent.type == EventType.LEAVE || currentEvent.type == EventType.FAILURE || currentEvent.type == EventType.DELAY)
				this.generateScenarioEvent();
		
			if (eventTime > SicsSimConfig.MAX_SIM_TIME) {
				System.out.println("Simulation maximum time achieved!");
				return null;
			}
			
			return currentEvent;
		}
		else
			return null;
	}

//----------------------------------------------------------------------------------
	public long getCurrentClock() {
		return this.clock;
	}	
}
