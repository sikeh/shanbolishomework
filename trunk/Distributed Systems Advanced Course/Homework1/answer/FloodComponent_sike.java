package assignment1.components;

import org.apache.log4j.Logger;
import assignments.util.TopologyDescriptor;
import assignments.util.LinkDescriptor;
import tbn.timer.TimerHandler;
import tbn.comm.mina.MessageHandler;
import tbn.comm.mina.NodeReference;
import tbn.comm.mina.TransportProtocol;
import tbn.comm.mina.events.MessageEvent;
import tbn.api.Component;
import assignment1.events.InitEvent;
import assignment1.events.FloodInitEvent;
import assignment1.events.FloodMessage;
import assignment1.events.FloodDoneEvent;
import assignment1.util.NodeMessagePair;

import java.util.*;
import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Shanbo Li
 * Date: Jan 31, 2008
 * Time: 9:22:15 PM
 *
 * @author Shanbo Li
 */
public class FloodComponent {

    private static Logger log = Logger.getLogger(FloodComponent.class);

    private TopologyDescriptor topologyDescriptor;

    private Component component;

    private MessageHandler messageHandler;

    private Map<String, Collection<NodeReference>> msgNodePairs;

    public FloodComponent() {
    }

    public FloodComponent(Component component) {
        this.component = component;
        messageHandler = new MessageHandler(component);
        msgNodePairs = new HashMap<String, Collection<NodeReference>>();
    }

    public void handleInitEvent(InitEvent event) {
        this.topologyDescriptor = event.getTopologyDescriptor();
    }

    /**
     * Upon receiving a FloodInitEvent, the flood
     * component sends a FloodMessage to all its neighbors.
     *
     * @param event
     */
    public void handleFloodInitEvent(FloodInitEvent event) {
        NodeReference myNodeRef = topologyDescriptor.getMyNodeRef();
        for (NodeReference otherNodeRef : topologyDescriptor.getAllOtherNodes()) {
            // TODO MessageEvent or FloodMessage ?
            MessageEvent floodMsg = new FloodMessage(event.getMessage(), otherNodeRef, myNodeRef, TransportProtocol.TCP);
            messageHandler.send(floodMsg, floodMsg.getDestination());
        }
        System.out.println("Node " + myNodeRef.getId() + ": initial flood");
    }


    /**
     * Upon receiving a FloodMessage that it sees for the first time, the food
     * component sends it to all its neighbors. When the food component has
     * received the same FloodMessage from every neighbor, it triggers to the
     * application a FloodDoneEvent.
     *
     * @param event
     */
    public void handleFloodMessage(FloodMessage event) {
        NodeReference srcNodeRef = event.getSource();
        String msg = event.getMessage();
        NodeReference myNodeRef = topologyDescriptor.getMyNodeRef();

        if (msgNodePairs.containsKey(msg) && msgNodePairs.get(msg).isEmpty()) {
            System.out.println("Node " + myNodeRef.getId() + ": receive FloodMessage <" + msg + "> from " + srcNodeRef.getId() + " that has been flooded in the network before, ignore it");
        }
        
        if (msgNodePairs.containsKey(msg) && !msgNodePairs.get(msg).isEmpty()) {
            msgNodePairs.get(msg).remove(srcNodeRef);
            System.out.println("Node " + myNodeRef.getId() + ": remove node " + srcNodeRef.getId() + " from waiting list");
            if (msgNodePairs.get(msg).isEmpty()) {
                System.out.println("Node " + myNodeRef.getId() + ": has received FloodMessage from everyone");
                FloodDoneEvent floodDoneEvent = new FloodDoneEvent(event.getMessage());
                component.raiseEvent(floodDoneEvent);
                System.out.println("");
                System.out.println("Node " + myNodeRef.getId() + ": Raising FloodDoneEvent");
            }
        }
        
        if (!msgNodePairs.containsKey(msg)) {
            System.out.printf("Node %d: receive FloodMessage %s for the first time, the message is from node %d%n", myNodeRef.getId(), msg, srcNodeRef.getId());
            Collection<NodeReference> allOtherNodes = topologyDescriptor.getAllOtherNodes();
//            allOtherNodes.remove(srcNodeRef);
            System.out.printf("Node %d: construct waiting list for all nodes except node %d%n", myNodeRef.getId(), srcNodeRef.getId());
            msgNodePairs.put(msg, allOtherNodes);
            for (NodeReference otherNodeRef : topologyDescriptor.getAllOtherNodes()) {
                // TODO MessageEvent or FloodMessage ?
                MessageEvent floodMsg = new FloodMessage(event.getMessage(), otherNodeRef, myNodeRef, TransportProtocol.TCP);
                messageHandler.send(floodMsg, floodMsg.getDestination());
            }
            System.out.println("Node " + myNodeRef.getId() + ": flood it");
        }
    }

}
