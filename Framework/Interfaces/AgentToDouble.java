package Framework.Interfaces;

import Framework.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface AgentToDouble<T extends AgentBase> {
    double EvalAgent(T agent);
}
