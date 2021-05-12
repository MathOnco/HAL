package HAL.GridsAndAgents;

import HAL.Interfaces.Grid2D;
import HAL.Util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a 2D Grid of ints
 */
public class Grid2Dint implements Grid2D,Serializable {
    final public int xDim;
    final public int yDim;
    final public int length;
    public boolean wrapX;
    public boolean wrapY;
    final public int[] field;

    /**
     * creates a new Grid2Dint of dimensions xDim by yDim without wraparound
     */
    public Grid2Dint(int xDim, int yDim) {
        this(xDim,yDim,false,false);
    }

    /**
     * creates a new Grid2Dint of dimensions xDim by yDim with optional wraparound
     */
    public Grid2Dint(int xDim, int yDim, boolean wrapX, boolean wrapY) {
        this.xDim=xDim;
        this.yDim=yDim;
        this.length=xDim*yDim;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
        field = new int[this.xDim * this.yDim];
    }

    /**
     * gets the current field value at the specified index
     */
    public int Get(int i) {
        return field[i];
    }

    /**
     * returns the complete field as an array
     */
    public int[] GetField() {
        return this.field;
    }

    /**
     * gets the current field value at the specified coordinates
     */
    public int Get(int x, int y) {
        return field[x * yDim + y];
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, int val) {
        field[i] = val;
    }

    /**
     * sets the current field value at the specified coordinates
     */
    public void Set(int x, int y, int val) {
        field[x * yDim + y] = val;
    }

    /**
     * adds to the current field value at the specified coordinates
     */
    public void Add(int x, int y, int val) {
        field[x * yDim + y] += val;
    }

    /**
     * multiplies the current field value at the specified coordinates
     */
    public void Scale(int x, int y, double val) {
        field[x * yDim + y] *= val;
    }

    /**
     * multiplies the current field value at the specified index
     */
    public void Scale(int i, double val) {
        field[i] *= val;
    }

    /**
     * adds to the current field value at the specified index
     */
    public void Add(int i, int val) {
        field[i] += val;
    }

    /**
     * Bounds all values in the current field between min and max
     */
    public void BoundAll(int min, int max) {
        for (int i = 0; i < length; i++) {
            field[i] = Util.Bound(field[i], min, max);
        }
    }

    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAll(int val) {
        Arrays.fill(field, val);
    }

    /**
     * adds specified value to all entries of the curr field
     */
    public void AddAll(int val) {
        for (int i = 0; i < length; i++) {
            field[i] += val;
        }
    }

    /**
     * adds specified value to all entries of the curr field
     */
    public void MulAll(double val) {
        for (int i = 0; i < length; i++) {
            Scale(i,val);
        }
    }

    /**
     * sets all squares in the field to the specified value
     */
    public void SetAll(int[] vals) {
        System.arraycopy(vals, 0, field, 0, length);
    }

    /**
     * gets the average value of all squares in the current field
     */
    public int GetAvg() {
        int tot = 0;
        for (int i = 0; i < length; i++) {
            tot += field[i];
        }
        return tot / length;
    }

    /**
     * returns the max value in the grid
     */
    public int GetMax() {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < length; i++) {
            max = Math.max(Get(i), max);
        }
        return max;
    }

    /**
     * returns the min value in the grid
     */
    public int GetMin() {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < length; i++) {
            min = Math.min(Get(i), min);
        }
        return min;
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

//    public Grid2Ddouble DotProduct(Grid2Ddouble rightMatrix){
//        if(yDim!=rightMatrix.xDim){
//            throw new IllegalArgumentException("xDim of this and yDim of other must match! this.xDim: "+xDim+" other.yDim: "+yDim);
//        }
//        Grid2Ddouble out=new Grid2Ddouble(xDim,rightMatrix.yDim);
//        for (int x = 0; x < xDim; x++) {
//            for (int y = 0; y < rightMatrix.yDim; y++) {
//                //fill in values by dot product
//                for (int i = 0; i < yDim; i++) {
//                    out.Add(x,y,Get(i,y)*rightMatrix.Get(x,i));
//                }
//            }
//        }
//        return out;
//    }
//    public Grid2Ddouble DotProductT(Grid2Ddouble rightMatrixToTranspose){
//        if(yDim!=rightMatrixToTranspose.yDim){
//            throw new IllegalArgumentException("xDim of this and xDim of other must match! this.xDim: "+xDim+" other.xDim: "+yDim);
//        }
//        Grid2Ddouble out=new Grid2Ddouble(xDim,rightMatrixToTranspose.yDim);
//        for (int x = 0; x < xDim; x++) {
//            for (int y = 0; y < rightMatrixToTranspose.xDim; y++) {
//                //fill in values by dot product
//                for (int i = 0; i < yDim; i++) {
//                    out.Add(x,y,Get(i,y)*rightMatrixToTranspose.Get(i,x));
//                }
//            }
//        }
//        return out;
//    }
}
