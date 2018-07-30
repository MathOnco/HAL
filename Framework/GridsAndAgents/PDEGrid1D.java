package Framework.GridsAndAgents;
import Framework.Util;

import java.io.Serializable;
import java.util.Arrays;

import static Framework.Tools.PDEequations.*;


/**
 * PDEGrid2D class facilitates 2D diffusion with two arrays of doubles called fields
 * the intended usage is that during a diffusion tick, the current values will be read, and the prev values will be written to
 * after updates, Update is called to set the prev field as the current field.
 */
public class PDEGrid1D extends GridBase1D implements Serializable{
    double[] nextField;
    double[]field;
    //double[] intermediateScratch;
    double[] scratch;
    double[] maxDifscratch;
    boolean adiOrder=true;
    boolean adiX=true;
    public PDEGrid1D(int xDim){
        super(xDim,false);
        nextField =new double[this.xDim];
    }
    public PDEGrid1D(int xDim, int yDim, boolean wrapX, boolean wrapY){
        super(xDim,wrapX);
        nextField =new double[this.xDim];
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
        return this.nextField;
    }



    /**
     * gets the prev field value at the specified coordinates
     */
    public double Get(int x){ return field[x]; }

    /**
     * sets the prev field value at the specified coordinates
     */
    public void Set(int x, double val){ nextField[x]=val; }

    /**
     * adds to the prev field value at the specified index
     */
    public void Add(int x, double val){
        nextField[x]+=val;}

    public void Mul(int x, double val){
        nextField[x]*=val;}

    public void Update(){
        System.arraycopy(nextField,0,field,0,length);
    }
    /**
     * Swaps the prev and current field
     */
    void SwapFields(){
        double[]temp= field;
        field = nextField;
        nextField =temp;

    }

    /**
     * Swaps the prev and current field, and increments the tick
     */
//    public void SwapInc(){
//        Update();
//        IncTick();
//    }

    //assumed wrap around if non-periodic
    public void Advection(double xVel){
        for (int x = 0; x < xDim; x++) {
            Advection1stOrder1D(x,field, nextField, xDim, xVel, false, 0.0);
        }
    }
    public void Advection(double xVel, double yVel, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
                Advection1stOrder1D(x, field, nextField, xDim, xVel, true, boundaryValue);
        }
    }
    public void Advection(double[] xVels,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
                Advection1stOrder1D(x,field, nextField,xDim,xVels[x],true,boundaryValue);
        }
    }
    public void Advection(double[] xVels){
        for (int x = 0; x < xDim; x++) {
                Advection1stOrder1D(x,field, nextField,xDim,xVels[x],false,0.0);
        }
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
                Diffusion1(x, field, nextField, xDim, diffCoef, false, 0.0, wrapX);
        }
    }
    public void DiffusionSwap(double diffCoef){
        if(diffCoef>0.25){
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: "+diffCoef);
        }
        for (int x = 0; x < xDim; x++) {
            Diffusion1Swap(x, field, nextField, xDim, diffCoef, false, 0.0, wrapX);
        }
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
                Diffusion1(x, field, nextField, xDim,  diffCoef, true, boundaryValue, wrapX);
        }
    }


    public void Diffusion(double[] diffCoefs){
        for (int x = 0; x < xDim; x++) {
                Diffusion1inhomogeneous(x, field, nextField, diffCoefs, xDim, false, 0.0, wrapX);
        }
    }
    public void Diffusion(double[] diffCoefs,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
                Diffusion1inhomogeneous(x,field, nextField, diffCoefs, xDim, true, boundaryValue, wrapX);
        }
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
  //      Util.Diffusion2(field, nextField,xDim,yDim,diffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
  //      Update();
  //      IncTick();
  //  }
  //  public void DiffSwapInc(double diffCoef,boolean boundaryCond,double boundaryValue){
  //      //NOTE: EXPLICIT DIFFUSION WILL ONLY BE STABLE IF diffCoef <= 1/4
  //      Util.Diffusion2(field, nextField,xDim,yDim,diffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
  //      Update();
  //      IncTick();
  //  }
  //  public void DiffSwapInc1(double diffCoef,boolean boundaryCond,double boundaryValue,boolean wrapX,boolean wrapY){
  //      //NOTE: EXPLICIT DIFFUSION WILL ONLY BE STABLE IF diffCoef <= 1/4
  //      Util.Diffusion(field, nextField,xDim,yDim,diffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
  //      Update();
  //      IncTick();
  //  }
  //  public void DiffSwapInc1(double diffCoef,boolean boundaryCond,double boundaryValue){
  //      //NOTE: EXPLICIT DIFFUSION WILL ONLY BE STABLE IF diffCoef <= 1/4
  //      Util.Diffusion(field, nextField,xDim,yDim,diffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
  //      Update();
  //      IncTick();
  //  }


    /**
     * sets all squares in the prev field to the specified value
     */
    public void SetAll(double val){
        Arrays.fill(nextField,val);
    }

    public void MulAll(double val){
        for (int i = 0; i < length; i++) {
            nextField[i]*=val;
        }
    }

    public void SetAll(double[] vals){
        System.arraycopy(vals,0, nextField,0,length);
    }

    /**
     * adds specified value to all entries of the prev field
     */
    public void AddAll(double val){
        for (int i = 0; i < length; i++) {
            nextField[i]+=val;
        }
    }


    /**
     * gets the average value of all squares in the prev field
     */
    public double GetAvg(){
        double tot=0;
        for(int i=0;i<length;i++){
            tot+= field[i];
        }
        return tot/length;
    }
    /**
     * returns the maximum difference between the current field and the prev field
     */
    public double MaxDifNext(){
        double maxDif=0;
        for(int i = 0; i< field.length; i++){
            maxDif=Math.max(maxDif,Math.abs((field[i]- nextField[i])));
        }
        return maxDif;
    }
    public double MaxDifSwapScaled(double denomOffset){
        double maxDif=0;
        for(int i = 0; i< field.length; i++){
            maxDif=Math.max(maxDif,Math.abs(field[i]- nextField[i])/ (nextField[i]+denomOffset));
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

    public double MaxDifOtherScaled(double[]compareTo, double denomOffset){
        double maxDif=0;
        for(int i = 0; i< field.length; i++){
            maxDif=Math.max(maxDif,Math.abs(field[i]- compareTo[i])/ (compareTo[i]+denomOffset));
        }
        return maxDif;
    }

    public double MaxDifference(){
        if(maxDifscratch==null){
            maxDifscratch=new double[length];
        }
        double ret= MaxDifOther(maxDifscratch);
        System.arraycopy(field,0,maxDifscratch,0,length);
        return ret;
    }
    public double MaxDifferenceScaled(double denomOffset){
        if(maxDifscratch==null){
            maxDifscratch=new double[length];
        }
        double ret= MaxDifOtherScaled(maxDifscratch,denomOffset);
        System.arraycopy(field,0,maxDifscratch,0,length);
        return ret;
    }
}
