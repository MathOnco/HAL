package Framework.Interfaces;

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
}
