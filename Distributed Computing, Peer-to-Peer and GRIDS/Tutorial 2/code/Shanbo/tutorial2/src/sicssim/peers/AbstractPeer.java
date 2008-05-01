package sicssim.peers;

import sicssim.links.AbstractLink;
import sicssim.network.Network;
import sicssim.network.NodeId;
import sicssim.types.Data;
import sicssim.types.EventType;

/**
 * Nodes simulated in the system have to implement this interface
 * @author tallat, amir and fatemeh
 *
 */
public abstract class AbstractPeer {
	protected NodeId nodeId;	
	protected AbstractLink link;
	protected Network network;
	
	// An array for listening to all event types that peer can listen to
	protected Object[] listeners = new Object[EventType.values().length];
	
//----------------------------------------------------------------------------------
	/**
	 * Initialize the peer.
	 */
	public void init(NodeId nodeId, AbstractLink link, Network network) {
		this.nodeId = nodeId;
		this.link = link;
		this.network = network;
		
		this.registerEvents();
	}

//----------------------------------------------------------------------------------
	/**
	 * Create
	 * @param seed seed used for random number generation
	 */
	public abstract void create(long currentTime);

//----------------------------------------------------------------------------------
	/**
	 * Join an existing overlay
	 * @param seed seed used for random number generation
	 */
	public abstract void join(long currentTime);

//----------------------------------------------------------------------------------
	/**
	 * Leave the overlay
	 *
	 */
	public abstract void leave(long currentTime);
	
//----------------------------------------------------------------------------------
	/**
	 * Notification that a node has failed. Remember that the node must have already 
	 * subscribed for the failure of this node.
	 * 
	 * @param failedId The node that has failed.
	 */
	public abstract void failure(NodeId failedId, long currentTime);
	
//----------------------------------------------------------------------------------
	/**
	 * Receive a message. Called by the simulator when a message is received
	 * for this peer. Note that this can be any message, e.g. an event delivered by 
	 * the simulator. In that case, the type of the message should be set accordingly
	 * @param fromId node id of message source
	 * @param data data with the message communicated
	 */
	public abstract void receive(NodeId srcId, Object data, long currentTime);
	
//----------------------------------------------------------------------------------
    public abstract void updatePeer(Object data, long currentTime);	

//----------------------------------------------------------------------------------
    protected abstract void registerEvents();	
	
//----------------------------------------------------------------------------------
	/**
	 * Add an event listener. This method will override the previous listener
     * @param eventType event to subscribe from
	 */
    protected void addEventListener(EventType eventType, PeerEventListener listener) {     
    	listeners[eventType.ordinal()] = listener;
    }

//----------------------------------------------------------------------------------
    /**
     * Remove an event listener
     * @param eventType event to unsubscribe from
     */
    protected void removeEventListener(EventType eventType) {     
    	listeners[eventType.ordinal()] = null;
    }
    
//----------------------------------------------------------------------------------
    public void sendControlData(NodeId destId, Data controlData) {
		this.link.send(destId, controlData);
	}

//----------------------------------------------------------------------------------
    protected void loopback(Data controlData, long time) {
		this.link.loopback(controlData, time);
	}
    
//----------------------------------------------------------------------------------
    protected void sendInstantControlData(Data controlData) {
		this.link.sendInstant(controlData);
	}    
    
//----------------------------------------------------------------------------------
	/**
	 * Get the identifier of this peer
	 * @return
	 */
	public NodeId getId() {
		return this.nodeId;
	}	

//----------------------------------------------------------------------------------
    public int getLinkLatency() {
    	return this.link.getLinkLatency();
    }
}
