package sicssim.links;

import sicssim.config.SicsSimConfig;
import sicssim.network.CoreNet;
import sicssim.network.NodeId;
import sicssim.types.EventType;

public class ReliableLink extends AbstractLink {
	
//----------------------------------------------------------------------------------
	public void send(NodeId destId, Object data) {
		this.sendData(this.totalLatency(destId), this.nodeId, destId, EventType.MSG, data);
	}

//----------------------------------------------------------------------------------
	public void sendDelay(NodeId destId, Object data, int delay) {
		this.sendData(this.totalLatency(destId) + delay, this.nodeId, destId, EventType.MSG, data);
	}

//----------------------------------------------------------------------------------	
	private int totalLatency(NodeId destId) {
		int srcLinkLatency = this.linkLatency;

		if (this.network.getNode(destId) != null) {
			int destLinkLatency = this.network.getNode(destId).getLinkLatency();
			int netLatency = CoreNet.getNetLatency(nodeId, destId);
			
			return (srcLinkLatency + destLinkLatency + netLatency);
		}
		else 
			return SicsSimConfig.FAILURE_DETECTOR_MAX_TIME;
	}
	
//----------------------------------------------------------------------------------
	/**
	 * Sends a message to another peer
	 * @param time time at which it will be delivered to the destination
	 * @param fromId source node
	 * @param toId destination node
	 * @param type type of message
	 * @param data message payload
	 */
	private void sendData(long time, NodeId srcId, NodeId destId, EventType type, Object data) {
		this.sim.addEvent(time, srcId, destId, type, data);				
	}
}
