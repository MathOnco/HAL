package Framework.GridsAndAgents;

import Framework.Utils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Rafael on 10/24/2017.
 */
public class Grid3Ddouble extends GridBase3D implements Serializable{
    double[] field;

    public Grid3Ddouble(int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ){
        super(xDim,yDim,zDim,wrapX,wrapY,wrapZ);
        field = new double[length];
        //middleField = new double[numElements];
    }
    public Grid3Ddouble(int xDim, int yDim, int zDim){
        super(xDim,yDim,zDim,false,false,false);

        int numElements = this.xDim * this.yDim * this.zDim;
        field = new double[numElements];
        //middleField = new double[numElements];
    }
    public double Get(int i){return field[i];}

    /**
     * gets the current field value at the specified coordinates
     */
    public double Get(int x, int y, int z) {
        return field[x*yDim*zDim+y*zDim+z];
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, double val){
        field[i]=val;}
    /**
     * sets the current field value at the specified coordinates
     */
    public void Set(int x, int y, int z, double val){
        field[x*yDim*zDim+y*zDim+z]=val;
    }

    public double[] GetField(){ return this.field; }
    public void Add(int x, int y, int z, double val){
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
    public void Add(int i, double val){
        field[i]+=val;
    }
    /**
     * Bounds all values in the current field between min and max
     */
    public void BoundAll(double min, double max){
        for(int i=0;i<length;i++){
            field[i]= Utils.Bound(field[i],min,max);
        }
    }
    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAll(double val){
        Arrays.fill(field,val);
    }
    public void AddAll(double val){
        for (int i = 0; i < length; i++) {
            field[i]+=val;
        }
    }
    public void MulAll(double val){
        for (int i = 0; i < length; i++) {
            field[i]*=val;
        }
    }
    /**
     * gets the average value of all squares in the current field
     */
    public double GetAvg(){
        double tot=0;
        for(int i=0;i<length;i++){
            tot+= field[i];
        }
        return tot/length;
    }

}
