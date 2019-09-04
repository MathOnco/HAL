package HAL.Interfaces;

@FunctionalInterface
public interface OdeSolution {
    void Get(double t,double[]out);
}
