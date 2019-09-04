package HAL.GridsAndAgents;

import java.io.Serializable;

/**
 * An agent type without any spatial representation
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
