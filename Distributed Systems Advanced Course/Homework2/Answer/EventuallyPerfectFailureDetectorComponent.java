package assignment2.components;

import org.apache.log4j.Logger;
import assignment2.events.*;
import assignments.util.TopologyDescriptor;
import tbn.api.*;
import tbn.comm.mina.MessageHandler;
import tbn.comm.mina.NodeReference;
import tbn.comm.mina.TransportProtocol;
import tbn.timer.TimerHandler;

import java.util.*;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Shanbo Li
 * Date: Feb 7, 2008
 * Time: 9:01:16 PM
 *
 * @author Shanbo Li, Sike Huang
 */
public class EventuallyPerfectFailureDetectorComponent {
    private static Logger log = Logger.getLogger(PerfectFailureDetectorComponent.class);

    private static HeartbeatMessage heartbeatMessage;

    private TopologyDescriptor topologyDescriptor;

    private Component component;

    private MessageHandler messageHandler;
    private TimerHandler timerHandler;


    private Collection<NodeReference> allNodes;
    private NodeReference myNodeRef;

    private Set<NodeReference> alive;
    private Set<NodeReference> suspected;
    private Set<NodeReference> intersectionNodes;

    private long period;
    private long increment;

    public EventuallyPerfectFailureDetectorComponent(Component component) {
        this.component = component;
        messageHandler = new MessageHandler(component);
        timerHandler = new TimerHandler(component);
        heartbeatMessage = new HeartbeatMessage(1124);
    }

    /**
     * @param params
     */
    public void init(Object[] params) {
        try {
            log.info("Loading period and increment...");
            Properties properties = new Properties();
            properties.load((InputStream) params[0]);
            period = Long.parseLong(properties.getProperty("period"));
            increment = Long.parseLong(properties.getProperty("increment"));
            log.info("period = " + period);
            log.info("increment = " + increment);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleInitEvent(InitEvent initEvent) {
        topologyDescriptor = initEvent.getTopologyDescriptor();
        allNodes = topologyDescriptor.getAllNodes();
        myNodeRef = topologyDescriptor.getMyNodeRef();

        alive = new HashSet<NodeReference>();
        alive.addAll(allNodes);
        suspected = new HashSet<NodeReference>();
        intersectionNodes = new HashSet<NodeReference>();

        try {
            timerHandler.startTimer(new TimeOutEvent(), "handleTimeOutEvent", period);
        } catch (HandlerNotSubscribedException e) {
            e.printStackTrace();
        } catch (tbn.api.NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void handleTimeOutEvent(TimeOutEvent timeOutEvent) {

//        checkUnion();
        intersectionNodes.clear();
        intersectionNodes.addAll(alive);
        intersectionNodes.retainAll(suspected);
        if (!intersectionNodes.isEmpty()) {
            period = period + increment; 
        }

        for (NodeReference node : allNodes) {
            if (!alive.contains(node) && !suspected.contains(node)) {
                suspected.add(node);
                component.raiseEvent(new SuspectEvent(node));
            } else if (alive.contains(node) && suspected.contains(node)) {
                suspected.remove(node);
                component.raiseEvent(new RestoreEvent(node));
            }

            heartbeatMessage = new HeartbeatMessage(1124, node, myNodeRef, TransportProtocol.TCP);
            messageHandler.send(heartbeatMessage, node);
        }

        alive.clear();

        try {
            timerHandler.startTimer(new TimeOutEvent(), "handleTimeOutEvent", period);
        } catch (HandlerNotSubscribedException e) {
            e.printStackTrace();
        } catch (tbn.api.NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    private void checkUnion() {
        for (NodeReference unionNode : suspected) {
            if (alive.contains(unionNode)) {
                period = period + increment;
                break;
            }
        }
    }

    public void handleHeartbeatMessage(HeartbeatMessage heartbeatMessage) {
        System.out.println("");
        System.out.println("Get a Heartbeat from \"Node " + heartbeatMessage.getSource().getId() + "\"!");
        alive.add(heartbeatMessage.getSource());
    }


}
