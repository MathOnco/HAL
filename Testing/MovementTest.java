package Testing;

import Framework.GridsAndAgents.AgentBaseSpatial;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;

/**
 * Created by Rafael on 10/28/2017.
 */

class Agent extends AgentSQ2Dunstackable<MovementTest> {

}
public class MovementTest extends AgentGrid2D<Agent> {
    public MovementTest(int x, int y) {
        super(x, y, Agent.class);
    }
}
