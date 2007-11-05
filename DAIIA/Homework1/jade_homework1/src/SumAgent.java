
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import jade.core.behaviours.OneShotBehaviour;
import java.util.Deque;
import java.util.LinkedList;

/**
 *
 * @author Sike Huang
 */
public class SumAgent extends Agent {

    // first in first out
    private Deque<Integer> queue;
    private static final String STATE_ZERO = "zero";
    private static final String STATE_FIVE = "five";
    private static final String STATE_TEN = "ten";
    private static final String STATE_FIFTEEN = "fifteen";

    @Override
    protected void setup() {
        // put all input numbers into a queue
        queue = new LinkedList<Integer>();
        Object[] args = getArguments();
        for (Object arg : args) {
            queue.offer(Integer.parseInt(String.valueOf(arg)));
        }

        FSMBehaviour behaviour = new FSMBehaviour(this) {
            @Override
            public int onEnd() {
                System.out.println("FSM behaviour completed.");
                myAgent.doDelete();
                return super.onEnd();
            }
        };
        // 4 states        
        behaviour.registerFirstState(new ZeroBehaviour(), STATE_ZERO);
        behaviour.registerState(new FiveBehaviour(), STATE_FIVE);
        behaviour.registerState(new TenBehaviour(), STATE_TEN);
        behaviour.registerLastState(new FifteenBehaviour(), STATE_FIFTEEN);
        // 6 transitions
        behaviour.registerTransition(STATE_ZERO, STATE_FIVE, 5);
        behaviour.registerTransition(STATE_ZERO, STATE_TEN, 10);
        behaviour.registerTransition(STATE_ZERO, STATE_FIFTEEN, 15);
        behaviour.registerTransition(STATE_FIVE, STATE_TEN, 10);
        behaviour.registerTransition(STATE_FIVE, STATE_FIFTEEN, 15);
        behaviour.registerTransition(STATE_TEN, STATE_FIFTEEN, 15);

        addBehaviour(behaviour);
    }

    class ZeroBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            System.out.println("Executing behaviour " + getBehaviourName());
        }

        @Override
        public int onEnd() {
            return queue.poll() + 0;
        }
    }

    class FiveBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            System.out.println("Executing behaviour " + getBehaviourName());
        }

        @Override
        public int onEnd() {
            return queue.poll() + 5;
        }
    }

    class TenBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            System.out.println("Executing behaviour " + getBehaviourName());
        }

        @Override
        public int onEnd() {
            return queue.poll() + 10;
        }
    }

    class FifteenBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            System.out.println("Executing behaviour " + getBehaviourName());
        }
    }
}
