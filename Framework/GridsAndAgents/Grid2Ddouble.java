package Framework.GridsAndAgents;

import Framework.Interfaces.DoubleToDouble;
import Framework.Tools.Internal.PDEequations;
import Framework.Util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by Rafael on 10/24/2017.
 */
public class Grid2Ddouble extends GridBase2D implements Serializable {
    double[] field;

    public Grid2Ddouble(int xDim, int yDim) {
        super(xDim, yDim, false, false);
        field = new double[this.xDim * this.yDim];
    }

    public Grid2Ddouble(int xDim, int yDim, boolean wrapX, boolean wrapY) {
        super(xDim, yDim, wrapX, wrapY);
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
    public void Mul(int x, int y, double val) {
        field[x * yDim + y] *= val;
    }

    /**
     * multiplies the current field value at the specified index
     */
    public void Mul(int i, double val) {
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
            field[i] *= val;
        }
    }

    /**
     * sets all squares in the field to the specified value
     */
    public void SetAll(double[] vals) {
        System.arraycopy(vals, 0, field, 0, length);
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
        double max = Double.MIN_VALUE;
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
        double left = PDEequations.DisplacedX2D(x - 1, y, field, xDim, yDim, x, false, 0, wrapX);
        double right = PDEequations.DisplacedX2D(x + 1, y, field, xDim, yDim, x, false, 0, wrapX);
        return right - left;
    }

    /**
     * returns the gradient of the field in the Y direction at the coordinates specified
     */
    public double GradientY(int x, int y) {
        double down = PDEequations.DisplacedY2D(x, y - 1, field, xDim, yDim, y, false, 0, wrapY);
        double up = PDEequations.DisplacedY2D(x, y + 1, field, xDim, yDim, y, false, 0, wrapY);
        return up - down;
    }

    /**
     * returns the gradient of the field in the X direction at the coordinates specified, will use the boundary value
     * provided for gradients that go over the boundary
     */
    public double GradientX(int x, int y, double boundaryCond) {
        double left = PDEequations.DisplacedX2D(x - 1, y, field, xDim, yDim, x, true, boundaryCond, wrapX);
        double right = PDEequations.DisplacedX2D(x + 1, y, field, xDim, yDim, x, true, boundaryCond, wrapX);
        return right - left;
    }

    /**
     * returns the gradient of the field in the Y direction at the coordinates specified, will use the boundary value
     * provided for gradients that go over the boundary
     */
    public double GradientY(int x, int y, double boundaryCond) {
        double down = PDEequations.DisplacedY2D(x, y - 1, field, xDim, yDim, y, true, boundaryCond, wrapY);
        double up = PDEequations.DisplacedY2D(x, y + 1, field, xDim, yDim, y, true, boundaryCond, wrapY);
        return up - down;
    }
}
