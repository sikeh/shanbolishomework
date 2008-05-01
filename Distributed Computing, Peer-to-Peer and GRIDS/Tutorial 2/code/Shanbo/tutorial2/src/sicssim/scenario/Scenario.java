package sicssim.scenario;

import java.util.*;

import sicssim.config.SicsSimConfig;
import sicssim.coolstream.peers.OriginNode;
import sicssim.coolstream.peers.Peer;
import sicssim.links.ReliableLink;
import sicssim.scenario.ScenarioEvent.Event;

public class Scenario {

    private ScenarioEvent ScenarioList[] = new ScenarioEvent[]{
            new LotteryEvent(1, 1, 1, 0, 0, OriginNode.class, ReliableLink.class),
            new LotteryEvent(SicsSimConfig.NUM_OF_EVENT, SicsSimConfig.EVENT_INTERVAL, SicsSimConfig.NUM_OF_JOIN, SicsSimConfig.NUM_OF_LEAVE, SicsSimConfig.NUM_OF_FAILURE, Peer.class, ReliableLink.class),
    };

    private List<ScenarioEvent> scenarios = Arrays.asList(ScenarioList);
    private Iterator<ScenarioEvent> scenariosIter = scenarios.iterator();
    private ScenarioEvent currentScenario = null;

    //----------------------------------------------------------------------------------
    public boolean hasNextEvent() {
        if (this.currentScenario == null && this.scenariosIter.hasNext())
            this.currentScenario = this.scenariosIter.next();
        else if (this.currentScenario == null)
            return false;

        while (true) {
            if (this.currentScenario.hasNext())
                return true;
            else if (!this.scenariosIter.hasNext())
                return false;
            else
                this.currentScenario = this.scenariosIter.next();
        }
    }

    //----------------------------------------------------------------------------------
    public Event nextEvent() {
        return this.currentScenario.nextEvent();
    }

    //----------------------------------------------------------------------------------
    public void undo() {
        this.currentScenario.undo();
    }
}
