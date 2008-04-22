package org.kth.sim.chord;

public enum EventType {

    // It is important that the enums start from zero,
    // and are let to have the original values

    /**
     * for calling periodic stabilization
     */
    PERIODIC,

    /**
     * notify successor
     * [initiator's id, initiator's ip]
     */
    NOTIFY,

    FIND_SUCCESSOR,

    REPLY_FIND_SUCCESSOR,

    CLOSEST_PRECEDING_NODE,

    FIX_FINGERS,

    ASK_PREDECESSOR,
    
    REPLY_ASK_PREDECESSOR,

}
