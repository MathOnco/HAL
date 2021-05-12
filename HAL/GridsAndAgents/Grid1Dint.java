package HAL.GridsAndAgents;

import HAL.Interfaces.Grid1D;
import HAL.Util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a 1D Grid of ints
 */
public class Grid1Dint implements Grid1D,Serializable {
    final public int xDim;
    final public int length;
    public boolean wrapX;
    final public int[] field;

    /**
     * creates a new Grid1Dint of length xDim without wraparound
     */
    public Grid1Dint(int xDim) {
        this.xDim=xDim;
        this.length=xDim;
        this.wrapX=false;
        field = new int[this.xDim];
    }

    /**
     * creates a new Grid1Dint of length xDim with optional wraparound
     */
    public Grid1Dint(int xDim, boolean wrapX) {
        this(xDim);
        this.wrapX=wrapX;
    }

    /**
     * gets the current field value at the specified index
     */
    public int Get(int x) {
        return field[x];
    }

    /**
     * returns the complete field as an array
     */
    public int[] GetField() {
        return this.field;
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int x, int val) {
        field[x] = val;
    }

    /**
     * multiplies the current field value at the specified index
     */
    public void Scale(int x, double val) {
        field[x] *= val;
    }

    /**
     * adds to the current field value at the specified index
     */
    public void Add(int x, int val) {
        field[x] += val;
    }

    /**
     * Bounds all values in the current field between min and max
     */
    public void BoundAll(int min, int max) {
        for (int i = 0; i < length; i++) {
            field[i] = Util.Bound(field[i], min, max);
        }
    }

    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAll(int val) {
        Arrays.fill(field, val);
    }

    /**
     * adds specified value to all entries of the curr field
     */
    public void AddAll(int val) {
        for (int i = 0; i < length; i++) {
            field[i] += val;
        }
    }

    /**
     * adds specified value to all entries of the curr field
     */
    public void ScaleAll(double val) {
        for (int i = 0; i < length; i++) {
            field[i] *= val;
        }
    }

    /**
     * copies the array argument into the field
     */

    public void SetAll(int[] vals) {
        System.arraycopy(vals, 0, field, 0, length);
    }

    /**
     * returns the mean value of the grid
     */

    public int GetAvg() {
        int tot = 0;
        for (int i = 0; i < length; i++) {
            tot += field[i];
        }
        return tot / length;
    }

    /**
     * returns the max value in the grid
     */
    public int GetMax() {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < length; i++) {
            max = Math.max(Get(i), max);
        }
        return max;
    }

    /**
     * returns the min value in the grid
     */
    public int GetMin() {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < length; i++) {
            min = Math.min(Get(i), min);
        }
        return min;
    }

    @Override
    public int Xdim() {
        return xDim;
    }

    @Override
    public int Length() {
        return length;
    }

    @Override
    public boolean IsWrapX() {
        return wrapX;
    }
}
