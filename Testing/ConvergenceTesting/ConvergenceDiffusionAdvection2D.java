package Testing.ConvergenceTesting;


import Framework.GridsAndAgents.PDEGrid2D;

@FunctionalInterface
public interface ConvergenceDiffusionAdvection2D {
    void DiffusionAdvection1D(double[]rateConstants,PDEGrid2D grid);
}
