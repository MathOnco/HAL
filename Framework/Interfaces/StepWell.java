package Framework.Interfaces;

@FunctionalInterface
public interface StepWell<T> {
    void Step(T model,int iWell,int tick);
}
