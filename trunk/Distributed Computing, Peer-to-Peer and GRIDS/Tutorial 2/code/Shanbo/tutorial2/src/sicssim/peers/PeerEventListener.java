package sicssim.peers;

import java.util.EventListener;

import sicssim.network.NodeId;

public interface PeerEventListener extends EventListener {
    public void receivedEvent(NodeId source, Object data);
}