package Framework.GridsAndAgents;

import Framework.Tools.PDEequations;
import Framework.Util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Rafael on 10/24/2017.
 */
public class Grid1Dobject<T> extends GridBase1D implements Serializable{
    T[] field;
    public Grid1Dobject(int xDim, boolean wrapX){
        super(xDim,wrapX);
        field =(T[])(new Object[this.xDim]);
    }
    public Grid1Dobject(int xDim){
        this(xDim,false);
    }
    /**
     * gets the current field value at the specified index
     */
    public T Get(int x){return field[x];}
    public T[] GetField(){
        return this.field;
    }
    /**
     * sets the current field value at the specified index
     */
    public void Set(int x, T val){
        field[x]=val;}


    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAll(T val){
        Arrays.fill(field,val);
    }
}
