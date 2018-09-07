package Framework.GridsAndAgents;
import Framework.Interfaces.Coords2DDouble;
import Framework.Interfaces.Coords2DSetArray;
import Framework.Interfaces.GridDiff2MultiThreadFunction;
import Framework.Tools.Internal.PDEequations;
import Framework.Util;

import java.io.Serializable;
import java.util.Arrays;

import static Framework.Tools.Internal.PDEequations.*;


/**
 * PDEGrid2D class facilitates 2D diffusion with two arrays of doubles called fields
 * the intended usage is that during a diffusion tick, the current values will be read, and the prev values will be written to
 * after updates, Update is called to set the prev field as the current field.
 */
public class PDEGrid2D extends GridBase2D implements Serializable {
    protected double[] nextField;
    protected double[] field;
    //double[] intermediateScratch;
    protected double[] scratch;
    protected double[] adiScratch;
    protected double[] maxDifscratch;
    boolean adiOrder = true;
    boolean adiX = true;

    public PDEGrid2D(int xDim, int yDim) {
        super(xDim, yDim, false, false);
        field = new double[this.xDim * this.yDim];
        nextField = new double[this.xDim * this.yDim];
    }

    public PDEGrid2D(int xDim, int yDim, boolean wrapX, boolean wrapY) {
        super(xDim, yDim, wrapX, wrapY);
        field = new double[this.xDim * this.yDim];
        nextField = new double[this.xDim * this.yDim];
    }

    /**
     * runs diffusion on the current field using the ADI (alternating direction implicit) method. without a
     * boundaryValue argument, a zero flux boundary is imposed. wraparound will not work with ADI. ADI is numerically
     * stable at any diffusion rate. An update is automatically made after the ADI computation, so ADI diffusion should
     * be done after all other changes to the PDEGrid are applied.
     */
    public void DiffusionADIupdate(double diffCoef) {
        EnsureScratch();
        EnsureADIscratch();
        DiffusionADI2(true, field, adiScratch, scratch, xDim, yDim, diffCoef / 2, false, 0);
        DiffusionADI2(false, adiScratch, field, scratch, xDim, yDim, diffCoef / 2, false, 0);
        Update();
    }

    /**
     * runs diffusion on the current field using the ADI (alternating direction implicit) method. ADI is numerically
     * stable at any diffusion rate. Adding a boundary value to the function call will cause boundary conditions to be
     * imposed. An update is automatically made after the ADI computation, so ADI diffusion should be done after all
     * other changes to the PDEGrid are applied.
     */
    public void DiffusionADIupdate(double diffCoef, double boundaryValue) {
        EnsureScratch();
        DiffusionADI2(true, field, adiScratch, scratch, xDim, yDim, diffCoef / 2, true, boundaryValue);
        DiffusionADI2(false, adiScratch, field, scratch, xDim, yDim, diffCoef / 2, true, boundaryValue);
        Update();
    }

    /**
     * gets the prev field value at the specified coordinates
     */
    public double Get(int x, int y) {
        return field[x * yDim + y];
    }

    /**
     * gets the prev field value at the specified index
     */
    public double Get(int i) {
        return field[i];
    }

    /**
     * sets the prev field value at the specified coordinates
     */
    public void Set(int x, int y, double val) {
        nextField[x * yDim + y] = val - field[x * yDim + y];
    }

    /**
     * sets the prev field value at the specified index
     */
    public void Set(int i, double val) {
        nextField[i] = val - field[i];
    }

    /**
     * sets the prev field value at the specified coordinates
     */
    public void Add(int x, int y, double val) {
        nextField[x * yDim + y] += val;
    }

    /**
     * adds to the prev field value at the specified index
     */
    public void Add(int i, double val) {
        nextField[i] += val;
    }

    /**
     * multiplies a value in the “current field” and adds the change to the “next field”
     */
    public void Mul(int i, double val) {
        nextField[i] += field[i] * (val - 1);
    }

    /**
     * multiplies a value in the “current field” and adds the change to the “next field”
     */
    public void Mul(int x, int y, double val) {
        nextField[x * yDim + y] += field[x * yDim + y] * (val - 1);
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
     * adds the next field into the current field, also increments the tick.
     */
    public void Update() {
        for (int i = 0; i < nextField.length; i++) {
            field[i] += nextField[i];
        }
        Arrays.fill(nextField, 0);
        IncTick();
    }

    /**
     * runs advection, which moves the concentrations using a constant flow with the x and y velocities passed. this
     * version of the function assumes wrap-around, so there can be no net flux of concentrations.
     */
    public void Advection(double xVel, double yVel) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection1stOrder(x, y, field, nextField, xDim, yDim, xVel, yVel, false, 0.0);
            }
        }
    }

    /**
     * runs advection as described above with a boundary value, meaning that the boundary value will advect in from the
     * upwind direction, and the concentration will disappear in the downwind direction.
     */
    public void Advection(double xVel, double yVel, double boundaryValue) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection1stOrder(x, y, field, nextField, xDim, yDim, xVel, yVel, true, boundaryValue);
            }
        }
    }


    /**
     * runs diffusion on the current field, adding the deltas to the next field. This form of the function assumes
     * either a reflective or wrapping boundary (depending on how the PDEGrid was specified). the diffCoef variable is
     * the nondimensionalized diffusion conefficient. If the dimensionalized diffusion coefficient is x then diffCoef
     * can be found by computing (x*SpaceStep)/TimeStep^2 Note that if the diffCoef exceeds 0.25, this diffusion method
     * will become numerically unstable.
     */
    public void Diffusion(double diffCoef) {
        if (diffCoef > 0.25) {
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: " + diffCoef);
        }
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2(x, y, field, nextField, xDim, yDim, diffCoef, false, 0.0, wrapX, wrapY);
            }
        }
    }

    /**
     * has the same effect as the above diffusion function without the boundary value argument, except rather than
     * assuming zero flux, the boundary condition is set to either the boundaryValue, or wrap around
     */
    public void Diffusion(double diffCoef, double boundaryValue) {
        if (diffCoef > 0.25) {
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: " + diffCoef);
        }
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2(x, y, field, nextField, xDim, yDim, diffCoef, true, boundaryValue, wrapX, wrapY);
            }
        }
    }

    /**
     * sets all squares in the next field to the specified value
     */
    public void SetAll(double val) {
        for (int i = 0; i < length; i++) {
            Set(i, val);
        }
    }


    /**
     * multiplies all values in the “current field” and puts the results into the “next field”
     */
    public void MulAll(double val) {
        for (int i = 0; i < length; i++) {
            Mul(i, val);
        }
    }

    /**
     * sets all squares in the next field using the vals array
     */
    public void SetAll(double[] vals) {
        for (int i = 0; i < length; i++) {
            Set(i, vals[i]);
        }
    }

    /**
     * adds specified value to all entries of the next field
     */
    public void AddAll(double val) {
        for (int i = 0; i < length; i++) {
            Add(i, val);
        }
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
     * returns the maximum difference as stored on the next field, call right before calling Update()
     */
    public double MaxDifNext() {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs((nextField[i])));
        }
        return maxDif;
    }

    /**
     * like MaxDifNext only the differences are scaled relative to the value in the current field. the denomOffset is
     * used to prevent a division by zero
     */
    public double MaxDifNextScaled(double denomOffset) {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs(nextField[i] / (Math.abs(field[i]) + denomOffset)));
        }
        return maxDif;
    }


    /**
     * like MaxDifNext only the differences are computed by comparing the current field to the compareTo argument
     */
    public double MaxDifOther(double[] compareTo) {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs(field[i] - compareTo[i]));
        }
        return maxDif;
    }

    /**
     * like MaxDifNext only the differences are computed by comparing the current field with the field state as it was
     * the last time MaxDifRecord was called
     */
    public double MaxDifRecord() {
        if (maxDifscratch == null) {
            maxDifscratch = new double[length];
        }
        double ret = MaxDifOther(maxDifscratch);
        System.arraycopy(field, 0, maxDifscratch, 0, length);
        return ret;
    }

    /**
     * like MaxDifOther only the differences are scaled relative to the value in the current field. the denomOffset is
     * used to prevent a division by zero
     */
    public double MaxDifOtherScaled(double[] compareTo, double denomOffset) {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs(field[i] - compareTo[i]) / (compareTo[i] + denomOffset));
        }
        return maxDif;
    }


    /**
     * like MaxDifRecord only the differences are scaled relative to the value in the current field. the denomOffset is
     * used to prevent a division by zero
     */
    public double MaxDifRecordScaled(double denomOffset) {
        if (maxDifscratch == null) {
            maxDifscratch = new double[length];
        }
        double ret = MaxDifOtherScaled(maxDifscratch, denomOffset);
        System.arraycopy(field, 0, maxDifscratch, 0, length);
        return ret;
    }


    /**
     * returns the gradient of the diffusible in the X direction at the coordinates specified
     */
    public double GradientX(int x, int y) {
        double left = PDEequations.DisplacedX2D(x - 1, y, field, xDim, yDim, x, false, 0, wrapX);
        double right = PDEequations.DisplacedX2D(x + 1, y, field, xDim, yDim, x, false, 0, wrapX);
        return right - left;
    }

    /**
     * returns the gradient of the diffusible in the Y direction at the coordinates specified
     */
    public double GradientY(int x, int y) {
        double down = PDEequations.DisplacedY2D(x, y - 1, field, xDim, yDim, y, false, 0, wrapY);
        double up = PDEequations.DisplacedY2D(x, y + 1, field, xDim, yDim, y, false, 0, wrapY);
        return up - down;
    }

    /**
     * returns the gradient of the diffusible in the X direction at the coordinates specified, will use the boundary
     * condition value if computing the gradient next to the boundary
     */
    public double GradientX(int x, int y, double boundaryCond) {
        double left = PDEequations.DisplacedX2D(x - 1, y, field, xDim, yDim, x, true, boundaryCond, wrapX);
        double right = PDEequations.DisplacedX2D(x + 1, y, field, xDim, yDim, x, true, boundaryCond, wrapX);
        return right - left;
    }

    /**
     * returns the gradient of the diffusible in the Y direction at the coordinates specified, will use the boundary
     * condition value if computing the gradient next to the boundary
     */
    public double GradientY(int x, int y, double boundaryCond) {
        double down = PDEequations.DisplacedY2D(x, y - 1, field, xDim, yDim, y, true, boundaryCond, wrapY);
        double up = PDEequations.DisplacedY2D(x, y + 1, field, xDim, yDim, y, true, boundaryCond, wrapY);
        return up - down;
    }

    protected void EnsureScratch() {
        if (scratch == null) {
            scratch = new double[Math.max(xDim, yDim) * 2 + 4];
        }
    }

    protected void EnsureADIscratch() {
        if (adiScratch == null) {
            adiScratch = new double[field.length];
        }
    }
}
