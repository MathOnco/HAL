package HAL.Interfaces;

import HAL.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface AgentRadDispToAction3D<T extends AgentBase> {
    void Action(T agent, double Xdisp, double Ydisp,double Zdisp, double distSq);
}
