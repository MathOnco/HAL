package Framework.Interfaces;

import Framework.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface AgentRadDispToAction1D<T extends AgentBase> {
    void Action(T agent, double Xdisp, double distSq);
}
