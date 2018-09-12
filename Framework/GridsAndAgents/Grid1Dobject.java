package Framework.GridsAndAgents;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a 1D Grid of objects
 */
public class Grid1Dobject<T> extends GridBase1D implements Serializable {
    T[] field;

    /**
     * creates a new Grid1Dobject of length xDim wtihout wraparound
     */
    public Grid1Dobject(int xDim) {
        this(xDim, false);
    }

    /**
     * creates a new Grid1Ddouble of length xDim with optional wraparound
     */
    public Grid1Dobject(int xDim, boolean wrapX) {
        super(xDim, wrapX);
        field = (T[]) (new Object[this.xDim]);
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
}
