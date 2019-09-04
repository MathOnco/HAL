package HAL.Interfaces;

/**
 * Created by rafael on 4/8/17.
 */
@FunctionalInterface
public interface GAScoreFunction <T> {
    double ScoreMutant(T mut);
}
