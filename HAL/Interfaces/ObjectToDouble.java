package HAL.Interfaces;

import HAL.GridsAndAgents.AgentBase;

@FunctionalInterface
public interface ObjectToDouble<T> {
    double GenDouble(T input);
}
