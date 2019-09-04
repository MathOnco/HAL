package HAL.GridsAndAgents;

import HAL.Interfaces.Grid1D;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a 1D Grid of objects
 */
public class Grid1Dobject<T> implements Grid1D,Serializable {
    final public int xDim;
    final public int length;
    public boolean wrapX;
    T[] field;

    /**
     * creates a new Grid1Dobject of length xDim wtihout wraparound
     */
    public Grid1Dobject(int xDim) {
        this.xDim=xDim;
        this.length=xDim;
        this.wrapX=false;
        field = (T[]) (new Object[this.xDim]);
    }

    /**
     * creates a new Grid1Ddouble of length xDim with optional wraparound
     */
    public Grid1Dobject(int xDim, boolean wrapX) {
        this(xDim);
        this.wrapX=wrapX;
    }

    /**
     * gets the current field value at the specified index
     */
    public T Get(int x) {
        return field[x];
    }

    public T[] GetField() {
        return this.field;
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int x, T val) {
        field[x] = val;
    }


    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAll(T val) {
        Arrays.fill(field, val);
    }

    @Override
    public int Xdim() {
        return 0;
    }

    @Override
    public int Length() {
        return 0;
    }

    @Override
    public boolean IsWrapX() {
        return false;
    }
}
