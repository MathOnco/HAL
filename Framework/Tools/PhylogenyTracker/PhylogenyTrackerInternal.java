package Framework.Tools.PhylogenyTracker;

import java.util.Iterator;

/**
 * Created by bravorr on 8/4/17.
 */
class PhylogenyTrackerInternal<T extends Genome> implements Iterable<T> {
    int nGenomesEver;
    int nLivingGenomes;
    int nTreeGenomes;
    long totalPop;
    final public boolean removeEmptyLeaves;
    T progenitor;
    T listFirst;

    public PhylogenyTrackerInternal(T progenitor, boolean removeEmptyLeaves) {
        this.progenitor = progenitor;
        this.removeEmptyLeaves = removeEmptyLeaves;
        this.listFirst=progenitor;
    }

    @Override
    public Iterator<T> iterator() {
        return new myIter(progenitor);
    }

    private class myIter implements Iterator<T> {
        T curr;

        myIter(T last) {
            this.curr = last;
        }

        @Override
        public boolean hasNext() {
            return curr != null;
        }

        @Override
        public T next() {
            T ret=curr;
            curr = (T) curr.prev;
            return ret;
        }
    }

    public int GetNumGenomes() {
        return nGenomesEver;
    }

    public int GetNumLivingGenomes() {
        return nLivingGenomes;
    }

    public int GetNumTreeGenomes() {
        return nTreeGenomes;
    }

    public long GetTotalPop() {
        return totalPop;
    }

    public T GetProgentior(){
        return progenitor;
    }
}
