package HAL.Interfaces;

import HAL.Tools.PhylogenyTracker.Genome;

@FunctionalInterface
public interface GetGenomeAttrs<T extends Genome> {
    String GetAttrs(T genome);
}
