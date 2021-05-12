package HAL.GridsAndAgents;

import HAL.Interfaces.Grid3D;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a 3D Grid of objects
 */
public class Grid3Dobject<T> implements Grid3D,Serializable {
    public final int xDim;
    public final int yDim;
    public final int zDim;
    public final int length;
    public boolean wrapX;
    public boolean wrapY;
    public boolean wrapZ;
    final public T[] field;

    /**
     * creates a new Grid3Dobject of dimensions xDim by yDim by zDim without wraparound
     */
    public Grid3Dobject(int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ) {
        this.xDim=xDim;
        this.yDim=yDim;
        this.zDim=zDim;
        this.length=xDim*yDim*zDim;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
        this.wrapZ=wrapZ;
        field = (T[]) (new Object[length]);
        //middleField = new T[numElements];
    }

    /**
     * creates a new Grid3Dojbect of dimensions xDim by yDim by zDim with optional wraparound
     */
    public Grid3Dobject(int xDim, int yDim, int zDim) {
        this(xDim, yDim, zDim, false, false, false);
    }

    /**
     * gets the field value at the specified index
     */
    public T Get(int i) {
        return field[i];
    }

    /**
     * gets the field value at the specified coordinates
     */
    public T Get(int x, int y, int z) {
        return field[x * yDim * zDim + y * zDim + z];
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, T val) {
        field[i] = val;
    }

    /**
     * sets the current field value at the specified coordinates
     */
    public void Set(int x, int y, int z, T val) {
        field[x * yDim * zDim + y * zDim + z] = val;
    }

    /**
     * returns the complete field as an array
     */
    public T[] GetField() {
        return this.field;
    }

    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAll(T val) {
        Arrays.fill(field, val);
    }

    @Override
    public int Xdim() {
        return xDim;
    }

    @Override
    public int Ydim() {
        return yDim;
    }

    @Override
    public int Zdim() {
        return zDim;
    }

    @Override
    public int Length() {
        return length;
    }

    @Override
    public boolean IsWrapX() {
        return wrapX;
    }

    @Override
    public boolean IsWrapY() {
        return wrapY;
    }

    @Override
    public boolean IsWrapZ() {
        return wrapZ;
    }
}
