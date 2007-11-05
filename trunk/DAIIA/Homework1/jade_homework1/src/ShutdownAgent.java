
import jade.core.Agent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;

/**
 *
 * @author Sike Huang
 */
public class ShutdownAgent extends Agent {
    
    @Override
    protected void setup() {
        
        System.out.println("Shut down in 10 seconds");
        
        WakerBehaviour firstWaker = new WakerBehaviour(this, 10000) {
            @Override
            protected void handleElapsedTimeout() {
                System.out.println("Shut down now");
            } 
        };
        
        TickerBehaviour ticker = new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                System.out.println("tick: " + getTickCount());
            }
        };
        
        ParallelBehaviour firstParaller = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ANY);
        firstParaller.addSubBehaviour(firstWaker);
        firstParaller.addSubBehaviour(ticker);
        
        // "progress indicator" 
        CyclicBehaviour cyclic = new CyclicBehaviour(this) {
            @Override
            public void action() {
                System.out.print(".");
            }
        };
        
        WakerBehaviour secondWaker = new WakerBehaviour(this, 50) {
            @Override
            protected void handleElapsedTimeout() {
                System.out.println("Succeed!");
            }            
        };
        
        ParallelBehaviour secondParaller = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ANY);
        secondParaller.addSubBehaviour(cyclic);
        secondParaller.addSubBehaviour(secondWaker);
        
        SequentialBehaviour sequential = new SequentialBehaviour(this);
        sequential.addSubBehaviour(firstParaller);
        sequential.addSubBehaviour(secondParaller);
        addBehaviour(sequential);
    }

}
