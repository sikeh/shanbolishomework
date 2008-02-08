package assignment2.components;

import assignment2.events.*;
import tbn.api.*;
import tbn.api.NoSuchMethodException;
import tbn.comm.mina.MessageHandler;
import tbn.comm.mina.NodeReference;
import tbn.comm.mina.TransportProtocol;
import tbn.timer.TimerHandler;
import org.apache.log4j.Logger;
import assignments.util.TopologyDescriptor;
import assignments.util.TopologyParser;
import assignments.util.LinkDescriptor;

import java.util.*;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Shanbo Li
 * Date: Feb 7, 2008
 * Time: 9:00:28 PM
 *
 * @author Shanbo Li
 */
public class PerfectFailureDetectorComponent {

    private static Logger log = Logger.getLogger(PerfectFailureDetectorComponent.class);

    private static HeartbeatMessage heartbeatMessage;

    private TopologyDescriptor topologyDescriptor;

    private Component component;

    private MessageHandler messageHandler;
    private TimerHandler timerHandler;


    private Collection<NodeReference> allNodes;
    private NodeReference myNodeRef;

    private Set<NodeReference> alive;
    private Set<NodeReference> detected;

    private long gamma;
    private long delta;


    LinkDescriptor linkDescriptor;

    public PerfectFailureDetectorComponent(Component component) {
        this.component = component;
        messageHandler = new MessageHandler(component);
        timerHandler = new TimerHandler(component);

        heartbeatMessage = new HeartbeatMessage(1124);
    }

    public void init(Object[] params) {
        try {
            log.info("Loading gamma and delta...");
            Properties properties = new Properties();
            properties.load((InputStream) params[0]);
            gamma = Long.parseLong(properties.getProperty("gamma"));
            delta = Long.parseLong(properties.getProperty("delta"));
            log.info("gamma = " + gamma);
            log.info("delta = " + delta);
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
        detected = new HashSet<NodeReference>();

        try {
            timerHandler.startTimer(new CheckTimeOutEvent(), "handleCheckTimeOutEvent", gamma + delta);
        } catch (HandlerNotSubscribedException e) {
            e.printStackTrace();
        } catch (tbn.api.NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            timerHandler.startTimer(new HeartbeatTimeOutEvent(), "handleHeartbeatTimeOutEvent", gamma);
        } catch (HandlerNotSubscribedException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void handleHeartbeatTimeOutEvent(HeartbeatTimeOutEvent heartbeatTimeOutEvent) {
        for (NodeReference targetNode : allNodes) {
            heartbeatMessage = new HeartbeatMessage(1124, targetNode, myNodeRef, TransportProtocol.TCP);
            messageHandler.send(heartbeatMessage, targetNode);
        }

        try {
            timerHandler.startTimer(new HeartbeatTimeOutEvent(), "handleHeartbeatTimeOutEvent", gamma);
        } catch (HandlerNotSubscribedException e) {
            e.printStackTrace();
        } catch (tbn.api.NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void handleCheckTimeOutEvent(CheckTimeOutEvent checkTimeOutEvent) {
        for (NodeReference node : allNodes) {
            if (!alive.contains(node) && !detected.contains(node)) {
                detected.add(node);
                component.raiseEvent(new CrashEvent(node));
            }
        }
        alive.clear();

        try {
            timerHandler.startTimer(new CheckTimeOutEvent(), "handleCheckTimeOutEvent", gamma + delta);
        } catch (HandlerNotSubscribedException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public void handleHeartbeatMessage(HeartbeatMessage heartbeatMessage) {
        System.out.println("Node " + myNodeRef.getId() + ": get a heartbeat from \"Node " + heartbeatMessage.getSource().getId() + "\"");
        alive.add(heartbeatMessage.getSource());
    }
}
