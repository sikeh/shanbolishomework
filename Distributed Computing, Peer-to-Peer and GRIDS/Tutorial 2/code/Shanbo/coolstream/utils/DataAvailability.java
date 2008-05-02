package sicssim.coolstream.utils;

import java.util.*;

import sicssim.config.SicsSimConfig;
import sicssim.coolstream.types.PartnerInfo;
import sicssim.coolstream.peers.Peer;
import sicssim.network.NodeId;

public class DataAvailability {
    private Peer me;
    private HashMap<String, PartnerInfo> dataAvailability = new HashMap<String, PartnerInfo>();


    public DataAvailability(Peer me) {
        this.me = me;
    }

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
            if (node.equalsIgnoreCase(SicsSimConfig.ORIGIN_NODEID.toString()))
                continue;
//            if ((new Random()).nextInt(10) % 2 == 0 && node.equalsIgnoreCase(SicsSimConfig.ORIGIN_NODEID.toString()))
//                continue;
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

        Map<NodeSegmentPair, Long> t = new HashMap<NodeSegmentPair, Long>();

        for (int i = this.me.getPlaybackPoint(); i < SicsSimConfig.BUFFER_SIZE; i++) {
            if (!this.me.getBuffer().containsSegment(i)) {
                int n = 0;
                String node;
                Iterator<String> nodesIter;

                nodesIter = this.dataAvailability.keySet().iterator();
                while (nodesIter.hasNext()) {
                    node = nodesIter.next();
                    t.put(new NodeSegmentPair(node, i), me.getDeadLine(i) - me.getCurrentTime());
                    n = n + bm(node,i);


                }

            }
        }

//        int bandwidth = -1;
//
//        for (Map.Entry<String, PartnerInfo> entry : dataAvailability.entrySet()) {
//            int n = 0;
//            String node = entry.getKey();
//            PartnerInfo parterInfo = entry.getValue();
//            NodeId aNode = new NodeId(node);
//            if (parterInfo.bufferMap.contains(segment)) {
//                if (parterInfo.bufferMap.uploadBw > bandwidth) {
//                    candidate = aNode;
//
//                }
//            }
//        }
        return candidate;
    }

    /**
     * get dataAvailability
     *
     * @return dataAvailability
     */
    public HashMap<String, PartnerInfo> getDataAvailability() {
        return dataAvailability;
    }

    //----------------------------------------------------------------------------------
    public Set<String> getSupplier() {
        return this.dataAvailability.keySet();
    }

    //----------------------------------------------------------------------------------
    public String toString() {
        return this.dataAvailability.toString();
    }

    //Shanbo: add follow class/method(s)
    private int bm(String node, int segment) {
        if (this.dataAvailability.get(node).bufferMap.contains(new Integer(segment))) {
            return 1;
        } else {
            return 0;
        }
    }

    class NodeSegmentPair {
        private String node;
        private int segment;

        NodeSegmentPair(String node, int segment) {
            this.node = node;
            this.segment = segment;
        }

        public String getNode() {
            return node;
        }

        public int getSegment() {
            return segment;
        }

        public void setSegment(int segment) {
            this.segment = segment;
        }

        public void setNode(String node) {
            this.node = node;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NodeSegmentPair nodeSegmentPair = (NodeSegmentPair) o;

            if (segment != nodeSegmentPair.segment) return false;
            if (node != null ? !node.equals(nodeSegmentPair.node) : nodeSegmentPair.node != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (node != null ? node.hashCode() : 0);
            result = 31 * result + segment;
            return result;
        }
    }

}
