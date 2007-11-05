
import jade.core.AID;
import jade.core.Agent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;

/**
 *
 * @author Sike Huang
 */
public class ReceiverAgent extends Agent {

    @Override
    protected void setup() {
        DataStore dataStore = new DataStore();
        AID sender = new AID();
        sender.setLocalName("scott");
        MessageTemplate matchInform = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        MessageTemplate matchSender = MessageTemplate.MatchSender(sender);
        MessageTemplate messageTemplate = MessageTemplate.and(matchInform, matchSender);
        long deadLine = System.currentTimeMillis() + 10000;
        addBehaviour(new MsgReceiver(this, messageTemplate, deadLine, dataStore, "key") {
            @Override
            protected void handleMessage(ACLMessage msg) {
                System.out.println(myAgent.getLocalName() + ": receive a msg from " + msg.getSender().getLocalName() + ", which is " + msg.getContent());
            }
            
        });
    }
}
