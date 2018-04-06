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
public class PDEGrid2D extends Grid2Ddouble implements Serializable{
    double[] swapField;
    //double[] intermediateScratch;
    double[] scratch;
    double[] maxDifscratch;
    boolean adiOrder=true;
    boolean adiX=true;
    public PDEGrid2D(int xDim, int yDim){
        super(xDim,yDim,false,false);
        swapField =new double[this.xDim * this.yDim];
    }
    public PDEGrid2D(int xDim, int yDim, boolean wrapX, boolean wrapY){
        super(xDim,yDim,wrapX,wrapY);
        swapField =new double[this.xDim * this.yDim];
    }
//    public double[]GetIntermediateScratch(){
//        if(intermediateScratch==null){
//            intermediateScratch=new double[length];
//        }
//        return intermediateScratch;
//    }

    void EnsureScratch(){
        if(scratch==null){
            scratch=new double[Math.max(xDim,yDim)*2+4];
        }
    }



    public void DiffusionADI(double nonDimDiffCoef){
        EnsureScratch();
        DiffusionADI2(true, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,false,0);
        SwapFields();
        DiffusionADI2(false, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,false,0);
        SwapFields();
        adiOrder=!adiOrder;
    }
    public void DiffusionADIChangeOrder(double nonDimDiffCoef){
        EnsureScratch();
        DiffusionADI2(adiX^adiOrder, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,false,0);
        adiX=!adiX;
        SwapFields();
        DiffusionADI2(adiX^adiOrder, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,false,0);
        adiX=!adiX;
        SwapFields();
        adiOrder=!adiOrder;
    }
    public void DiffusionADI(double nonDimDiffCoef,double boundaryValue){
        EnsureScratch();
        DiffusionADI2(adiX^adiOrder, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,true,boundaryValue);
        adiX=!adiX;
        SwapFields();
        DiffusionADI2(adiX^adiOrder, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,true,boundaryValue);
        adiX=!adiX;
        SwapFields();
        adiOrder=!adiOrder;
    }
    public void DiffusionADIChangeOrder(double nonDimDiffCoef,double boundaryValue){
        EnsureScratch();
        DiffusionADI2(adiX^adiOrder, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,true,boundaryValue);
        adiX=!adiX;
        SwapFields();
        DiffusionADI2(adiX^adiOrder, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,true,boundaryValue);
        adiX=!adiX;
        SwapFields();
        adiOrder=!adiOrder;
    }
    public void DiffusionADIHalf(double nonDimDiffCoef){
        EnsureScratch();
        DiffusionADI2(adiX^adiOrder, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,false,0);
        adiX=!adiX;
        SwapFields();
        if(!adiX) {
            adiOrder = !adiOrder;
        }
    }
    public void DiffusionADIHalfX(double nonDimDiffCoef,boolean boundaryCond,double boundaryValue){
        EnsureScratch();
        DiffusionADI2(true, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,boundaryCond,boundaryValue);
        SwapFields();
    }
    public void DiffusionADIHalfY(double nonDimDiffCoef,boolean boundaryCond,double boundaryValue){
        EnsureScratch();
        DiffusionADI2(true, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,boundaryCond,boundaryValue);
        SwapFields();
    }
    public void DiffusionADIHalf(double nonDimDiffCoef,double boundaryValue){
        EnsureScratch();
        DiffusionADI2(adiX^adiOrder, field, swapField,scratch,xDim,yDim,nonDimDiffCoef/2,true,boundaryValue);
        adiX=!adiX;
        SwapFields();
        if(!adiX) {
            adiOrder = !adiOrder;
        }
    }

    public double[] GetSwapField(){
        return this.swapField;
    }



    /**
     * gets the prev field value at the specified coordinates
     */
    public double GetSwap(int x, int y){ return swapField[x*yDim+y]; }

    /**
     * gets the prev field value at the specified index
     */
    public double GetSwap(int i){return swapField[i];}

    /**
     * sets the prev field value at the specified coordinates
     */
    public void SetSwap(int x, int y, double val){ swapField[x*yDim+y]=val; }

    /**
     * sets the prev field value at the specified index
     */
    public void SetSwap(int i, double val){
        swapField[i]=val;}

    /**
     * sets the prev field value at the specified coordinates
     */
    public void AddSwap(int x, int y, double val){ swapField[x*yDim+y]+=val; }

    /**
     * adds to the prev field value at the specified index
     */
    public void AddSwap(int i, double val){
        swapField[i]+=val;}

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
    public void Advection(double xVel, double yVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
            Advection1stOrder(x,y, field, swapField, xDim, yDim, xVel, yVel, false, 0.0);
            }
        }
        SwapFields();
    }
    public void Advection(double xVel, double yVel, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection1stOrder(x, y, field, swapField, xDim, yDim, xVel, yVel, true, boundaryValue);
            }
        }
        SwapFields();
    }
    public void Advection(double[] xVels,double[] yVels,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection1stOrder(x,y, field, swapField,xDim,yDim,xVels[i],yVels[i],true,boundaryValue);
            }
        }
        SwapFields();
    }
    public void Advection(double[] xVels,double[] yVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection1stOrder(x,y, field, swapField,xDim,yDim,xVels[i],yVels[i],false,0.0);
            }
        }
        SwapFields();
    }
    public void Advection(Coords2DSetArray CoordsToVels,double[]scratch){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                CoordsToVels.SetArray(x,y,scratch);
                Advection1stOrder(x,y, field, swapField,xDim,yDim,scratch[0],scratch[1],false,0.0);
            }
        }
        SwapFields();
    }
    public void Advection(Coords2DSetArray CoordsToVels,double boundaryValue,double[]scratch){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                CoordsToVels.SetArray(x,y,scratch);
                Advection1stOrder(x,y, field, swapField,xDim,yDim,scratch[0],scratch[1],true,boundaryValue);
            }
        }
        SwapFields();
    }
    public void Advection2nd(double xVel, double yVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection2ndOrder(x,y, field, swapField, xDim, yDim, xVel, yVel, false, 0.0);
            }
        }
        SwapFields();
    }
    public void Advection2nd(double xVel, double yVel, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection2ndOrder(x, y, field, swapField, xDim, yDim, xVel, yVel, true, boundaryValue);
            }
        }
        SwapFields();
    }
    public void Advection2nd(double[] xVels,double[] yVels,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection2ndOrder(x,y, field, swapField,xDim,yDim,xVels[i],yVels[i],true,boundaryValue);
            }
        }
        SwapFields();
    }
    public void Advection2nd(double[] xVels,double[] yVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection2ndOrder(x,y, field, swapField,xDim,yDim,xVels[i],yVels[i],false,0.0);
            }
        }
        SwapFields();
    }
    public void Advection2ndPredCorr(double xVel,double yVel,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderPrediction(x, y, field, swapField, xDim, yDim, xVel, yVel, true, boundaryValue);
                //double[] intField;
                //intField=swapField;
            }
        }
        SwapFields();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderCorrection(x,y,field,swapField, xDim, yDim, xVel, yVel, true, boundaryValue);
            }
        }
        SwapFields();
    }
    public void Advection2ndPredCorr(double xVel,double yVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderPrediction(x, y, field, swapField, xDim, yDim, xVel, yVel, false, 0.0);
                //double[] intField;
                //intField=swapField;
            }
        }
        SwapFields();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderCorrection(x,y,field,swapField, xDim, yDim, xVel, yVel, false, 0.0);
            }
        }
        SwapFields();
    }
    public void Advection2ndPredCorr(double[] xVels,double[] yVels,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderPrediction(x, y, field, swapField, xDim, yDim, xVels[i], yVels[i], true,boundaryValue);
                //double[] intField;
                //intField=swapField;
            }
        }
        SwapFields();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderCorrection(x,y,field,swapField, xDim, yDim, xVels[i], yVels[i], true,boundaryValue);
            }
        }
        SwapFields();
    }
    public void Advection2ndPredCorr(double[] xVels,double[] yVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderPrediction(x, y, field, swapField, xDim, yDim, xVels[i], yVels[i], false, 0.0);
                //double[] intField;
                //intField=swapField;
            }
        }
        SwapFields();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderCorrection(x,y,field,swapField, xDim, yDim, xVels[i], yVels[i], false, 0.0);
            }
        }
        SwapFields();
    }
    public void Advection3rd(double xVel, double yVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection3rdOrder(x,y,field, swapField, xDim, yDim, xVel, yVel, false, 0.0);
            }
        }
        SwapFields();
    }
    public void Advection3rd(double xVel, double yVel, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection3rdOrder(x, y, field, swapField, xDim, yDim, xVel, yVel, true, boundaryValue);
            }
        }
        SwapFields();
    }
    public void Advection3rd(double[] xVels,double[] yVels,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection3rdOrder(x,y,field, swapField,xDim,yDim,xVels[i],yVels[i],true,boundaryValue);
            }
        }
        SwapFields();
    }
    public void Advection3rd(double[] xVels,double[] yVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection3rdOrder(x,y,field, swapField,xDim,yDim,xVels[i],yVels[i],false,0.0);
            }
        }
        SwapFields();
    }
    public void Advection2ndLW(double xVel, double yVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection2ndOrderLW(x,y, field, swapField, xDim, yDim, xVel, yVel, false, 0.0);
            }
        }
        SwapFields();
    }
    public void Advection2ndLW(double xVel, double yVel, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection2ndOrderLW(x, y, field, swapField, xDim, yDim, xVel, yVel, true, boundaryValue);
            }
        }
        SwapFields();
    }
    public void Advection2ndLW(double[] xVels,double[] yVels,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection2ndOrderLW(x,y, field, swapField,xDim,yDim,xVels[i],yVels[i],true,boundaryValue);
            }
        }
        SwapFields();
    }
    public void Advection2ndLW(double[] xVels,double[] yVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection2ndOrderLW(x,y, field, swapField,xDim,yDim,xVels[i],yVels[i],false,0.0);
            }
        }
        SwapFields();
    }
    /**
     * Runs diffusion on the current field, putting the result into the prev field, then swaps current and prev
     * @param nonDimDiffCoef rate of diffusion
     */
    public void Diffusion(double nonDimDiffCoef){
        if(nonDimDiffCoef>0.25){
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: "+nonDimDiffCoef);
        }
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2(x,y, field, swapField, xDim, yDim, nonDimDiffCoef, false, 0.0, wrapX, wrapY);
            }
        }
        SwapFields();
    }
    public void Diffusion(double nonDimDiffCoef, boolean wrapX, boolean wrapY){
        if(nonDimDiffCoef>0.25){
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: "+nonDimDiffCoef);
        }
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2(x, y, field, swapField, xDim, yDim, nonDimDiffCoef, false, 0.0, wrapX, wrapY);
            }
        }
        SwapFields();
    }
    /**
     * Runs diffusion on the current field, putting the result into the prev field, then swaps current and prev
     * @param nonDimDiffCoef rate of diffusion
     * @param boundaryValue value that diffuses in from the boundary
     */
    public void Diffusion(double nonDimDiffCoef, double boundaryValue){
        if(nonDimDiffCoef>0.25){
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: "+nonDimDiffCoef);
        }
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2(x,y, field, swapField, xDim, yDim, nonDimDiffCoef, true, boundaryValue, wrapX, wrapY);
            }
        }
        SwapFields();
    }
    public void Diffusion(double nonDimDiffCoef, double boundaryValue, boolean wrapX, boolean wrapY){
        if(nonDimDiffCoef>0.25){
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: "+nonDimDiffCoef);
        }
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
        Diffusion2(x,y, field, swapField,xDim,yDim,nonDimDiffCoef,true,boundaryValue,wrapX,wrapY);
            }
        }
        SwapFields();
    }


    public void Diffusion(double[] nonDimDiffCoefs, boolean wrapX, boolean wrapY){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2inhomogeneous(x,y, field, swapField, nonDimDiffCoefs, xDim, yDim, false, 0.0, wrapX, wrapY);
            }
        }
        SwapFields();
    }
    public void Diffusion(double[] nonDimDiffCoefs){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2inhomogeneous(x,y, field, swapField, nonDimDiffCoefs, xDim, yDim, false, 0.0, wrapX, wrapY);
            }
        }
        SwapFields();
    }
    public void Diffusion(double[] nonDimDiffCoefs,double boundaryValue, boolean wrapX, boolean wrapY){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2inhomogeneous(x,y, field, swapField, nonDimDiffCoefs, xDim, yDim, true, boundaryValue, wrapX, wrapY);
            }
        }
        SwapFields();
    }
    public void Diffusion(double[] nonDimDiffCoefs,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2inhomogeneous(x,y, field, swapField, nonDimDiffCoefs, xDim, yDim, true, boundaryValue, wrapX, wrapY);
            }
        }
        SwapFields();
    }

    /**
     * Runs diffusion on the current field, putting the result into the prev field, then swaps current and prev, and increments the tick
     * @param nonDimDiffCoef rate of diffusion
     * @param boundaryCond whether a boundary condition value will diffuse in from the field boundaries
     * @param boundaryValue only applies when boundaryCond is true, the boundary condition value
     * @param wrapX whether to wrap the field over the left and right boundaries
     */
  //  public void DiffSwapInc(double nonDimDiffCoef,boolean boundaryCond,double boundaryValue,boolean wrapX,boolean wrapY){
  //      //NOTE: EXPLICIT DIFFUSION WILL ONLY BE STABLE IF nonDimDiffCoef <= 1/4
  //      Util.Diffusion2(field, swapField,xDim,yDim,nonDimDiffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
  //      SwapFields();
  //      IncTick();
  //  }
  //  public void DiffSwapInc(double nonDimDiffCoef,boolean boundaryCond,double boundaryValue){
  //      //NOTE: EXPLICIT DIFFUSION WILL ONLY BE STABLE IF nonDimDiffCoef <= 1/4
  //      Util.Diffusion2(field, swapField,xDim,yDim,nonDimDiffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
  //      SwapFields();
  //      IncTick();
  //  }
  //  public void DiffSwapInc1(double nonDimDiffCoef,boolean boundaryCond,double boundaryValue,boolean wrapX,boolean wrapY){
  //      //NOTE: EXPLICIT DIFFUSION WILL ONLY BE STABLE IF nonDimDiffCoef <= 1/4
  //      Util.Diffusion(field, swapField,xDim,yDim,nonDimDiffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
  //      SwapFields();
  //      IncTick();
  //  }
  //  public void DiffSwapInc1(double nonDimDiffCoef,boolean boundaryCond,double boundaryValue){
  //      //NOTE: EXPLICIT DIFFUSION WILL ONLY BE STABLE IF nonDimDiffCoef <= 1/4
  //      Util.Diffusion(field, swapField,xDim,yDim,nonDimDiffCoef,boundaryCond,boundaryValue,wrapX,wrapY);
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


    public void SetOuterLayerSwap(double val){
        for (int x = 0; x < xDim; x++) {
            SetSwap(x,0,val);
            SetSwap(x,yDim-1,val);
        }
        for (int y = 1; y < yDim; y++) {
            SetSwap(0,y,val);
            SetSwap(xDim-1,y,val);
        }
    }

    public void MultiThread(int nThreads, GridDiff2MultiThreadFunction UpdateFun){
        Util.MultiThread(nThreads,nThreads,(iThread)->{
            int iStart=iThread/nThreads*length;
            int iEnd=(iThread+1)/nThreads*length;
            for (int i = iStart; i < iEnd; i++) {
                UpdateFun.GridDiff2MulitThreadFunction(ItoX(i),ItoY(i),i);
            }
        });
        SwapFields();
    }
}
