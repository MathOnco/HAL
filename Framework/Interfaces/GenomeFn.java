package Framework.Interfaces;

import Framework.Tools.GenomeBase;

@FunctionalInterface
public interface GenomeFn<T extends GenomeBase>{
    void GenomeFn(T c);
}

