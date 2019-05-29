package Testing.ConvergenceTesting;

@FunctionalInterface
public interface ConvergenceReaction3D {
    public double React(double x, double y, double z,int t, double value, double spaceStep);
}
