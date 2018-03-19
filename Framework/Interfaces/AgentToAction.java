package Framework.Interfaces;

import Framework.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface AgentToAction<T extends AgentBase> {
    void Action(T agent,int validCount);
}
