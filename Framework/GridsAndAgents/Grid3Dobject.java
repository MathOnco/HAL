package Framework.GridsAndAgents;

import Framework.Tools.PDEequations;
import Framework.Util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Rafael on 10/24/2017.
 */
public class Grid3Dobject<T> extends GridBase3D implements Serializable{
    T[] field;

    public Grid3Dobject(int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ){
        super(xDim,yDim,zDim,wrapX,wrapY,wrapZ);
        field = (T[])(new Object[length]);
        //middleField = new T[numElements];
    }
    public Grid3Dobject(int xDim, int yDim, int zDim){
        this(xDim,yDim,zDim,false,false,false);
    }
    public T Get(int i){return field[i];}

    /**
     * gets the current field value at the specified coordinates
     */
    public T Get(int x, int y, int z) {
        return field[x*yDim*zDim+y*zDim+z];
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, T val){
        field[i]=val;}
    /**
     * sets the current field value at the specified coordinates
     */
    public void Set(int x, int y, int z, T val){
        field[x*yDim*zDim+y*zDim+z]=val;
    }

    public T[] GetField(){ return this.field; }
    /**
     * Bounds all values in the current field between min and max
     */
    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAll(T val){
        Arrays.fill(field,val);
    }

}
