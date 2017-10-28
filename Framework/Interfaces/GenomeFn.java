package Framework.Interfaces;

import Framework.Tools.GenomeInfo;

@FunctionalInterface
public interface GenomeFn<T extends GenomeInfo>{
    void GenomeFn(T c);
}

