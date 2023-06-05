package HAL.GridsAndAgents;

import HAL.Interfaces.Grid2D;
import HAL.Tools.Internal.PDEequations;
import HAL.Util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a 2D Grid of doubles
 */
public class Grid2Ddouble implements Grid2D,Serializable {
    final public int xDim;
    final public int yDim;
    final public int length;
    public boolean wrapX;
    public boolean wrapY;
    final public double[] field;

    /**
     * creates a new Grid2Ddouble of length xDim without wraparound
     */
    public Grid2Ddouble(int xDim, int yDim) {
        this(xDim,yDim,false,false);
    }

    /**
     * creates a new Grid2Ddouble of length xDim with optional wraparound
     */
    public Grid2Ddouble(int xDim, int yDim, boolean wrapX, boolean wrapY) {
        this.xDim=xDim;
        this.yDim=yDim;
        this.length=xDim*yDim;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
        field = new double[this.xDim * this.yDim];
    }

    /**
     * gets the current field value at the specified index
     */
    public double Get(int i) {
        return field[i];
    }

    /**
     * returns the complete field as an array
     */
    public double[] GetField() {
        return this.field;
    }

    /**
     * gets the current field value at the specified coordinates
     */
    public double Get(int x, int y) {
        return field[x * yDim + y];
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, double val) {
        field[i] = val;
    }

    /**
     * sets the current field value at the specified coordinates
     */
    public void Set(int x, int y, double val) {
        field[x * yDim + y] = val;
    }

    /**
     * adds to the current field value at the specified coordinates
     */
    public void Add(int x, int y, double val) {
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
    public void Add(int i, double val) {
        field[i] += val;
    }

    /**
     * Bounds all values in the current field between min and max
     */
    public void BoundAll(double min, double max) {
        for (int i = 0; i < length; i++) {
            field[i] = Util.Bound(field[i], min, max);
        }
    }

    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAll(double val) {
        Arrays.fill(field, val);
    }

    /**
     * adds specified value to all entries of the curr field
     */
    public void AddAll(double val) {
        for (int i = 0; i < length; i++) {
            field[i] += val;
        }
    }

    /**
     * multiplies all entries in the field by the value
     */
    public void MulAll(double val) {
        for (int i = 0; i < length; i++) {
            Scale(i,val);
        }
    }

    /**
     * sets all squares in the field to the specified value
     */
    public void SetAll(double[] vals) {
        System.arraycopy(vals, 0, field, 0, length);
    }

    public double GetSum(){
        double tot = 0;
        for (int i = 0; i < length; i++) {
            tot += field[i];
        }
        return tot;
    }

    /**
     * gets the average value of all squares in the current field
     */
    public double GetAvg() {
        double tot = 0;
        for (int i = 0; i < length; i++) {
            tot += field[i];
        }
        return tot / length;
    }

    /**
     * returns the max value in the grid
     */
    public double GetMax() {
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < length; i++) {
            max = Math.max(Get(i), max);
        }
        return max;
    }

    /**
     * returns the min value in the grid
     */
    public double GetMin() {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < length; i++) {
            min = Math.min(Get(i), min);
        }
        return min;
    }

    /**
     * returns the gradient of the field in the X direction at the coordinates specified
     */
    public double GradientX(int x, int y) {
        double left = PDEequations.DisplacedX2D(field,x-1,y, xDim, yDim, wrapX,(X,Y)->Get(X+1,Y));
        double right = PDEequations.DisplacedX2D(field,x + 1, y, xDim, yDim,wrapX,(X,Y)->Get(X-1,Y));
        return right - left;
    }

    /**
     * returns the gradient of the field in the Y direction at the coordinates specified
     */
    public double GradientY(int x, int y) {
        double down = PDEequations.DisplacedX2D(field,x,y-1, xDim, yDim, wrapX,(X,Y)->Get(X+1,Y));
        double up = PDEequations.DisplacedX2D(field,x, y+1, xDim, yDim,wrapX,(X,Y)->Get(X-1,Y));
        return up - down;
    }

    /**
     * returns the gradient of the field in the X direction at the coordinates specified, will use the boundary value
     * provided for gradients that go over the boundary
     */
    public double GradientX(int x, int y, double boundaryCond) {
        double left = PDEequations.DisplacedX2D(field,x-1,y, xDim, yDim, wrapX,(X,Y)->boundaryCond);
        double right = PDEequations.DisplacedX2D(field,x + 1, y, xDim, yDim,wrapX,(X,Y)->boundaryCond);
        return right - left;
    }

    /**
     * returns the gradient of the field in the Y direction at the coordinates specified, will use the boundary value
     * provided for gradients that go over the boundary
     */
    public double GradientY(int x, int y, double boundaryCond) {
        double down = PDEequations.DisplacedY2D(field,x,y-1, xDim, yDim, wrapX,(X,Y)->boundaryCond);
        double up = PDEequations.DisplacedY2D(field,x, y+1, xDim, yDim,wrapX,(X,Y)->boundaryCond);
        return up - down;
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
