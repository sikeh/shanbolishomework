package sicssim.coolstream.utils;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import sicssim.network.Network;
import sicssim.network.NodeId;
import sicssim.peers.BandwidthPeer;
import sicssim.types.Data;

public class Broadcast {
	public static void broadcast(Object data, Network network, BandwidthPeer peer) {
        NodeId nodeId;

        Data boradcastMsg = (Data)data;
        Enumeration<NodeId> nodeEnum = network.getNodeList().elements();

        while (nodeEnum.hasMoreElements()) {
        	nodeId = nodeEnum.nextElement();

        	if (nodeId.equals(peer.getId()))
        		continue;

        	peer.sendControlData(nodeId, boradcastMsg);
        }
		
	}
	
//----------------------------------------------------------------------------------
	public static void multicast(Object data, Set<String> group, BandwidthPeer peer) {
        String partner;

        Data multicastMsg = (Data)data;
		Iterator<String> mCacheIter = group.iterator();

		while (mCacheIter.hasNext()) {
			partner = mCacheIter.next();
			
			peer.sendControlData(new NodeId(partner), multicastMsg);
		}
	}
}
