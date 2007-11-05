
import jade.core.AID;
import jade.core.Agent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Sike Huang
 */
public class SenderAgent extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new SimpleBehaviour(this) {
            private boolean finished = false;
            
            @Override
            public void action() {
                System.out.println(getLocalName() + ": about to inform angela hello");
                doWait(5000);
                AID to = new AID();
                to.setLocalName("angela");
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setSender(getAID());
                msg.addReceiver(to);
                msg.setContent("Hello");
                send(msg);
                System.out.println(getLocalName() + ": send hello to angela");
                finished = true;
            }

            @Override
            public boolean done() {
                return finished;
            }
        });
    }

}
