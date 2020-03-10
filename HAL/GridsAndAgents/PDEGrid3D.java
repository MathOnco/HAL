package HAL.GridsAndAgents;
import HAL.Interfaces.Coords3DDouble;
import HAL.Interfaces.Grid3D;
import HAL.Tools.Internal.PDEequations;
import HAL.Tools.TdmaSolver;
import HAL.Util;
//import AgentFramework.Util;


import java.io.Serializable;
import java.util.Arrays;

import static HAL.Tools.Internal.ADIequations.Diffusion3DADI;
import static HAL.Tools.Internal.PDEequations.*;

/**
 * PDEGrid3D class facilitates 3D diffusion with two arrays of doubles called fields
 * the intended usage is that during a diffusion tick, the current values will be read, and the next values will be written to
 * after updates, Update is called to set the delta field as the current field.
 */
public class PDEGrid3D implements Grid3D,Serializable {
    public final int xDim;
    public final int yDim;
    public final int zDim;
    public final int length;
    public boolean wrapX;
    public boolean wrapY;
    public boolean wrapZ;
    public double[] field;
    public double[] deltas;
    public double[] scratch;
    public double[] scratch2;
    public double[] maxDifscratch;
    int updateCt=0;
    TdmaSolver tdma;

    public PDEGrid3D(int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ) {
        this.xDim=xDim;
        this.yDim=yDim;
        this.zDim=zDim;
        this.length=xDim*yDim*zDim;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
        this.wrapZ=wrapZ;
        int numElements = this.xDim * this.yDim * this.zDim;

        field = new double[numElements];
        deltas = new double[numElements];
        scratch = null;
    }

    public PDEGrid3D(int xDim, int yDim, int zDim) {
        this(xDim, yDim, zDim, false, false, false);
    }
    public double[]GetField(){
        return field;
    }
    public double[]GetDeltas(){
        return deltas;
    }
    /**
     * gets to the current field value at the specified coordinates
     */
    public double Get(int x, int y, int z) {
        return field[x * yDim * zDim + y * zDim + z];
    }
    public double Get(double x, double y, double z) {
        return Get((int)x,(int)y,(int)z);
    }

    /**
     * gets the delta field value at the specified index
     */
    public double Get(int i) {
        return field[i];
    }

    /**
     * sets to the current field value at the specified coordinates
     */
    public void Set(int x, int y, int z, double val) {
        deltas[x * yDim * zDim + y * zDim + z] = val - field[x * yDim * zDim + y * zDim + z];
    }
    public void Set(double x, double y, double z, double val) {
        Set((int)x,(int)y,(int)z,val);
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, double val) {
        deltas[i] = val - field[i];
    }


    /**
     * adds to the delta field value at the specified index
     */
    public void Add(int x, int y, int z, double val) {
        deltas[x * yDim * zDim + y * zDim + z] += val;
    }
    public void Add(double x, double y, double z, double val) {
        Add((int)x,(int)y,(int)z,val);
    }

    /**
     * adds to the delta field value at the specified index
     */
    public void Add(int i, double val) {
        deltas[i] += val;
    }

    /**
     * multiplies a value in the “current field” and adds the change to the “delta field”
     */
    public void Mul(int i, double val) {
        deltas[i] += field[i] * val;
    }

    /**
     * multiplies a value in the “current field” and adds the change to the “delta field”
     */
    public void Mul(int x, int y, int z, double val) {
        int i = x * yDim * zDim + y * zDim + z;
        deltas[i] += field[i] * val;
    }
    public void Mul(double x, double y, double z, double val) {
        Mul((int)x,(int)y,(int)z,val);
    }

    /**
     * adds the delta field into the current field, also increments the tick.
     */
    public void Update() {
        for (int i = 0; i < deltas.length; i++) {
            field[i] += deltas[i];
        }
        Arrays.fill(deltas, 0);
        updateCt++;
    }
    public int UpdateCt(){
        return updateCt;
    }

    /**
     * swaps the next and current field
     */
    void SwapFields() {
        double[] temp = field;
        field = deltas;
        deltas = temp;
    }

    /**
     * Bounds all values in the delta field between min and max
     */
    public void BoundAllSwap(double min, double max) {
        for (int i = 0; i < length; i++) {
            deltas[i] = Util.Bound(deltas[i], min, max);
        }
    }

    /**
     * /** runs diffusion on the current field, adding the deltas to the delta field. This form of the function assumes
     * either a reflective or wrapping boundary (depending on how the PDEGrid was specified). the diffCoef variable is
     * the nondimensionalized diffusion conefficient. If the dimensionalized diffusion coefficient is x then diffCoef
     * can be found by computing (x*SpaceStep)/TimeStep^2 Note that if the diffCoef exceeds 0.25, this diffusion method
     * will become numerically unstable.
     */
    public void Diffusion(double diffCoef) {
        if (diffCoef > 1.0 / 6) {
            throw new IllegalArgumentException("3D Diffusion is unstable if rate is above 0.1666666! rate: " + diffCoef);
        }
        Diffusion3(field, deltas,diffCoef, xDim, yDim, zDim, wrapX, wrapY, wrapZ,null);
    }

    /**
     * has the same effect as the above diffusion function without the boundary value argument, except rather than
     * assuming zero flux, the boundary condition is set to either the boundaryValue, or wrap around
     */
    public void Diffusion(double diffCoef, double boundaryValue) {
        if (diffCoef > 1.0 / 6) {
            throw new IllegalArgumentException("3D Diffusion is unstable if rate is above 0.1666666! rate: " + diffCoef);
        }
        Diffusion3(field, deltas,diffCoef, xDim, yDim, zDim, wrapX, wrapY, wrapZ,(x,y,z)->boundaryValue);
    }

    /**
     * has the same effect as the above diffusion function with a boundary condition function, which will be evaluated
     * with the out of bounds coordinates as arguments whenever a boundary value is needed, and should return the
     * boundary value
     */
    public void Diffusion(double diffCoef, Coords3DDouble BoundaryConditionFn) {
        if (diffCoef > 1.0 / 6) {
            throw new IllegalArgumentException("3D Diffusion is unstable if rate is above 0.1666666! rate: " + diffCoef);
        }
        Diffusion3(field, deltas,diffCoef, xDim, yDim, zDim, wrapX, wrapY, wrapZ,BoundaryConditionFn);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(double[] diffRatesX,double[]diffRatesY,double[]diffRatesZ){
        Diffusion3(field,deltas,diffRatesX,diffRatesY,diffRatesZ,xDim,yDim,zDim,wrapX,wrapY,wrapZ,null,null,null,null);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(Grid3Ddouble diffRatesX,Grid3Ddouble diffRatesY,Grid3Ddouble diffRatesZ){
        Diffusion3(field,deltas,diffRatesX.field,diffRatesY.field,diffRatesZ.field,xDim,yDim,zDim,wrapX,wrapY,wrapZ,null,null,null,null);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(double[] diffRatesX,double[] diffRatesY,double[] diffRatesZ, Coords3DDouble BoundaryConditionFn, Coords3DDouble BoundaryDiffusionRatesX, Coords3DDouble BoundaryDiffusionRatesY, Coords3DDouble BoundaryDiffusionRatesZ){
        Diffusion3(field,deltas,diffRatesX,diffRatesY,diffRatesZ,xDim,yDim,zDim,wrapX,wrapY,wrapZ,BoundaryConditionFn,BoundaryDiffusionRatesX,BoundaryDiffusionRatesY,BoundaryDiffusionRatesZ);
    }

    /**
     * runs diffusion with discontinuous diffusion rates
     */
    public void Diffusion(Grid3Ddouble diffRatesX,Grid3Ddouble diffRatesY,Grid3Ddouble diffRatesZ, Coords3DDouble BoundaryConditionFn, Coords3DDouble BoundaryDiffusionRatesX, Coords3DDouble BoundaryDiffusionRatesY, Coords3DDouble BoundaryDiffusionRatesZ){
        Diffusion3(field,deltas,diffRatesX.field,diffRatesY.field,diffRatesZ.field,xDim,yDim,zDim,wrapX,wrapY,wrapZ,BoundaryConditionFn,BoundaryDiffusionRatesX,BoundaryDiffusionRatesY,BoundaryDiffusionRatesZ);
    }
//    /**
//     * runs diffusion with discontinuous diffusion rates
//     */
//    public void Diffusion(double[] diffRates){
//        Diffusion3(field,deltas,diffRates,xDim,yDim,zDim,wrapX,wrapY,wrapZ,null,null);
//    }

//    /**
//     * runs diffusion with discontinuous diffusion rates
//     */
//    public void Diffusion(Grid2Ddouble diffRates){
//        Diffusion3(field,deltas,diffRates.field,xDim,yDim,zDim,wrapX,wrapY,wrapZ,null,null);
//    }

//    /**
//     * runs diffusion with discontinuous diffusion rates
//     */
//    public void Diffusion(double[] diffRates, Coords3DDouble BoundaryConditionFn, Coords3DDouble BoundaryDiffusionRateFn){
//        Diffusion3(field,deltas,diffRates,xDim,yDim,zDim,wrapX,wrapY,wrapZ,BoundaryConditionFn,BoundaryDiffusionRateFn);
//    }

//    /**
//     * runs diffusion with discontinuous diffusion rates
//     */
//    public void Diffusion(Grid2Ddouble diffRates, Coords3DDouble BoundaryConditionFn, Coords3DDouble BoundaryDiffusionRateFn){
//        Diffusion3(field,deltas,diffRates.field,xDim,yDim,zDim,wrapX,wrapY,wrapZ,BoundaryConditionFn,BoundaryDiffusionRateFn);
//    }
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


    public void Advection(double xVel, double yVel, double zVel) {
        if(Math.abs(xVel)+Math.abs(yVel)+Math.abs(zVel)>0.5){
            throw new IllegalArgumentException("Advection rate component sum above stable maximum value of 0.5");
        }
        Advection3(field, deltas,xVel,yVel,zVel, xDim, yDim, zDim,wrapX,wrapY,wrapZ,(x,y,z)->0);
    }

    /**
     * runs advection as described above with a boundary value, meaning that the boundary value will advect in from the
     * upwind direction, and the concentration will disappear in the downwind direction.
     */
    public void Advection(double xVel, double yVel, double zVel, double boundaryVal) {
        if(Math.abs(xVel)+Math.abs(yVel)+Math.abs(zVel)>0.5){
            throw new IllegalArgumentException("Advection rate component sum above stable maximum value of 0.5");
        }
        Advection3(field, deltas,xVel,yVel,zVel, xDim, yDim, zDim,wrapX,wrapY,wrapZ,(x,y,z)->boundaryVal);
    }

    /**
     * runs advection as described above with a boundary condition function, which will be evaluated with the out of
     * bounds coordinates as arguments whenever a boundary value is needed, and should return the boundary value
     */
    public void Advection(double xVel, double yVel, double zVel, Coords3DDouble BoundaryConditionFn) {
        if(Math.abs(xVel)+Math.abs(yVel)+Math.abs(zVel)>0.5){
            throw new IllegalArgumentException("Advection rate component sum above stable maximum value of 0.5");
        }
        Advection3(field, deltas,xVel,yVel,zVel, xDim, yDim, zDim,wrapX,wrapY,wrapZ,BoundaryConditionFn);
    }

    /**
     * runs discontinuous advection
     */
    public void Advection(double[]xVels,double[]yVels,double[]zVels){
        Advection3(field,deltas,xVels,yVels,zVels,xDim,yDim,zDim,wrapX,wrapY,wrapZ,null,null,null,null);
    }
    /**
     * runs discontinuous advection
     */
    public void Advection(Grid2Ddouble xVels,Grid2Ddouble yVels,Grid2Ddouble zVels){
        Advection3(field,deltas,xVels.field,yVels.field,zVels.field,xDim,yDim,zDim,wrapX,wrapY,wrapZ,null,null,null,null);
    }
    /**
     * runs discontinuous advection
     */
    public void Advection(double[]xVels,double[]yVels,double[]zVels,Coords3DDouble BoundaryConditionFn, Coords3DDouble BoundaryXvels,Coords3DDouble BondaryYvels,Coords3DDouble BoundaryZvels){
        Advection3(field,deltas,xVels,yVels,zVels,xDim,yDim,zDim,wrapX,wrapY,wrapZ,BoundaryConditionFn,BoundaryXvels,BondaryYvels,BoundaryZvels);
    }
    /**
     * runs discontinuous advection
     */
    public void Advection(Grid2Ddouble xVels,Grid2Ddouble yVels,Grid2Ddouble zVels,Coords3DDouble BoundaryConditionFn, Coords3DDouble BoundaryXvels,Coords3DDouble BondaryYvels,Coords3DDouble BoundaryZvels){
        Advection3(field,deltas,xVels.field,yVels.field,zVels.field,xDim,yDim,zDim,wrapX,wrapY,wrapZ,BoundaryConditionFn,BoundaryXvels,BondaryYvels,BoundaryZvels);
    }
    /**
     * returns the maximum difference between the field passed in and the current field. call right after Update()
     */
    public double MaxDifInternal() {
        if (maxDifscratch == null) {
            maxDifscratch = new double[length];
        }
        double ret = MaxDelta();
        System.arraycopy(field, 0, maxDifscratch, 0, length);
        return ret;
    }


    public void DiffusionADI(double diffRate){
        if(scratch==null){
            scratch=new double[length];
        }
        if(scratch2==null){
            scratch2=new double[length];
        }
        if(tdma==null){
            tdma=new TdmaSolver(Math.max(Math.max(xDim,yDim),zDim));
        }
        Diffusion3DADI(field,scratch,scratch2,deltas,diffRate,xDim,yDim,zDim,wrapX,wrapY,wrapZ,null,tdma);
    }
    public void DiffusionADI(double diffRate,double boundaryValue){
        if(scratch==null){
            scratch=new double[length];
        }
        if(scratch2==null){
            scratch2=new double[length];
        }
        if(tdma==null){
            tdma=new TdmaSolver(Math.max(Math.max(xDim,yDim),zDim));
        }
        Diffusion3DADI(field,scratch,scratch2,deltas,diffRate,xDim,yDim,zDim,wrapX,wrapY,wrapZ,(x,y,z)->boundaryValue,tdma);
    }

    /**
     * sets all squares in the delta field using the vals array
     */
    public void SetAll(double val) {
        for (int i = 0; i < length; i++) {
            Set(i, val);
        }
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
     * adds specified value to all entries of the delta field
     */
    public void AddAll(double val) {
        for (int i = 0; i < length; i++) {
            Add(i, val);
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
     * returns the gradient of the diffusible in the X direction at the coordinates specified
     */
    public double GradientX(int x, int y,int z) {
        double left = PDEequations.DisplacedX3D(field,x-1,y,z, xDim, yDim,zDim, wrapX,(X, Y, Z)->Get(X+1,Y,Z));
        double right = PDEequations.DisplacedX3D(field,x + 1, y,z, xDim, yDim,zDim,wrapX,(X,Y,Z)->Get(X-1,Y,Z));
        return right - left;
    }
    public double GradientX(double x, double y,double z) {
        return GradientX((int)x,(int)y,(int)z);
    }

    /**
     * returns the gradient of the diffusible in the Y direction at the coordinates specified
     */
    public double GradientY(int x, int y,int z) {
        double down = PDEequations.DisplacedY3D(field,x,y-1,z, xDim, yDim,zDim, wrapY,(X,Y,Z)->Get(X,Y+1,Z));
        double up = PDEequations.DisplacedY3D(field,x, y+1,z, xDim, yDim,zDim,wrapY,(X,Y,Z)->Get(X,Y-1,Z));
        return up - down;
    }
    public double GradientY(double x, double y,double z) {
        return GradientY((int)x,(int)y,(int)z);
    }
    /**
     * returns the gradient of the diffusible in the Z direction at the coordinates specified
     */
    public double GradientZ(int x, int y,int z) {
        double down = PDEequations.DisplacedY3D(field,x,y,z-1, xDim, yDim,zDim, wrapZ,(X,Y,Z)->Get(X,Y,Z+1));
        double up = PDEequations.DisplacedY3D(field,x, y,z+1, xDim, yDim,zDim,wrapZ,(X,Y,Z)->Get(X,Y,Z-1));
        return up - down;
    }
    public double GradientZ(double x, double y,double z) {
        return GradientZ((int)x,(int)y,(int)z);
    }

    /**
     * returns the gradient of the diffusible in the X direction at the coordinates specified, will use the boundary
     * condition value if computing the gradient next to the boundary
     */
    public double GradientX(int x, int y,int z,double boundaryCond) {
        double left = PDEequations.DisplacedX3D(field,x-1,y,z, xDim, yDim,zDim, wrapX,(X, Y,Z)->boundaryCond);
        double right = PDEequations.DisplacedX3D(field,x + 1, y,z, xDim, yDim,zDim,wrapX,(X,Y,Z)->boundaryCond);
        return right - left;
    }
    public double GradientX(double x, double y,double z,double boundaryCond) {
        return GradientX((int)x,(int)y,(int)z,boundaryCond);
    }

    /**
     * returns the gradient of the diffusible in the Y direction at the coordinates specified, will use the boundary
     * condition value if computing the gradient next to the boundary
     */
    public double GradientY(int x, int y,int z,double boundaryCond) {
        double down = PDEequations.DisplacedY3D(field,x,y-1,z, xDim, yDim,zDim, wrapY,(X,Y,Z)->boundaryCond);
        double up = PDEequations.DisplacedY3D(field,x, y+1,z, xDim, yDim,zDim,wrapY,(X,Y,Z)->boundaryCond);
        return up - down;
    }
    public double GradientY(double x, double y,double z,double boundaryCond) {
        return GradientY((int)x,(int)y,(int)z,boundaryCond);
    }

    /**
     * returns the gradient of the diffusible in the Z direction at the coordinates specified, will use the boundary
     * condition value if computing the gradient next to the boundary
     */
    public double GradientZ(int x, int y,int z,double boundaryCond) {
        double down = PDEequations.DisplacedY3D(field,x,y,z-1, xDim, yDim,zDim, wrapZ,(X,Y,Z)->boundaryCond);
        double up = PDEequations.DisplacedY3D(field,x, y,z+1, xDim, yDim,zDim,wrapZ,(X,Y,Z)->boundaryCond);
        return up - down;
    }
    public double GradientZ(double x, double y,double z,double boundaryCond) {
        return GradientZ((int)x,(int)y,(int)z,boundaryCond);
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
    public int Zdim() {
        return zDim;
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

    @Override
    public boolean IsWrapZ() {
        return wrapZ;
    }
}
