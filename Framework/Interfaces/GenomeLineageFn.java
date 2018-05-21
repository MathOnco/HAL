package Framework.Interfaces;

import Framework.Tools.GenomeBase;

import java.util.ArrayList;

@FunctionalInterface
public interface GenomeLineageFn<T extends GenomeBase>{
    void GenomeLineageFn(ArrayList<T> lineage);
}

