package Framework.GridsAndAgents;
import Framework.Interfaces.Coords2DDouble;
import Framework.Interfaces.Coords2DSetArray;
import Framework.Interfaces.GridDiff2MultiThreadFunction;
import Framework.Tools.PDEequations;
import Framework.Util;

import java.io.Serializable;
import java.util.Arrays;

import static Framework.Tools.PDEequations.*;


/**
 * PDEGrid2D class facilitates 2D diffusion with two arrays of doubles called fields
 * the intended usage is that during a diffusion tick, the current values will be read, and the prev values will be written to
 * after updates, Update is called to set the prev field as the current field.
 */
public class PDEGrid2D extends GridBase2D implements Serializable{
    double[] nextField;
    double[] field;
    //double[] intermediateScratch;
    double[] scratch;
    double[] maxDifscratch;
    boolean adiOrder=true;
    boolean adiX=true;
    public PDEGrid2D(int xDim, int yDim){
        super(xDim,yDim,false,false);
        field=new double[this.xDim*this.yDim];
        nextField =new double[this.xDim * this.yDim];
    }
    public PDEGrid2D(int xDim, int yDim, boolean wrapX, boolean wrapY){
        super(xDim,yDim,wrapX,wrapY);
        field=new double[this.xDim*this.yDim];
        nextField =new double[this.xDim * this.yDim];
    }
    void EnsureScratch(){
        if(scratch==null){
            scratch=new double[Math.max(xDim,yDim)*2+4];
        }
    }



    public void DiffusionADIupdate(double diffCoef){
        EnsureScratch();
        SwapFields();
        DiffusionADI2(true, field, nextField,scratch,xDim,yDim,diffCoef/2,false,0);
        SwapFields();
        DiffusionADI2(false, field, nextField,scratch,xDim,yDim,diffCoef/2,false,0);
        adiOrder=!adiOrder;
        Update();
    }
    public void DiffusionADIChangeOrderUpdate(double diffCoef){
        EnsureScratch();
        SwapFields();
        DiffusionADI2(adiX^adiOrder, field, nextField,scratch,xDim,yDim,diffCoef/2,false,0);
        adiX=!adiX;
        SwapFields();
        DiffusionADI2(adiX^adiOrder, field, nextField,scratch,xDim,yDim,diffCoef/2,false,0);
        adiX=!adiX;
        Update();
        adiOrder=!adiOrder;
    }
    public void DiffusionADIupdate(double diffCoef, double boundaryValue){
        EnsureScratch();
        SwapFields();
        DiffusionADI2(adiX^adiOrder, field, nextField,scratch,xDim,yDim,diffCoef/2,true,boundaryValue);
        adiX=!adiX;
        SwapFields();
        DiffusionADI2(adiX^adiOrder, field, nextField,scratch,xDim,yDim,diffCoef/2,true,boundaryValue);
        adiX=!adiX;
        //SwapFields();
        Update();
        adiOrder=!adiOrder;
    }
    public void DiffusionADIChangeOrderUpdate(double diffCoef, double boundaryValue){
        EnsureScratch();
        SwapFields();
        DiffusionADI2(adiX^adiOrder, field, nextField,scratch,xDim,yDim,diffCoef/2,true,boundaryValue);
        adiX=!adiX;
        SwapFields();
        DiffusionADI2(adiX^adiOrder, field, nextField,scratch,xDim,yDim,diffCoef/2,true,boundaryValue);
        adiX=!adiX;
        //SwapFields();
        Update();
        adiOrder=!adiOrder;
    }
    public void DiffusionADIHalfUpdate(double diffCoef){
        EnsureScratch();
        SwapFields();
        DiffusionADI2(adiX^adiOrder, field, nextField,scratch,xDim,yDim,diffCoef/2,false,0);
        adiX=!adiX;
        Update();
        if(!adiX) {
            adiOrder = !adiOrder;
        }
    }
    public void DiffusionADIHalfXupdate(double diffCoef, boolean boundaryCond, double boundaryValue){
        EnsureScratch();
        SwapFields();
        DiffusionADI2(true, field, nextField,scratch,xDim,yDim,diffCoef/2,boundaryCond,boundaryValue);
        Update();
    }
    public void DiffusionADIHalfYupdate(double diffCoef, boolean boundaryCond, double boundaryValue){
        EnsureScratch();
        SwapFields();
        DiffusionADI2(true, field, nextField,scratch,xDim,yDim,diffCoef/2,boundaryCond,boundaryValue);
        Update();
    }
    public void DiffusionADIHalfUpdate(double diffCoef, double boundaryValue){
        EnsureScratch();
        SwapFields();
        DiffusionADI2(adiX^adiOrder, field, nextField,scratch,xDim,yDim,diffCoef/2,true,boundaryValue);
        adiX=!adiX;
        Update();
        if(!adiX) {
            adiOrder = !adiOrder;
        }
    }

    /**
     * gets the prev field value at the specified coordinates
     */
    public double Get(int x, int y){ return field[x*yDim+y]; }

    /**
     * gets the prev field value at the specified index
     */
    public double Get(int i){return field[i];}

    /**
     * sets the prev field value at the specified coordinates
     */
    public void Set(int x, int y, double val){ nextField[x*yDim+y]=val; }

    /**
     * sets the prev field value at the specified index
     */
    public void Set(int i, double val){
        nextField[i]=val;}

    /**
     * sets the prev field value at the specified coordinates
     */
    public void Add(int x, int y, double val){ nextField[x*yDim+y]+=val; }

    /**
     * adds to the prev field value at the specified index
     */
    public void Add(int i, double val){
        nextField[i]+=val;}

    public void Mul(int i, double val){
        nextField[i]+=field[i]*(val-1);}
    public void Mul(int x,int y, double val){
        nextField[x*yDim+y]+=field[x*yDim+y]*(val-1);}
    /**
     * copies the current field into the prev field
     */

    public double GetMax(){
        double max=Double.MIN_VALUE;
        for (int i = 0; i < length; i++) {
            max=Math.max(Get(i),max);
        }
        return max;
    }
    public double GetMin(){
        double min=Double.MAX_VALUE;
        for (int i = 0; i < length; i++) {
            min=Math.min(Get(i),min);
        }
        return min;
    }


    /**
     * Swaps the prev and current field
     */
    public void Update(){
        System.arraycopy(nextField,0,field,0,length);
        IncTick();
    }
    void SwapFields(){
        double[]temp=field;
        field=nextField;
        nextField=temp;
    }

    /**
     * Swaps the prev and current field, and increments the tick
     */
//    public void SwapInc(){
//        Update();
//        IncTick();
//    }

    //assumed wrap around if non-periodic
    public void Advection(double xVel, double yVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
            Advection1stOrder(x,y, field, nextField, xDim, yDim, xVel, yVel, false, 0.0);
            }
        }
    }
    public void Advection(double xVel, double yVel, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection1stOrder(x, y, field, nextField, xDim, yDim, xVel, yVel, true, boundaryValue);
            }
        }
    }
    public void Advection(double[] xVels,double[] yVels,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection1stOrder(x,y, field, nextField,xDim,yDim,xVels[i],yVels[i],true,boundaryValue);
            }
        }
    }
    public void Advection(double[] xVels,double[] yVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection1stOrder(x,y, field, nextField,xDim,yDim,xVels[i],yVels[i],false,0.0);
            }
        }
    }
    public void Advection(Coords2DSetArray CoordsToVels,double[]scratch){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                CoordsToVels.SetArray(x,y,scratch);
                Advection1stOrder(x,y, field, nextField,xDim,yDim,scratch[0],scratch[1],false,0.0);
            }
        }
    }
    public void Advection(Coords2DSetArray CoordsToVels,double boundaryValue,double[]scratch){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                CoordsToVels.SetArray(x,y,scratch);
                Advection1stOrder(x,y, field, nextField,xDim,yDim,scratch[0],scratch[1],true,boundaryValue);
            }
        }
    }
    public void Advection2nd(double xVel, double yVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection2ndOrder(x,y, field, nextField, xDim, yDim, xVel, yVel, false, 0.0);
            }
        }
    }
    public void Advection2nd(double xVel, double yVel, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection2ndOrder(x, y, field, nextField, xDim, yDim, xVel, yVel, true, boundaryValue);
            }
        }
    }
    public void Advection2nd(double[] xVels,double[] yVels,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection2ndOrder(x,y, field, nextField,xDim,yDim,xVels[i],yVels[i],true,boundaryValue);
            }
        }
    }
    public void Advection2nd(double[] xVels,double[] yVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection2ndOrder(x,y, field, nextField,xDim,yDim,xVels[i],yVels[i],false,0.0);
            }
        }
    }
    public void Advection2ndPredCorrOverwrite(double xVel, double yVel, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderPrediction(x, y, field, nextField, xDim, yDim, xVel, yVel, true, boundaryValue);
                //double[] intField;
                //intField=nextField;
            }
        }
        Update();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderCorrection(x,y,field, nextField, xDim, yDim, xVel, yVel, true, boundaryValue);
            }
        }
        Update();
    }
    public void Advection2ndPredCorrOverwrite(double xVel, double yVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderPrediction(x, y, field, nextField, xDim, yDim, xVel, yVel, false, 0.0);
                //double[] intField;
                //intField=nextField;
            }
        }
        Update();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderCorrection(x,y,field, nextField, xDim, yDim, xVel, yVel, false, 0.0);
            }
        }
        Update();
    }
    public void Advection2ndPredCorrOverwrite(double[] xVels, double[] yVels, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderPrediction(x, y, field, nextField, xDim, yDim, xVels[i], yVels[i], true,boundaryValue);
                //double[] intField;
                //intField=nextField;
            }
        }
        Update();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderCorrection(x,y,field, nextField, xDim, yDim, xVels[i], yVels[i], true,boundaryValue);
            }
        }
        Update();
    }
    public void Advection2ndPredCorrOverwrite(double[] xVels, double[] yVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderPrediction(x, y, field, nextField, xDim, yDim, xVels[i], yVels[i], false, 0.0);
                //double[] intField;
                //intField=nextField;
            }
        }
        Update();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = I(x, y);
                Advection2ndOrderCorrection(x,y,field, nextField, xDim, yDim, xVels[i], yVels[i], false, 0.0);
            }
        }
        Update();
    }
    public void Advection3rd(double xVel, double yVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection3rdOrder(x,y,field, nextField, xDim, yDim, xVel, yVel, false, 0.0);
            }
        }
    }
    public void Advection3rd(double xVel, double yVel, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection3rdOrder(x, y, field, nextField, xDim, yDim, xVel, yVel, true, boundaryValue);
            }
        }
    }
    public void Advection3rd(double[] xVels,double[] yVels,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection3rdOrder(x,y,field, nextField,xDim,yDim,xVels[i],yVels[i],true,boundaryValue);
            }
        }
    }
    public void Advection3rd(double[] xVels,double[] yVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection3rdOrder(x,y,field, nextField,xDim,yDim,xVels[i],yVels[i],false,0.0);
            }
        }
    }
    public void Advection2ndLW(double xVel, double yVel){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection2ndOrderLW(x,y, field, nextField, xDim, yDim, xVel, yVel, false, 0.0);
            }
        }
    }
    public void Advection2ndLW(double xVel, double yVel, double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Advection2ndOrderLW(x, y, field, nextField, xDim, yDim, xVel, yVel, true, boundaryValue);
            }
        }
    }
    public void Advection2ndLW(double[] xVels,double[] yVels,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection2ndOrderLW(x,y, field, nextField,xDim,yDim,xVels[i],yVels[i],true,boundaryValue);
            }
        }
    }
    public void Advection2ndLW(double[] xVels,double[] yVels){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i= I(x,y);
                Advection2ndOrderLW(x,y, field, nextField,xDim,yDim,xVels[i],yVels[i],false,0.0);
            }
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
            for (int y = 0; y < yDim; y++) {
                Diffusion2(x,y, field, nextField, xDim, yDim, diffCoef, false, 0.0, wrapX, wrapY);
            }
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
            for (int y = 0; y < yDim; y++) {
                Diffusion2(x,y, field, nextField, xDim, yDim, diffCoef, true, boundaryValue, wrapX, wrapY);
            }
        }
    }


    public void Diffusion(double[] diffCoefs){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2inhomogeneous(x,y, field, nextField, diffCoefs, xDim, yDim, false, 0.0, wrapX, wrapY);
            }
        }
    }
    public void Diffusion(double diffCoef,Coords2DDouble BoundaryGen){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2Boundary(x,y, field, nextField, xDim, yDim, diffCoef,BoundaryGen, wrapX, wrapY);
            }
        }
    }
    public void Diffusion(double[] diffCoefs,double boundaryValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Diffusion2inhomogeneous(x,y, field, nextField, diffCoefs, xDim, yDim, true, boundaryValue, wrapX, wrapY);
            }
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
    public double MaxDifScaled(double denomOffset){
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

    public double MaxDifInternal(){
        if(maxDifscratch==null){
            maxDifscratch=new double[length];
        }
        double ret= MaxDifOther(maxDifscratch);
        System.arraycopy(field,0,maxDifscratch,0,length);
        return ret;
    }
    public double MaxDifOtherScaled(double[]compareTo, double denomOffset){
        double maxDif=0;
        for(int i = 0; i< field.length; i++){
            maxDif=Math.max(maxDif,Math.abs(field[i]- compareTo[i])/ (compareTo[i]+denomOffset));
        }
        return maxDif;
    }


    public double MaxDifferenceScaled(double denomOffset){
        if(maxDifscratch==null){
            maxDifscratch=new double[length];
        }
        double ret= MaxDifOtherScaled(maxDifscratch,denomOffset);
        System.arraycopy(field,0,maxDifscratch,0,length);
        return ret;
    }


    public void SetOuterLayerSwap(double val){
        for (int x = 0; x < xDim; x++) {
            Set(x,0,val);
            Set(x,yDim-1,val);
        }
        for (int y = 1; y < yDim; y++) {
            Set(0,y,val);
            Set(xDim-1,y,val);
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
    }

    public double GradientX(int x,int y){
        double left= PDEequations.DisplacedX2D(x-1,y,field,xDim,yDim,x,false,0,wrapX);
        double right=PDEequations.DisplacedX2D(x+1,y,field,xDim,yDim,x,false,0,wrapX);
        return right-left;
    }

    public double GradientY(int x,int y){
        double down=PDEequations.DisplacedY2D(x,y-1,field,xDim,yDim,y,false,0,wrapY);
        double up=PDEequations.DisplacedY2D(x,y+1,field,xDim,yDim,y,false,0,wrapY);
        return up-down;
    }
    public double GradientX(int x,int y,boolean wrapX){
        double left=PDEequations.DisplacedX2D(x-1,y,field,xDim,yDim,x,false,0,wrapX);
        double right=PDEequations.DisplacedX2D(x+1,y,field,xDim,yDim,x,false,0,wrapX);
        return right-left;
    }

    public double GradientY(int x,int y,boolean wrapY){
        double down=PDEequations.DisplacedY2D(x,y-1,field,xDim,yDim,y,false,0,wrapY);
        double up=PDEequations.DisplacedY2D(x,y+1,field,xDim,yDim,y,false,0,wrapY);
        return up-down;
    }
    public double GradientX(int x,int y,double boundaryCond){
        double left=PDEequations.DisplacedX2D(x-1,y,field,xDim,yDim,x,true,boundaryCond,wrapX);
        double right=PDEequations.DisplacedX2D(x+1,y,field,xDim,yDim,x,true,boundaryCond,wrapX);
        return right-left;
    }

    public double GradientY(int x,int y,double boundaryCond){
        double down=PDEequations.DisplacedY2D(x,y-1,field,xDim,yDim,y,true,boundaryCond,wrapY);
        double up=PDEequations.DisplacedY2D(x,y+1,field,xDim,yDim,y,true,boundaryCond,wrapY);
        return up-down;
    }

}
