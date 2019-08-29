package Framework.Interfaces;

import Framework.Tools.PhylogenyTracker.Genome;

@FunctionalInterface
public interface GetGenomeAttrs<T extends Genome> {
    String[]GetAttrs(T genome);
}
