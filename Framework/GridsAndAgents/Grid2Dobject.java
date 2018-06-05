package Framework.GridsAndAgents;

import Framework.Interfaces.DoubleToDouble;
import Framework.Tools.PDEequations;
import Framework.Util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by Rafael on 10/24/2017.
 */
public class Grid2Dobject<T> extends GridBase2D implements Serializable{
    T[] field;
    public Grid2Dobject(int xDim, int yDim){
        this(xDim,yDim,false,false);
    }
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
     * sets all squares in current the field to the specified value
     */
    public void SetAll(T val){
        Arrays.fill(field,val);
    }

    public void SetAll(T[]vals){
        System.arraycopy(vals,0, field,0,length);
    }
}
