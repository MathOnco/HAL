package Testing.ConvergenceTesting;


import HAL.GridsAndAgents.PDEGrid2D;

@FunctionalInterface
public interface ConvergenceDiffusionAdvection2D {
    void DiffusionAdvection2D(PDEGrid2D grid,double[]rateConstants);
}
