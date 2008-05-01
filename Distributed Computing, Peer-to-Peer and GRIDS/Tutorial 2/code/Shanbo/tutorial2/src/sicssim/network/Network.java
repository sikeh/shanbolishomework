package sicssim.network;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import sicssim.config.SicsSimConfig;
import sicssim.peers.AbstractPeer;
import sicssim.utils.*;


public class Network {
	private Bandwidth bandwidth;
	private Hashtable<Integer, Integer> idIpList = new Hashtable<Integer, Integer>();
	private Hashtable<String, AbstractPeer> network = new Hashtable<String, AbstractPeer>();
	
	// Random number generator
	private RandomSet randomSet = new RandomSet(SicsSimConfig.MAX_NODE, SicsSimConfig.SEED);
	
	// Random IP generator
	private Random randomIp = new Random(7 * SicsSimConfig.SEED);
	
//----------------------------------------------------------------------------------
	public Network(Bandwidth bandwidth) {
		this.bandwidth = bandwidth;
	}

//----------------------------------------------------------------------------------
	public void addNode(NodeId nodeId, AbstractPeer node) {
		this.network.put(nodeId.toString(), node);
		this.idIpList.put(new Integer(nodeId.id), new Integer(nodeId.ip));
	}
	
//----------------------------------------------------------------------------------
	public void removeNode(NodeId nodeId) {
		this.network.remove(nodeId.toString());
		this.idIpList.remove(new Integer(nodeId.id));
		this.bandwidth.removeNode(nodeId);
	}
	
//----------------------------------------------------------------------------------
	public AbstractPeer getNode(NodeId nodeId) {
		return this.network.get(nodeId.toString());
	}

//----------------------------------------------------------------------------------
	public boolean containsNode(NodeId nodeId) {
		return this.network.containsKey(nodeId.toString());
	}
	
//----------------------------------------------------------------------------------
	public long size() {
		return this.network.size();
	}
	
//----------------------------------------------------------------------------------
	private boolean containsId(int id) {
		return this.idIpList.containsKey(new Integer(id));
	}

//----------------------------------------------------------------------------------
	public Bandwidth getBandwidth() {
		return this.bandwidth;
	}
	
//----------------------------------------------------------------------------------
	public NodeId generateUniqeNodeId() {
		int ip = this.randomIp.nextInt(SicsSimConfig.MAX_NODE);
		int id = randomSet.addInt();
		
		while (this.containsId(id)) {
			randomSet.undoLastAdd();
			id = randomSet.addInt();
		}
		
		return new NodeId(id, ip);		
	}
	
//----------------------------------------------------------------------------------
    public NodeId getRandomNodeIdFromNetwork() {
		int count = 0;
		Random rand = new Random();
		int randomIndex = rand.nextInt(this.network.size());
		Enumeration<String> netEnum = this.network.keys();

		while (netEnum.hasMoreElements() && count < randomIndex) {
			netEnum.nextElement();
			count++;
		}
		
		return this.network.get(netEnum.nextElement()).getId();
    }

//----------------------------------------------------------------------------------
	public String toString() {
		String str = new String("Network: ");
		
		Enumeration<String> netEnum = this.network.keys();
		while (netEnum.hasMoreElements())
			str += (netEnum.nextElement() + " ");
		
		return str;
	}

//----------------------------------------------------------------------------------
	public Vector<NodeId> getNodeList() {

		Vector<NodeId> nodeList = new Vector<NodeId>();
		Integer id;
		Integer ip;
		
		Enumeration<Integer> idipListEnum = this.idIpList.keys();
		while(idipListEnum.hasMoreElements()) {
			id = idipListEnum.nextElement();
			ip = idIpList.get(id);
			nodeList.add(new NodeId(id, ip));
		}

		return nodeList;
	}
	
//----------------------------------------------------------------------------------
	public void updateNetwork(long currentTime) {
		Enumeration<String> netEnum = this.network.keys();
		while (netEnum.hasMoreElements())
			this.network.get(netEnum.nextElement()).updatePeer(null, currentTime);
	}
	
}
