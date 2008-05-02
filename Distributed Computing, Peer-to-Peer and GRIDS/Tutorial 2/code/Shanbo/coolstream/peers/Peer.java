package sicssim.coolstream.peers;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import sicssim.config.SicsSimConfig;
import sicssim.coolstream.types.MembershipMessage;
import sicssim.coolstream.types.PartnerInfo;
import sicssim.coolstream.utils.Broadcast;
import sicssim.coolstream.utils.Buffer;
import sicssim.coolstream.utils.DataAvailability;
import sicssim.links.AbstractLink;
import sicssim.network.Network;
import sicssim.network.NodeId;
import sicssim.peers.BandwidthPeer;
import sicssim.peers.PeerEventListener;
import sicssim.types.Data;
import sicssim.types.EventType;

public class Peer extends BandwidthPeer implements Comparable<Peer> {
    public static final Logger logger = Logger.getLogger(Peer.class.getName());

    private Buffer buffer = new Buffer();
    private HashMap<String, Integer> mCache = new HashMap<String, Integer>();
    private DataAvailability dataAvailability;

    private long recvDataTime = Long.MAX_VALUE;
    private int memberMsgSeqNum = 0;
    private boolean firstRecv = false;

    private long currentTime = Long.MAX_VALUE;


    static {
        logger.setLevel(Level.ALL);
    }

    //Shanbo: add these fields for scheduling
    /**
     * Store partners
     */
    private List<NodeId> partners = new LinkedList<NodeId>();
    private NodeId supplier;

    //Shanbo: ------------begin of new method------------
    /**
     * Get buffer
     *
     * @return buffer
     */
    public Buffer getBuffer() {
        return buffer;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public long getDeadLine(int segment) {
        return segment * 10 - currentTime;
    }

    //Shanbo: ------------end of new mothod--------------

    //----------------------------------------------------------------------------------

    public void init(NodeId nodeId, AbstractLink link, Network network) {
        super.init(nodeId, link, network);

        dataAvailability = new DataAvailability(this);
    }

    //----------------------------------------------------------------------------------
    public void create(long currentTime) {
    }

    //----------------------------------------------------------------------------------
    public void join(long currentTime) {
        //TODO:
        //When the peer joins to the system, this method is called by simulator.
        Data msg = new Data();
        msg.type = EventType.SEND_MEMBERSHIP_MSG;
        this.sendControlData(SicsSimConfig.ORIGIN_NODEID, msg);


        msg = new Data();
        msg.type = EventType.SCHEDULING;
        this.loopback(msg, SicsSimConfig.SCHEDULING_PERIOD);

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

        if (this.firstRecv == false && msg.type == EventType.STOP_RECV_DATA) {
            this.firstRecv = true;
            this.recvDataTime = currentTime;
        }

        if (listeners[msg.type.ordinal()] != null)
            ((PeerEventListener) listeners[msg.type.ordinal()]).receivedEvent(srcId, data);
    }

    //----------------------------------------------------------------------------------
    public int getnumOfMissedSegments() {
        return this.buffer.numOfMissedSegments();
    }

    //----------------------------------------------------------------------------------
    public int getPlaybackPoint() {
        return this.buffer.getPlaybackPoint();
    }

    //----------------------------------------------------------------------------------
    public Set<String> getUploadPartnerList() {
        return this.mCache.keySet();
    }

    //----------------------------------------------------------------------------------
    public Set<String> getDownloadPartnerList() {
        return this.dataAvailability.getSupplier();
    }

    //----------------------------------------------------------------------------------
    public void updatePeer(Object data, long currentTime) {
        if (currentTime - this.recvDataTime >= SicsSimConfig.BUFFERING_TIME) {
            if (this.buffer.updatePlaybackPoint())
                this.buffer.countNumOfMissedSegments();
        }
        this.currentTime = currentTime;
    }

    //----------------------------------------------------------------------------------
    public void pullSegment(NodeId partner, int segment) {
        Data msg = new Data();
        msg.type = EventType.PULL_SEGMENT;
        msg.data = new String(this.nodeId + "-" + Integer.toString(segment));
        this.sendControlData(partner, msg);
    }

    //----------------------------------------------------------------------------------
    private void handleScheduling() {
        //TODO:
        //You should define from which peer, which segment should be fetched.
        //Hint: don't forget to call loopback method at the end of this module for this method.
        //As a sample the loopback method can be as follow:

        //Shanbo: add the Scheduling Algorithm
        //local field, use for get partner
        String [] suppliers = this.dataAvailability.findSupplier();
        for (int i = this.getPlaybackPoint(); i < SicsSimConfig.MEDIA_SIZE ;i++){
            if (suppliers[i] != null) {
                pullSegment(new NodeId(suppliers[i]),i );
                logger.info("pull segment \""+ i+ " from " + suppliers[i]);
            }
        }

        Data msg = new Data();
        msg.type = EventType.SCHEDULING;
        this.loopback(msg, SicsSimConfig.SCHEDULING_PERIOD);
    }

    //----------------------------------------------------------------------------------
    private void handleSendBufferMap() {
        //TODO:
        //Here the peer should multicast its buffer map and its upload bandwidth to
        //the peers in its partner list.
        PartnerInfo parterInfo = new PartnerInfo(buffer.getBufferMap(), this.getUploadBandwidth());
        Data msg = new Data();
        msg.type = EventType.SEND_BUFFER_MAP;
        msg.data = parterInfo;
        Broadcast.multicast(msg, mCache.keySet(), this);
    }

    //----------------------------------------------------------------------------------
    private void handleRecvBufferMap(NodeId srcId, Object data) {
        //TODO:
        //This method is called when a peer receives the buffer map of other peers.
        Data msg = (Data) data;
        PartnerInfo parterInfo = (PartnerInfo) msg.data;
        dataAvailability.put(srcId.toString(), parterInfo);
    }

    //----------------------------------------------------------------------------------
    private void handleSendMembershipMsg() {
        //TODO:
        //Here the peer should broadcast the membership message to all peers in system.
        MembershipMessage memMsg = new MembershipMessage(memberMsgSeqNum, this.getId());
        Data msg = new Data();
        msg.type = EventType.MEMBERSHIP_MSG;
        msg.data = memMsg;
//        logger.info("Node "+ this.nodeId.id + ": "+ this.network.size()+"");
        Broadcast.broadcast(msg, this.network, this);
        memberMsgSeqNum++;
    }

    //----------------------------------------------------------------------------------
    private void handleRecvMembershipMsg(Object data) {
        //TODO:
        //This method is called when a peer receives the membership messages of other peers.
        Data msg = (Data) data;
        MembershipMessage memMsg = (MembershipMessage) msg.data;
        NodeId srcNode = memMsg.id;
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
    }

    //----------------------------------------------------------------------------------
    private void handleRecvSegment(NodeId srcId, Object data) {
    }

    //----------------------------------------------------------------------------------
    private void handleFinishRecvSegment(NodeId srcId, Object data) {
        Data msg = (Data) data;
        String segment = (String) msg.data;

        this.buffer.addSegment(Integer.parseInt(segment));
        //System.out.println(this.nodeId + ", my buffer: " + this.buffer);

        Data bufferMsg = new Data();
        bufferMsg.type = EventType.SEND_BUFFER_MAP;
        this.loopback(bufferMsg, SicsSimConfig.BUFFER_MAP_PERIOD);
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
        super.addEventListener(EventType.SCHEDULING, new PeerEventListener() {
            public void receivedEvent(NodeId srcId, Object data) {
                handleScheduling();
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


        super.addEventListener(EventType.SEND_BUFFER_MAP, new PeerEventListener() {
            public void receivedEvent(NodeId srcId, Object data) {
                handleSendBufferMap();
            }
        });

        super.addEventListener(EventType.BUFFER_MAP, new PeerEventListener() {
            public void receivedEvent(NodeId srcId, Object data) {
                handleRecvBufferMap(srcId, data);
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

        super.addEventListener(EventType.START_RECV_DATA, new PeerEventListener() {
            public void receivedEvent(NodeId srcId, Object data) {
                handleRecvSegment(srcId, data);
            }
        });

        super.addEventListener(EventType.STOP_RECV_DATA, new PeerEventListener() {
            public void receivedEvent(NodeId srcId, Object data) {
                handleFinishRecvSegment(srcId, data);
            }
        });

    }

    public int compareTo(Peer that) {
        if (this.getUploadBandwidth() > that.getUploadBandwidth()) {
            return 1;
        } else {
            return -1;
        }
    }
}
