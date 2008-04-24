package org.kth.sim;

import java.util.*;

import org.kth.sim.events.DelayEvent;
import org.kth.sim.events.LotteryEvent;
import org.kth.sim.events.SaveSONEvent;
import org.kth.sim.interfaces.ScenEvent;

public class Scenario {

    public static int seed = 7;

    static int choice = 0; //detailsLevel of the validation Use: 0: "Only successors", 1: "Successors and successor lists", 2: "Complete(including fingers)"
    static boolean debug = false;
    static long maxSimTime = 30000;
    static int N = 128;

    private ScenEvent scenArr[] = new ScenEvent[]{
            //              NrEvents,    Time, Joins, 	Fails
//			new LotteryEvent(     100,      20,     2,      1),
			new LotteryEvent(     100,      20,     7,      3),
//			new LotteryEvent(     20,      20,     7,      3),

            /* typical senario */
//            new LotteryEvent(     40,      20,     7,      3),
//			new LotteryEvent(     30,      20,     7,      3),

//            new LotteryEvent(100, 50, 7, 0),
//            new LotteryEvent(25, 0, 0, 3),


            new DelayEvent(3000),
            new SaveSONEvent()    // this should always be the last event in the scenario
    };

    private List<ScenEvent> scenarios = Arrays.asList(scenArr);

    private Iterator<ScenEvent> lotteryIter = scenarios.iterator();
    private ScenEvent currLottery = null;

    public static Scenario instance = new Scenario();

    private Scenario() {
    }

    private Scenario(boolean loadnmerge) {
        if (loadnmerge) {
            scenArr = new ScenEvent[]{
                    //new LoadSONEvent(),
                    //new MergerEvent(-1, 1),
                    new LotteryEvent(225, 10, 5, 1)
            };
            scenarios = Arrays.asList(scenArr);
            lotteryIter = scenarios.iterator();
        }
    }

    public boolean hasNextEvent() {
        if (currLottery == null && lotteryIter.hasNext())
            currLottery = lotteryIter.next();
        else if (currLottery == null)
            return false;

        do {
            if (currLottery.hasNext())
                return true;
            else if (!lotteryIter.hasNext())
                return false;
            else {
                if (!lotteryIter.hasNext())
                    return false;
                else
                    currLottery = lotteryIter.next();
            }
        } while (1 == 1);
    }

    public LotteryEvent.Event nextEvent() {
        return currLottery.nextEvent();
    }

    public static Scenario getInstance() {
        return instance;
    }

    public static Scenario newInstance(boolean loadnmerge) {
        if (loadnmerge)
            instance = new Scenario(loadnmerge);
        else
            instance = new Scenario();
        return instance;
    }

    public void undo() {
        currLottery.undo();
    }

}
