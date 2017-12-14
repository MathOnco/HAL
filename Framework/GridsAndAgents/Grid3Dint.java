package Framework.GridsAndAgents;

import Framework.Util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Rafael on 10/24/2017.
 */
public class Grid3Dint extends GridBase3D implements Serializable{
    int[] field;

    public Grid3Dint(int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ){
        super(xDim,yDim,zDim,wrapX,wrapY,wrapZ);
        field = new int[length];
        //middleField = new int[numElements];
    }
    public Grid3Dint(int xDim, int yDim, int zDim){
        super(xDim,yDim,zDim,false,false,false);

        int numElements = this.xDim * this.yDim * this.zDim;
        field = new int[numElements];
        //middleField = new int[numElements];
    }
    public int Get(int i){return field[i];}

    /**
     * gets the current field value at the specified coordinates
     */
    public int Get(int x, int y, int z) {
        return field[x*yDim*zDim+y*zDim+z];
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, int val){
        field[i]=val;}
    /**
     * sets the current field value at the specified coordinates
     */
    public void Set(int x, int y, int z, int val){
        field[x*yDim*zDim+y*zDim+z]=val;
    }

    //FIXME- copied and pasted by Jake Hogan7/13 from GridDirr2.java
    public int[] GetField(){ return this.field; }
    public void Add(int x, int y, int z, int val){
        field[x*yDim*zDim+y*zDim+z]+=val;
    }
    public void Mul(int x, int y, int z, double val){
        field[x*yDim*zDim+y*zDim+z]*=val;
    }
    public void Mul(int i, double val){
        field[i]*=val;
    }

    /**
     * adds to the current field value at the specified index
     */
    public void Add(int i, int val){
        field[i]+=val;
    }
    /**
     * Bounds all values in the current field between min and max
     */
    public void BoundAll(int min, int max){
        for(int i=0;i<length;i++){
            field[i]= Util.Bound(field[i],min,max);
        }
    }
    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAll(int val){
        Arrays.fill(field,val);
    }
    public void AddAll(int val){
        for (int i = 0; i < length; i++) {
            field[i]+=val;
        }
    }
    public void MulAll(int val){
        for (int i = 0; i < length; i++) {
            field[i]*=val;
        }
    }
    /**
     * gets the average value of all squares in the current field
     */
    public int GetAvg(){
        int tot=0;
        for(int i=0;i<length;i++){
            tot+= field[i];
        }
        return tot/length;
    }

}
