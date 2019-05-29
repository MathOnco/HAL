package Testing.ConvergenceTesting;

@FunctionalInterface
public interface ConvergenceReaction2D {
    public double React(double x,double y, int t, double value, double spaceStep);
}
