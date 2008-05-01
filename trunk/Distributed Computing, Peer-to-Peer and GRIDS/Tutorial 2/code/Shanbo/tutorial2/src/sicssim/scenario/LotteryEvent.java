 package sicssim.scenario;

import java.util.*;

import sicssim.config.SicsSimConfig;
import sicssim.types.EventType;
import sicssim.utils.Distribution;


public class LotteryEvent extends ScenarioEvent {
	
	private long count;
	private int deltaTime;
	private int numJoins;
	private int numLeaves;
	private int numFailures;
	
	private Class nodeType;
	private Class linkType;
	
	private Random rand = new Random(SicsSimConfig.SEED);
	private long eventCount = 0;
	
//----------------------------------------------------------------------------------
	/**
	 * Generate a new lottery event
	 * @param count total number of events u want to generate
	 * @param time time between two events
	 * @param joins ratio of # of joins u want out of the total lottery events(given as 'count')
	 * @param leaves ratio of # of leaves u want out of the total lottery events(given as 'count')
	 * @param failures ratio of # of leaves u want out of the total lottery events(given as 'count')
	 */
	public LotteryEvent(long count, int time, int joins, int leaves, int failures, Class nodeType, Class linkType) {
		this.count = count;
		this.deltaTime = time;
		this.numJoins = joins;
		this.numLeaves = leaves;
		this.numFailures = failures;
		this.nodeType = nodeType;
		this.linkType = linkType;
	}
	
//----------------------------------------------------------------------------------
	public boolean hasNext() {
		return (this.eventCount < this.count);
	}
 
//----------------------------------------------------------------------------------
	public Event nextEvent() {
		EventType eventType;
		int time = Distribution.exp(this.deltaTime);
		int randResult = this.rand.nextInt(this.numJoins + this.numLeaves + this.numFailures);
		
		if (randResult < this.numJoins)
			eventType = EventType.JOIN;
		else if (randResult < this.numJoins + this.numLeaves)
			eventType = EventType.LEAVE;
		else
			eventType = EventType.FAILURE;
		
		this.eventCount++;
		
		return new Event(eventType, time, null, null, this.nodeType, this.linkType);
	}	
	
//----------------------------------------------------------------------------------
	public void undo() {
		this.eventCount--;
	}
}
