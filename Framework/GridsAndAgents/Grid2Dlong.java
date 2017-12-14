package Framework.GridsAndAgents;

import Framework.Util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by Rafael on 10/24/2017.
 */
public class Grid2Dlong extends GridBase2D implements Serializable{
    long[] field;
    public Grid2Dlong(int xDim, int yDim){
        super(xDim,yDim,false,false);
        field =new long[this.xDim * this.yDim];
    }
    public Grid2Dlong(int xDim, int yDim, boolean wrapX, boolean wrapY){
        super(xDim,yDim,wrapX,wrapY);
        field =new long[this.xDim * this.yDim];
    }
    /**
     * gets the current field value at the specified index
     */
    public long Get(int i){return field[i];}
    public long[] GetField(){
        return this.field;
    }
    /**
     * gets the current field value at the specified coordinates
     */
    public long Get(int x, int y) { return field[x*yDim+y]; }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, long val){
        field[i]=val;}

    /**
     * sets the current field value at the specified coordinates
     */
    public void Set(int x, int y, long val){ field[x*yDim+y]=val; }

    /**
     * adds to the current field value at the specified coordinates
     */
    public void Add(int x, int y, long val){ field[x*yDim+y]+=val; }

    public void Mul(int x, int y, long val){ field[x*yDim+y]*=val; }
    public void Mul(int i, long val){ field[i]*=val; }
    /**
     * adds to the current field value at the specified index
     */
    public void Add(int i, long val){
        field[i]+=val;}
    /**
     * Bounds all values in the current field between min and max
     */
    public void BoundAll(long min, long max){
        for(int i=0;i<length;i++){
            field[i]= Util.Bound(field[i],min,max);
        }
    }

    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAll(long val){
        Arrays.fill(field,val);
    }
    /**
     * adds specified value to all entries of the curr field
     */
    public void AddAll(long val){
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

    public void SetAll(long[]vals){
        System.arraycopy(vals,0, field,0,length);
    }
    /**
     * gets the average value of all squares in the current field
     */
    public long GetAvg(){
        long tot=0;
        for(int i=0;i<length;i++){
            tot+= field[i];
        }
        return tot/length;
    }
    public long GetMax(){
        long max=Long.MIN_VALUE;
        for (int i = 0; i < length; i++) {
            max=Math.max(Get(i),max);
        }
        return max;
    }
    public long GetMin(){
        long min=Long.MAX_VALUE;
        for (int i = 0; i < length; i++) {
            min=Math.min(Get(i),min);
        }
        return min;
    }
    public long MaxDifScaled(long[]compareTo, long denomOffset){
        long maxDif=0;
        for(int i = 0; i< field.length; i++){
            maxDif=Math.max(maxDif,Math.abs(field[i]- compareTo[i])/ (compareTo[i]+denomOffset));
        }
        return maxDif;
    }
    public void SetOuterLayer(long val){
        for (int x = 0; x < xDim; x++) {
            Set(x,0,val);
            Set(x,yDim-1,val);
        }
        for (int y = 1; y < yDim; y++) {
            Set(0,y,val);
            Set(xDim-1,y,val);
        }
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

//    public Grid2Ddouble DotProduct(Grid2Ddouble rightMatrix){
//        if(yDim!=rightMatrix.xDim){
//            throw new IllegalArgumentException("xDim of this and yDim of other must match! this.xDim: "+xDim+" other.yDim: "+yDim);
//        }
//        Grid2Ddouble out=new Grid2Ddouble(xDim,rightMatrix.yDim);
//        for (int x = 0; x < xDim; x++) {
//            for (int y = 0; y < rightMatrix.yDim; y++) {
//                //fill in values by dot product
//                for (int i = 0; i < yDim; i++) {
//                    out.Add(x,y,Get(i,y)*rightMatrix.Get(x,i));
//                }
//            }
//        }
//        return out;
//    }
//    public Grid2Ddouble DotProductT(Grid2Ddouble rightMatrixToTranspose){
//        if(yDim!=rightMatrixToTranspose.yDim){
//            throw new IllegalArgumentException("xDim of this and xDim of other must match! this.xDim: "+xDim+" other.xDim: "+yDim);
//        }
//        Grid2Ddouble out=new Grid2Ddouble(xDim,rightMatrixToTranspose.yDim);
//        for (int x = 0; x < xDim; x++) {
//            for (int y = 0; y < rightMatrixToTranspose.xDim; y++) {
//                //fill in values by dot product
//                for (int i = 0; i < yDim; i++) {
//                    out.Add(x,y,Get(i,y)*rightMatrixToTranspose.Get(i,x));
//                }
//            }
//        }
//        return out;
//    }
}
