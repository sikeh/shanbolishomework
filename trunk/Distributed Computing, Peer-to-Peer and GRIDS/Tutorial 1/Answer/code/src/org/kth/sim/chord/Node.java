package org.kth.sim.chord;

import org.kth.sim.interfaces.PeerInterface;
import org.kth.sim.interfaces.CommInterface;
import org.kth.sim.util.MathMiscConstant;
import org.kth.sim.NodeId;
import org.kth.sim.SicsSim;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * NOTES:
 * 1) for simulation, you dont need to implement recursive calls
 * 2) after joining the overlay, add a periodic event for stabilization by using:
 * this.sim.addPeriodicEvent(stabilizeDelay, myid, new Message(EventType.PERIODIC, null));
 * 3) for closestPrecedingNode(int id), start searching from farthest finger coming to the first ie. ur successor
 * 4) whenever u update ur succ, pred, or a finger, then update your subscrition list by calling
 * subUpd(new value, previous value)
 * 5) for intervals checking, use
 * math.belongs*
 */

public class Node implements PeerInterface {
    public static final Logger logger = Logger.getLogger(Node.class.getName());
    public int N = 128 * 1024;
    private CommInterface com;
    public MathMiscConstant math = null;
    private Random rnd = null;
    public NodeId myid;

    // for finger table
    public NodeId[] fingers;
    public int m;    // size of the finger table
    // for successor list
    public NodeId[] successors;
    public int r = 7;    // size of the successor list  10->1024, 6->64 , 7->128, 8->256, 9->512

    public NodeId pred = new NodeId(-1, -1); //int pred = -1;
    public NodeId succ = new NodeId(-2, -1); //int succ = -2;

    private SicsSim sim;
    // The time delay used for periodic stabilization
    private final int stabilizeDelay = 10;
    private final int buildFingersDelay = 7 * stabilizeDelay;
    public int bfdelay = 0;

    private int next = 0;   // 1 <= next <= m

    // failed while join
    boolean haveJoined = false;
    int waited = 0;


    // hashtable for keeping track of nodes that i want to subscribe to (ie. to be notified when they fail) x=id, y=# of times subscribed
    public Hashtable<NodeId, Integer> subsc = new Hashtable<NodeId, Integer>(m); // just for the fingers!

    // An array for listening to all event types that chord can listen to
    // remember that only one listener is allowed for each type of event
    // can easily be extended but is not needed
    Object[] listeners = new Object[EventType.values().length];
    private static final int TIMEOUT_WAITS = 10;

    /**
     * General Notes:
     * 1) Not subscribing for failures of nodes in the successor list
     * 2) After failure of a node, successor list gets reconciled the next time periodic stabilization is done
     */
    public Node() {
//        logger.setLevel(Level.SEVERE);
        logger.setLevel(Level.OFF);
        haveJoined = true;
        this.sim = SicsSim.getInstance();
        // no event listener is subscribed yet
        for (int i = 0; i < listeners.length; i++)
            listeners[i] = null;
        // registering for events locally
        registerEvents();
        // casual stuff:
        this.N = this.sim.getN();
        this.math = new MathMiscConstant(N, 2);   // for chord, k=2
        this.m = (int) math.logk(N) + 1;
    }

    /**
     * When a node is loaded from persistence storage, this method should be used
     * for initializing its status
     */
    public void initialize(int N, int seed, CommInterface com) {
        if (N != this.N)
            System.err.println("Wrong initialization of Node");
        this.N = N;
        this.math = new MathMiscConstant(N, 2);   // for chord, k=2
        this.com = com;
        this.rnd = new Random(seed);

        this.m = (int) math.logk(N) + 1;

        //this.sim.addPeriodicEvent(stabilizeDelay, myid, /*networkid, */new Message(EventType.PERIODIC, null));
    }

    private void init(int N, int seed, NodeId/*int*/ id, CommInterface com) {

        this.N = N;
        this.math = new MathMiscConstant(N, 2);   // for chord, k=2
        this.myid = id;
//        System.out.println("********** myid: " + myid.id + ", ip:" + myid.ip);
        this.com = com;
        this.rnd = new Random(seed);

        this.m = (int) math.logk(N) + 1;
        this.fingers = new NodeId[m];
        this.successors = new NodeId[r];
        for (int i = 0; i < r; i++) {
            successors[i] = new NodeId(-1, -1);
        }

        // initializing fingers
        fingers[0] = myid;
        for (int i = 0; i < m; i++)
//            fingers[i] = new NodeId(-250, -1);
            fingers[i] = new NodeId(-1, -1);
    }

    public void create(int N, int seed, NodeId/*int*/ id, CommInterface com) {

        init(N, seed, id, com);

        succ = myid;
        pred = new NodeId(-1, -1);//myid;
//        status = Status.INSIDE;
        // build fingers!
        for (int i = 0; i < m; i++)
            fingers[i] = new NodeId(-1, -1);    // TODO sikeh: should it be null (instead of myid)?
        // build successor list!
        for (int i = 0; i < r; i++)
            successors[i] = new NodeId(-1, -1);   // TODO sikeh: should it be null (instead of myid)?

//        successors[0] = succ;

        // registering for periodic event with the simulator
        this.sim.addPeriodicEvent(stabilizeDelay, myid, /*networkid, */new Message(EventType.PERIODIC, null));
        //System.out.println("\t Node: "+myid+", i'm starting the ring. This shouldnt happen after merger");
    }

    public void join(int N, int seed, NodeId/*int*/ id, NodeId/*int*/ existingId, CommInterface com) {
        haveJoined = false;
        init(N, seed, id, com);

//        pred = null;
        pred = new NodeId(-1, -1);
        int[] data = new int[5];
        data[0] = 0; // find_successor from join
        //date[2] and data[3] store the initial findSuccessor id.
        data[1] = id.id; // param id
        data[2] = id.id; // init node id
        data[3] = id.ip; // init node ip
        data[4] = -1; // pos in fingers[], since it's not a join, make it -1
//        System.out.printf("%d: join, ask %d about who is my successor%n", id.id, existingId.id);
        Message msg = new Message(EventType.FIND_SUCCESSOR, data);
        com.send(existingId, msg);

        this.sim.addPeriodicEvent(stabilizeDelay, myid, /*networkid, */new Message(EventType.PERIODIC, null));
    }

    public void leave() {
        System.err.println("Leave should not have been called at " + myid);
    }

    public void failure(NodeId fid) {
//        System.out.println(myid.id + ": node " + fid.id + " fails");
        if (fid.id < 0) {
            System.err.println("Node " + myid + "[@" + sim.getClock() + "]: Invalid fail node id -> " + fid);
            return;
        }

        if (fid.equals(myid)) {        // if i m the one failing
            // unsubscribe from all events
            unsubsall();
            subsc.clear();
            return;
        }

        // TODO:
        // case 1: my successor failed ->
        //		1. replace succ by first 'alive' node in ur successor list
        //		2. update your first finger as it is the same as ur successor
        //		3. update your subscription list for failures
        if (fid.equals(succ)) {
            doUpdateMySuccessorList();
        }

        // case 2: one of my fingers failed ->
        //		1. set it to null and let periodic stabilization fix it
        //		2. update your subscription list for failures
        for (int i = 1; i < fingers.length; i++) {
            if (fid.equals(fingers[i])) {
                fingers[i] = new NodeId(-1, -1);
                break;
            }
        }

        // case 3: my predecessor failed ->
        //		1. set it to null and let periodic stabilization fix it
        //		2. update your subscription list for failures
        if (fid.equals(pred)) {
            pred = new NodeId(-1, -1);
        }

        // unsubscride from this node
        subsc.remove(fid);

    }

    private void doUpdateMySuccessorList() {
        for (NodeId aNode : successors) {
            if (aNode.id >= 0 && sim.isAlive(myid.id, aNode)) {
                succ = aNode;
                fingers[1] = succ;
                Message outMsg = new Message(EventType.ASK_SUCCESSOR_LIST, null);
                logger.info("ASK_SUCCESSOR_LIST");
                com.send(succ, outMsg);
                break;
            } else {
                subsc.remove(aNode);
            }
        }
    }

    /**
     * Periodically verify's immediate successor, and tells the successor about itself
     */
    private void stabilize() {
        logger.info("node " + myid.id + ": stabilize");

        if (haveJoined) {
            //TODO check if your predecessor is alive. If not, set to null and update susbcription list
            if (!sim.isAlive(myid.id, pred)) {
                pred = new NodeId(-1, -1);
                subsc.remove(pred);
            }

            if (succ.id >= 0 && sim.isAlive(myid.id, succ)) {
                Message outMsg = new Message(EventType.ASK_SUCCESSOR_LIST, null);
                logger.info("ASK_SUCCESSOR_LIST");
                com.send(succ, outMsg);
            }

            // if its time to stabilize fingers, do it!
            bfdelay += stabilizeDelay;
            if (bfdelay > buildFingersDelay) {
                bfdelay = 0;
                //TODO buildFingers(); -> build fingers here
                fixFingers();
            }

            //TODO do the part in the protocol i.e. ask your successor abt its predecessor
            Message msg = new Message(EventType.ASK_PREDECESSOR, null);
            logger.info("ASK_PREDECESSOR");
            if (succ.id >= 0) {
                com.send(succ, msg);
            } else {
//            logger.severe("ASK_PREDECESSOR -> succ is null");
//            System.out.println("$$$$$$$$$$$ Biiblesoft try to send to a null succ");
            }

        } else {
            waited++; // increment you waiting status
            if (waited > TIMEOUT_WAITS) // waited enough for a reply to join
                waited = 0;
            // get another existing node from the sim
            NodeId anotherNode = sim.getExistingNode(myid);
            // send join request to the existing node
            int[] data = new int[5];
            data[0] = 0; // find_successor from join
            //date[2] and data[3] store the initial findSuccessor id.
            data[1] = myid.id; // param id
            data[2] = myid.id; // init node id
            data[3] = myid.ip; // init node ip
            data[4] = -1; // pos in fingers[], since it's not a join, make it -1
//        System.out.printf("%d: join, ask %d about who is my successor%n", id.id, existingId.id);
            Message msg = new Message(EventType.FIND_SUCCESSOR, data);
            com.send(anotherNode, msg);

        }

//        for (NodeId aNode : successors) {
//            if (aNode.id >= 0 && sim.isAlive(myid.id, aNode)) {
//                succ = aNode;
//                Message msg = new Message(EventType.ASK_PREDECESSOR, null);
//                logger.info("ASK_PREDECESSOR");
//                com.send(aNode, msg);
//                break;
//            }
//        }


    }

    private void fixFingers() {
        logger.info("node " + myid.id + ": fix_fingers");
        next++;
        if (next == m) {
            next = 1;
        }

        int[] data = new int[5];
        data[0] = 1;
        data[1] = (int) math.modPlus(myid.id, (long) Math.pow(2, next - 1));
//        data[1] = (myid.id + (int) Math.pow(2, next - 1)) % (int) Math.pow(2, m - 1);


        data[2] = myid.id;
        data[3] = myid.ip;
        data[4] = next;
        Message msg = new Message(EventType.FIND_SUCCESSOR, data);
        logger.info("Sending");
        com.send(myid, msg);

    }

    /**
     * pos_pred things it might be our predecessor.
     */
    private void notify_s(NodeId pos_pred) {
        // received a notify
        if (pred.id < 0 || math.belongsTonn(pos_pred.id, pred.id, myid.id)) {
            pred = pos_pred;
        }
    }

    private void findSuccessor(NodeId source, Message inMsg) {
        int[] inData = inMsg.data;
        int flag = inData[0];
        int paramId = inData[1];
        int initId = inData[2];
        int initIp = inData[3];
        int index = inData[4];
        if (paramId < 0 && succ.id < 0) {
            System.out.println("************ never happen ************");
        }
        if (myid.id >= 0 && math.belongsTo(paramId, myid.id, succ.id)) {
//        if (myid.id < paramId && paramId <= succ.id) {
            NodeId initNode = new NodeId(initId, initIp);
            int[] outData = {flag, paramId, succ.id, succ.ip, index};
            Message outMsg = new Message(EventType.REPLY_FIND_SUCCESSOR, outData);
            logger.info("REPLY_FIND_SUCCESSOR");
            com.send(initNode, outMsg);
        } else {
            NodeId nPrime = closestPrecedingNode(paramId);
            int[] outData = {flag, paramId, initId, initIp, index};
            Message outMsg = new Message(EventType.FIND_SUCCESSOR, outData);
            logger.info("FIND_SUCCESSOR");
            com.send(nPrime, outMsg);
        }
    }

    private void handleReplyAskPredecessor(NodeId source, Message msg) {
        int[] data = msg.data;
        int id = data[0];
        int ip = data[1];
        if (id >= 0 && math.belongsTonn(id, myid.id, succ.id)) {
            succ = new NodeId(id, ip);
        }
        int[] outData = new int[2];
        outData[0] = myid.id;
        outData[1] = myid.ip;
        Message outMsg = new Message(EventType.NOTIFY, outData);
        logger.info("NOTIFY");
        com.send(succ, outMsg);
//
//        Message outMsg2 = new Message(EventType.ASK_SUCCESSOR_LIST, null);
//        logger.info("Sending");
//        com.send(succ, outMsg2);
    }

    private void askPredecessor(NodeId source, Message msg) {
        int[] data = new int[2];
        data[0] = pred.id;
        data[1] = pred.ip;
        Message outMsg = new Message(EventType.REPLY_ASK_PREDECESSOR, data);
        logger.info("REPLY_ASK_PREDECESSOR");
        com.send(source, outMsg);
    }

    private void handleReplyFindSuccessor(NodeId source, Message msg) {
        //TODO Not yet implemented
        int[] data = msg.data;
        int paramId = data[1];
        int succId = data[2];
        int succIp = data[3];
        int flag = data[0]; // 0 -> join, 1 -> fix_fingers
        switch (flag) {
            case 0:
//                System.out.printf("%d: successor of %d is %d%n", myid.id, paramId, succId);
                haveJoined = true;
                succ = new NodeId(succId, succIp);
                Message outMsg = new Message(EventType.ASK_SUCCESSOR_LIST, null);
                logger.info("ASK_SUCCESSOR_LIST");
                com.send(succ, outMsg);
                break;
            case 1:
                int index = data[4];
                fingers[index] = new NodeId(succId, succIp);
                break;
        }
    }

    private void handleReplyAskSuccessorList(NodeId source, Message msg) {

        successors[0] = source;
        int j = 1;
        // TODO two-d array merge into one-d array, check it!!!
        for (int i = 0; i < ((r * 2) - 2); i += 2) {
            int id = msg.data[i];
            int ip = msg.data[i + 1];
            if (id >= 0 && sim.isAlive(myid.id, new NodeId(id, ip))) {
                successors[j] = new NodeId(id, ip);
                j++;
            } else if (id >=0){
                Message msg24 = new Message(EventType.UPDATE_YUOR_SUCCESSOR_LIST,null);
                com.send(new NodeId(id,ip),msg24);
            }

//            successors[j] = new NodeId(id, ip);
//            j++;
//
//            if (id >= 0 && sim.isAlive(myid.id, new NodeId(id,ip))){
//
//            }
//
//            successors[j] = new NodeId(id, ip);
//            j++;
        }
    }

    private void askSuccessorList(NodeId source, Message msg) {
        int[] outData = new int[2 * r];
        int j = 0;
        for (int i = 0; i < successors.length; i++) {
            outData[j] = successors[i].id;
            outData[j + 1] = successors[i].ip;
            j += 2;
        }
        Message outMsg = new Message(EventType.REPLY_ASK_SUCCESSOR_LIST, outData);
        logger.info("REPLY_ASK_SUCCESSOR_LIST");
        com.send(source, outMsg);
    }

    private NodeId closestPrecedingNode(int id) {
        for (int i = m - 1; i >= 1; i--) {
            if (fingers[i].id >= 0 && math.belongsTonn(fingers[i].id, myid.id, id)) {
                return fingers[i];
            }
        }
        // TODO search successor list, same if as above
        for (int i = 0; i < successors.length; i++) {
            if (successors[i].id >= 0 && math.belongsTonn(successors[i].id, myid.id, id)) {
                return successors[i];
            }
        }
        return myid;
    }

    /**
     * Received an event from the underlying simulator
     */
    public void receive(NodeId fromId, Object payload) {
        Message m = (Message) payload;
        if (listeners[m.evnt.ordinal()] == null)
            System.err.println("No listener added for event '" + m.evnt.toString() + "(" + m.evnt.ordinal() + ")'");
        else
            ((ChordEventListener) listeners[m.evnt.ordinal()]).receivedEvent(fromId, m);
    }

    private void subUpd(NodeId newValue, NodeId previousValue) {
        unsubs(previousValue, newValue);
        subs(newValue, previousValue);
    }

    /**
     * Subscribe for a nodes activitiy (failure for now)
     *
     * @param to       The node to subscribe for
     * @param oldvalue The previous value
     */
    public void subs(NodeId to, NodeId oldvalue) {
        if (oldvalue.equals(to) || to.id < 0 || to.equals(myid))    // no need for a new subscription
            return;
        //Integer t = new Integer(to);
        if (subsc.containsKey(to))    // means we are already subscribed to it, so just increment the count of subscriptions
            subsc.put(to, new Integer(((Integer) subsc.get(to)).intValue() + 1));
        else {                    // its a new subscription
            subsc.put(to, new Integer(1));
            sim.subscribe(myid, to);
        }
    }

    /**
     * Unsubscribe for a nodes activitiy (failure for now)
     *
     * @param to       The node to unsubscribe for
     * @param newvalue The new value
     */
    private void unsubs(NodeId to, NodeId newvalue) {
        if (newvalue.equals(to) || to.id < 0 || to.equals(myid))    // no need for unsubscription
            return;
        //Integer t = new Integer(to);
        if (subsc.containsKey(to)) {  // means we are subscribed to it, so decrement the count of subscriptions

            int numOfSubs = ((Integer) subsc.get(to)).intValue() - 1;
            subsc.put(to, new Integer(numOfSubs));
            if (numOfSubs == 0) {
                subsc.remove(to);
                sim.unsubscribe(myid, to);
            }
        } else {                // its a new unsubscription
            System.err.println("\tNode " + myid + ": Unsubsribing from [" + to + "] for which i wasnt already subscribed!");
        }
    }

    /**
     * Unsubscribe from all activities that i subscribed to. This is useful when the node fails itself
     */
    private void unsubsall() {
        Enumeration en = subsc.keys();
        while (en.hasMoreElements())
            sim.unsubscribe(myid, (NodeId) en.nextElement());
    }

    private NodeId[]/*int[]*/ leftshift(NodeId[]/*int[]*/ o) {
        NodeId[]/*int[]*/ rn = new NodeId/*int*/[o.length];
        for (int i = 1; i < o.length; i++)
            rn[i - 1] = o[i];
        rn[o.length - 1] = new NodeId(-1, -1);//-1;
        return rn;
    }

    /**
     * Returns the state of this node as a string. This method is used for storing the state of this node.
     * <p/>
     * Giving it a shape of:
     * <p/>
     * NODE
     * <p/>
     * ID	5@515
     * SUCC	7@123
     * PRED	3@324
     * FINGER_TABLE	1@232	2@123		  9@154	23@343
     * <p/>
     * SUCCESSOR_LIST	7@12	6@34     17@54  16@45
     * <p/>
     * NODE_END
     * <p/>
     * }
     */
    public String toString() {
        String nl = System.getProperty("line.separator");

        String str = nl + "NODE" + nl;

        str += nl + "ID \t" + myid;
        str += nl + "SUCC \t" + succ;
        str += nl + "PRED \t" + pred;
        str += nl + "FINGER_TABLE \t";
        for (int i = 0; i < m; i++)
            str += fingers[i] + "\t";
        str += nl + "SUCCESSOR_LIST \t";
        for (int i = 0; i < r; i++)
            str += successors[i] + "\t";
        str += nl + nl + "}" + nl;
        return str;
    }
    // ------------ Event triggering related stuff

    /**
     * Add an event listener. This method will override the previous listener
     */
    public void addEventListener(EventType evnt, ChordEventListener listener) {
        listeners[evnt.ordinal()] = listener;
    }

    /**
     * Remove an event listener
     *
     * @param evnt event to unsubscribe from
     */
    public void removeEventListener(EventType evnt) {
        listeners[evnt.ordinal()] = null;
    }

    private void registerEvents() {
        addEventListener(EventType.PERIODIC, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                stabilize();
            }
        });

        addEventListener(EventType.NOTIFY, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                notify_s(source);
            }
        });

        addEventListener(EventType.FIND_SUCCESSOR, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                findSuccessor(source, msg);
            }
        });

        addEventListener(EventType.REPLY_FIND_SUCCESSOR, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                handleReplyFindSuccessor(source, msg);
            }
        });

        addEventListener(EventType.ASK_PREDECESSOR, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                askPredecessor(source, msg);
            }
        });

        addEventListener(EventType.REPLY_ASK_PREDECESSOR, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                handleReplyAskPredecessor(source, msg);
            }
        });

        addEventListener(EventType.ASK_SUCCESSOR_LIST, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                askSuccessorList(source, msg);
            }
        });
        addEventListener(EventType.REPLY_ASK_SUCCESSOR_LIST, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                handleReplyAskSuccessorList(source, msg);
            }
        });

        addEventListener(EventType.UPDATE_YUOR_SUCCESSOR_LIST, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                doUpdateMySuccessorList();
            }
        });


    }


    // for GUI
    public NodeId pred() {
        return pred;
    }

    public NodeId succ() {
        return succ;
    }

    public NodeId getid() {
        return myid;
    }

    public boolean inside() {
        return (succ.id < 0) ? false : true;
    }

    public boolean isInterestingMessage(Object payload) {
        return false;
    }
}

// ----- Private class for triggering events
interface ChordEventListener extends EventListener {
    public void receivedEvent(NodeId/*int*/ source, Message msg);
}
