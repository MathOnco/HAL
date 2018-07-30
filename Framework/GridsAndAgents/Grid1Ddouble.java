package Framework.GridsAndAgents;

import Framework.Interfaces.DoubleToDouble;
import Framework.Tools.PDEequations;
import Framework.Util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by Rafael on 10/24/2017.
 */
public class Grid1Ddouble extends GridBase1D implements Serializable{
    double[] field;
    public Grid1Ddouble(int xDim){
        super(xDim,false);
        field =new double[this.xDim];
    }
    public Grid1Ddouble(int xDim, boolean wrapX){
        super(xDim,wrapX);
        field =new double[this.xDim];
    }
    /**
     * gets the current field value at the specified index
     */
    public double Get(int x){return field[x];}
    public double[] GetField(){
        return this.field;
    }
    /**
     * sets the current field value at the specified index
     */
    public void Set(int x, double val){
        field[x]=val;}

    public void Mul(int x, double val){ field[x]*=val; }
    /**
     * adds to the current field value at the specified index
     */
    public void Add(int x, double val){
        field[x]+=val;}
    /**
     * Bounds all values in the current field between min and max
     */
    public void BoundAll(double min, double max){
        for(int i=0;i<length;i++){
            field[i]= Util.Bound(field[i],min,max);
        }
    }

    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAll(double val){
        Arrays.fill(field,val);
    }
    /**
     * adds specified value to all entries of the curr field
     */
    public void AddAll(double val){
        for (int i = 0; i < length; i++) {
            field[i]+=val;
        }
    }
    /**
     * adds specified value to all entries of the curr field
     */
    public void MulAll(double val){
        for (int i = 0; i < length; i++) {
            field[i]*=val;
        }
    }

    public void SetAll(double[]vals){
        System.arraycopy(vals,0, field,0,length);
    }
    /**
     * gets the average value of all squares in the current field
     */
    public double GetAvg(){
        double tot=0;
        for(int i=0;i<length;i++){
            tot+= field[i];
        }
        return tot/length;
    }
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


    public double GradientX(int x){
        double left=PDEequations.Displaced1D(x-1,field,xDim,x,false,0,wrapX);
        double right=PDEequations.Displaced1D(x+1,field,xDim,x,false,0,wrapX);
        return right-left;
    }

    public double GradientX(int x,boolean wrapX){
        double left=PDEequations.Displaced1D(x-1,field,xDim,x,false,0,wrapX);
        double right=PDEequations.Displaced1D(x+1,field,xDim,x,false,0,wrapX);
        return right-left;
    }

    public double GradientX(int x,double boundaryCond){
        double left=PDEequations.Displaced1D(x-1,field,xDim,x,true,boundaryCond,wrapX);
        double right=PDEequations.Displaced1D(x+1,field,xDim,x,true,boundaryCond,wrapX);
        return right-left;
    }
}
