package assignment3.components;

import org.apache.log4j.Logger;
import assignments.util.TopologyDescriptor;
import tbn.api.Component;
import tbn.comm.mina.NodeReference;
import tbn.comm.mina.TransportProtocol;

import java.util.*;

import assignment3.util.SourceMessagePair;
import assignment3.util.MessageType;
import assignment3.events.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sike Huang
 * Date: 2008-2-13
 * Time: 20:54:09
 * To change this template use File | Settings | File Templates.
 */
public class UniformReliableBroadcastComponent {
    private static Logger log = Logger.getLogger(PerfectFailureDetectorComponent.class);

    private TopologyDescriptor topologyDescriptor;

    private Component component;

    private Collection<NodeReference> allNodes;
    private NodeReference myNodeRef;

    //    private Set<Long> delivered;
    private Set<SourceMessagePair> delivered;
    private Map<NodeReference, Set<SourceMessagePair>> pending;
    private Set<NodeReference> correct;

    private Map<SourceMessagePair, Set<NodeReference>> ack;
    private SourceMessagePair garbageMsg;


    public UniformReliableBroadcastComponent(Component component) {
        this.component = component;
    }

    public void handleInitEvent(InitEvent initEvent) {
        topologyDescriptor = initEvent.getTopologyDescriptor();
        allNodes = topologyDescriptor.getAllNodes();
        myNodeRef = topologyDescriptor.getMyNodeRef();

        delivered = new HashSet<SourceMessagePair>();
        pending = new HashMap<NodeReference, Set<SourceMessagePair>>();

        correct = new HashSet<NodeReference>();
        correct.addAll(allNodes);

        ack = new HashMap<SourceMessagePair, Set<NodeReference>>();
    }

    public void handleRbBroadcastEvent(RbBroadcastEvent rbBroadcastEvent) {
        if (!pending.containsKey(myNodeRef)) {
            pending.put(myNodeRef, new HashSet<SourceMessagePair>());
        }
        pending.get(myNodeRef).add(new SourceMessagePair(rbBroadcastEvent.getTimeStamp(), rbBroadcastEvent.getMessage(), rbBroadcastEvent.getSource(), rbBroadcastEvent.getSource()));
        component.raiseEvent(new BebBroadcastEvent(rbBroadcastEvent.getTimeStamp(), MessageType.DATA, rbBroadcastEvent.getMessage(), myNodeRef, myNodeRef, TransportProtocol.TCP));
    }

    public void handleBebDeliverEvent(BebDeliverEvent bebDeliverEvent) {
        NodeReference init = bebDeliverEvent.getInitNode();
        SourceMessagePair msg = new SourceMessagePair(bebDeliverEvent.getTimeStamp(), bebDeliverEvent.getMessage(), bebDeliverEvent.getSource(), init);
        if (!ack.containsKey(msg)) {
            ack.put(msg, new HashSet<NodeReference>());
        }
        ack.get(msg).add(bebDeliverEvent.getSource());
        if (!pending.containsKey(init)) {
            pending.put(init, new HashSet<SourceMessagePair>());
        }
        if (!pending.get(init).contains(msg)) {
            pending.get(init).add(msg);
            component.raiseEvent(new BebBroadcastEvent(bebDeliverEvent.getTimeStamp(), MessageType.DATA, bebDeliverEvent.getMessage(), bebDeliverEvent.getSource(), init, TransportProtocol.TCP));
        }
        deliver();
    }

    public void handleCrashEvent(CrashEvent crashEvent) {
        System.out.println("Node " + myNodeRef.getId() + " : discovers that node " + crashEvent.getNode() + " crashed!");
        correct.remove(crashEvent.getNode());
        deliver();
    }

    private boolean canDeliver(SourceMessagePair msg) {
        Set<NodeReference> acks = ack.get(msg);
//        if (acks == null)
//            return false;
        return acks.containsAll(correct);
    }

    private void deliver() {

        for (Map.Entry<NodeReference, Set<SourceMessagePair>> entry : pending.entrySet()) {
            Set<SourceMessagePair> msgs = entry.getValue();
            for (SourceMessagePair msg : msgs) {
                if (canDeliver(msg) && !delivered.contains(msg)) {
                    delivered.add(msg);
                    garbageMsg = msg;
                    component.raiseEvent(new RbDeliverEvent(msg.getTimeStamp(), MessageType.DATA, msg.getMessage(), msg.getSource(), msg.getInitNode()));
                }
            }
        }
        if (garbageMsg != null) {
            pending.get(garbageMsg.getInitNode()).remove(garbageMsg);
            garbageMsg = null;
            pendingOutput();
        }
    }

    private void pendingOutput() {
        System.out.println("Pending list :");
        int counter = 0;
        for (Map.Entry<NodeReference, Set<SourceMessagePair>> entry : pending.entrySet()) {
            Set<SourceMessagePair> msgs = entry.getValue();
            for (SourceMessagePair msg : msgs) {
                System.out.println("\"" + msg.getMessage() + "\" from Node " + msg.getInitNode().getId());
                counter++;
            }
        }
        if (counter == 0) {
            System.out.println("Empty!");
        }
        counter = 0;
        System.out.println("");

    }
}
