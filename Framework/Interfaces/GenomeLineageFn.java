package Framework.Interfaces;

import Framework.Tools.GenomeInfo;

import java.util.ArrayList;

@FunctionalInterface
public interface GenomeLineageFn<T extends GenomeInfo>{
    void GenomeLineageFn(ArrayList<T> lineage);
}

