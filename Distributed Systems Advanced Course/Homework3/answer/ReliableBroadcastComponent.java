package assignment3.components;

import org.apache.log4j.Logger;
import assignment3.events.*;
import assignment3.util.SourceMessagePair;
import assignment3.util.MessageType;
import assignments.util.TopologyDescriptor;
import tbn.api.Component;
import tbn.api.HandlerNotSubscribedException;
import tbn.comm.mina.MessageHandler;
import tbn.comm.mina.NodeReference;
import tbn.comm.mina.TransportProtocol;
import tbn.timer.TimerHandler;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Shanbo Li
 * Date: Feb 13, 2008
 * Time: 8:56:39 PM
 *
 * @author Shanbo Li
 */
public class ReliableBroadcastComponent {
    private static Logger log = Logger.getLogger(PerfectFailureDetectorComponent.class);

    private TopologyDescriptor topologyDescriptor;

    private Component component;

    private MessageHandler messageHandler;
    private TimerHandler timerHandler;


    private Collection<NodeReference> allOtherNodes;
    private Collection<NodeReference> allNodes;
    private NodeReference myNodeRef;

    private Collection<Long> delivered;
    private Collection<NodeReference> correct;

    private Map<NodeReference, Set<SourceMessagePair>> from;


    public ReliableBroadcastComponent(Component component) {
        this.component = component;
        messageHandler = new MessageHandler(component);
        timerHandler = new TimerHandler(component);
    }

    public void handleInitEvent(InitEvent initEvent) {
        topologyDescriptor = initEvent.getTopologyDescriptor();
        allOtherNodes = topologyDescriptor.getAllOtherNodes();
        allNodes = topologyDescriptor.getAllNodes();
        myNodeRef = topologyDescriptor.getMyNodeRef();

        delivered = new HashSet<Long>();
        delivered.clear();

        correct = new HashSet<NodeReference>();
        correct.addAll(allNodes);

        from = new HashMap<NodeReference, Set<SourceMessagePair>>();


        for (NodeReference node : allNodes) {
            from.put(node, new HashSet<SourceMessagePair>());
        }
    }

    public void handleRbBroadcastEvent(RbBroadcastEvent rbBroadcastEvent) {
        component.raiseEvent(new BebBroadcastEvent(rbBroadcastEvent.getTimeStamp(), MessageType.DATA, rbBroadcastEvent.getMessage(), rbBroadcastEvent.getSource(), TransportProtocol.UDP));
    }

    public void handleBebDeliverEvent(BebDeliverEvent bebDeliverEvent) {
        if (!delivered.contains(bebDeliverEvent.getTimeStamp())) {
            delivered.add(bebDeliverEvent.getTimeStamp());
            component.raiseEvent(new RbDeliverEvent(bebDeliverEvent.getTimeStamp(), bebDeliverEvent.getMessageType(), bebDeliverEvent.getMessage(), bebDeliverEvent.getSource()));
            from.get(bebDeliverEvent.getSource()).add(new SourceMessagePair(bebDeliverEvent.getTimeStamp(),bebDeliverEvent.getMessage(),bebDeliverEvent.getSource()));

            if (!correct.contains(bebDeliverEvent.getSource())){
                 component.raiseEvent(new BebBroadcastEvent(bebDeliverEvent.getTimeStamp(),bebDeliverEvent.getMessageType(),bebDeliverEvent.getMessage(),bebDeliverEvent.getSource(),TransportProtocol.TCP));
            }
        }
    }

    public void handleCrashEvent(CrashEvent crashEvent){
        correct.remove(crashEvent.getNode());
        for (SourceMessagePair sourceMessagePair: from.get(crashEvent.getNode())){
            component.raiseEvent(new BebBroadcastEvent(sourceMessagePair.getTimeStamp(),MessageType.DATA ,sourceMessagePair.getMessage(),sourceMessagePair.getNode(),TransportProtocol.TCP));
        }
    }


}
