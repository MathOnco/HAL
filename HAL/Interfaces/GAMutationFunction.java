package HAL.Interfaces;

/**
 * Created by rafael on 4/8/17.
 */
@FunctionalInterface
public interface GAMutationFunction<T>{
    T[] CreateNextGen(T[] parents,double[] scores,int generation);
}
