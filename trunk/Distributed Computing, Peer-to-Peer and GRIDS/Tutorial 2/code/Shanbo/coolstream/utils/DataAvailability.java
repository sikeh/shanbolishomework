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
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, PartnerInfo> entry : dataAvailability.entrySet()) {
            PartnerInfo parterInfo = entry.getValue();
            if (parterInfo.bufferMap.contains(segment)) {
                int uploadBw = parterInfo.uploadBw;
                if (uploadBw > 0) {
                    list.add(entry.getKey());
                }
            }
        }
        Collections.shuffle(list);
        return new NodeId(list.get(0));
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
