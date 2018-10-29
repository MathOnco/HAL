package Framework.GridsAndAgents;

import Framework.Tools.Internal.PDEequations;
import Framework.Util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a 3D Grid of doubles
 */
public class Grid3Ddouble extends GridBase3D implements Serializable{
    double[] field;

    /**
     * creates a new Grid3Ddouble of dimensions xDim by yDim by zDim without wraparound
     */
    public Grid3Ddouble(int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ){
        super(xDim,yDim,zDim,wrapX,wrapY,wrapZ);
        field = new double[length];
        //middleField = new double[numElements];
    }

    /**
     * creates a new Grid3Ddouble of dimensions xDim by yDim by zDim with optional wraparound
     */
    public Grid3Ddouble(int xDim, int yDim, int zDim){
        super(xDim,yDim,zDim,false,false,false);

        int numElements = this.xDim * this.yDim * this.zDim;
        field = new double[numElements];
        //middleField = new double[numElements];
    }

    /**
     * gets the current field value at the specified index
     */
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

    /**
     * returns the complete field as an array
     */
    public double[] GetField(){ return this.field; }

    /**
     * adds to the current field value at the specified coordinates
     */
    public void Add(int x, int y, int z, double val){
        field[x*yDim*zDim+y*zDim+z]+=val;
    }

    /**
     * multiplies the current field value at the specified coordinates
     */
    public void Scale(int x, int y, int z, double val){
        field[x*yDim*zDim+y*zDim+z]*=val;
    }

    /**
     * multiplies the current field value at the specified index
     */
    public void Scale(int i, double val){
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

    /**
     * adds specified value to all entries of the curr field
     */
    public void AddAll(double val){
        for (int i = 0; i < length; i++) {
            field[i]+=val;
        }
    }

    /**
     * multiplies all entries in the field by the value
     */
    public void ScaleAll(double val){
        for (int i = 0; i < length; i++) {
            Scale(i,val);
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

    /**
     * returns the gradient of the field in the X direction at the coordinates specified
     */
    public double GradientX(int x,int y,int z){
        double left = PDEequations.DisplacedX3D(field,x-1,y,z, xDim, yDim,zDim, wrapX,(X,Y,Z)->Get(X+1,Y,Z));
        double right = PDEequations.DisplacedX3D(field,x+1,y,z, xDim, yDim,zDim, wrapX,(X,Y,Z)->Get(X-1,Y,Z));
        return right - left;
    }

    /**
     * returns the gradient of the field in the Y direction at the coordinates specified
     */
    public double GradientY(int x,int y,int z){
        double down = PDEequations.DisplacedX3D(field,x,y-1,z, xDim, yDim,zDim, wrapX,(X,Y,Z)->Get(X+1,Y,Z));
        double up = PDEequations.DisplacedX3D(field,x,y+1,z, xDim, yDim,zDim, wrapX,(X,Y,Z)->Get(X-1,Y,Z));
        return up-down;
    }
    /**
     * returns the gradient of the field in the Z direction at the coordinates specified
     */
    public double GradientZ(int x,int y,int z){
        double in = PDEequations.DisplacedX3D(field,x,y,z-1, xDim, yDim,zDim, wrapX,(X,Y,Z)->Get(X,Y,Z+1));
        double out = PDEequations.DisplacedX3D(field,x,y,z+1, xDim, yDim,zDim, wrapX,(X,Y,Z)->Get(X,Y,Z-1));
        return out-in;
    }
    /**
     * returns the gradient of the field in the X direction at the coordinates specified, will use the boundary value
     * provided for gradients that go over the boundary
     */
    public double GradientX(int x,int y,int z,double boundaryCond){
        double left = PDEequations.DisplacedX3D(field,x-1,y,z, xDim, yDim,zDim, wrapX,(X,Y,Z)->boundaryCond);
        double right = PDEequations.DisplacedX3D(field,x+1,y,z, xDim, yDim,zDim, wrapX,(X,Y,Z)->boundaryCond);
        return right - left;
    }

    /**
     * returns the gradient of the field in the Y direction at the coordinates specified, will use the boundary value
     * provided for gradients that go over the boundary
     */
    public double GradientY(int x,int y,int z,double boundaryCond){
        double down = PDEequations.DisplacedX3D(field,x,y-1,z, xDim, yDim,zDim, wrapX,(X,Y,Z)->boundaryCond);
        double up = PDEequations.DisplacedX3D(field,x,y+1,z, xDim, yDim,zDim, wrapX,(X,Y,Z)->boundaryCond);
        return up-down;
    }
    /**
     * returns the gradient of the field in the Z direction at the coordinates specified, will use the boundary value
     * provided for gradients that go over the boundary
     */
    public double GradientZ(int x,int y,int z,double boundaryCond){
        double in = PDEequations.DisplacedX3D(field,x,y,z-1, xDim, yDim,zDim, wrapX,(X,Y,Z)->boundaryCond);
        double out = PDEequations.DisplacedX3D(field,x,y,z+1, xDim, yDim,zDim, wrapX,(X,Y,Z)->boundaryCond);
        return out-in;
    }

}
