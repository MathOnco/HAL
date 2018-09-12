package Framework.GridsAndAgents;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a 2D Grid of objects
 */
public class Grid2Dobject<T> extends GridBase2D implements Serializable{
    T[] field;

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
        super(xDim,yDim,wrapX,wrapY);
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
}
