package HAL.Tools.MultiWellExperiment;

@FunctionalInterface
public interface DrawWell<T> {
    int GetPixColor(T model, int x, int y);
}
