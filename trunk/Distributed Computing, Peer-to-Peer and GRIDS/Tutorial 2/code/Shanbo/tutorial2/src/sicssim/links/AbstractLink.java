package sicssim.links;

import sicssim.config.SicsSimConfig;
import sicssim.network.CoreNet;
import sicssim.network.Network;
import sicssim.network.NodeId;
import sicssim.scheduler.SicsSimB;
import sicssim.types.EventType;

/**
 * The communication interface tied to the simulator for sending messages between nodes
 * @author tallat, amir and fatemeh
 *
 */
public abstract class AbstractLink {
	protected NodeId nodeId;
	protected SicsSimB sim;
	protected Network network;
	protected CoreNet coreNet;
	protected int linkLatency;
	
//----------------------------------------------------------------------------------
	public void init(NodeId nodeId, int linkLatency, SicsSimB sim, Network network, CoreNet coreNet) {
		this.nodeId = nodeId;
		this.sim = sim;
		this.network = network;
		this.coreNet = coreNet;
		this.linkLatency = linkLatency;
	}

//----------------------------------------------------------------------------------	
	/**
	 * Send a message to a node
	 * @param dest destination node
	 * @param data message to be sent to destination
	 */
	public abstract void send(NodeId destId, Object data);

//----------------------------------------------------------------------------------
	public abstract void sendDelay(NodeId destId, Object data, int delay);

//----------------------------------------------------------------------------------
	public void loopback(Object data, long time) {
		this.sim.addEvent(time, this.nodeId, this.nodeId, EventType.MSG, data);
	}

//----------------------------------------------------------------------------------
	public void sendInstant(Object data) {
		this.sim.addEvent(0, this.nodeId, SicsSimConfig.SICSSIM_NODEID, EventType.INSTANT_MSG, data);
	}

//----------------------------------------------------------------------------------	
	public int getLinkLatency() {
		return this.linkLatency;
	}
}
