package HAL.Interfaces;

import HAL.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface AgentAction<T extends AgentBase> {
    void Action(T agent,int validCount);
}
