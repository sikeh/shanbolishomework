package sicssim.coolstream.utils;

import java.util.*;

import sicssim.config.SicsSimConfig;
import sicssim.coolstream.types.PartnerInfo;
import sicssim.network.NodeId;

public class DataAvailability {
    private HashMap<String, PartnerInfo> dataAvailability = new HashMap<String, PartnerInfo>();

    //----------------------------------------------------------------------------------
    public void put(String node, PartnerInfo info) {
        this.dataAvailability.put(node, info);
    }

    //----------------------------------------------------------------------------------
    public int size() {
        return this.dataAvailability.size();
    }

    //----------------------------------------------------------------------------------
    public boolean containsSegment(int segment) {
        //TODO:
        //To get a better result you can modify this method also.

        String node;
        Iterator<String> nodesIter;

        nodesIter = this.dataAvailability.keySet().iterator();
        while (nodesIter.hasNext()) {
            node = nodesIter.next();
            if ((new Random()).nextInt(10) % 2 == 0 && node.equalsIgnoreCase(SicsSimConfig.ORIGIN_NODEID.toString()))
                continue;

            if (this.dataAvailability.get(node).bufferMap.contains(new Integer(segment)))
                return true;
        }

        return false;
    }

    //----------------------------------------------------------------------------------
    public NodeId findSupplier(int segment) {
        //TODO:
        //You should find a supplier for the segment. Better heuristic better result ;)

        /**
         * Among the multiple potential suppliers, the one with the highest bandwidth.
         */
        NodeId candidate = null;
        int bandwidth = -1;

        for (Map.Entry<String, PartnerInfo> entry : dataAvailability.entrySet()) {
            String node = entry.getKey();
            PartnerInfo parterInfo = entry.getValue();
            NodeId aNode = new NodeId(node);
            if (parterInfo.bufferMap.contains(segment)) {
                if (parterInfo.uploadBw > bandwidth) {
                    candidate = aNode;
                }
            }
        }
        return candidate;
    }

    //----------------------------------------------------------------------------------
    public Set<String> getSupplier() {
        return this.dataAvailability.keySet();
    }

    //----------------------------------------------------------------------------------
    public String toString() {
        return this.dataAvailability.toString();
    }

}
