package Framework.GridsAndAgents;
import Framework.Interfaces.Coords3DSetArray;
import Framework.Util;
//import AgentFramework.Util;


import java.io.Serializable;
import java.util.Arrays;

import static Framework.Tools.PDEequations.*;

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
    //public double[] middleField;

    public PDEGrid3D(int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ){
        super(xDim,yDim,zDim,wrapX,wrapY,wrapZ);
        int numElements = this.xDim * this.yDim * this.zDim;

        field=new double[numElements];
        nextField = new double[numElements];
        scratch=null;
        //middleField = new double[numElements];
    }

    public PDEGrid3D(int xDim, int yDim, int zDim){
        super(xDim,yDim,zDim,false,false,false);

        int numElements = this.xDim * this.yDim * this.zDim;
        field=new double[numElements];
        nextField = new double[numElements];
        scratch=null;
        //middleField = new double[numElements];
    }


    /**
     * sets the current field value at the specified index
     */
    //public void SetMiddle(int i,double val){
    //    middleField[i]=val;}

    /**
     * gets the next field value at the specified index
     */
    //public double GetMiddle(int i){return middleField[i];}

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
        nextField[x*yDim*zDim+y*zDim+z]=val;
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, double val){
        nextField[i]=val;}



    /**
     * adds to the next field value at the specified index
     */
    public void Add(int x, int y, int z, double val){
        nextField[x*yDim*zDim+y*zDim+z]+=val;
    }
    public void Add(int i, double val) {
        nextField[i] += val;
    }
    public void Mul(int i, double val){
        nextField[i]+=field[i]*(val-1);}
    public void Mul(int x,int y,int z, double val){
        nextField[x*yDim*zDim+y*zDim+z]+=field[x*yDim*zDim+y*zDim+z]*(val-1);}

    public void Update(){
        System.arraycopy(nextField,0,field,0,length);
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
     * Swaps the next and current field, and increments the tick
     */
//    public void SwapInc(){
//        Update();
//        IncTick();
//    }

    /**
     * Bounds all values in the next field between min and max
     */
    public void BoundAllSwap(double min, double max){
        for(int i=0;i<length;i++){
            nextField[i]= Util.Bound(nextField[i],min,max);
        }
    }
    /**
     * Runs diffusion on the current field, putting the results into the next field
     */
    public void Diffusion(double diffCoef){
        if(diffCoef>1.0/6){
            throw new IllegalArgumentException("3D Diffusion is unstable if rate is above 0.1666666! rate: "+diffCoef);
        }
        Diffusion3(field, nextField,xDim,yDim,zDim,diffCoef,false,0.0,wrapX,wrapY,wrapZ);
    }
    public void Diffusion(double diffCoef, double boundaryValue){
        if(diffCoef>1.0/6){
            throw new IllegalArgumentException("3D Diffusion is unstable if rate is above 0.1666666! rate: "+diffCoef);
        }
        Diffusion3(field, nextField,xDim,yDim,zDim,diffCoef,true,boundaryValue,wrapX,wrapY,wrapZ);
    }
    //    public void ADITripleDiffSwap(final double diffCoef){
//        if(scratch==null){
//            scratch=new double[Math.max(Math.max(xDim,yDim),zDim)*2];
//        }
//        Util.DiffusionADI3(0,field,nextField,scratch,xDim,yDim,zDim,diffCoef/3);
//        Update();
//        Util.DiffusionADI3(1,field,nextField,scratch,xDim,yDim,zDim,diffCoef/3);
//        Update();
//        Util.DiffusionADI3(2,field,nextField,scratch,xDim,yDim,zDim,diffCoef/3);
//        Update();
//    }

    /**
     * returns the maximum difference between the current field and the next field
     */
    public double MaxDif() {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs((field[i] - nextField[i]) / field[i]));
        }
        return maxDif;
    }
    public double MaxDif(double[] compareTo){
        double maxDif=0;
            for(int i = 0; i< field.length; i++){
                maxDif=Math.max(maxDif,Math.abs((field[i]- compareTo[i])/ field[i]));
            }
        return maxDif;
    }

    public void Advection(double xVel,double yVel,double zVel,double boundaryVal){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    Advection3D1stOrder(x,y,z,field, nextField,xDim,yDim,zDim,xVel,yVel,zVel,true,boundaryVal);
                }
            }
        }
    }
    public void Advection(double[] xVels,double[] yVels,double[] zVels,double boundaryVal){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    Advection3D1stOrder(x,y,z,field, nextField,xDim,yDim,zDim,xVels[I(x,y,z)],yVels[I(x,y,z)],zVels[I(x,y,z)],true,boundaryVal);
                }
            }
        }
    }
    public void Advection(Coords3DSetArray CoordsToVel, double boundaryVal,double[]scratch){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    CoordsToVel.SetArray(x,y,z,scratch);
                    Advection3D1stOrder(x,y,z,field, nextField,xDim,yDim,zDim,scratch[0],scratch[1],scratch[2],true,boundaryVal);
                }
            }
        }
    }
    public void Advection(double xVel,double yVel,double zVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    Advection3D1stOrder(x,y,z,field, nextField,xDim,yDim,zDim,xVel,yVel,zVel,false,0);
                }
            }
        }
    }
    public void Advection(double[] xVels,double[] yVels,double[] zVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    Advection3D1stOrder(x,y,z,field, nextField,xDim,yDim,zDim,xVels[I(x,y,z)],yVels[I(x,y,z)],zVels[I(x,y,z)],false,0);
                }
            }
        }
    }
    public void Advection(Coords3DSetArray CoordsToVel,double[]scratch){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    CoordsToVel.SetArray(x,y,z,scratch);
                    Advection3D1stOrder(x,y,z,field, nextField,xDim,yDim,zDim,scratch[0],scratch[1],scratch[2],false,0);
                }
            }
        }
    }

    public double MaxDifInternal(){
        if(maxDifscratch==null){
            maxDifscratch=new double[length];
        }
        double ret=MaxDif();
        System.arraycopy(field,0,maxDifscratch,0,length);
        return ret;
    }


    /**
     * sets all squares in the next field to the specified value
     */
    public void SetAll(double val){
        Arrays.fill(nextField,val);
    }


    public void AddAll(double val){
        for (int i = 0; i < length; i++) {
            nextField[i]+=val;
        }
    }
    public void MulAll(double val){
        for (int i = 0; i < length; i++) {
            nextField[i]*=val;
        }
    }

    /**
     * gets the average value of all squares in the next field
     */
    public double GetAvgNext(){
        double tot=0;
        for(int i=0;i<length;i++){
            tot+= nextField[i];
        }
        return tot/length;
    }

}
