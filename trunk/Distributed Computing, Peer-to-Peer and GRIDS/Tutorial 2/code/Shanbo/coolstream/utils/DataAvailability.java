package sicssim.coolstream.utils;

import java.util.*;

import sicssim.config.SicsSimConfig;
import sicssim.coolstream.types.PartnerInfo;
import sicssim.coolstream.peers.Peer;
import sicssim.network.NodeId;

public class DataAvailability {
    private Peer me;
    private static final int SEGMENT_SIZE = SicsSimConfig.SEGMENT_RATE * SicsSimConfig.ONE_SECOND;
    private HashMap<String, PartnerInfo> dataAvailability = new HashMap<String, PartnerInfo>();
    private Long aLong;
    private Long aLong2;
    private int anInt;
    private Long aLong3;


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
    public String[] findSupplier() {
        //TODO:
        //You should find a supplier for the segment. Better heuristic better result ;)

        /**
         * Among the multiple potential suppliers, the one with the highest bandwidth.
         */
        NodeId candidate = null;
        String[] suppliers = new String[1124];
        Map<NodeSegmentPair, Long> t = new HashMap<NodeSegmentPair, Long>();
        Map<Integer, Set<Integer>> dup_set = new LinkedHashMap<Integer, Set<Integer>>(1124);


        String k = SicsSimConfig.ORIGIN_NODEID.toString();

        for (int i = this.me.getPlaybackPoint(); i <= SicsSimConfig.MEDIA_SIZE; i++) {
            if (!this.me.getBuffer().containsSegment(i)) {
                int n = 0;
                String node;
                Iterator<String> nodesIter;
                nodesIter = this.dataAvailability.keySet().iterator();
                while (nodesIter.hasNext()) {
                    node = nodesIter.next();
                    t.put(new NodeSegmentPair(node, i), me.getDeadLine(i) - me.getCurrentTime());
                    n = n + bm(node, i);
                }
                if (n == 1) {
                    for (Map.Entry<String, PartnerInfo> entry : dataAvailability.entrySet()) {
                        PartnerInfo parterInfo = entry.getValue();
                        if (parterInfo.bufferMap.contains(i)) {
                            k = entry.getKey();
                        }
                    }
                    suppliers[i] = k;
                    for (int j = this.me.getPlaybackPoint(); j < SicsSimConfig.MEDIA_SIZE; j++) {
                        aLong = t.get(new NodeSegmentPair(k, j));
                        if (aLong != null) {
                            t.put(new NodeSegmentPair(k, j), aLong - SEGMENT_SIZE / dataAvailability.get(k).uploadBw);
                        }
                    }
                } else {
                    if (dup_set.get(n) == null) {
                        dup_set.put(n, new HashSet<Integer>());
                        dup_set.get(n).add(i);
                    } else {
                        dup_set.get(n).add(i);
                    }
                    suppliers[n] = null;
                }
            }
        }

        List<String> candidates = new LinkedList<String>();
        for (int n = 2; n < dataAvailability.keySet().size(); n++) {
            if (dup_set.get(n) != null) {
                for (Integer i : dup_set.get(n)) {
                    for (Map.Entry<String, PartnerInfo> entry : dataAvailability.entrySet()) {
                        PartnerInfo parterInfo = entry.getValue();
                        String node = entry.getKey();
                        anInt = SEGMENT_SIZE / parterInfo.uploadBw;
                        aLong3 = t.get(new NodeSegmentPair(node, i));
                        if (parterInfo.bufferMap.contains(i) && aLong3 > anInt) {
                            if (!candidates.contains(node)) {
                                candidates.add(node);
                            }
                        }
                    }
                    if (candidates.size() > 1) {
//                        Collections.shuffle(candidates);
//                        Collections.shuffle(candidates);
                        Collections.shuffle(candidates);
                        Collections.shuffle(candidates);
                        k = candidates.get(1);
//                        k = Collections.max(candidates);
                        suppliers[i] = k;
                        for (int j = this.me.getPlaybackPoint(); j < SicsSimConfig.MEDIA_SIZE; j++) {
                            aLong2 = t.get(new NodeSegmentPair(k, j));
                            if (aLong2 != null) {
                                t.put(new NodeSegmentPair(k, j), aLong2 - SEGMENT_SIZE / dataAvailability.get(k).uploadBw);
                            }
                        }
                    }
                }
            }
        }

        return suppliers;
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
