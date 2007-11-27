/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auction.dutch;

import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionInitiator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ontology.AuctionOntology;
import ontology.Bids;
import ontology.Phone;
import ontology.Sell;

/**
 *
 * @author Sike Huang
 */
public class InitiatorAgent extends Agent {

    private Ontology ontology;
    private Codec language;
    private List<AID> participants;
    private MessageTemplate mt;
    private int numOfParticipants;
    private ContentManager manager;

    @Override
    public void setup() {
        Object[] obj = getArguments();
        numOfParticipants = Integer.parseInt(String.valueOf(obj[0]));
        int initialPrice = 60; //Integer.parseInt(String.valueOf(obj[1]));
        int endPrice = 10; //Integer.parseInt(String.valueOf(obj[2]));
        int step = 5; //Integer.parseInt(String.valueOf(obj[3]));
        long sleep = 5000;
        participants = new ArrayList<AID>(numOfParticipants);
        ontology = AuctionOntology.getInstance();
        language = new SLCodec();
        manager = getContentManager();
        manager.registerLanguage(language);
        manager.registerOntology(ontology);

        mt = MessageTemplate.and(MessageTemplate.MatchLanguage(language.getName()),
                MessageTemplate.MatchOntology(ontology.getName()));

        addBehaviour(new RegisterMyselfToDF(this));

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription templateSd = new ServiceDescription();
        templateSd.setName("Participant");
        templateSd.setType("Dutch Auction");
        template.addServices(templateSd);
        SearchConstraints sc = new SearchConstraints();
        sc.setMaxResults(new Long(numOfParticipants));
        addBehaviour(new MySubscriptionInitiator(template, sc));

        ParallelBehaviour parallel = new ParallelBehaviour(ParallelBehaviour.WHEN_ANY);
        parallel.addSubBehaviour(new ReceiveBahaviour(this));
        parallel.addSubBehaviour(new SendBehaviour(this, sleep, initialPrice, endPrice, step));
        addBehaviour(parallel);
    }

    class SendBehaviour extends TickerBehaviour {

        private int currentPrice;
        private int endPrice;
        private int step;

        public SendBehaviour(Agent agent, Long period, int initialPrice, int endPrice, int step) {
            super(agent, period);
            currentPrice = initialPrice;
            this.endPrice = endPrice;
            this.step = step;
        }

        @Override
        protected void onTick() {
            if (numOfParticipants == participants.size()) {
                try {
                    System.out.println("Current price: " + currentPrice);
                    ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
                    msg.setOntology(ontology.getName());
                    msg.setLanguage(language.getName());
                    msg.setSender(myAgent.getAID());
                    for (AID participant : participants) {
                        msg.addReceiver(participant);
                    }
                    Phone phone = new Phone();
                    phone.setBrand("Nokia");
                    phone.setModel("N82");
                    Bids bids = new Bids();
                    bids.setItem(phone);
                    bids.setPrice(currentPrice);
                    manager.fillContent(msg, bids);
                    send(msg);
                    currentPrice -= step;
                    if (currentPrice < endPrice) {
                        System.out.println("Can't decrease price anymore, auction abort!");
                        myAgent.doDelete();
                    }
                } catch (CodecException ex) {
                    Logger.getLogger(InitiatorAgent.class.getName()).log(Level.SEVERE, null, ex);
                } catch (OntologyException ex) {
                    Logger.getLogger(InitiatorAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class ReceiveBahaviour extends SimpleBehaviour {

        private boolean isDone;
        private AID winner;

        public ReceiveBahaviour(Agent agent) {
            super(agent);
        }

        @Override
        public void action() {
            if (numOfParticipants == participants.size()) {
                ACLMessage msg = receive(mt);
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        try {
                            ContentElement content = manager.extractContent(msg);
                            if (content instanceof Bids) {
                                Bids aBids = (Bids) content;
                                winner = aBids.getAuctioner();
                                int price = aBids.getPrice();
                                Phone phone = aBids.getItem();
                                System.out.println("Received a bid for " + phone.getBrand() + " " + phone.getModel() + " with price " + price + " from " + aBids.getAuctioner().getLocalName());
                                // inform all participants about who is winner
//                                ACLMessage reply = msg.createReply();
//                                reply.setPerformative(ACLMessage.INFORM);
                                ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                                reply.setSender(myAgent.getAID());
                                reply.setLanguage(language.getName());
                                reply.setOntology(ontology.getName());
                                Sell sell = new Sell(phone, price, winner);
                                for (AID participant : participants) {
                                    reply.addReceiver(participant);
                                }
                                manager.fillContent(reply, new Action(winner, sell));
                                send(reply);
                                System.out.println("Auction done!");
                                isDone = true;
                                myAgent.doDelete();
                            }
                        } catch (CodecException ex) {
                            Logger.getLogger(InitiatorAgent.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (UngroundedException ex) {
                            Logger.getLogger(InitiatorAgent.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (OntologyException ex) {
                            Logger.getLogger(InitiatorAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    block();
                }
            }
        }

        @Override
        public boolean done() {
            return isDone;
        }
    }

    class RegisterMyselfToDF extends OneShotBehaviour {

        public RegisterMyselfToDF(Agent agent) {
            super(agent);
        }

        public void action() {
            DFAgentDescription agentDes = new DFAgentDescription();
            agentDes.setName(getAID());
            ServiceDescription serviceDes = new ServiceDescription();
            serviceDes.setName("Initiator");
            serviceDes.setType("Dutch Auction");
            agentDes.addServices(serviceDes);
            try {
                DFService.register(myAgent, agentDes);
            } catch (FIPAException ex) {
                ex.printStackTrace();
            }
        }
    }

    class MySubscriptionInitiator extends SubscriptionInitiator {

        public MySubscriptionInitiator(DFAgentDescription template, SearchConstraints sc) {
            super(InitiatorAgent.this, DFService.createSubscriptionMessage(InitiatorAgent.this, InitiatorAgent.this.getDefaultDF(), template, sc));
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            System.out.println();
            System.out.println("Initiator " + getLocalName() + ": Notification received from DF");
            try {
                DFAgentDescription[] results = DFService.decodeNotification(inform.getContent());
                if (results.length > 0) {
                    for (int i = 0; i < results.length; ++i) {
                        DFAgentDescription dfd = results[i];
                        AID provider = dfd.getName();
                        Iterator it = dfd.getAllServices();
                        while (it.hasNext()) {
                            ServiceDescription sd = (ServiceDescription) it.next();
//                            if (sd.getType().equals("Dutch Action") && sd.getName().equals("Participant")) {
                            System.out.println("Initiator " + getLocalName() + ": get a Participants \"" + provider.getLocalName() + "\"");
                            participants.add(provider);
//                            }
                        }
                    }
                }
                System.out.println();
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }
    }
}
