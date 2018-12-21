package Framework.Tools.ODESolver;

public interface Derivative {
    void Set(double t, double[]state, double[]derivativesOut);
}
