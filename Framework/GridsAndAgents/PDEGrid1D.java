package Framework.GridsAndAgents;

import java.io.Serializable;
import java.util.Arrays;

import static Framework.Tools.Internal.PDEequations.*;


/**
 * a 1D
 */
public class PDEGrid1D extends GridBase1D implements Serializable {
    protected double[] nextField;
    protected double[] field;
    //double[] intermediateScratch;
    double[] scratch;
    double[] maxDifscratch;
    boolean adiOrder = true;
    boolean adiX = true;

    public PDEGrid1D(int xDim) {
        super(xDim, false);
        field = new double[this.xDim];
        nextField = new double[this.xDim];
    }

    public PDEGrid1D(int xDim, boolean wrapX) {
        super(xDim, wrapX);
        field = new double[this.xDim];
        nextField = new double[this.xDim];
    }

    public double[] GetSwapField() {
        return this.nextField;
    }

    /**
     * gets the prev field value at the specified coordinates
     */
    public double Get(int x) {
        return field[x];
    }

    /**
     * sets the prev field value at the specified coordinates
     */
    public void Set(int x, double val) {
        nextField[x] = val;
    }

    /**
     * adds to the prev field value at the specified index
     */
    public void Add(int x, double val) {
        nextField[x] += val;
    }

    /**
     * multiplies a value in the “current field” and adds the change to the “next field”
     */
    public void Mul(int x, double val) {
        nextField[x] += field[x] * (val - 1);
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
    public void Advection(double xVel) {
        if(Math.abs(xVel)>1){
            throw new IllegalArgumentException("Advection rate above maximum stable value of 1.0");
        }
        for (int x = 0; x < xDim; x++) {
            Advection1stOrder1D(x, field, nextField, xDim, xVel, false, 0.0);
        }
    }

    /**
     * runs advection as described above with a boundary value, meaning that the boundary value will advect in from the
     * upwind direction, and the concentration will disappear in the downwind direction.
     */
    public void Advection(double xVel, double boundaryValue) {
        if(Math.abs(xVel)>1){
            throw new IllegalArgumentException("Advection rate above maximum stable value of 1.0");
        }
        for (int x = 0; x < xDim; x++) {
            Advection1stOrder1D(x, field, nextField, xDim, xVel, true, boundaryValue);
        }
    }

    /**
     * runs diffusion on the current field, adding the deltas to the next field. This form of the function assumes
     * either a reflective or wrapping boundary (depending on how the PDEGrid was specified). the diffCoef variable is
     * the nondimensionalized diffusion conefficient. If the dimensionalized diffusion coefficient is x then diffCoef
     * can be found by computing (x*SpaceStep)/TimeStep^2 Note that if the diffCoef exceeds 0.5, this diffusion method
     * will become numerically unstable.
     */
    public void Diffusion(double diffCoef) {
        if (diffCoef > 0.5) {
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.5 value: " + diffCoef);
        }
        for (int x = 0; x < xDim; x++) {
            Diffusion1(x, field, nextField, xDim, diffCoef, false, 0.0, wrapX);
        }
    }

    /**
     * has the same effect as the above diffusion function without the boundary value argument, except rather than
     * assuming zero flux, the boundary condition is set to either the boundaryValue, or wrap around
     */
    public void Diffusion(double diffCoef, double boundaryValue) {
        if (diffCoef > 0.5) {
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.5 value: " + diffCoef);
        }
        for (int x = 0; x < xDim; x++) {
            Diffusion1(x, field, nextField, xDim, diffCoef, true, boundaryValue, wrapX);
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
    public double MaxDelta() {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs((nextField[i])));
        }
        return maxDif;
    }

    /**
     * like MaxDelta only the differences are scaled relative to the value in the current field. the denomOffset is
     * used to prevent a division by zero
     */
    public double MaxDeltaScaled(double denomOffset) {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs(nextField[i] / (Math.abs(field[i]) + denomOffset)));
        }
        return maxDif;
    }


    void EnsureScratch() {
        if (scratch == null) {
            scratch = new double[xDim * 2 + 4];
        }
    }
}
