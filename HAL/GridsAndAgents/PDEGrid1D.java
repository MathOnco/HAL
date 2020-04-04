package HAL.GridsAndAgents;

import HAL.Interfaces.Coords1DDouble;
import HAL.Interfaces.Grid1D;
import HAL.Tools.Internal.ADIequations;
import HAL.Tools.TdmaSolver;

import java.io.Serializable;
import java.util.Arrays;

import static HAL.Tools.Internal.PDEequations.*;


/**
 * a 1D
 */
public class PDEGrid1D implements Grid1D,Serializable {
    final public int xDim,length;
    public boolean wrapX;
    protected double[] deltas;
    protected double[] field;
    //double[] intermediateScratch;
    double[] scratch;
    double[] maxDifscratch;
    boolean adiOrder = true;
    boolean adiX = true;
    int updateCt;
    private TdmaSolver tdma;

    public PDEGrid1D(int xDim) {
        this.xDim=xDim;
        this.length=xDim;
        this.wrapX=false;
        field = new double[this.xDim];
        deltas = new double[this.xDim];
        tdma=new TdmaSolver(xDim);
    }

    public PDEGrid1D(int xDim, boolean wrapX) {
        this(xDim);
        this.wrapX=wrapX;
    }
    public double[]GetField(){
        return field;
    }
    public double[]GetDeltas(){
        return deltas;
    }
    /**
     * gets the prev field value at the specified coordinates
     */
    public double Get(int x) {
        return field[x];
    }
    public double Get(double x) {
        return Get((int)x);
    }

    /**
     * sets the delta field value at the specified coordinates
     */
    public void Set(int x, double val) {
        deltas[x] = val-field[x];
    }
    public void Set(double x, double val) {
        Set((int)x,val);
    }


    /**
     * adds to the delta field value at the specified index
     */
    public void Add(int x, double val) {
        deltas[x] += val;
    }
    public void Add(double x, double val) {
        Add((int)x,val);
    }

    /**
     * multiplies a value in the “current field” and adds the change to the “delta field”
     */
    public void Mul(int x, double val) {
        deltas[x] += field[x] * val;
    }
    public void Mul(double x, double val) {
        Mul((int)x,val);
    }
    /**
     * scales the value by the input upon update
     */
    public void Scale(int x, double val) {
        deltas[x] += field[x] * (val-1);
    }
    public void Scale(double x, double val) {
        Mul((int)x,(val-1));
    }

    /**
     * adds the delta field into the current field, also increments the tick.
     */
    public void Update() {
        for (int i = 0; i < deltas.length; i++) {
            field[i] += deltas[i];
        }
        Arrays.fill(deltas, 0);
    }
    public int UpdateCt(){
        return updateCt;
    }



    public void Advection(double xVel) {
        if (Math.abs(xVel) > 0.5) {
            throw new IllegalArgumentException("Advection rate above maximum stable value of 0.5");
        }
        Advection1(field, deltas, xVel, xDim, wrapX, (x) -> 0);
    }


    /**
     * runs advection as described above with a boundary value, meaning that the boundary value will advect in from the
     * upwind direction, and the concentration will disappear in the downwind direction.
     */
    public void Advection(double xVel, double boundaryValue) {
        if (Math.abs(xVel) > 0.5) {
            throw new IllegalArgumentException("Advection rate above maximum stable value of 0.5");
        }
        Advection1(field, deltas, xVel, xDim, wrapX, (x) -> boundaryValue);
    }

    /**
     * runs advection as described above with a boundary condition function, which will be evaluated with the out of
     * bounds coordinates as arguments whenever a boundary value is needed, and should return the boundary value
     */
    public void Advection(double xVel, Coords1DDouble BoundaryConditionFn) {
        if (Math.abs(xVel) > 0.5) {
            throw new IllegalArgumentException("Advection rate above maximum stable value of 0.5");
        }
        Advection1(field, deltas, xVel, xDim, wrapX, BoundaryConditionFn);
    }

    /**
     * runs discontinuous advection
     */
    public void Advection(double[]xVels){
        Advection1(field,deltas,xVels,xDim,wrapX,null,null);
    }
    /**
     * runs discontinuous advection
     */
    public void Advection(Grid2Ddouble xVels){
        Advection1(field,deltas,xVels.field,xDim,wrapX,null,null);
    }
    /**
     * runs discontinuous advection
     */
    public void Advection(double[]xVels,Coords1DDouble BoundaryConditionFn,Coords1DDouble BoundaryXvel){
        Advection1(field,deltas,xVels,xDim,wrapX,BoundaryConditionFn,BoundaryXvel);
    }
    /**
     * runs discontinuous advection
     */
    public void Advection(Grid2Ddouble xVels,Coords1DDouble BoundaryConditionFn,Coords1DDouble BoundaryXvel){
        Advection1(field,deltas,xVels.field,xDim,wrapX,BoundaryConditionFn,BoundaryXvel);
    }
    public void DiffusionCrank(double diffCoef){
        ADIequations.Diffusion1ADI(field,deltas,diffCoef,xDim,wrapX,null,tdma);
    }
    public void DiffusionCrank(double diffCoef,Coords1DDouble BC){
        ADIequations.Diffusion1ADI(field,deltas,diffCoef,xDim,wrapX,BC,tdma);
    }

    /**
     * runs diffusion on the current field, adding the deltas to the delta field. This form of the function assumes
     * either a reflective or wrapping boundary (depending on how the PDEGrid was specified). the diffCoef variable is
     * the nondimensionalized diffusion conefficient. If the dimensionalized diffusion coefficient is x then diffCoef
     * can be found by computing (x*SpaceStep)/TimeStep^2 Note that if the diffCoef exceeds 0.5, this diffusion method
     * will become numerically unstable.
     */
    public void Diffusion(double diffCoef) {
        if (diffCoef > 0.5) {
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.5 value: " + diffCoef);
        }
        Diffusion1(field, deltas, diffCoef, xDim, wrapX, null);
    }

    /**
     * has the same effect as the above diffusion function without the boundary value argument, except rather than
     * assuming zero flux, the boundary condition is set to either the boundaryValue, or wrap around
     */
    public void Diffusion(double diffCoef, double boundaryValue) {
        if (diffCoef > 0.5) {
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.5 value: " + diffCoef);
        }
        Diffusion1(field, deltas, diffCoef, xDim, wrapX, (x) -> boundaryValue);
    }

    /**
     * has the same effect as the above diffusion function with a boundary condition function, which will be evaluated
     * with the out of bounds coordinates as arguments whenever a boundary value is needed, and should return the
     * boundary value
     */
    public void Diffusion(double diffCoef, Coords1DDouble BoundaryConditionFn) {
        if (diffCoef > 0.5) {
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.5 value: " + diffCoef);
        }
        Diffusion1(field, deltas, diffCoef, xDim, wrapX, BoundaryConditionFn);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(double[] diffRates){
        Diffusion1(field,deltas,diffRates,xDim,wrapX,null,null);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(Grid1Ddouble diffRates){
        Diffusion1(field,deltas,diffRates.field,xDim,wrapX,null,null);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(double[] diffRates, Coords1DDouble BoundaryConditionFn, Coords1DDouble BoundaryDiffusionRateFn){
        Diffusion1(field,deltas,diffRates,xDim,wrapX,BoundaryConditionFn,BoundaryDiffusionRateFn);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(Grid1Ddouble diffRates, Coords1DDouble BoundaryConditionFn, Coords1DDouble BoundaryDiffusionRateFn){
        Diffusion1(field,deltas,diffRates.field,xDim,wrapX,BoundaryConditionFn,BoundaryDiffusionRateFn);
    }



    /**
     * sets all squares in the delta field to the specified value
     */
    public void SetAll(double val) {
        for (int i = 0; i < length; i++) {
            Set(i, val);
        }
    }

    /**
     * multiplies all values in the “current field” and puts the results into the “delta field”
     */
    public void MulAll(double val) {
        for (int i = 0; i < length; i++) {
            Mul(i, val);
        }
    }

    /**
     * sets all squares in the delta field using the vals array
     */
    public void SetAll(double[] vals) {
        for (int i = 0; i < length; i++) {
            Set(i, vals[i]);
        }
    }

    /**
     * adds specified value to all entries of the delta field
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
     * returns the maximum difference as stored on the delta field, call right before calling Update()
     */
    public double MaxDelta() {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs((deltas[i])));
        }
        return maxDif;
    }

    /**
     * like MaxDelta only the differences are scaled relative to the value in the current field. the denomOffset is used
     * to prevent a division by zero
     */
    public double MaxDeltaScaled(double denomOffset) {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs(deltas[i] / (Math.abs(field[i]) + denomOffset)));
        }
        return maxDif;
    }

    /**
     * ensures that all values will be non-negative on the next timestep, call before Update
     */
    public void SetNonNegative(){
        for (int i = 0; i < length; i++) {
            if(field[i]+deltas[i]<0){
                Set(i,0);
            }
        }
    }

    void EnsureScratch() {
        if (scratch == null) {
            scratch = new double[xDim * 2 + 4];
        }
    }

    @Override
    public int Xdim() {
        return 0;
    }

    @Override
    public int Length() {
        return 0;
    }

    @Override
    public boolean IsWrapX() {
        return false;
    }
}
