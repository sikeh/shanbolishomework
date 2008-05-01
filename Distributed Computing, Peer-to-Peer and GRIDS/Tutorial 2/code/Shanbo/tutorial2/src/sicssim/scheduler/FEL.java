package sicssim.scheduler;

import java.util.PriorityQueue;
import java.util.Queue;

public class FEL {
	private Queue<FutureEvent> futureEventList = new PriorityQueue<FutureEvent>();	

//----------------------------------------------------------------------------------
	public void addEvent(FutureEvent event) {
		this.futureEventList.add(event);
	}

//----------------------------------------------------------------------------------
	public int size() {
		return this.futureEventList.size();
	}

//----------------------------------------------------------------------------------
	public boolean hasEvent() {
		return !(this.futureEventList.isEmpty());
	}
	
//----------------------------------------------------------------------------------
	public FutureEvent peekLastEvent() {
		return this.futureEventList.peek();
	}
	
//----------------------------------------------------------------------------------
	public FutureEvent getLastEvent() {
		return this.futureEventList.poll();
	}
}
