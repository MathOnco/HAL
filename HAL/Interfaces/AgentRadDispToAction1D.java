package HAL.Interfaces;

import HAL.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface AgentRadDispToAction1D<T extends AgentBase> {
    void Action(T agent, double Xdisp, double distSq);
}
