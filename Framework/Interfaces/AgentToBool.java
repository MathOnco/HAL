package Framework.Interfaces;

import Framework.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface AgentToBool<T extends AgentBase> {
    boolean EvalAgent(T agent);
}
