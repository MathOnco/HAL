package HAL.Interfaces;

/**
 * Created by Rafael on 8/3/2017.
 */
@FunctionalInterface
public interface GetMutationInfo <T>{
    String MutantToString(T mutant);
}
