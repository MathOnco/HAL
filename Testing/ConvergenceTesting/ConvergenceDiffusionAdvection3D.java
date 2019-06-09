package Testing.ConvergenceTesting;


import Framework.GridsAndAgents.PDEGrid3D;

@FunctionalInterface
public interface ConvergenceDiffusionAdvection3D {
    void DiffusionAdvection3D(PDEGrid3D grid, double[]rateConstants);
}
