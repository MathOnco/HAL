package HAL.Interfaces;

import HAL.GridsAndAgents.SphericalAgent2D;

/**
 * Created by philipaxelrod on 7/11/17.
 */
@FunctionalInterface
public interface OverlapForceResponse2D<T extends SphericalAgent2D>{
     double CalcForce(double overlap, T other);
}
