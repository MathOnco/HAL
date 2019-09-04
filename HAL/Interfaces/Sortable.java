package HAL.Interfaces;

/**
 * allows for the QuickSort function to be used to sort the object
 */

public interface Sortable {
    /**
     * should return a positive number if first > second, negative if second > first, 0 if equal
     * @param iFirst the index of the first object to compare
     * @param iSecond the index of the second object to compare
     */
    double Compare(int iFirst, int iSecond);//pos if iFirst > iSecond

    /**
     * should swap the objects at these indices so that they switch places in their data structure
     * @param iFirst the index of the first object to compare
     * @param iSecond the index of the second object to compare
     */
    void Swap(int iFirst, int iSecond);

    /**
     * should return the number of objects to sort in the data structure
     */
    int Length();

    /**
     * Runs quicksort on an object that implements Sortable
     *
     * @param greatestToLeast if true, sorting will be form greatest to least, otherwise will be least to greatest
     */
    default<T extends Sortable> void QuickSort(boolean greatestToLeast) {
        _SortHelper(this, 0, this.Length() - 1, greatestToLeast);
    }

    default<T extends Sortable> void _SortHelper(T sortMe, int lo, int hi, boolean greatestToLeast) {
        if (lo < hi) {
            int p = _Partition(sortMe, lo, hi, greatestToLeast);
            _SortHelper(sortMe, lo, p - 1, greatestToLeast);
            _SortHelper(sortMe, p + 1, hi, greatestToLeast);
        }
    }

    default<T extends Sortable> int _Partition(T sortMe, int lo, int hi, boolean greatestToLeast) {
        if (greatestToLeast) {
            for (int j = lo; j < hi; j++) {
                if (sortMe.Compare(hi, j) <= 0) {
                    sortMe.Swap(lo, j);
                    lo++;
                }
            }
            sortMe.Swap(lo, hi);
            return lo;
        } else {
            for (int j = lo; j < hi; j++) {
                if (sortMe.Compare(hi, j) >= 0) {
                    sortMe.Swap(lo, j);
                    lo++;
                }
            }
            sortMe.Swap(lo, hi);
            return lo;
        }
    }

}
