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
import sicssim.network.Bandwidth;
import sicssim.peers.BandwidthPeer;
import sicssim.peers.PeerEventListener;
import sicssim.peers.AbstractPeer;
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

    private long currentTime = 0;


    static {
        logger.setLevel(Level.ALL);
    }


    //Shanbo: add these fields for scheduling
    /**
     * Store partners
     */
    private List<NodeId> partners = new LinkedList<NodeId>();
    private NodeId supplier;
    private int calcDelay;
    private String supplier0;
    private String supplier1;
    private AbstractPeer abstractPeer;
    private int index;

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
        return (segment - this.getPlaybackPoint()) * 10;
    }

    public Network getNetwork() {
        return this.network;
    }

    public Bandwidth getBandwidth() {
        return this.bandwidth;
    }

    //Shanbo: ------------end of new mothod--------------

    //----------------------------------------------------------------------------------

    public void init(NodeId nodeId, AbstractLink link, Network network) {
        super.init(nodeId, link, network);
//        mCache.put(SicsSimConfig.ORIGIN_NODEID.toString(), 0);
        dataAvailability = new DataAvailability(this);
//        System.out.println("### initial node " + nodeId + " bandwidth = " + this.bandwidth + "; uploadBandwidth = " + this.uploadBandwidth);

    }

    //----------------------------------------------------------------------------------
    public void create(long currentTime) {
    }

    //----------------------------------------------------------------------------------
    public void join(long currentTime) {
        //TODO:
        //When the peer joins to the system, this method is called by simulator.

        Data msg = new Data();
        msg.type = EventType.SCHEDULING;
        this.loopback(msg, SicsSimConfig.SCHEDULING_PERIOD);

        Data msg2 = new Data();
        msg2.type = EventType.SEND_MEMBERSHIP_MSG;
        this.loopback(msg2, SicsSimConfig.MEMBERSHIP_MSG_PERIOD);

        Data msg3 = new Data();
        msg3.type = EventType.SEND_BUFFER_MAP;
        this.loopback(msg3, SicsSimConfig.BUFFER_MAP_PERIOD);

        OriginNode.peers.add(nodeId);

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

        strategy1();




        Data msg = new Data();
        msg.type = EventType.SCHEDULING;
        this.loopback(msg, SicsSimConfig.SCHEDULING_PERIOD);
    }

    private void strategy2() {
        int index = OriginNode.peers.indexOf(this.nodeId);
        abstractPeer = this.network.getNode(OriginNode.peers.get(index - 1));
        if (abstractPeer instanceof Peer) {
            Peer peer = (Peer) abstractPeer;
            for (int i = this.getPlaybackPoint(); i < this.getPlaybackPoint() + 10; i++) {
//            for (int i = this.getPlaybackPoint(); i < this.getPlaybackPoint() + peer.getUploadBandwidth()/ SicsSimConfig.SEGMENT_RATE; i++) {
                if (!this.buffer.containsSegment(i)) {
                    pullSegment(OriginNode.peers.get(index - 1), i);
                    pullSegment(OriginNode.peers.get(index - 2), i);
                }
            }
        } else {
            OriginNode originNode = (OriginNode) abstractPeer;
            for (int i = this.getPlaybackPoint(); i < this.getPlaybackPoint() + 7; i++) {
                if (!this.buffer.containsSegment(i)) {
                    pullSegment(OriginNode.peers.get(index - 1), i);
                }
            }
        }
    }

    private void strategy1() {
        //Shanbo: add the Scheduling Algorithm
        //local field, use for get partner
        String[][] suppliers = this.dataAvailability.findSupplier();
//        for (int i = this.getPlaybackPoint(); i <= this.getPlaybackPoint() + 1;i++){
        int end;
        int start;
        if (this.getPlaybackPoint() < 5) {
            start = this.getPlaybackPoint();
            end = this.getPlaybackPoint() + 3;
        } else if (this.getPlaybackPoint() < 10) {
            start =this.getPlaybackPoint();
            end = this.getPlaybackPoint() + 10;
        } else if (this.getPlaybackPoint() < 20) {
            start = this.getPlaybackPoint();
            end = this.getPlaybackPoint() + 30;
        }

//        else if (this.getPlaybackPoint() < 10){
//            start  = 0;
//            end = this.getPlaybackPoint() + 24;
//        }
        else if (this.getPlaybackPoint() < 40) {
            start = 0;
            end = this.getPlaybackPoint() + 60;
        } else {
            start = this.getPlaybackPoint();
            end = SicsSimConfig.MEDIA_SIZE;
        }

        start = this.getPlaybackPoint();


        boolean ask = false;
        for (int j = this.getPlaybackPoint() + 1; j < this.getPlaybackPoint() + 10; j++) {
            if (!this.buffer.containsSegment(j)) {
                ask = true;
            }
        }

        for (int i = start; i <= end; i++) {
            if (suppliers[i] != null && !this.buffer.containsSegment(i)) {
                supplier0 = suppliers[i][0];
                if (supplier0 != null) {
                    pullSegment(new NodeId(supplier0), i);
                    System.out.println("Node " + this.nodeId + " : pull segment \"" + i + " from " + suppliers[i][0]);
                }
                supplier1 = suppliers[i][1];
                if (supplier1 != null) {
                    pullSegment(new NodeId(supplier1), i);
                    System.out.println("Node " + this.nodeId + " : pull segment \"" + i + " from " + suppliers[i][1]);
                }
//                System.out.println("Node " + this.nodeId + " : pull segment \"" + i + " from " + suppliers[i]);
                System.out.println("Node " + this.nodeId + " : playBackPoint = " + this.getPlaybackPoint());
            }
        }
    }

    //----------------------------------------------------------------------------------
    private void handleSendBufferMap() {
        //TODO:
        //Here the peer should multicast its buffer map and its upload bandwidth to
        //the peers in its partner list.
        PartnerInfo parterInfo = new PartnerInfo(buffer.getBufferMap(), this.getAvailableUploadBandwidth());
        Data msg = new Data();
        msg.type = EventType.BUFFER_MAP;
        msg.data = parterInfo;
        Broadcast.multicast(msg, mCache.keySet(), this);

        Data msg2 = new Data();
        msg2.type = EventType.SEND_BUFFER_MAP;
        this.loopback(msg2, SicsSimConfig.BUFFER_MAP_PERIOD);
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
        // ignore memebership message from myself
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
    }

    //----------------------------------------------------------------------------------
    private void handleRecvSegment(NodeId srcId, Object data) {
    }

    //----------------------------------------------------------------------------------
    private void handleFinishRecvSegment(NodeId srcId, Object data) {
        Data msg = (Data) data;
        String segment = (String) msg.data;

        this.buffer.addSegment(Integer.parseInt(segment));
//        System.out.println("***************Node " + this.nodeId + " : playBackPoint = " + this.getPlaybackPoint());
//        System.out.println("***************Node " + this.nodeId + " : finished receive segment " + segment);

        Data bufferMsg = new Data();
        bufferMsg.type = EventType.SEND_BUFFER_MAP;
        this.loopback(bufferMsg, 0);
//        this.loopback(bufferMsg, SicsSimConfig.BUFFER_MAP_PERIOD);
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
            int delay = calcDelay();
            Data stopPushMsg = new Data();
            stopPushMsg.type = EventType.STOP_PUSH_SEGMENT;
            stopPushMsg.data = new String(segmentData);
            this.loopback(stopPushMsg, delay);

        }
    }

    public int calcDelay() {
        return (SicsSimConfig.SEGMENT_RATE * SicsSimConfig.ONE_SECOND) / (this.uploadBandwidth - this.bandwidth.getTotalUploadBandwidth(this.nodeId) + SicsSimConfig.SEGMENT_RATE);
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

        //Shanbo:
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
        if (this.bandwidth.getTotalUploadBandwidth(this.nodeId) < (this.bandwidth.getTotalUploadBandwidth(that.nodeId))) {
            return 1;
        } else {
            return -1;
        }
    }
}
