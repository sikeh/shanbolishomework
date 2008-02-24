package assignment4.components;

import org.apache.log4j.Logger;
import tbn.api.Component;
import tbn.comm.mina.MessageHandler;
import tbn.comm.mina.NodeReference;
import tbn.comm.mina.TransportProtocol;
import assignments.util.TopologyDescriptor;

import java.util.*;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import assignment4.events.*;
import assignment4.util.ReadSetElement;
import assignment4.util.WriteMessage;
import assignment4.util.MessageType;
import assignment4.util.ReadMessage;

/**
 * Created by IntelliJ IDEA.
 * User: Sike Huang
 * Date: 2008-2-22
 * Time: 18:48:07
 *
 * @author Shanbo Li, Sike Huang
 */
public class FailSilentAtomicRegisterComponent {
    private static Logger log = Logger.getLogger(FailSilentAtomicRegisterComponent.class);

    private Component component;
    private TopologyDescriptor topologyDescriptor;
    private MessageHandler messageHandler;

    private NodeReference self;

    // holds acks
    private Map<Integer, Set<NodeReference>> writeSet;
    private Map<Integer, Set<ReadSetElement>> readSet;
    // my op on reg
    private boolean[] reading;
    private int[] requestId;

    // read value to return
    private int[] readVal;
    // local cache
    private int[] value;
    private int[] timeStamp;
    private int[] maxRank;

    // numOfRegs
    private int capacity;

    private int myRank = -1;
    private int[] writeval;

    // flag of finished
    private Map<Integer, boolean[]> finished;

    public FailSilentAtomicRegisterComponent(Component component) {
        this.component = component;
        messageHandler = new MessageHandler(component);
    }

    public void init(Object[] params) {
        try {
            log.info("Loading capacity...");
            Properties properties = new Properties();
            properties.load((InputStream) params[0]);
            capacity = Integer.parseInt(properties.getProperty("capacity"));
            log.info("capacity = " + capacity);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleInitEvent(InitEvent initEvent) {
        writeSet = new HashMap<Integer, Set<NodeReference>>();
        readSet = new HashMap<Integer, Set<ReadSetElement>>();
        finished = new HashMap<Integer, boolean[]>();
        reading = new boolean[capacity];
        requestId = new int[capacity];
        value = new int[capacity];
        timeStamp = new int[capacity];
        maxRank = new int[capacity];
        writeval = new int[capacity];
        readVal = new int[capacity];

        topologyDescriptor = initEvent.getTopologyDescriptor();
        // myRank myself, simply put id as myRank
        self = topologyDescriptor.getMyNodeRef();
        myRank = self.getId().intValue();
        for (int i = 0; i < capacity; i++) {
            writeSet.put(i, new HashSet<NodeReference>());
            readSet.put(i, new HashSet<ReadSetElement>());
            finished.put(i, new boolean[1124]);
        }
    }

    public void handleReadEvent(AtomicRegisterReadEvent readEvent) {
        int register = readEvent.getRegister();
        System.out.println(System.currentTimeMillis() % 100000 + ": Node " + self.getId() + ": AtomicRegisterReadEvent -> r:" + register);
        requestId[register] = requestId[register] + 1;
        reading[register] = true;
        writeSet.get(register).clear();
        readSet.get(register).clear();
        ReadMessage readMessage = new ReadMessage(register, requestId[register]);
        component.raiseEvent(new BebBroadcastEvent(readMessage));
    }


    public void handleWriteEvent(AtomicRegisterWriteEvent writeEvent) {
        int register = writeEvent.getRegister();
        int value = writeEvent.getValue();
        requestId[register] = requestId[register] + 1;
        writeval[register] = value;
        writeSet.get(register).clear();
        readSet.get(register).clear();
        System.out.println(System.currentTimeMillis() % 100000 + ": Node " + self.getId() + ": broabcast AtomicRegisterWriteEvent -> r:" + register + ", val:" + value);
        ReadMessage readMessage = new ReadMessage(register, requestId[register]);
        component.raiseEvent(new BebBroadcastEvent(readMessage));
    }

    public void handleBebDeliverEvent(BebDeliverEvent bebDeliverEvent) {
        if (bebDeliverEvent.getMessageType() == MessageType.READ) {
            System.out.println(System.currentTimeMillis() % 100000 + ": Node " + self.getId() + ": receive ReadMessage -> from:" + bebDeliverEvent.getSource().getId() + ", r:" + bebDeliverEvent.getReadMessage().getRegister());
            ReadMessage readMessage = bebDeliverEvent.getReadMessage();
            ReadValueMessage readValueMessage = new ReadValueMessage(bebDeliverEvent.getSource(), self, TransportProtocol.TCP, readMessage.getRegister(), readMessage.getRequstId(), timeStamp[readMessage.getRegister()], maxRank[readMessage.getRegister()], value[readMessage.getRegister()]);
            messageHandler.send(readValueMessage, self, TransportProtocol.TCP);
        } else if (bebDeliverEvent.getMessageType() == MessageType.WRITE) {
            NodeReference source = bebDeliverEvent.getSource();
            WriteMessage writeMessage = bebDeliverEvent.getWriteMessage();
            int ts = writeMessage.getTimeStamp();
            int rank = writeMessage.getRank();
            int val = writeMessage.getValue();
            int id = writeMessage.getRequstId();
            int register = writeMessage.getRegister();
            System.out.println(System.currentTimeMillis() % 100000 + ": Node " + self.getId() + ": receive WriteMessage -> from:" + source.getId() + ", r:" + register + ", val:" + val);
            // TODO (t, j) > (ts[r], mrank[r])
            if (ts > timeStamp[register] || (ts == timeStamp[register] && rank > maxRank[register])) {
                value[register] = val;
                timeStamp[register] = ts;
                maxRank[register] = rank;
                System.out.println(System.currentTimeMillis() % 100000 + ": ------ Write to local!");
            }
            AckMessage ackMessage = new AckMessage(source, self, TransportProtocol.TCP, id, register);
            messageHandler.send(ackMessage, source, TransportProtocol.TCP);
        }
    }

    public void handleReadValueMessage(ReadValueMessage readValueMessage) {
        if (readValueMessage.getId() == requestId[readValueMessage.getRegister()]) {
            readSet.get(readValueMessage.getRegister()).add(new ReadSetElement(readValueMessage.getTimeStamp(), readValueMessage.getRank(), readValueMessage.getValue()));
            checkReadSet(readValueMessage.getRegister());
        }
    }


    public void handleAckMessage(AckMessage ackMessage) {
        NodeReference src = ackMessage.getSource();
        int id = ackMessage.getId();
        int register = ackMessage.getRegister();
        System.out.println(System.currentTimeMillis() % 100000 + ": Node " + self.getId() + ": receive AckMessage -> from:" + src.getId() + ", r:" + register);
        if (id == requestId[register]) {
            writeSet.get(register).add(src);
            checkWriteSet(register);
        }
    }

    private void checkReadSet(int r) {
        if (readSet.get(r).size() > topologyDescriptor.getTotalNodes() / 2) {
            ReadSetElement hightest = Collections.max(readSet.get(r));
            readVal[r] = hightest.getVal();
//            System.out.println("readVal = " + readVal[r]);
            if (reading[r]) {
                component.raiseEvent(new BebBroadcastEvent(new WriteMessage(r, requestId[r], hightest.getTimeStamp(), hightest.getRank(), readVal[r])));
            } else {
                component.raiseEvent(new BebBroadcastEvent(new WriteMessage(r, requestId[r], hightest.getTimeStamp() + 1, myRank, writeval[r])));
            }
        }
    }

    private void checkWriteSet(int r) {
        if (writeSet.get(r).size() > topologyDescriptor.getTotalNodes() / 2) {
            if (!finished.get(r)[requestId[r]]) {
                finished.get(r)[requestId[r]] = true;
                if (reading[r]) {
                    reading[r] = false;
                    AtomicRegisterReadReturnEvent readReturnEvent = new AtomicRegisterReadReturnEvent(r, readVal[r]);
                    component.raiseEvent(readReturnEvent);
                } else {
                    AtomicRegisterWriteReturnEvent writeReturnEvent = new AtomicRegisterWriteReturnEvent(r, writeval[r]);
                    component.raiseEvent(writeReturnEvent);
                }
            }
        }
    }
}
