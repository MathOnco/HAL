package HAL.Tools.PhylogenyTracker;

@FunctionalInterface
public interface GenomeFn<T extends Genome>{
    void GenomeFn(T c);
}

