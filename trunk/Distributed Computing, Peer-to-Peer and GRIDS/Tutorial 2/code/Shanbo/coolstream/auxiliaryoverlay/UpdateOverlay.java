package sicssim.coolstream.auxiliaryoverlay;

import java.util.Enumeration;

import sicssim.config.SicsSimConfig;
import sicssim.coolstream.peers.Peer;
import sicssim.network.AuxiliaryOverlay;
import sicssim.network.Bandwidth;
import sicssim.network.Network;
import sicssim.network.NodeId;

public class UpdateOverlay extends AuxiliaryOverlay {
//----------------------------------------------------------------------------------
	public void init(Network network, Bandwidth bandwidth) {
		super.init(network, bandwidth);
	}
	
//----------------------------------------------------------------------------------
	public void update(long currentTime) {
		NodeId nodeId;
		Enumeration<NodeId> nodeEnum = this.network.getNodeList().elements();
    	System.out.println("===> (clock = " + currentTime + ")");
		
		while (nodeEnum.hasMoreElements()) {
			nodeId = nodeEnum.nextElement();
			
			this.network.getNode(nodeId).updatePeer(null, currentTime);
		}
	}
	
//----------------------------------------------------------------------------------
	public void log(long currentTime) {
		NodeId nodeId;
		Enumeration<NodeId> nodeEnum = this.network.getNodeList().elements();
		
		while (nodeEnum.hasMoreElements()) {
			Peer peer;
			nodeId = nodeEnum.nextElement();
			
			if (nodeId.equals(SicsSimConfig.ORIGIN_NODEID))
				continue;
			
			peer = (Peer)this.network.getNode(nodeId);
			System.out.println("---------------------------");
			System.out.println(peer.getId() + ":");
			System.out.println("playback point: " + peer.getPlaybackPoint() + ", missed segments: " + peer.getnumOfMissedSegments());
			System.out.println("upload partners: " + peer.getUploadPartnerList());
			System.out.println("download partners: " + peer.getDownloadPartnerList());
		}

	}
}
