package Framework.Interfaces;

@FunctionalInterface
public interface DrawWell<T> {
    int GetPixColor(T model, int x, int y);
}
