package org.kth.sim.chord;

import org.kth.sim.interfaces.PeerInterface;
import org.kth.sim.interfaces.CommInterface;
import org.kth.sim.util.MathMiscConstant;
import org.kth.sim.NodeId;
import org.kth.sim.SicsSim;

import java.util.Random;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.EventListener;

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
    public int r = 9;    // size of the successor list  10->1024, 6->64

    private NodeId pred = new NodeId(-1, -1); //int pred = -1;
    private NodeId succ = new NodeId(-2, -1); //int succ = -2;

    private SicsSim sim;
    // The time delay used for periodic stabilization
    private final int stabilizeDelay = 10;
    private final int buildFingersDelay = 7 * stabilizeDelay;
    public int bfdelay = 0;

    private NodeId x;

    // hashtable for keeping track of nodes that i want to subscribe to (ie. to be notified when they fail) x=id, y=# of times subscribed
    public Hashtable<NodeId, Integer> subsc = new Hashtable<NodeId, Integer>(m); // just for the fingers!

    // An array for listening to all event types that chord can listen to
    // remember that only one listener is allowed for each type of event
    // can easily be extended but is not needed
    Object[] listeners = new Object[EventType.values().length];

    /**
     * General Notes:
     * 1) Not subscribing for failures of nodes in the successor list
     * 2) After failure of a node, successor list gets reconciled the next time periodic stabilization is done
     */
    public Node() {
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
        this.com = com;
        this.rnd = new Random(seed);

        this.m = (int) math.logk(N) + 1;
        this.fingers = new NodeId[m];
        this.successors = new NodeId[r];

        // initializing fingers
        fingers[0] = myid;
        for (int i = 0; i < m; i++)
            fingers[i] = new NodeId(-1, -1);
    }

    public void create(int N, int seed, NodeId/*int*/ id, CommInterface com) {

        init(N, seed, id, com);

        succ = myid;
        pred = null;
        //status = Status.INSIDE;
        // build fingers!
        for (int i = 0; i < m; i++)
            fingers[i] = null;    // TODO sikeh: should it be null (instead of myid)?
        // build successor list!
        for (int i = 0; i < r; i++)
            successors[i] = null;   // TODO sikeh: should it be null (instead of myid)?
        // registering for periodic event with the simulator
        this.sim.addPeriodicEvent(stabilizeDelay, myid, /*networkid, */new Message(EventType.PERIODIC, null));
        //System.out.println("\t Node: "+myid+", i'm starting the ring. This shouldnt happen after merger");
    }

    public void join(int N, int seed, NodeId/*int*/ id, NodeId/*int*/ existingId, CommInterface com) {

        init(N, seed, id, com);

        pred = null;
        int[] data = new int[4];
        data[0] = id.id;
        data[1] = id.ip;
        //date[2] and data[3] store the initial findSuccessor id.
        data[2] = id.id;
        data[3] = id.ip;
        Message msg = new Message(EventType.FIND_SUCCESSOR, data);
        com.send(existingId, msg);
    }

    public void leave() {
        System.err.println("Leave should not have been called at " + myid);
    }

    public void failure(NodeId fid) {
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
            for (NodeId aNode : successors) {
                if (sim.isAlive(aNode.id, myid)) {
                    succ = aNode;
                    fingers[0] = succ;
                    break;
                } else {
                    subsc.remove(aNode);
                }
            }
        }

        // case 2: one of my fingers failed ->
        //		1. set it to null and let periodic stabilization fix it
        //		2. update your subscription list for failures
        for (int i = 0; i < fingers.length; i++) {
            if (fid.equals(fingers[i])) {
                fingers[i] = null;
            }
        }

        // case 3: my predecessor failed ->
        //		1. set it to null and let periodic stabilization fix it
        //		2. update your subscription list for failures
        if (fid.equals(pred)) {
            pred = null;
        }

        // unsubscride from this node
        subsc.remove(fid);

    }

    /**
     * Periodically verify's immediate successor, and tells the successor about itself
     */
    private void stabilize() {

        //TODO check if your predecessor is alive. If not, set to null and update susbcription list
        if (!sim.isAlive(pred.id,  myid)) {
            pred = null;
            subsc.remove(pred);
        }

        // if its time to stabilize fingers, do it!
        bfdelay += stabilizeDelay;
        if (bfdelay > buildFingersDelay) {
            bfdelay = 0;
            //TODO buildFingers(); -> build fingers here
        }

        //TODO do the part in the protocol i.e. ask your successor abt its predecessor

    }

    /**
     * pos_pred things it might be our predecessor.
     */
    private void notify_s(NodeId pos_pred) {
        // received a notify
        if (pred == null || math.belongsTonn(pos_pred.id, pred.id, myid.id)) {
            pred = pos_pred;
        }
    }

    private void findSuccessor(NodeId id, Message msg) {
        if (math.belongsTo(id.id, myid.id, succ.id)) {
            int[] tmp = {succ.id, succ.ip};
            //TODO modify here
            Message returnMsg = new Message(EventType.REPLY_FIND_SUCCESSOR, tmp);
            com.send(id, msg);
        } else {
            NodeId nPrime = closestPrecedingNode(id);

        }
    }

    private void findPredecessor(NodeId source, Message msg) {
        //TODO: handle find predecessor
    }

    private void handleReplyAskPredecessor(NodeId source, Message msg) {
        succ = new NodeId(msg.data[0], msg.data[1]);
    }

    private void handleReplyAskSuccessor(NodeId source, Message msg) {
        //TODO handle Reply Ask Successor
    }


    private void askPredecessor(NodeId source, Message msg) {
        //TODO Not yet implemented
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void askSuccessor(NodeId source, Message msg) {
        //TODO
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleReplyFindSuccessor(NodeId source, Message msg) {
        //TODO Not yet implemented
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleReplyFindPredecessor(NodeId source, Message msg) {
        //TODO Not yet implemented
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private NodeId closestPrecedingNode(NodeId id) {
        for (int i = m; i >= 1; i--) {
            if (math.belongsTo(fingers[i - 1].id, myid.id, id.id)) {
                return fingers[i - 1];
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

        addEventListener(EventType.FIND_PREDECESSOR, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                findPredecessor(source, msg);
            }
        });

        addEventListener(EventType.REPLY_FIND_PREDECESSOR, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                handleReplyFindPredecessor(source, msg);
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

        addEventListener(EventType.ASK_SUCCESSOR, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                askSuccessor(source, msg);
            }
        });

        addEventListener(EventType.REPLY_ASK_SUCCESSOR, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                handleReplyAskSuccessor(source, msg);
            }
        });

        addEventListener(EventType.REPLY_ASK_PREDECESSOR, new ChordEventListener() {
            public void receivedEvent(NodeId source, Message msg) {
                handleReplyAskPredecessor(source, msg);
            }
        });

//        addEventListener(EventType.CLOSEST_PRECEDING_NODE, new ChordEventListener() {
//            public void receivedEvent(NodeId source, Message msg) {
//                closestPrecedingNode(source, msg);
//            }
//        });


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
