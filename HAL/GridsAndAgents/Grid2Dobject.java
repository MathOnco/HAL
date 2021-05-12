package HAL.GridsAndAgents;

import HAL.Interfaces.Grid2D;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a 2D Grid of objects
 */
public class Grid2Dobject<T> implements Grid2D,Serializable{
    final public int xDim;
    final public int yDim;
    final public int length;
    public boolean wrapX;
    public boolean wrapY;
    final public T[] field;

    /**
     * creates a new Grid2Dobject of dimensions xDim by yDim without wraparound
     */
    public Grid2Dobject(int xDim, int yDim){
        this(xDim,yDim,false,false);
    }

    /**
     * creates a new Grid2Dobject of dimensions xDim by yDim with optional wraparound
     */
    public Grid2Dobject(int xDim, int yDim, boolean wrapX, boolean wrapY){
        this.xDim=xDim;
        this.yDim=yDim;
        this.length=xDim*yDim;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
        field =(T[])new Object[this.xDim * this.yDim];
    }
    /**
     * gets the current field value at the specified index
     */
    public T Get(int i){return field[i];}
    public T[] GetField(){
        return this.field;
    }
    /**
     * gets the current field value at the specified coordinates
     */
    public T Get(int x, int y) { return field[x*yDim+y]; }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, T val){
        field[i]=val;}

    /**
     * sets the current field value at the specified coordinates
     */
    public void Set(int x, int y, T val){ field[x*yDim+y]=val; }

    /**
     * sets all squares in the field to the specified value
     */
    public void SetAll(T val){
        Arrays.fill(field,val);
    }

    /**
     * copies the array of values into the field
     */
    public void SetAll(T[]vals){
        System.arraycopy(vals,0, field,0,length);
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
}
