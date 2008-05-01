package sicssim.network;

/**
 * This class represents a node in the system. It essentially contains
 * the node identifier and the ip address of the node. 
 * 
 * @author tallat
 *
 */
public class NodeId {

	public int id;
	public int ip;

//----------------------------------------------------------------------------------
	public NodeId(int id, int ip) {
		this.id = id;
		this.ip = ip;
	}
	
//----------------------------------------------------------------------------------
	public NodeId(String s) {
		this.id = Integer.parseInt(s.substring(0, s.indexOf("@")));
		this.ip = Integer.parseInt(s.substring(s.indexOf("@") + 1, s.length()));
	}
	
//----------------------------------------------------------------------------------
	public boolean equals(NodeId nodeId) {
		if (nodeId.id == id && nodeId.ip == ip)
			return true;
		
		return false;
	}
	
//----------------------------------------------------------------------------------
	public String toString() {
		return id + "@" + ip;
	}

//----------------------------------------------------------------------------------
	public int hashCode() {
		return (new Integer(id)).hashCode();
	}
}
