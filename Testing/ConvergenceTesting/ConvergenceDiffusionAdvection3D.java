package Testing.ConvergenceTesting;


import HAL.GridsAndAgents.PDEGrid3D;

@FunctionalInterface
public interface ConvergenceDiffusionAdvection3D {
    void DiffusionAdvection3D(PDEGrid3D grid, double[]rateConstants);
}
