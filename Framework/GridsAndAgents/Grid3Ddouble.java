package Framework.GridsAndAgents;

import Framework.Tools.PDEequations;
import Framework.Util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Rafael on 10/24/2017.
 */
public class Grid3Ddouble extends GridBase3D implements Serializable{
    double[] field;

    public Grid3Ddouble(int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ){
        super(xDim,yDim,zDim,wrapX,wrapY,wrapZ);
        field = new double[length];
        //middleField = new double[numElements];
    }
    public Grid3Ddouble(int xDim, int yDim, int zDim){
        super(xDim,yDim,zDim,false,false,false);

        int numElements = this.xDim * this.yDim * this.zDim;
        field = new double[numElements];
        //middleField = new double[numElements];
    }
    public double Get(int i){return field[i];}

    /**
     * gets the current field value at the specified coordinates
     */
    public double Get(int x, int y, int z) {
        return field[x*yDim*zDim+y*zDim+z];
    }

    /**
     * sets the current field value at the specified index
     */
    public void Set(int i, double val){
        field[i]=val;}
    /**
     * sets the current field value at the specified coordinates
     */
    public void Set(int x, int y, int z, double val){
        field[x*yDim*zDim+y*zDim+z]=val;
    }

    public double[] GetField(){ return this.field; }
    public void Add(int x, int y, int z, double val){
        field[x*yDim*zDim+y*zDim+z]+=val;
    }
    public void Mul(int x, int y, int z, double val){
        field[x*yDim*zDim+y*zDim+z]*=val;
    }
    public void Mul(int i, double val){
        field[i]*=val;
    }

    /**
     * adds to the current field value at the specified index
     */
    public void Add(int i, double val){
        field[i]+=val;
    }
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
    public void AddAll(double val){
        for (int i = 0; i < length; i++) {
            field[i]+=val;
        }
    }
    public void MulAll(double val){
        for (int i = 0; i < length; i++) {
            field[i]*=val;
        }
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
    public double GradientX(int x,int y,int z){
        double left= PDEequations.DisplacedX3D(x-1,y,z,field,xDim,yDim,zDim,x,false,0,wrapX);
        double right=PDEequations.DisplacedX3D(x+1,y,z,field,xDim,yDim,zDim,x,false,0,wrapX);
        return right-left;
    }

    public double GradientY(int x,int y,int z){
        double down=PDEequations.DisplacedY3D(x,y-1,z,field,xDim,yDim,zDim,y,false,0,wrapY);
        double up=PDEequations.DisplacedY3D(x,y+1,z,field,xDim,yDim,zDim,y,false,0,wrapY);
        return up-down;
    }
    public double GradientZ(int x,int y,int z){
        double in=PDEequations.DisplacedZ3D(x,y,z-1,field,xDim,yDim,zDim,z,false,0,wrapY);
        double out=PDEequations.DisplacedZ3D(x,y,z+1,field,xDim,yDim,zDim,z,false,0,wrapY);
        return out-in;
    }
    public double GradientX(int x,int y,int z,boolean wrapX){
        double left=PDEequations.DisplacedX3D(x-1,y,z,field,xDim,yDim,zDim,x,false,0,wrapX);
        double right=PDEequations.DisplacedX3D(x+1,y,z,field,xDim,yDim,zDim,x,false,0,wrapX);
        return right-left;
    }

    public double GradientY(int x,int y,int z,boolean wrapY){
        double down=PDEequations.DisplacedY3D(x,y-1,z,field,xDim,yDim,zDim,y,false,0,wrapY);
        double up=PDEequations.DisplacedY3D(x,y+1,z,field,xDim,yDim,zDim,y,false,0,wrapY);
        return up-down;
    }
    public double GradientZ(int x,int y,int z,boolean wrapZ){
        double down=PDEequations.DisplacedZ3D(x,y,z-1,field,xDim,yDim,zDim,z,false,0,wrapZ);
        double up=PDEequations.DisplacedZ3D(x,y,z+1,field,xDim,yDim,zDim,z,false,0,wrapZ);
        return up-down;
    }
    public double GradientX(int x,int y,int z,double boundaryCond){
        double left=PDEequations.DisplacedX3D(x-1,y,z,field,xDim,yDim,zDim,x,true,boundaryCond,wrapX);
        double right=PDEequations.DisplacedX3D(x+1,y,z,field,xDim,yDim,zDim,x,true,boundaryCond,wrapX);
        return right-left;
    }

    public double GradientY(int x,int y,int z,double boundaryCond){
        double down=PDEequations.DisplacedY3D(x,y-1,z,field,xDim,yDim,zDim,y,true,boundaryCond,wrapY);
        double up=PDEequations.DisplacedY3D(x,y+1,z,field,xDim,yDim,zDim,y,true,boundaryCond,wrapY);
        return up-down;
    }
    public double GradientZ(int x,int y,int z,double boundaryCond){
        double down=PDEequations.DisplacedZ3D(x,y,z-1,field,xDim,yDim,zDim,z,true,boundaryCond,wrapZ);
        double up=PDEequations.DisplacedZ3D(x,y,z+1,field,xDim,yDim,zDim,z,true,boundaryCond,wrapZ);
        return up-down;
    }

}
