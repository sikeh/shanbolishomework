package sicssim.coolstream.types;

import sicssim.network.NodeId;

public class MembershipMessage {
	public int seqNum;
	public NodeId id;

//----------------------------------------------------------------------------------
	public MembershipMessage(int seqNum, NodeId id) {
		this.seqNum = seqNum;
		this.id = id;
	}
}
