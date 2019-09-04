package HAL.Interfaces;

import HAL.GridsAndAgents.SphericalAgent3D;

/**
 * Created by philipaxelrod on 7/11/17.
 */
@FunctionalInterface
public interface OverlapForceResponse3D <T extends SphericalAgent3D>{
     double CalcForce(double overlap, T other);
}
