package sicssim.coolstream.peers;

import java.util.*;
import java.util.logging.Logger;

import sicssim.config.SicsSimConfig;
import sicssim.coolstream.types.MembershipMessage;
import sicssim.coolstream.types.PartnerInfo;
import sicssim.coolstream.utils.Broadcast;
import sicssim.coolstream.utils.Buffer;
import sicssim.links.AbstractLink;
import sicssim.network.Network;
import sicssim.network.NodeId;
import sicssim.peers.BandwidthPeer;
import sicssim.peers.PeerEventListener;
import sicssim.types.Data;
import sicssim.types.EventType;

public class OriginNode extends BandwidthPeer {
    Logger logger = Logger.getLogger(OriginNode.class.getName());
    private Buffer buffer = new Buffer();
    private Map<String, Integer> mCache = new LinkedHashMap<String, Integer>();
    public static List<NodeId> peers = new LinkedList<NodeId>();
    private int memberMsgSeqNum = 0;

    public static List<Peer> myPeers = new LinkedList<Peer>();

    

    


    public OriginNode() {
        super();
        peers.add(new NodeId(SicsSimConfig.ORIGIN_NODEID.toString()));
    }

    //----------------------------------------------------------------------------------
    public void init(NodeId nodeId, AbstractLink link, Network network) {
        super.init(SicsSimConfig.ORIGIN_NODEID, link, network, SicsSimConfig.MEDIA_SERVER_UPLOAD_BANDWIDTH, SicsSimConfig.MEDIA_SERVER_DOWNLOAD_BANDWIDTH);
    }

    //----------------------------------------------------------------------------------
    public void create(long currentTime) {
        //TODO:
        //The origin node is the first node in the system, and this the first method called by simulator for this node.
        for (int i = 0; i < SicsSimConfig.BUFFER_SIZE; i++) {
            this.buffer.addSegment(i);
        }

        // triger periodical event
        Data msg = new Data();
        msg.type = EventType.SEND_MEMBERSHIP_MSG;
        this.loopback(msg, SicsSimConfig.MEMBERSHIP_MSG_PERIOD);

        Data msg2 = new Data();
        msg2.type = EventType.SEND_BUFFER_MAP;
        this.loopback(msg2, SicsSimConfig.BUFFER_MAP_PERIOD);
    }

    //----------------------------------------------------------------------------------
    public void join(long currentTime) {
    }

    //----------------------------------------------------------------------------------
    public void leave(long currentTime) {
    }

    //----------------------------------------------------------------------------------
    public void failure(NodeId failedId, long currentTime) {
        //TODO:
        //This method is called by simulator for all peers.
        String node = failedId.toString();
        mCache.remove(node);
    }

    //----------------------------------------------------------------------------------
    public void receive(NodeId srcId, Object data, long currentTime) {
        Data msg = (Data) data;

        if (listeners[msg.type.ordinal()] != null)
            ((PeerEventListener) listeners[msg.type.ordinal()]).receivedEvent(srcId, data);
    }

    //----------------------------------------------------------------------------------
    public void updatePeer(Object data, long currentTime) {
        if (this.buffer.getLastSegment() < SicsSimConfig.MEDIA_SIZE)
            this.buffer.update();
    }

    //----------------------------------------------------------------------------------
    private void handleSendBufferMap(NodeId id) {
        //TODO:

        //Here the peer should multicast its buffer map and its upload bandwidth to
        //the peers in its partner list.
//        SortedSet<Integer> sortedSet = new TreeSet<Integer>();
//        // if requestion node is in my partnet list
//        if (mCache.containsKey(id)) {
//            // find out the requesting node is the k-th partnet
//            int k = 0;
//            for (String node : mCache.keySet()) {
//                if (node.equals(id.toString())) {
//                    break;
//                } else {
//                    k++;
//                }
//            }
//            // origin node has M partners will request seqment i from the origin node
//            for (int i = 0; i < SicsSimConfig.BUFFER_SIZE; i++) {
//                if (i % mCache.size() == k) {
//                    sortedSet.add(i);
//                }
//            }
//        }
//        PartnerInfo parterInfo = new PartnerInfo(sortedSet, this.getUploadBandwidth());
        PartnerInfo parterInfo = new PartnerInfo(buffer.getBufferMap(), this.getAvailableUploadBandwidth());
        Data msg = new Data();
        msg.type = EventType.BUFFER_MAP;
        msg.data = parterInfo;
        Broadcast.multicast(msg, mCache.keySet(), this);

        // periodical event
        Data msg2 = new Data();
        msg2.type = EventType.SEND_BUFFER_MAP;
        this.loopback(msg2, SicsSimConfig.BUFFER_MAP_PERIOD);
    }

    //----------------------------------------------------------------------------------
    private void handleSendMembershipMsg() {
        //TODO:
        //Here the peer should broadcast the membership message to all peers in system.
        MembershipMessage memMsg = new MembershipMessage(memberMsgSeqNum, this.getId());
        Data msg = new Data();
        msg.type = EventType.MEMBERSHIP_MSG;
        msg.data = memMsg;
        Broadcast.broadcast(msg, this.network, this);
        memberMsgSeqNum++;

        // perodical event
        Data msg2 = new Data();
        msg2.type = EventType.SEND_MEMBERSHIP_MSG;
        this.loopback(msg2, SicsSimConfig.MEMBERSHIP_MSG_PERIOD);
    }

    //----------------------------------------------------------------------------------
    private void handleRecvMembershipMsg(Object data) {
        //TODO:
        //This method is called when a peer receives the membership messages of other peers.
        Data msg = (Data) data;
        MembershipMessage memMsg = (MembershipMessage) msg.data;
        NodeId srcNode = memMsg.id;
        // does not add myself to my mCache
        if (srcNode.equals(this.getId())) {
            return;
        }
        int timeStamp = memMsg.seqNum;
        // sikeh: must store NodeId.toString()
        String node = srcNode.toString();
        if (!mCache.containsKey(node)) {
            mCache.put(node, timeStamp);
        }
        // filter out old membership message
        if (mCache.get(node) < timeStamp) {
            mCache.put(node, timeStamp);
        }
//        if (!peers.contains(srcNode)) {
//            peers.add(srcNode);
//        }
    }

    //----------------------------------------------------------------------------------
    private void handleStartPushSegment(NodeId destId, Object data) {
        Data msg = (Data) data;
        String segmentData = (String) msg.data;
        String dest = segmentData.substring(0, segmentData.indexOf("-"));
        String segment = segmentData.substring(segmentData.indexOf("-") + 1, segmentData.length());

        if (this.startSendData(new NodeId(dest), segment) == false) {
            Data retryMsg = new Data();
            retryMsg.type = EventType.PULL_SEGMENT;
            retryMsg.data = new String(segmentData);
            this.loopback(retryMsg, SicsSimConfig.RETRY);
        } else {
            int delay = (SicsSimConfig.SEGMENT_RATE * SicsSimConfig.ONE_SECOND) / (this.uploadBandwidth - this.bandwidth.getTotalUploadBandwidth(this.nodeId) + SicsSimConfig.SEGMENT_RATE);
            Data stopPushMsg = new Data();
            stopPushMsg.type = EventType.STOP_PUSH_SEGMENT;
            stopPushMsg.data = new String(segmentData);
            this.loopback(stopPushMsg, delay);

        }
    }

    //----------------------------------------------------------------------------------
    private void handleStopPushSegment(NodeId destId, Object data) {
        Data msg = (Data) data;
        String segmentData = (String) msg.data;
        String dest = segmentData.substring(0, segmentData.indexOf("-"));
        String segment = segmentData.substring(segmentData.indexOf("-") + 1, segmentData.length());

        this.stopSendData(new NodeId(dest), segment);
    }

    //----------------------------------------------------------------------------------
    public void registerEvents() {
        super.addEventListener(EventType.SEND_BUFFER_MAP, new PeerEventListener() {
            public void receivedEvent(NodeId srcId, Object data) {
                handleSendBufferMap(srcId);
            }
        });

        super.addEventListener(EventType.SEND_MEMBERSHIP_MSG, new PeerEventListener() {
            public void receivedEvent(NodeId srcId, Object data) {
                handleSendMembershipMsg();
            }
        });

        super.addEventListener(EventType.MEMBERSHIP_MSG, new PeerEventListener() {
            public void receivedEvent(NodeId srcId, Object data) {
                handleRecvMembershipMsg(data);
            }
        });

        super.addEventListener(EventType.PULL_SEGMENT, new PeerEventListener() {
            public void receivedEvent(NodeId srcId, Object data) {
                handleStartPushSegment(srcId, data);
            }
        });

        super.addEventListener(EventType.STOP_PUSH_SEGMENT, new PeerEventListener() {
            public void receivedEvent(NodeId srcId, Object data) {
                handleStopPushSegment(srcId, data);
            }
        });
    }
}
