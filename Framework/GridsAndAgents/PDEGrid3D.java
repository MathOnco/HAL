package Framework.GridsAndAgents;
import Framework.Interfaces.Coords3DSetArray;
import Framework.Util;
//import AgentFramework.Util;


import java.io.Serializable;
import java.util.Arrays;

import static Framework.Tools.Internal.PDEequations.*;

/**
 * PDEGrid3D class facilitates 2D diffusion with two arrays of doubles called fields
 * the intended usage is that during a diffusion tick, the current values will be read, and the next values will be written to
 * after updates, Update is called to set the next field as the current field.
 */
public class PDEGrid3D extends GridBase3D implements Serializable{
    public double[] field;
    public double[] nextField;
    public double[] scratch;
    public double[] maxDifscratch;

    public PDEGrid3D(int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ){
        super(xDim,yDim,zDim,wrapX,wrapY,wrapZ);
        int numElements = this.xDim * this.yDim * this.zDim;

        field=new double[numElements];
        nextField = new double[numElements];
        scratch=null;
    }

    public PDEGrid3D(int xDim, int yDim, int zDim){
        super(xDim,yDim,zDim,false,false,false);

        int numElements = this.xDim * this.yDim * this.zDim;
        field=new double[numElements];
        nextField = new double[numElements];
        scratch=null;
    }

    /**
     * gets to the current field value at the specified coordinates
     */
    public double Get(int x, int y, int z){
        return field[x*yDim*zDim+y*zDim+z];
    }

    /**
     * gets the next field value at the specified index
     */
    public double Get(int i){return field[i];}

    /**
     * gets to the current field value at the specified coordinates
     */
    public void Set(int x, int y, int z, double val){
        nextField[x*yDim*zDim+y*zDim+z]=val-field[x*yDim*zDim+y*zDim+z];
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, double val){
        nextField[i]=val-field[i];}



    /**
     * adds to the next field value at the specified index
     */
    public void Add(int x, int y, int z, double val){
        nextField[x*yDim*zDim+y*zDim+z]+=val;
    }
    /**
     * adds to the next field value at the specified index
     */
    public void Add(int i, double val) {
        nextField[i] += val;
    }
    /**
     *  multiplies a value in the “current field” and adds the change to the “next field”
     */
    public void Mul(int i, double val){
        nextField[i]+=field[i]*(val-1);}
    /**
     *  multiplies a value in the “current field” and adds the change to the “next field”
     */
    public void Mul(int x,int y,int z, double val){
        nextField[x*yDim*zDim+y*zDim+z]+=field[x*yDim*zDim+y*zDim+z]*(val-1);}

    /**
     adds the next field into the current field, also increments the tick.
     */
    public void Update(){
        for (int i = 0; i < nextField.length; i++) {
            field[i]+=nextField[i];
        }
        Arrays.fill(nextField,0);
        IncTick();
    }

    /**
     * swaps the next and current field
     */
    void SwapFields(){
        double[] temp= field;
        field = nextField;
        nextField =temp;
    }

    /**
     * Bounds all values in the next field between min and max
     */
    public void BoundAllSwap(double min, double max){
        for(int i=0;i<length;i++){
            nextField[i]= Util.Bound(nextField[i],min,max);
        }
    }
    /**
     /**
     * runs diffusion on the current field, adding the deltas to the next field. This form of the function assumes
     * either a reflective or wrapping boundary (depending on how the PDEGrid was specified). the diffCoef variable is
     * the nondimensionalized diffusion conefficient. If the dimensionalized diffusion coefficient is x then diffCoef
     * can be found by computing (x*SpaceStep)/TimeStep^2 Note that if the diffCoef exceeds 0.25, this diffusion method
     * will become numerically unstable.
     */
    public void Diffusion(double diffCoef){
        if(diffCoef>1.0/6){
            throw new IllegalArgumentException("3D Diffusion is unstable if rate is above 0.1666666! rate: "+diffCoef);
        }
        Diffusion3(field, nextField,xDim,yDim,zDim,diffCoef,false,0.0,wrapX,wrapY,wrapZ);
    }

    /**
     * has the same effect as the above diffusion function without the boundary value argument, except rather than
     * assuming zero flux, the boundary condition is set to either the boundaryValue, or wrap around
     */
    public void Diffusion(double diffCoef, double boundaryValue){
        if(diffCoef>1.0/6){
            throw new IllegalArgumentException("3D Diffusion is unstable if rate is above 0.1666666! rate: "+diffCoef);
        }
        Diffusion3(field, nextField,xDim,yDim,zDim,diffCoef,true,boundaryValue,wrapX,wrapY,wrapZ);
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
     * like MaxDifNext only the differences are scaled relative to the value in the current field. the denomOffset is used to prevent a division by zero
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
     * like MaxDifNext only the differences are computed by comparing the current field with the field state as it was the last time MaxDifRecord was called
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
     * like MaxDifOther only the differences are scaled relative to the value in the current field. the denomOffset is used to prevent a division by zero
     */
    public double MaxDifOtherScaled(double[] compareTo, double denomOffset) {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs(field[i] - compareTo[i]) / (compareTo[i] + denomOffset));
        }
        return maxDif;
    }


    /**
     * like MaxDifRecord only the differences are scaled relative to the value in the current field. the denomOffset is used to prevent a division by zero
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
     * runs advection, which moves the concentrations using a constant flow with the x and y velocities passed. this signature of the function assumes wrap-around, so there can be no net flux of concentrations.
     */
    public void Advection(double xVel,double yVel,double zVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    Advection3D1stOrder(x,y,z,field, nextField,xDim,yDim,zDim,xVel,yVel,zVel,false,0);
                }
            }
        }
    }


    /**
     * runs advection as described above with a boundary value, meaning that the boundary value will advect in from the upwind direction, and the concentration will disappear in the downwind direction.
     */
    public void Advection(double xVel,double yVel,double zVel,double boundaryVal){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    Advection3D1stOrder(x,y,z,field, nextField,xDim,yDim,zDim,xVel,yVel,zVel,true,boundaryVal);
                }
            }
        }
    }

    /**
     * returns the maximum difference between the field passed in and the current field. call right after Update()
     */
    public double MaxDifInternal(){
        if(maxDifscratch==null){
            maxDifscratch=new double[length];
        }
        double ret= MaxDifNext();
        System.arraycopy(field,0,maxDifscratch,0,length);
        return ret;
    }


    /**
     * sets all squares in the next field using the vals array
     */
    public void SetAll(double val){
        for (int i = 0; i < length; i++) {
            Set(i,val);
        }
    }


    /**
     * adds specified value to all entries of the next field
     */
    public void AddAll(double val){
        for (int i = 0; i < length; i++) {
            Add(i,val);
        }
    }

    /**
     *  multiplies all values in the “current field” and puts the results into the “next field”
     */
    public void MulAll(double val){
        for (int i = 0; i < length; i++) {
            Mul(i,val);
        }
    }
}
