package sicssim.config;

import sicssim.network.NodeId;

public class SicsSimConfig {
    // Simulator parameters
    public static final int SEED = 5;
    public static final int SIM_TIME = 1400;
    public static final long MAX_SIM_TIME = 500000;
    public static final boolean SYNC_UPDATE = false;
    public static final boolean AUXILIARY_OVERLAY = true;
    public static final NodeId SICSSIM_NODEID = new NodeId(-1, -1);

    // Simulator timing parameters
    public static final int PERIOD_TIME = 100;
    public static final int FAILURE_DETECTOR_TIME = 10;
    public static final int FAILURE_DETECTOR_MAX_TIME = 50;

    // Network parameters
    public static final int MAX_NODE = 1000;
    public static final int NETWORK_LATENCY = 2;
    public static final int NETWORK_LATENCY_DRIFT = 1;
    public static final int LINK_LATENCY = 2;

    // Bandwidth parameters
    public static final int UPLOAD_BW = 4;
    public static final int DOWNLOAD_BW = 10;
    public static final int SEGMENT_RATE = 128;
    public static final int MEDIA_SERVER_UPLOAD_BANDWIDTH = 8 * SicsSimConfig.SEGMENT_RATE;
    public static final int MEDIA_SERVER_DOWNLOAD_BANDWIDTH = 0;

    // Scenario parameters
    public static final long NUM_OF_EVENT = 20;
    public static final int EVENT_INTERVAL = 10;
    public static final int NUM_OF_JOIN = 1;
    public static final int NUM_OF_LEAVE = 0;
    public static final int NUM_OF_FAILURE = 0;

    // CoolStreaming parameters
    public static final int MEDIA_SIZE = 100;
    public static final int BUFFER_SIZE = 100;
    public static final int PARTNER_LIST_SIZE = 20;
    public static final int ONE_SECOND = 10;
    public static final NodeId ORIGIN_NODEID = new NodeId(0, 0);

//    // CoolStreaming timing parameters
//    public static final long WAIT = 20;
//    public static final long RETRY = 10;
//    public static final long BUFFERING_TIME = 10;
//    public static final long SCHEDULING_PERIOD = 30;
//    public static final long BUFFER_MAP_PERIOD = 30;
//    public static final long MEMBERSHIP_MSG_PERIOD = 30;

    // CoolStreaming timing parameters (task 1 -data delivery)
    public static final long WAIT = 20;
    public static final long RETRY = 10;
    public static final long BUFFERING_TIME = 10;
    public static final long SCHEDULING_PERIOD = 20;
    public static final long BUFFER_MAP_PERIOD = 20;
    public static final long MEMBERSHIP_MSG_PERIOD = 20;
}
