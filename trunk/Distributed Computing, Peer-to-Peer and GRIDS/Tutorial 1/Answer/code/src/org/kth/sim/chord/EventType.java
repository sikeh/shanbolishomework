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

    FIND_SUCC,

    FIND_PRED,

    ASK_PRED,

    REPLY_ASK_PRED,

    ASK_SUCC,

    REPLY_ASK_SUCC,


    

}
