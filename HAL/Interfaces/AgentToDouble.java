package HAL.Interfaces;

import HAL.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface AgentToDouble<T extends AgentBase> {
    double EvalAgent(T agent);
}
