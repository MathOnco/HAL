package Framework.GridsAndAgents;
import Framework.Interfaces.Coords2DSetArray;
import Framework.Interfaces.GridDiff2MultiThreadFunction;
import Framework.Util;

import java.io.Serializable;
import java.util.Arrays;

import static Framework.Tools.PDEequations.*;


/**
 * PDEGrid2D class facilitates 2D diffusion with two arrays of doubles called fields
 * the intended usage is that during a diffusion step, the current values will be read, and the prev values will be written to
 * after updates, SwapFields is called to set the prev field as the current field.
 */
public class PDEGrid1D extends Grid1Ddouble implements Serializable{
    double[] swapField;
    //double[] intermediateScratch;
    double[] scratch;
    double[] maxDifscratch;
    boolean adiOrder=true;
    boolean adiX=true;
    public PDEGrid1D(int xDim){
        super(xDim,false);
        swapField =new double[this.xDim];
    }
    public PDEGrid1D(int xDim, int yDim, boolean wrapX, boolean wrapY){
        super(xDim,wrapX);
        swapField =new double[this.xDim];
    }
//    public double[]GetIntermediateScratch(){
//        if(intermediateScratch==null){
//            intermediateScratch=new double[length];
//        }
//        return intermediateScratch;
//    }

    void EnsureScratch(){
        if(scratch==null){
            scratch=new double[xDim*2+4];
        }
    }




    public double[] GetSwapField(){
        return this.swapField;
    }



    /**
     * gets the prev field value at the specified coordinates
     */
    public double GetSwap(int x){ return swapField[x]; }

    /**
     * sets the prev field value at the specified coordinates
     */
    public void SetSwap(int x, double val){ swapField[x]=val; }

    /**
     * adds to the prev field value at the specified index
     */
    public void AddSwap(int x, double val){
        swapField[x]+=val;}

    /**
     * copies the current field into the prev field
     */
    public void CurrIntoSwap(){ System.arraycopy(field, 0, swapField, 0, field.length); }


    /**
     * Bounds all values in the prev field between min and max
     */
    public void BoundAllSwap(double min, double max){
        for(int i=0;i<length;i++){
            swapField[i]= Util.Bound(swapField[i],min,max);
        }
    }
    /**
     * Swaps the prev and current field
     */
    public void SwapFields(){
        double[]temp= field;
        field = swapField;
        swapField =temp;

    }

    /**
     * Swaps the prev and current field, and increments the tick
     */
//    public void SwapInc(){
//        SwapFields();
//        IncTick();
//    }

    //assumed wrap around if non-periodic
    public void Advection(double xVel){
        for (int x = 0; x < xDim; x++) {
            Advection1stOrder1D(x,field, swapField, xDim, xVel, false, 0.0);
        }
        SwapFields();
    }
    public void Advection(double xVel, double yVel, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
                Advection1stOrder1D(x, field, swapField, xDim, xVel, true, boundaryValue);
        }
        SwapFields();
    }
    public void Advection(double[] xVels,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
                Advection1stOrder1D(x,field, swapField,xDim,xVels[x],true,boundaryValue);
        }
        SwapFields();
    }
    public void Advection(double[] xVels){
        for (int x = 0; x < xDim; x++) {
                Advection1stOrder1D(x,field, swapField,xDim,xVels[x],false,0.0);
        }
        SwapFields();
    }
    /**
     * Runs diffusion on the current field, putting the result into the prev field, then swaps current and prev
     * @param diffCoef rate of diffusion
     */
    public void Diffusion(double diffCoef){
        if(diffCoef>0.25){
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: "+diffCoef);
        }
        for (int x = 0; x < xDim; x++) {
                Diffusion1(x, field, swapField, xDim, diffCoef, false, 0.0, wrapX);
        }
        SwapFields();
    }
    public void Diffusion(double diffCoef, boolean wrapX, boolean wrapY){
        if(diffCoef>0.25){
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: "+diffCoef);
        }
        for (int x = 0; x < xDim; x++) {
                Diffusion1(x, field, swapField, xDim, diffCoef, false, 0.0, wrapX);
        }
        SwapFields();
    }
    /**
     * Runs diffusion on the current field, putting the result into the prev field, then swaps current and prev
     * @param diffCoef rate of diffusion
     * @param boundaryValue value that diffuses in from the boundary
     */
    public void Diffusion(double diffCoef, double boundaryValue){
        if(diffCoef>0.25){
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: "+diffCoef);
        }
        for (int x = 0; x < xDim; x++) {
                Diffusion1(x, field, swapField, xDim,  diffCoef, true, boundaryValue, wrapX);
        }
        SwapFields();
    }
    public void Diffusion(double diffCoef, double boundaryValue, boolean wrapX, boolean wrapY){
        if(diffCoef>0.25){
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: "+diffCoef);
        }
        for (int x = 0; x < xDim; x++) {
        Diffusion1(x,field, swapField,xDim,diffCoef,true,boundaryValue,wrapX);
        }
        SwapFields();
    }


    public void Diffusion(double[] diffCoefs, boolean wrapX, boolean wrapY){
        for (int x = 0; x < xDim; x++) {
                Diffusion1inhomogeneous(x, field, swapField, diffCoefs, xDim, false, 0.0, wrapX);
        }
        SwapFields();
    }
    public void Diffusion(double[] diffCoefs){
        for (int x = 0; x < xDim; x++) {
                Diffusion1inhomogeneous(x, field, swapField, diffCoefs, xDim, false, 0.0, wrapX);
        }
        SwapFields();
    }
    public void Diffusion(double[] diffCoefs,double boundaryValue, boolean wrapX, boolean wrapY){
        for (int x = 0; x < xDim; x++) {
                Diffusion1inhomogeneous(x, field, swapField, diffCoefs, xDim, true, boundaryValue, wrapX);
            }
        SwapFields();
    }
    public void Diffusion(double[] diffCoefs,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
                Diffusion1inhomogeneous(x,field, swapField, diffCoefs, xDim, true, boundaryValue, wrapX);
        }
        SwapFields();
    }

    /**
     * Runs diffusion on the current field, putting the result into the prev field, then swaps current and prev, and increments the tick
     * @param diffCoef rate of diffusion
     * @param boundaryCond whether a boundary condition value will diffuse in from the field boundaries
     * @param boundaryValue only applies when boundaryCond is true, the boundary condition value
     * @param wrapX whether to wrap the field over the left and right boundaries
     */
  //  public void DiffSwapInc(double diffCoef,boolean boundaryCond,double boundaryValue,boolean wrapX,boolean wrapY){
  //      //NOTE: EXPLICIT DIFFUSION WILL ONLY BE STABLE IF diffCoef <= 1/4
  //      Util.Diffusion2(field, swapField,xDim,yDim,diffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
  //      SwapFields();
  //      IncTick();
  //  }
  //  public void DiffSwapInc(double diffCoef,boolean boundaryCond,double boundaryValue){
  //      //NOTE: EXPLICIT DIFFUSION WILL ONLY BE STABLE IF diffCoef <= 1/4
  //      Util.Diffusion2(field, swapField,xDim,yDim,diffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
  //      SwapFields();
  //      IncTick();
  //  }
  //  public void DiffSwapInc1(double diffCoef,boolean boundaryCond,double boundaryValue,boolean wrapX,boolean wrapY){
  //      //NOTE: EXPLICIT DIFFUSION WILL ONLY BE STABLE IF diffCoef <= 1/4
  //      Util.Diffusion(field, swapField,xDim,yDim,diffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
  //      SwapFields();
  //      IncTick();
  //  }
  //  public void DiffSwapInc1(double diffCoef,boolean boundaryCond,double boundaryValue){
  //      //NOTE: EXPLICIT DIFFUSION WILL ONLY BE STABLE IF diffCoef <= 1/4
  //      Util.Diffusion(field, swapField,xDim,yDim,diffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
  //      SwapFields();
  //      IncTick();
  //  }


    /**
     * sets all squares in the prev field to the specified value
     */
    public void SetAllSwap(double val){
        Arrays.fill(swapField,val);
    }


    public void MulAllSwap(double val){
        for (int i = 0; i < length; i++) {
            swapField[i]*=val;
        }
    }

    public void SetAllSwap(double[] vals){
        System.arraycopy(vals,0, swapField,0,length);
    }

    /**
     * adds specified value to all entries of the prev field
     */
    public void AddAllSwap(double val){
        for (int i = 0; i < length; i++) {
            swapField[i]+=val;
        }
    }


    /**
     * gets the average value of all squares in the prev field
     */
    public double GetAvgSwap(){
        double tot=0;
        for(int i=0;i<length;i++){
            tot+= swapField[i];
        }
        return tot/length;
    }
    /**
     * returns the maximum difference between the current field and the prev field
     */
    public double MaxDifSwap(){
        double maxDif=0;
        for(int i = 0; i< field.length; i++){
            maxDif=Math.max(maxDif,Math.abs((field[i]- swapField[i])));
        }
        return maxDif;
    }
    public double MaxDifSwapScaled(double denomOffset){
        double maxDif=0;
        for(int i = 0; i< field.length; i++){
            maxDif=Math.max(maxDif,Math.abs(field[i]- swapField[i])/ (swapField[i]+denomOffset));
        }
        return maxDif;
    }


    public double MaxDifOther(double[] compareTo){
        double maxDif=0;
            for(int i = 0; i< field.length; i++){
                maxDif=Math.max(maxDif,Math.abs(field[i]- compareTo[i]));
            }
        return maxDif;
    }

    public double MaxDifference(){
        if(maxDifscratch==null){
            maxDifscratch=new double[length];
        }
        double ret= MaxDifOther(maxDifscratch);
        System.arraycopy(GetField(),0,maxDifscratch,0,length);
        return ret;
    }
    public double MaxDifferenceScaled(double denomOffset){
        if(maxDifscratch==null){
            maxDifscratch=new double[length];
        }
        double ret= MaxDifOtherScaled(maxDifscratch,denomOffset);
        System.arraycopy(GetField(),0,maxDifscratch,0,length);
        return ret;
    }
}
