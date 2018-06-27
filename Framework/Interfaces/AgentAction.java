package Framework.Interfaces;

import Framework.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface AgentAction<T extends AgentBase> {
    void Action(T agent,int validCount);
}
