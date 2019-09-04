package HAL.Interfaces;

import HAL.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface AgentRadDispToAction2D<T extends AgentBase> {
    void Action(T agent,double Xdisp,double Ydisp,double distSq);
}
