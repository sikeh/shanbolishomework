package sicssim.network;

import sicssim.config.SicsSimConfig;
import sicssim.utils.Distribution;

public class CoreNet {
	
//----------------------------------------------------------------------------------
	public static int getNetLatency(NodeId srcId, NodeId destId) {
		return (Distribution.normal(SicsSimConfig.NETWORK_LATENCY, 0.5, srcId.id + destId.id) + Distribution.uniform(SicsSimConfig.NETWORK_LATENCY_DRIFT));
	}
}
