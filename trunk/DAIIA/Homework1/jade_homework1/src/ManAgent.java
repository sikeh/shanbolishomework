
import jade.core.AID;
import jade.core.Agent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import jade.domain.FIPANames.InteractionProtocol;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;

/**
 *
 * @author Sike Huang
 */
public class ManAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": I'm going to propose to angela");
        doWait(5000);
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        AID to = new AID();
        to.setLocalName("angela");
        msg.setSender(getAID());
        msg.addReceiver(to);
        msg.setContent("Marry Me!");
        msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
        addBehaviour(new MarriageProposer(this, msg));
    }

    class MarriageProposer extends SimpleAchieveREInitiator {

        public MarriageProposer(Agent agent, ACLMessage msg) {
            super(agent, msg);
        }

        @Override
        protected void handleAgree(ACLMessage msg) {
            System.out.println(myAgent.getLocalName() + ": Bingo, " + msg.getSender().getLocalName() + " has agreed to marry me!");
        }

        @Override
        protected void handleRefuse(ACLMessage msg) {
            System.out.println(myAgent.getLocalName() + ": Oops, " + msg.getSender().getLocalName() + " has rejected my proposal...");
        }

        @Override
        protected void handleInform(ACLMessage msg) {
            System.out.println(myAgent.getLocalName() + ": " + msg.getSender().getLocalName() + " has informed me that " + msg.getContent());
        }

        @Override
        protected void handleNotUnderstood(ACLMessage msg) {
            System.out.println(myAgent.getLocalName() + ": " + msg.getSender().getLocalName() + " didn't unstand what I said.");
        }
        
        @Override
        protected void handleOutOfSequence(ACLMessage msg) {
            System.out.println(myAgent.getLocalName() + ": " + msg.getSender().getLocalName() + " has sent me a message which is " + msg.getContent());
        }
    }
}
