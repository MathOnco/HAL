package HAL.Interfaces;

import HAL.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface AgentToBool<T extends AgentBase> {
    boolean EvalAgent(T agent);
}
