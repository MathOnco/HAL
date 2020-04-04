package HAL.GridsAndAgents;

import HAL.Interfaces.Coords2DDouble;
import HAL.Interfaces.DoubleArrayToVoid;
import HAL.Interfaces.Grid2D;
import HAL.Tools.Internal.PDEequations;
import HAL.Tools.TdmaSolver;

import java.io.Serializable;
import java.util.Arrays;

import static HAL.Tools.Internal.ADIequations.*;
import static HAL.Tools.Internal.PDEequations.Advection2;
import static HAL.Tools.Internal.PDEequations.Diffusion2;


/**
 * PDEGrid2D class facilitates 2D diffusion with two arrays of doubles called fields
 * the intended usage is that during a diffusion tick, the current values will be read, and the prev values will be written to
 * after updates, Update is called to set the prev field as the current field.
 */
public class PDEGrid2D implements Grid2D,Serializable {
    final public int xDim;
    final public int yDim;
    final public int length;
    public boolean wrapX;
    public boolean wrapY;
    protected double[] deltas;
    protected double[] field;
    //double[] intermediateScratch;
    protected double[] scratch;
    protected double[] scratchField1;
    protected double[] scratchField2;
    protected double[] maxDifscratch;
    boolean adiOrder = true;
    boolean adiX = true;
    int updateCt;
    TdmaSolver tdma;

    public PDEGrid2D(int xDim, int yDim) {
        this(xDim,yDim,false,false);
    }

    public PDEGrid2D(int xDim, int yDim, boolean wrapX, boolean wrapY) {
        this.xDim=xDim;
        this.yDim=yDim;
        this.length=xDim*yDim;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
        field = new double[this.xDim * this.yDim];
        deltas = new double[this.xDim * this.yDim];
    }
    public int UpdateCt(){
        return updateCt;
    }

    public double[]GetField(){
        return field;
    }
    public double[]GetDeltas(){
        return deltas;
    }

 //   /**
 //    * runs diffusion on the current field using the ADI (alternating direction implicit) method. without a
 //    * boundaryValue argument, a zero flux boundary is imposed. wraparound will not work with ADI. ADI is numerically
 //    * stable at any diffusion rate.
 //    */
 //   public void DiffusionADI(double diffCoef) {
 //       EnsureScratch();
 //       EnsureScratchF1();
 //       EnsureScratchF2();
 //       DiffusionADI2(true, field, scratchField1, scratch, xDim, yDim, diffCoef / 2, false, 0);
 //       DiffusionADI2(false, scratchField1, scratchField2, scratch, xDim, yDim, diffCoef / 2, false, 0);
 //       for (int i = 0; i < length; i++) {
 //           deltas[i]+=scratchField2[i]-field[i];
 //       }
 //   }

    public void DiffusionADI(double diffCoef){
        if(scratch==null){
            scratch=new double[length];
        }
        if(tdma==null){
            tdma=new TdmaSolver(Math.max(xDim,yDim));
        }
        Diffusion2DADI(field,scratch,deltas,diffCoef,xDim,yDim,wrapX,wrapY,null,tdma);
    }
    public void DiffusionADI(double diffCoef,double boundaryCond){
        if(scratch==null){
            scratch=new double[length];
        }
        if(tdma==null){
            tdma=new TdmaSolver(Math.max(xDim,yDim));
        }
        Diffusion2DADI(field,scratch,deltas,diffCoef,xDim,yDim,wrapX,wrapY,(x,y)->boundaryCond,tdma);
    }
    public void DiffusionADI(double diffCoef, Coords2DDouble BC){
        if(scratch==null){
            scratch=new double[length];
        }
        if(tdma==null){
            tdma=new TdmaSolver(Math.max(xDim,yDim));
        }
        Diffusion2DADI(field,scratch,deltas,diffCoef,xDim,yDim,wrapX,wrapY,BC,tdma);
    }
    public void DiffusionADI(double diffCoef,double boundaryCond, DoubleArrayToVoid BetweenAction) {
        if(scratch==null){
            scratch=new double[length];
        }
        if(tdma==null){
            tdma=new TdmaSolver(Math.max(xDim,yDim));
        }
        Diffusion2DADIBetweenAction(field,scratch,deltas,diffCoef,xDim,yDim,wrapX,wrapY,(x,y)->boundaryCond,tdma,BetweenAction);
    }
    public static void Diffusion2DADIBetweenAction(double[]field, double[]scratch, double[]deltas, double diffRate, int xDim, int yDim, boolean wrapX, boolean wrapY, Coords2DDouble BC, TdmaSolver tdma, DoubleArrayToVoid BetweenAction){
        for (int y = 0; y < yDim; y++) {//do the x rows
            int finalY = y;
            ADISolveRow(xDim,diffRate,2,wrapX,BC!=null,tdma,
                    (i)->ExplicitDiffusionY2ADI(i,finalY,field,diffRate,xDim,yDim,wrapX,wrapY,BC),
                    (i,v)->scratch[i*yDim+finalY]=v);
        }
        BetweenAction.Action(scratch);
        for (int x = 0; x < xDim; x++) {//do the y columns
            int finalX = x;
            ADISolveRow(yDim,diffRate,2,wrapY,BC!=null,tdma,
                    (i)->ExplicitDiffusionX2ADI(finalX,i,scratch,diffRate,xDim,yDim,wrapX,wrapY,BC),
                    (i,v)->{
                        int index=finalX*yDim+i;
                        deltas[index]+=v-field[index];
                    });
        }
    }

//    /**
//     * runs diffusion on the current field using the ADI (alternating direction implicit) method. ADI is numerically
//     * stable at any diffusion rate. Adding a boundary value to the function call will cause boundary conditions to be
//     * imposed.
//     */
//    public void DiffusionADI(double diffCoef, double boundaryValue) {
//        EnsureScratch();
//        EnsureScratchF1();
//        EnsureScratchF2();
//        DiffusionADI2(true, field, scratchField1, scratch, xDim, yDim, diffCoef / 2, true, boundaryValue);
//        DiffusionADI2(false, scratchField1, scratchField2, scratch, xDim, yDim, diffCoef / 2, true, boundaryValue);
//        for (int i = 0; i < length; i++) {
//            deltas[i]+=scratchField2[i]-field[i];
//        }
//    }
//    /**
//     * runs diffusion on the current field using the ADI (alternating direction implicit) method. without a
//     * boundaryValue argument, a zero flux boundary is imposed. wraparound will not work with ADI. ADI is numerically
//     * stable at any diffusion rate.
//     */
//    public void DiffusionADI(double diffCoef, DoubleArrayToVoid BetweenAction) {
//        EnsureScratch();
//        EnsureScratchF1();
//        EnsureScratchF2();
//        DiffusionADI2(true, field, scratchField1, scratch, xDim, yDim, diffCoef / 2, false, 0);
//        BetweenAction.Action(scratchField1);
//        DiffusionADI2(false, scratchField1, scratchField2, scratch, xDim, yDim, diffCoef / 2, false, 0);
//        for (int i = 0; i < length; i++) {
//            deltas[i]+=scratchField2[i]-field[i];
//        }
//    }
//
//    /**
//     * runs diffusion on the current field using the ADI (alternating direction implicit) method. ADI is numerically
//     * stable at any diffusion rate. Adding a boundary value to the function call will cause boundary conditions to be
//     * imposed.
//     */
//    public void DiffusionADI(double diffCoef, double boundaryValue, DoubleArrayToVoid BetweenAction) {
//        EnsureScratch();
//        EnsureScratchF1();
//        EnsureScratchF2();
//        DiffusionADI2(true, field, scratchField1, scratch, xDim, yDim, diffCoef / 2, true, boundaryValue);
//        BetweenAction.Action(scratchField1);
//        DiffusionADI2(false, scratchField1, scratchField2, scratch, xDim, yDim, diffCoef / 2, true, boundaryValue);
//        for (int i = 0; i < length; i++) {
//            deltas[i]+=scratchField2[i]-field[i];
//        }
//    }

    /**
     * gets the prev field value at the specified coordinates
     */
    public double Get(int x, int y) {
        return field[x * yDim + y];
    }
    public double Get(double x, double y) {
        return Get((int)x,(int)y);
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
        deltas[x * yDim + y] = val - field[x * yDim + y];
    }
    public void Set(double x, double y, double val) {
        Set((int)x,(int)y,val);
    }

    /**
     * sets the prev field value at the specified index
     */
    public void Set(int i, double val) {
        deltas[i] = val - field[i];
    }

    /**
     * sets the prev field value at the specified coordinates
     */
    public void Add(int x, int y, double val) {
        deltas[x * yDim + y] += val;
    }
    public void Add(double x, double y, double val) {
        Add((int)x,(int)y,val);
    }

    /**
     * adds to the prev field value at the specified index
     */
    public void Add(int i, double val) {
        deltas[i] += val;
    }

    /**
     * multiplies a value in the “current field” and adds the change to the “delta field”
     */
    public void Mul(int x, int y, double val) {
        deltas[x * yDim + y] += field[x * yDim + y] * val;
    }
    public void Mul(double x, double y, double val) {
        Mul((int)x,(int)y,val);
    }

    /**
     * multiplies a value in the “current field” and adds the change to the “delta field”
     */
    public void Mul(int i,double val){
        deltas[i] += field[i] * val;
    }
    /**
     * scales the value by the input upon update
     */
    public void Scale(int x, int y, double val) {
        deltas[x * yDim + y] += field[x * yDim + y] * (val-1);
    }
    public void Scale(double x, double y, double val) {
        Mul((int)x,(int)y,(val-1));
    }
    public void Scale(int i,double val){
        deltas[i] += field[i] * (val-1);
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
     * adds the delta field into the current field
     */
    public void Update() {
        for (int i = 0; i < deltas.length; i++) {
            field[i] += deltas[i];
        }
        Arrays.fill(deltas, 0);
        updateCt++;
    }


    public void Advection(double xVel, double yVel) {
        if(Math.abs(xVel)+Math.abs(yVel)>0.5){
            throw new IllegalArgumentException("Advection rate component sum above stable maximum value of 0.5");
        }
        Advection2(field, deltas,xVel,yVel, xDim, yDim, wrapX,wrapY,(x,y)->0);
    }
    /**
     * runs advection as described above with a boundary value, meaning that the boundary value will advect in from the
     * upwind direction, and the concentration will disappear in the downwind direction.
     */
    public void Advection(double xVel, double yVel, double boundaryValue) {
        if(Math.abs(xVel)+Math.abs(yVel)>0.5){
            throw new IllegalArgumentException("Advection rate component sum above stable maximum value of 0.5");
        }
        Advection2(field, deltas,xVel,yVel, xDim, yDim, wrapX,wrapY,(x,y)->boundaryValue);
    }

    /**
     * runs advection as described above with a boundary condition function, which will be evaluated with the out of
     * bounds coordinates as arguments whenever a boundary value is needed, and should return the boundary value
     */
    public void Advection(double xVel, double yVel, Coords2DDouble BoundaryConditionFn) {
        if(Math.abs(xVel)+Math.abs(yVel)>0.5){
            throw new IllegalArgumentException("Advection rate component sum above stable maximum value of 0.5");
        }
        Advection2(field, deltas,xVel,yVel, xDim, yDim, wrapX,wrapY,BoundaryConditionFn);
    }

    /**
     * runs discontinuous advection
     */
    public void Advection(double[]xVels,double[]yVels){
        Advection2(field,deltas,xVels,yVels,xDim,yDim,wrapX,wrapY,null,null,null);
    }
    /**
     * runs discontinuous advection
     */
    public void Advection(Grid2Ddouble xVels, Grid2Ddouble yVels){
        Advection2(field,deltas,xVels.field,yVels.field,xDim,yDim,wrapX,wrapY,null,null,null);
    }
    /**
     * runs discontinuous advection
     */
    public void Advection(double[]xVels, double[]yVels, Coords2DDouble BoundaryyConditionFn, Coords2DDouble BoundaryXvels, Coords2DDouble BoundaryYvels) {
        Advection2(field, deltas, xVels, yVels, xDim, yDim, wrapX, wrapY, BoundaryyConditionFn, BoundaryXvels, BoundaryYvels);
    }
    /**
     * runs discontinuous advection
     */
    public void Advection(Grid2Ddouble xVels, Grid2Ddouble yVels, Coords2DDouble BoundaryyConditionFn, Coords2DDouble BoundaryXvels, Coords2DDouble BoundaryYvels){
        Advection2(field,deltas,xVels.field,yVels.field,xDim,yDim,wrapX,wrapY,BoundaryyConditionFn, BoundaryXvels, BoundaryYvels);
    }

    /**
     * runs diffusion on the current field, adding the deltas to the delta field. This form of the function assumes
     * either a reflective or wrapping boundary (depending on how the PDEGrid was specified). the diffCoef variable is
     * the nondimensionalized diffusion conefficient. If the dimensionalized diffusion coefficient is x then diffCoef
     * can be found by computing (x*SpaceStep)/TimeStep^2 Note that if the diffCoef exceeds 0.25, this diffusion method
     * will become numerically unstable.
     */
    public void Diffusion(double diffCoef) {
        if (diffCoef > 0.25) {
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: " + diffCoef);
        }
                Diffusion2(field, deltas,diffCoef, xDim, yDim,wrapX,wrapY,null);
    }

    /**
     * has the same effect as the above diffusion function without the boundary value argument, except rather than
     * assuming zero flux, the boundary condition is set to either the boundaryValue, or wrap around
     */
    public void Diffusion(double diffCoef, double boundaryValue) {
        if (diffCoef > 0.25) {
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: " + diffCoef);
        }
        Diffusion2(field, deltas,diffCoef, xDim, yDim,wrapX,wrapY,(x,y)->boundaryValue);
    }

    /**
     * has the same effect as the above diffusion function with a boundary condition function, which will be evaluated
     * with the out of bounds coordinates as arguments whenever a boundary value is needed, and should return the
     * boundary value
     */
    public void Diffusion(double diffCoef, Coords2DDouble BoundaryConditionFn) {
        if (diffCoef > 0.25) {
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: " + diffCoef);
        }
        Diffusion2(field, deltas,diffCoef, xDim, yDim,wrapX,wrapY,BoundaryConditionFn);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(double[] diffRatesX,double[]diffRatesY){
        Diffusion2(field,deltas,diffRatesX,diffRatesY,xDim,yDim,wrapX,wrapY,null,null,null);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(Grid2Ddouble diffRatesX, Grid2Ddouble diffRatesY){
        Diffusion2(field,deltas,diffRatesX.field,diffRatesY.field,xDim,yDim,wrapX,wrapY,null,null,null);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(double[] diffRatesX, double[]diffRatesY, Coords2DDouble BoundaryConditionFn, Coords2DDouble BoundaryDiffusionRatesX, Coords2DDouble BoundaryDiffusionRatesY){
        Diffusion2(field,deltas,diffRatesX,diffRatesY,xDim,yDim,wrapX,wrapY,BoundaryConditionFn,BoundaryDiffusionRatesX,BoundaryDiffusionRatesY);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(Grid2Ddouble diffRatesX, Grid2Ddouble diffRatesY, Coords2DDouble BoundaryConditionFn, Coords2DDouble BoundaryDiffusionRatesX, Coords2DDouble BoundaryDiffusionRatesY){
        Diffusion2(field,deltas,diffRatesX.field,diffRatesY.field,xDim,yDim,wrapX,wrapY,BoundaryConditionFn,BoundaryDiffusionRatesX,BoundaryDiffusionRatesY);
    }
//    /**
//     * runs diffusion with discontinuous diffusion rates
//     */
//    public void Diffusion(double[] diffRates){
//        Diffusion2(field,deltas,diffRates,xDim,yDim,wrapX,wrapY,null,null);
//    }

//    /**
//     * runs diffusion with discontinuous diffusion rates
//     */
//    public void Diffusion(Grid2Ddouble diffRates){
//        Diffusion2(field,deltas,diffRates.field,xDim,yDim,wrapX,wrapY,null,null);
//    }

//    /**
//     * runs diffusion with discontinuous diffusion rates
//     */
//    public void Diffusion(double[] diffRates, Coords2DDouble BoundaryConditionFn, Coords2DDouble BoundaryDiffusionRateFn){
//        Diffusion2(field,deltas,diffRates,xDim,yDim,wrapX,wrapY,BoundaryConditionFn,BoundaryDiffusionRateFn);
//    }

//    /**
//     * runs diffusion with discontinuous diffusion rates
//     */
//    public void Diffusion(Grid2Ddouble diffRates, Coords2DDouble BoundaryConditionFn, Coords2DDouble BoundaryDiffusionRateFn){
//        Diffusion2(field,deltas,diffRates.field,xDim,yDim,wrapX,wrapY,BoundaryConditionFn,BoundaryDiffusionRateFn);
//    }

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
     * like MaxDelta only the differences are scaled relative to the value in the current field. the denomOffset is
     * used to prevent a division by zero
     */
    public double MaxDeltaScaled(double denomOffset) {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs(deltas[i] / (Math.abs(field[i]) + denomOffset)));
        }
        return maxDif;
    }


    /**
     * returns the gradient of the diffusible in the X direction at the coordinates specified
     */
    public double GradientX(int x, int y) {
        double left = PDEequations.DisplacedX2D(field,x-1,y, xDim, yDim, wrapX,(X, Y)->Get(X+1,Y));
        double right = PDEequations.DisplacedX2D(field,x + 1, y, xDim, yDim,wrapX,(X, Y)->Get(X-1,Y));
        return right - left;
    }
    public double GradientX(double x, double y) {
        return GradientX((int)x,(int)y);
    }

    /**
     * returns the gradient of the diffusible in the Y direction at the coordinates specified
     */
    public double GradientY(int x, int y) {
        double down = PDEequations.DisplacedY2D(field,x,y-1, xDim, yDim, wrapX,(X, Y)->Get(X,Y+1));
        double up = PDEequations.DisplacedY2D(field,x, y+1, xDim, yDim,wrapX,(X, Y)->Get(X,Y-1));
        return up - down;
    }
    public double GradientY(double x, double y) {
        return GradientY((int)x,(int)y);
    }

    /**
     * returns the gradient of the diffusible in the X direction at the coordinates specified, will use the boundary
     * condition value if computing the gradient next to the boundary
     */
    public double GradientX(int x, int y, double boundaryCond) {
        double left = PDEequations.DisplacedX2D(field,x-1,y, xDim, yDim, wrapX,(X, Y)->boundaryCond);
        double right = PDEequations.DisplacedX2D(field,x + 1, y, xDim, yDim,wrapX,(X, Y)->boundaryCond);
        return right - left;
    }
    public double GradientX(double x, double y, double boundaryCond) {
        return GradientX((int)x,(int)y,boundaryCond);
    }

    /**
     * returns the gradient of the diffusible in the Y direction at the coordinates specified, will use the boundary
     * condition value if computing the gradient next to the boundary
     */
    public double GradientY(int x, int y, double boundaryCond) {
        double down = PDEequations.DisplacedY2D(field,x,y-1, xDim, yDim, wrapX,(X, Y)->boundaryCond);
        double up = PDEequations.DisplacedY2D(field,x, y+1, xDim, yDim,wrapX,(X, Y)->boundaryCond);
        return up - down;
    }
    public double GradientY(double x, double y, double boundaryCond) {
        return GradientY((int)x,(int)y,boundaryCond);
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

    protected void EnsureScratch() {
        if (scratch == null) {
            scratch = new double[Math.max(xDim, yDim) * 2 + 4];
        }
    }

    protected void EnsureScratchF1() {
        if (scratchField1 == null) {
            scratchField1 = new double[field.length];
        }
    }
    protected void EnsureScratchF2() {
        if (scratchField2 == null) {
            scratchField2 = new double[field.length];
        }
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
