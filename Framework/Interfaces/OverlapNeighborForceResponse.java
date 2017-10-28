package Framework.Interfaces;

import Framework.GridsAndAgents.AgentBase;

import java.util.function.BiFunction;

/**
 * Created by philipaxelrod on 7/11/17.
 */
@FunctionalInterface
public interface OverlapNeighborForceResponse<T> {
    double CalcForce(double overlap, T other);
}
