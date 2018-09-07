package Framework.GridsAndAgents;

import java.io.Serializable;

/**
 * extend the AgentSQ2D class if you want agents that exist on a 2D discrete lattice
 * with the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended AgentGrid2D class that the agents will live in
 * Created by rafael on 11/18/16.
 */
abstract public class Agent0D<T extends AgentGrid0D> extends AgentBase<T> implements Serializable {

    /**
     * deletes the agent
     */
    public void Dispose() {
        //kills agent
        G.RemoveAgent(this);
        if (myNodes != null) {
            myNodes.DisposeAll();
        }
    }

    /**
     * returns the age of the agent, in ticks. Be sure to use IncTick on the AgentGrid appropriately for this function
     * to work.
     */
    public int Age() {
        return birthTick - G.GetTick();
    }
}
