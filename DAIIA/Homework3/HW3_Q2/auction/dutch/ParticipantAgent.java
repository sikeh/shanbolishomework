/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auction.dutch;

import jade.content.Concept;
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
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ontology.AuctionOntology;
import ontology.Bids;
import ontology.Phone;
import ontology.Sell;

/**
 *
 * @author Sike Huang
 */
public class ParticipantAgent extends Agent {

    private Ontology ontology;
    private Codec language;
    private MessageTemplate mt;
    private ContentManager manager;

    @Override
    public void setup() {
        Object[] objs = getArguments();
        int priceLimit = Integer.parseInt(String.valueOf(objs[0]));
        ontology = AuctionOntology.getInstance();
        language = new SLCodec();
        mt = MessageTemplate.and(MessageTemplate.MatchLanguage(language.getName()),
                MessageTemplate.MatchOntology(ontology.getName()));
        manager = getContentManager();
        manager.registerLanguage(language);
        manager.registerOntology(ontology);

        addBehaviour(new RegisterMyselfToDF(this));
        addBehaviour(new BidBehaviour(this, priceLimit));
    }

    class RegisterMyselfToDF extends OneShotBehaviour {

        public RegisterMyselfToDF(Agent agent) {
            super(agent);
        }

        @Override
        public void action() {
            DFAgentDescription agentDes = new DFAgentDescription();
            agentDes.setName(myAgent.getAID());
            ServiceDescription serviceDes = new ServiceDescription();
            serviceDes.setName("Participant");
            serviceDes.setType("Dutch Auction");
            agentDes.addServices(serviceDes);
            try {
                DFService.register(myAgent, agentDes);
            } catch (FIPAException ex) {
                ex.printStackTrace();
            }
        }
    }

    class BidBehaviour extends SimpleBehaviour {

        private boolean isDone;
        private int priceLimit;

        public BidBehaviour(Agent agent, int priceLimit) {
            super(agent);
            this.priceLimit = priceLimit;
        }

        @Override
        public void action() {
            ACLMessage msg = receive(mt);
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                    try {
                        ContentElement content = manager.extractContent(msg);
                        if (content instanceof Bids) {
                            Bids aBids = (Bids) content;
                            Phone phone = aBids.getItem();
                            int price = aBids.getPrice();
                            System.out.println("Initiator has anounced a price " + price + " for " + phone.getBrand() + " " + phone.getModel());
                            if (price <= priceLimit) {
                                // inform all participants about who is winner
                                ACLMessage reply = msg.createReply();
                                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                aBids.setAuctioner(myAgent.getAID());
                                manager.fillContent(reply, aBids);
                                send(reply);
                                System.out.println("I will bid");
                            }
                        }
                    } catch (CodecException ex) {
                        ex.printStackTrace();
                    } catch (UngroundedException ex) {
                        ex.printStackTrace();
                    } catch (OntologyException ex) {
                        ex.printStackTrace();
                    }
                }
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    try {
                        ContentElement content = manager.extractContent(msg);
                        Concept action = ((Action) content).getAction();
                        if (action instanceof Sell) {
                            Sell sell = (Sell) action;
                            Phone phone = sell.getItem();
                            int price = sell.getPrice();
                            AID winner = sell.getAuctioner();
                            if (winner.equals(myAgent.getAID())) {
                                System.out.println("I win " + phone.getBrand() + " " + phone.getModel() + " with price " + price);
                            } else {
                                System.out.println(winner.getLocalName() + " win " + phone.getBrand() + " " + phone.getModel() + " with price " + price);
                            }
                            isDone = true;
                        }
                    } catch (CodecException ex) {
                        ex.printStackTrace();
                    } catch (UngroundedException ex) {
                        ex.printStackTrace();
                    } catch (OntologyException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                block();
            }
        }

        @Override
        public boolean done() {
            return isDone;
        }
    }
}
