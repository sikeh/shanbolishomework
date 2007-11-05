
import jade.core.AID;
import jade.core.Agent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames.InteractionProtocol;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREResponder;

/**
 *
 * @author Sike Huang
 */
public class WomanAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": I wonder if anybody wants to marry me?");
        addBehaviour(new MarriageResponder(this));
    }
    
    class MarriageResponder extends SimpleAchieveREResponder {
        public MarriageResponder(Agent agent) {
            super(agent, createMessageTemplate(InteractionProtocol.FIPA_REQUEST));
        }

        @Override
        protected ACLMessage prepareResponse(ACLMessage msg) throws NotUnderstoodException, RefuseException {
            ACLMessage response = msg.createReply();
            if (msg.getContent() != null && msg.getContent().equals("Marry Me!")) {
                System.out.println(myAgent.getLocalName() + ": " + msg.getSender().getLocalName() + " has asked me to marry him!");
                AID sender = msg.getSender();
                if (sender.getLocalName().equals("scott")) {
                    response.setPerformative(ACLMessage.AGREE);
                    System.out.println(myAgent.getLocalName() + ": I'm going to agree.");
                } else {
                    response.setPerformative(ACLMessage.REFUSE);
                    System.out.println(myAgent.getLocalName() + ": I'm going to turn him down.");
                }
            } else {
                response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                System.out.println(myAgent.getLocalName() + ": I don't understand what " + msg.getSender().getLocalName() + " just said.");
            }
            return response;
        }

        @Override
        protected ACLMessage prepareResultNotification(ACLMessage inMsg, ACLMessage outMsg) throws FailureException {
            ACLMessage msg = inMsg.createReply();
            msg.setContent("I Do!");
            return msg;
        }
        
        

    }

}
