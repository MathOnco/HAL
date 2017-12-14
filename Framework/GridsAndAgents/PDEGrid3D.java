package Framework.GridsAndAgents;
import Framework.Interfaces.Coords3DSetArray;
import Framework.Util;
//import AgentFramework.Util;


import java.io.Serializable;
import java.util.Arrays;

import static Framework.Tools.PDEequations.*;

/**
 * PDEGrid3D class facilitates 2D diffusion with two arrays of doubles called fields
 * the intended usage is that during a diffusion step, the current values will be read, and the next values will be written to
 * after updates, SwapFields is called to set the next field as the current field.
 */
public class PDEGrid3D extends Grid3Ddouble implements Serializable{
    public double[] swapField;
    public double[] scratch;
    public double[] maxDifscratch;
    //public double[] middleField;

    public PDEGrid3D(int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ){
        super(xDim,yDim,zDim,wrapX,wrapY,wrapZ);
        int numElements = this.xDim * this.yDim * this.zDim;

        swapField = new double[numElements];
        scratch=null;
        //middleField = new double[numElements];
    }

    public PDEGrid3D(int xDim, int yDim, int zDim){
        super(xDim,yDim,zDim,false,false,false);

        int numElements = this.xDim * this.yDim * this.zDim;
        swapField = new double[numElements];
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
    public double GetSwap(int x, int y, int z){
        return swapField[x*yDim*zDim+y*zDim+z];
    }

    /**
     * gets the next field value at the specified index
     */
    public double GetSwap(int i){return swapField[i];}

    /**
     * gets to the current field value at the specified coordinates
     */
    public void SetSwap(int x, int y, int z, double val){
        swapField[x*yDim*zDim+y*zDim+z]=val;
    }

    /**
     * sets the current field value at the specified index
     */
    public void SetSwap(int i, double val){
        swapField[i]=val;}



    /**
     * adds to the next field value at the specified index
     */
    public void AddSwap(int x, int y, int z, double val){
        swapField[x*yDim*zDim+y*zDim+z]+=val;
    }
    public void AddSwap(int i, double val) {
        swapField[i] += val;
    }

    /**
     * copies the current field into the next field
     */
    public void NextCopyCurr(){
        System.arraycopy(field,0, swapField,0, field.length);
    }

    /**
     * swaps the next and current field
     */
    public void SwapFields(){
        double[] temp= field;
        field = swapField;
        swapField =temp;
    }

    /**
     * Swaps the next and current field, and increments the tick
     */
    public void SwapInc(){
        SwapFields();
        IncTick();
    }

    /**
     * Bounds all values in the next field between min and max
     */
    public void BoundAllSwap(double min, double max){
        for(int i=0;i<length;i++){
            swapField[i]= Util.Bound(swapField[i],min,max);
        }
    }
    /**
     * Runs diffusion on the current field, putting the results into the next field
     */
    public void Diffusion(double diffRate){
        if(diffRate>1.0/6){
            throw new IllegalArgumentException("3D Diffusion is unstable if rate is above 0.1666666! rate: "+diffRate);
        }
        Diffusion3(field, swapField,xDim,yDim,zDim,diffRate,false,0.0,wrapX,wrapY,wrapZ);
        SwapFields();
    }
    public void Diffusion(double diffRate, boolean wrapX, boolean wrapY, boolean wrapZ){
        if(diffRate>1.0/6){
            throw new IllegalArgumentException("3D Diffusion is unstable if rate is above 0.1666666! rate: "+diffRate);
        }
        Diffusion3(field, swapField,xDim,yDim,zDim,diffRate,false,0.0,wrapX,wrapY,wrapZ);
        SwapFields();
    }
    public void Diffusion(double diffRate, double boundaryValue){
        if(diffRate>1.0/6){
            throw new IllegalArgumentException("3D Diffusion is unstable if rate is above 0.1666666! rate: "+diffRate);
        }
        Diffusion3(field, swapField,xDim,yDim,zDim,diffRate,true,boundaryValue,wrapX,wrapY,wrapZ);
        SwapFields();
    }
    public void Diffusion(double diffRate, double boundaryValue, boolean wrapX, boolean wrapY, boolean wrapZ){
        if(diffRate>1.0/6){
            throw new IllegalArgumentException("3D Diffusion is unstable if rate is above 0.1666666! rate: "+diffRate);
        }
        Diffusion3(field, swapField,xDim,yDim,zDim,diffRate,true,boundaryValue,wrapX,wrapY,wrapZ);
        SwapFields();
    }
    //    public void ADITripleDiffSwap(final double diffRate){
//        if(scratch==null){
//            scratch=new double[Math.max(Math.max(xDim,yDim),zDim)*2];
//        }
//        Util.DiffusionADI3(0,field,swapField,scratch,xDim,yDim,zDim,diffRate/3);
//        SwapFields();
//        Util.DiffusionADI3(1,field,swapField,scratch,xDim,yDim,zDim,diffRate/3);
//        SwapFields();
//        Util.DiffusionADI3(2,field,swapField,scratch,xDim,yDim,zDim,diffRate/3);
//        SwapFields();
//    }

    /**
     * returns the maximum difference between the current field and the next field
     */
    public double MaxDif() {
        double maxDif = 0;
        for (int i = 0; i < field.length; i++) {
            maxDif = Math.max(maxDif, Math.abs((field[i] - swapField[i]) / field[i]));
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
                    Advection3D1stOrder(x,y,z,field,swapField,xDim,yDim,zDim,xVel,yVel,zVel,true,boundaryVal);
                }
            }
        }
        SwapFields();
    }
    public void Advection(double[] xVels,double[] yVels,double[] zVels,double boundaryVal){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    Advection3D1stOrder(x,y,z,field,swapField,xDim,yDim,zDim,xVels[I(x,y,z)],yVels[I(x,y,z)],zVels[I(x,y,z)],true,boundaryVal);
                }
            }
        }
        SwapFields();
    }
    public void Advection(Coords3DSetArray CoordsToVel, double boundaryVal,double[]scratch){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    CoordsToVel.SetArray(x,y,z,scratch);
                    Advection3D1stOrder(x,y,z,field,swapField,xDim,yDim,zDim,scratch[0],scratch[1],scratch[2],true,boundaryVal);
                }
            }
        }
        SwapFields();
    }
    public void Advection(double xVel,double yVel,double zVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    Advection3D1stOrder(x,y,z,field,swapField,xDim,yDim,zDim,xVel,yVel,zVel,false,0);
                }
            }
        }
        SwapFields();
    }
    public void Advection(double[] xVels,double[] yVels,double[] zVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    Advection3D1stOrder(x,y,z,field,swapField,xDim,yDim,zDim,xVels[I(x,y,z)],yVels[I(x,y,z)],zVels[I(x,y,z)],false,0);
                }
            }
        }
        SwapFields();
    }
    public void Advection(Coords3DSetArray CoordsToVel,double[]scratch){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    CoordsToVel.SetArray(x,y,z,scratch);
                    Advection3D1stOrder(x,y,z,field,swapField,xDim,yDim,zDim,scratch[0],scratch[1],scratch[2],false,0);
                }
            }
        }
        SwapFields();
    }

    public double MaxDifInternal(){
        if(maxDifscratch==null){
            maxDifscratch=new double[length];
        }
        double ret=MaxDif();
        System.arraycopy(GetField(),0,maxDifscratch,0,length);
        return ret;
    }


    /**
     * sets all squares in the next field to the specified value
     */
    public void SetAllSwap(double val){
        Arrays.fill(swapField,val);
    }


    public void AddAllSwap(double val){
        for (int i = 0; i < length; i++) {
            swapField[i]+=val;
        }
    }
    public void MulAllSwap(double val){
        for (int i = 0; i < length; i++) {
            swapField[i]*=val;
        }
    }

    /**
     * gets the average value of all squares in the next field
     */
    public double GetAvgNext(){
        double tot=0;
        for(int i=0;i<length;i++){
            tot+= swapField[i];
        }
        return tot/length;
    }

    /**
     * Copies the values currently contained in field into swapField
     */
    public void CurrIntoNext() {
        System.arraycopy(field,0, swapField,0,length);
    }
}
