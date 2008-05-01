package sicssim.coolstream.utils;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import sicssim.config.SicsSimConfig;

public class Buffer {
	private SortedSet<Integer> buffer = new TreeSet<Integer>();
	private int playbackPoint = 0;
	private int missedSegments = 0;
	private boolean hasData = false;
	private int segmentCounter = 0;
	private int playbackPointerCounter = 0;
	
//----------------------------------------------------------------------------------
	public void addSegment(int segment) {
		this.hasData = true;
		
		if (this.buffer.size() > SicsSimConfig.BUFFER_SIZE)
			this.buffer.remove(this.buffer.first());
		
		this.buffer.add(new Integer(segment));
	}

//----------------------------------------------------------------------------------
	public int getLastSegment() {
		return this.buffer.last();
	}

//----------------------------------------------------------------------------------
	public void update() {
		if (this.getLastSegment() < SicsSimConfig.MEDIA_SIZE) {
			this.segmentCounter++;
			if (this.segmentCounter % SicsSimConfig.ONE_SECOND == 0) {
				this.segmentCounter = 0;
				this.addSegment(this.getLastSegment() + 1);
			}
		}
	}

//----------------------------------------------------------------------------------
	public boolean updatePlaybackPoint() {
		if (this.playbackPoint < SicsSimConfig.MEDIA_SIZE) {
			this.playbackPointerCounter++;
			if (this.playbackPointerCounter % SicsSimConfig.ONE_SECOND == 0) {
				playbackPointerCounter = 0;
				this.playbackPoint++;
				return true;
			}
		}
		
		return false;
	}

//----------------------------------------------------------------------------------
	public boolean containsSegment(int segment) {
		return (this.buffer.contains(new Integer(segment)));
	}

//----------------------------------------------------------------------------------
	public boolean hasData() {
		return this.hasData;
	}
	
//----------------------------------------------------------------------------------
	public int getPlaybackPoint() {
		return this.playbackPoint;
	}

//----------------------------------------------------------------------------------
	public SortedSet<Integer> getBufferMap() {
		return this.buffer;
	}

//----------------------------------------------------------------------------------
	public void countNumOfMissedSegments() {
		if (!this.buffer.contains(this.playbackPoint - 1))
			this.missedSegments++;
	}
	
//----------------------------------------------------------------------------------
	public int numOfMissedSegments() {
		return this.missedSegments;
	}

//----------------------------------------------------------------------------------
	public String toString() {
		String str = new String("[");
		Integer segment;
		Iterator<Integer> bufferIter = this.buffer.iterator();
		
		while (bufferIter.hasNext()) {
			segment = bufferIter.next();
			str += (segment + ", ");
		}
		str += "]";
		
		return str;
	}
}

