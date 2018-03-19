package Framework.Interfaces;

/**
 * Created by philipaxelrod on 7/11/17.
 */
@FunctionalInterface
public interface OverlapNeighborForceResponse<T> {
    double CalcForce(double overlap, T other);
}
