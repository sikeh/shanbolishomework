package sicssim.scheduler;


import java.util.Enumeration;
import java.util.Random;

import sicssim.config.SicsSimConfig;
import sicssim.links.AbstractLink;
import sicssim.network.AuxiliaryOverlay;
import sicssim.network.Bandwidth;
import sicssim.network.CoreNet;
import sicssim.network.Network;
import sicssim.network.NodeId;
import sicssim.peers.*;
import sicssim.scenario.Scenario;
import sicssim.types.Data;
import sicssim.types.EventType;
import sicssim.utils.Distribution;

/**
 * The core class of simulator. It contains the methods
 * to handle join, leave and failure of nodes.
 * It also handles the bandwidth changines in system.
 * 
 * @author Amir Payberah
 * @author Fatemeh Rahimian
 *
 */
public class SicsSimB {
	
	private Bandwidth bandwidth = new Bandwidth();
	private Network network = new Network(bandwidth);
	private CoreNet coreNet = new CoreNet();
	private Scenario scenario = new Scenario();
	private FEL futureEventList = new FEL();
	private Scheduler scheduler = new Scheduler(this.scenario, this.futureEventList);
	private AuxiliaryOverlay auxiliaryOverlay;
	
	private long overlayLastTime = -1;

//----------------------------------------------------------------------------------
	/**
	 * SicsSim constructor
	 */
	public SicsSimB() {
	}
	
//----------------------------------------------------------------------------------
	/**
	 * SicsSim constructor that accepts an object of Class as input.
	 *  
	 * @param auxiliaryOverlay An object of Class that we cast it to AuxiliaryOverlay class.
	 */
	public SicsSimB(Class auxiliaryOverlay) {
		try {
			this.auxiliaryOverlay = (AuxiliaryOverlay)auxiliaryOverlay.newInstance();
			this.auxiliaryOverlay.init(network, bandwidth);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
//----------------------------------------------------------------------------------
	/**
	 * It moves one step in future event list (FEL).
	 */
	public boolean singleStep() {
		if (SicsSimConfig.SYNC_UPDATE)
			this.network.updateNetwork(this.scheduler.getCurrentClock());
		
		if (SicsSimConfig.AUXILIARY_OVERLAY) {
			if (this.overlayLastTime < this.scheduler.getCurrentClock()) {
				this.auxiliaryOverlay.update(this.scheduler.getCurrentClock());
				this.overlayLastTime = this.scheduler.getCurrentClock();
			}
		}
		
		FutureEvent currentEvent = this.scheduler.nextStep();
		if (currentEvent != null)
			return doStep(currentEvent);
		
		return false;
	}
	
//----------------------------------------------------------------------------------
	/**
	 * It calls the singleStep as much as defined by till argument.
	 *  
	 * @param till Shows the number of times that singleStep will be called.
	 */
	public void runTill(long till) {
		while (this.scheduler.getCurrentClock() < till && singleStep());
		
		System.out.println("---------------------------");
		System.out.println("finish!");
		System.out.println(this.network.toString());
		this.auxiliaryOverlay.log(this.scheduler.getCurrentClock());
	}
	
//----------------------------------------------------------------------------------
	/**
	 * It receives an event as input and appropriate methods to handle it.
	 *  
	 * @param currentEvent It is an object of FutureEvent and shows the event receives by doStep method.
	 */
	private boolean doStep(FutureEvent currentEvent) {
		long eventTime = currentEvent.time;
		
		//System.out.println("sicssim: " + currentEvent.type);
		
		if (eventTime > SicsSimConfig.MAX_SIM_TIME) {
			System.out.println("SICSSIM: Simulation maximum time achieved!");
			return false;
		}
		
		if (currentEvent.type == EventType.MSG)
			handleMsgEvent(currentEvent);
		else if (currentEvent.type == EventType.INSTANT_MSG)
			handleInstantMsgEvent(currentEvent);
		else if (currentEvent.type == EventType.PERIODIC)
			handlePeriodicEvent(currentEvent);
		else if (currentEvent.type == EventType.JOIN)
			handleJoinEvent(currentEvent);
		else if (currentEvent.type == EventType.LEAVE)
			handleLeaveEvent(currentEvent);				
		else if (currentEvent.type == EventType.FAILURE)
			handleFailureEvent(currentEvent);				
		else if (currentEvent.type == EventType.DELAY)
			handleDelayEvent(currentEvent);
		else if (currentEvent.type == EventType.FAILURE_DETECTION)
			handleFailureDetectionEvent(currentEvent);
		
		return true; 
	}

//----------------------------------------------------------------------------------
	/**
	 * It handles the message event.
	 *  
	 * @param event It is an object of FutureEvent.
	 */
	private boolean handleMsgEvent(FutureEvent event) {
		AbstractPeer srcNode = this.network.getNode(event.srcId);
		AbstractPeer destNode = this.network.getNode(event.destId);

		if (destNode == null)			
			return false;

		if (((Data)event.data).type == EventType.START_RECV_DATA) {
			if (srcNode != null)
				this.bandwidth.useDownloadBandwidth(event.srcId, event.destId, SicsSimConfig.SEGMENT_RATE);
		} else if (((Data)event.data).type == EventType.STOP_RECV_DATA) {
			if (srcNode != null)
				this.bandwidth.releaseDownloadBandwidth(event.srcId, event.destId, SicsSimConfig.SEGMENT_RATE);
		}

		destNode.receive(event.srcId, event.data, this.scheduler.getCurrentClock());
		
		return true;
	}
	
//----------------------------------------------------------------------------------
	/**
	 * It handles the instant message event. Instant message is a type of message that is
	 * directly sends from peers to simulator.
	 *  
	 * @param event It is an object of FutureEvent.
	 */

	private void handleInstantMsgEvent(FutureEvent event) {
		AbstractPeer srcNode = this.network.getNode(event.srcId);
		
		if (srcNode == null)
			return;
	}
	
//----------------------------------------------------------------------------------
	/**
	 * It handles the periodic events.
	 *  
	 * @param event It is an object of FutureEvent that should be handle periodically.
	 */
	private boolean handlePeriodicEvent(FutureEvent event) {
		AbstractPeer destNode = this.network.getNode(event.srcId);
		
		if (destNode == null) 
			return false;

		addEvent(this.scheduler.getCurrentClock() + SicsSimConfig.PERIOD_TIME, event.srcId, event.destId, event.type, event.data);

		return true;		
	}

//----------------------------------------------------------------------------------
	/**
	 * It handles the join event.
	 *  
	 * @param nodeType It is an object of Class that we make an instance of AbstractPeer.
	 * @param linkType It is an object of Class that we make an instance of AbstractLink.
	 */
	private boolean handleJoinEvent(FutureEvent currentEvent) {
		Class nodeType = currentEvent.nodeType;
		Class linkType = currentEvent.linkType;
		
		try {
			AbstractPeer node = (AbstractPeer)nodeType.newInstance();
			AbstractLink link = (AbstractLink)linkType.newInstance();

			int linkLatency = Distribution.uniform(SicsSimConfig.LINK_LATENCY);
			NodeId nodeId = this.network.generateUniqeNodeId();

			node.init(nodeId, link, this.network);
			link.init(node.getId(), linkLatency, this, this.network, this.coreNet);

			if (this.network.size() == 0)
				node.create(this.scheduler.getCurrentClock());
			else
				node.join(this.scheduler.getCurrentClock());			

			this.network.addNode(node.getId(), node);
			System.out.println("SICSSIM: node " + node.getId() + " joins the system ---> time: " + this.scheduler.getCurrentClock());
		} catch (InstantiationException e) {
			System.err.println(e.getMessage());
			return false;
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
			return false;
		}
		
		return true;
	}
	
//----------------------------------------------------------------------------------
	/**
	 * It handles the leave event.
	 */
	private boolean handleLeaveEvent(FutureEvent currentEvent) {
		if (this.network.size() == 0) {
			System.out.println("SICSSIM: Trying to leave a node from an empty network in time: " + this.scheduler.getCurrentClock());
			return false;
		}

		NodeId nodeId = this.network.getRandomNodeIdFromNetwork();
		if (nodeId.id > 0 && nodeId.ip > 0) {		
			AbstractPeer node = this.network.getNode(nodeId);
			System.out.println("SICSSIM: node " + nodeId + " want to leave the system ---> time: " + this.scheduler.getCurrentClock());
			node.leave(this.scheduler.getCurrentClock());
		}

		return true;
	}
	
//----------------------------------------------------------------------------------
	/**
	 * It handles the failure event.
	 */
	private boolean handleFailureEvent(FutureEvent currentEvent) {
		if (this.network.size() == 0) {
			System.out.println("SICSSIM: Trying to fail a node from an empty network in time: " + this.scheduler.getCurrentClock());
			return false;
		}
			
		NodeId failedId = this.network.getRandomNodeIdFromNetwork();
		if (failedId.id > 0 && failedId.ip > 0) {		
			System.out.println("SICSSIM: node " + failedId + " is failed ---> time: " + this.scheduler.getCurrentClock());
			this.network.removeNode(failedId);
			
			NodeId destId;
			Enumeration<NodeId> nodeEnum = this.network.getNodeList().elements();
			while (nodeEnum.hasMoreElements()) {
				destId = nodeEnum.nextElement();
				this.addEvent(SicsSimConfig.FAILURE_DETECTOR_TIME + (new Random()).nextInt(SicsSimConfig.FAILURE_DETECTOR_TIME), SicsSimConfig.SICSSIM_NODEID, destId, EventType.FAILURE_DETECTION, failedId);
			}
		}
		
		return true;
	}
	
//----------------------------------------------------------------------------------
	/**
	 * It handles the delay event.
	 */
	private void handleDelayEvent(FutureEvent currentEvent) {		
	}

//----------------------------------------------------------------------------------
	private void handleFailureDetectionEvent(FutureEvent currentEvent) {
		NodeId destId = currentEvent.destId;
		NodeId failedId = (NodeId)currentEvent.data;
		
		if (this.network.getNode(destId) != null)
			this.network.getNode(destId).failure(failedId, this.scheduler.getCurrentClock());
	}
	
//----------------------------------------------------------------------------------
	/**
	 * Add an event
	 * @param time Time at which the event should occur
	 * @param fromId Source of the event
	 * @param toId Destination of the event
	 * @param type Type of the event
	 * @param data Data attached with the event
	 */
	public void addEvent(long time, NodeId srcId, NodeId destId, EventType type, Object data) {
		if (!srcId.equals(SicsSimConfig.SICSSIM_NODEID) && !destId.equals(SicsSimConfig.SICSSIM_NODEID)) {
			if (((Data)data).type == EventType.START_RECV_DATA)
				this.bandwidth.useUploadBandwidth(srcId, destId, SicsSimConfig.SEGMENT_RATE);
			else if (((Data)data).type == EventType.STOP_RECV_DATA)
				this.bandwidth.releaseUploadBandwidth(srcId, destId, SicsSimConfig.SEGMENT_RATE);
		}
		
		this.futureEventList.addEvent(new FutureEvent(this.scheduler.getCurrentClock() + time, srcId, destId, type, data));
	}	
}
