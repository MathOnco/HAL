package Testing.ConvergenceTesting;

@FunctionalInterface
public interface ConvergenceReaction1D {
    public double React(double x,int t,double value,double spaceStep);
}
