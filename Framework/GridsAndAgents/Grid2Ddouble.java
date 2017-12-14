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
public class Grid2Ddouble extends GridBase2D implements Serializable{
    double[] field;
    public Grid2Ddouble(int xDim, int yDim){
        super(xDim,yDim,false,false);
        field =new double[this.xDim * this.yDim];
    }
    public Grid2Ddouble(int xDim, int yDim, boolean wrapX, boolean wrapY){
        super(xDim,yDim,wrapX,wrapY);
        field =new double[this.xDim * this.yDim];
    }
    /**
     * gets the current field value at the specified index
     */
    public double Get(int i){return field[i];}
    public double[] GetField(){
        return this.field;
    }
    /**
     * gets the current field value at the specified coordinates
     */
    public double Get(int x, int y) { return field[x*yDim+y]; }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, double val){
        field[i]=val;}

    /**
     * sets the current field value at the specified coordinates
     */
    public void Set(int x, int y, double val){ field[x*yDim+y]=val; }

    /**
     * adds to the current field value at the specified coordinates
     */
    public void Add(int x, int y, double val){ field[x*yDim+y]+=val; }

    public void Mul(int x, int y, double val){ field[x*yDim+y]*=val; }
    public void Mul(int i, double val){ field[i]*=val; }
    /**
     * adds to the current field value at the specified index
     */
    public void Add(int i, double val){
        field[i]+=val;}
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
    public double MaxDifScaled(double[]compareTo, double denomOffset){
        double maxDif=0;
        for(int i = 0; i< field.length; i++){
            maxDif=Math.max(maxDif,Math.abs(field[i]- compareTo[i])/ (compareTo[i]+denomOffset));
        }
        return maxDif;
    }
    public void SetOuterLayer(double val){
        for (int x = 0; x < xDim; x++) {
            Set(x,0,val);
            Set(x,yDim-1,val);
        }
        for (int y = 1; y < yDim; y++) {
            Set(0,y,val);
            Set(xDim-1,y,val);
        }
    }
    public String ToMatrixString(String delim, DoubleToDouble ValueTransform, int decimalDigits){
        String dfStr="#.";
        for (int i = 0; i < decimalDigits; i++) {
            dfStr+="0";
        }
        DecimalFormat df=new DecimalFormat(dfStr);
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < yDim; y++) {
            for (int x = 0; x < xDim; x++) {
                if(x==xDim-1){
                    sb.append(df.format(ValueTransform.DoubleToDouble(Get(x,y))));
                }
                else{
                    sb.append(df.format(ValueTransform.DoubleToDouble(Get(x,y)))+delim);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    public String ToMatrixString(String delim,int decimalDigits){
        String dfStr="#.";
        for (int i = 0; i < decimalDigits; i++) {
            dfStr+="0";
        }
        DecimalFormat df=new DecimalFormat(dfStr);
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                if(y==yDim-1){
                    sb.append(df.format(Get(x,y)));
                }
                else{
                    sb.append(df.format(Get(x,y))+delim);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String ToMatrixString(String delim){
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                if(y==yDim-1){
                    sb.append(Get(x,y));
                }
                else{
                    sb.append(Get(x,y)+delim);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    public double GradientX(int x,int y){
        double left=PDEequations.DisplacedX2D(x-1,y,field,xDim,yDim,x,false,0,wrapX);
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
