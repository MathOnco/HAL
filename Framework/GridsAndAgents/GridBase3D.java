package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Util;

import java.util.ArrayList;

/**
 * Created by bravorr on 5/17/17.
 */
public abstract class GridBase3D{
    public final int xDim;
    public final int yDim;
    public final int zDim;
    public final int length;
    public boolean wrapX;
    public boolean wrapY;
    public boolean wrapZ;
    int tick;
    GridBase3D(int x,int y,int z,boolean wrapX,boolean wrapY,boolean wrapZ){
        xDim=x;
        yDim=y;
        zDim=z;
        length=x*y*z;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
        this.wrapZ=wrapZ;
    }
    public int WrapI(int x, int y, int z){
        //wraps Coords to proper index
        if(In(x,y,z)) { return I(x,y,z);}
        return I(Util.ModWrap(x,xDim), Util.ModWrap(y,yDim), Util.ModWrap(z,zDim));
    }

    /**
     * gets the index of the voxel at the specified coordinates
     */
    public int I(int x, int y, int z){
        //gets typeGrid index from location
        return x*yDim*zDim+y*zDim+z;
    }

    /**
     * gets the xDim component of the voxel at the specified index
     */
    public int ItoX(int i){
        return i/(yDim*zDim);
    }

    /**
     * gets the yDim component of the voxel at the specified index
     */
    public int ItoY(int i){return (i/zDim)%yDim;}

    /**
     * gets the z component of the voxel at the specified index
     */
    public int ItoZ(int i){
        return i%zDim;
    }

    /**
     * gets the index of the square that contains the specified coordinates
     */
    public int I(double x, double y, double z){
        //gets typeGrid index from location
        return (int)Math.floor(x)*yDim*zDim+(int)Math.floor(y)*zDim+(int)Math.floor(z);
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    public boolean In(int x, int y, int z){
        if(x>=0&&x<xDim&&y>=0&&y<yDim&&z>=0&&z<zDim){
            return true;
        }
        return false;
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    public boolean In(double x, double y, double z){
        int xInt=(int)Math.floor(x);
        int yInt=(int)Math.floor(y);
        int zInt=(int)Math.floor(z);
        return In(xInt,yInt,zInt);
    }
    public int MapHood(int[] hood, int centerX, int centerY, int centerZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart=hood.length/4;
        for (int i = iStart; i < hood.length; i+=3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(z, zDim)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            hood[ptCt]= I(x,y,z);
            ptCt++;
        }
        return ptCt;
    }
    public int MapHood(int[] hood, int centerX, int centerY, int centerZ,IndexCoords3DBool Eval) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart=hood.length/4;
        for (int i = iStart; i < hood.length; i+=3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(z, zDim)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            int j=I(x,y,z);
            if(Eval.Eval(j,x,y,z)) {
                hood[ptCt] = j;
                ptCt++;
            }
        }
        return ptCt;
    }
    public int MapHood(int[] hood, int centerI) {
        return MapHood(hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI));
    }
    public int MapHood(int[] hood, int centerI,IndexCoords3DBool Eval) {
        return MapHood(hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI),Eval);
    }

    public int HoodToIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart=hood.length/4;
        for (int i = iStart; i < hood.length; i+=3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(z, zDim)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            ret[ptCt]= I(x,y,z);
            ptCt++;
        }
        return ptCt;
    }

    public int[]BoundaryIs(){
        int[] ret=new int[(xDim*yDim+xDim*zDim+yDim*zDim)*2];
        int side1=xDim*yDim;
        int side2=xDim*zDim;
        int side3=yDim*zDim;
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                ret[x*yDim+y]= I(x,y,0);
                ret[x*yDim+y+side1]= I(x,y,zDim-1);
            }
        }
        for (int x = 0; x < xDim; x++) {
            for (int z = 0; z < zDim; z++) {
                ret[x*zDim+z+side1*2]= I(x,0,z);
                ret[x*zDim+z+side1*2+side2]= I(x,yDim-1,z);
            }
        }
        for (int y = 0; y < yDim; y++) {
            for (int z = 0; z < zDim; z++) {
                ret[y*zDim+z+(side1+side2)*2]= I(0,y,z);
                ret[y*zDim+z+(side1+side2)*2+side3]= I(xDim-1,y,z);
            }
        }
        return ret;
    }
    public void IncTick(){
        tick++;
    }
    public int GetTick(){
        return tick;
    }

    public int ConvXsq(int x,GridBase3D other){
        return (int)(((x+0.5)*other.xDim)/xDim);
    }
    public int ConvYsq(int y,GridBase3D other){
        return (int)(((y+0.5)*other.yDim)/yDim);
    }
    public int ConvZsq(int z,GridBase3D other){
        return (int)(((z+0.5)*other.zDim)/zDim);
    }
    public int ConvI(int i,GridBase3D other){
        int x=ItoX(i);
        int y=ItoY(i);
        int z=ItoZ(i);
        return other.I(ConvXsq(x,other),ConvYsq(y,other),ConvZsq(z,other));
    }
    public double ConvXpt(double x,GridBase3D other){
        return x*other.xDim/xDim;
    }
    public double ConvYpt(double y,GridBase3D other){
        return y*other.yDim/yDim;
    }
    public double ConvZpt(double z,GridBase3D other){
        return z*other.zDim/zDim;
    }

    public void ApplyRectangle(int startX, int startY,int startZ, int width, int height,int depth, Coords3DAction Action) {
        for (int x = startX; x < startX+width; x++) {
            for (int y = startY; y < startY+height; y++) {
                for (int z = startZ; z < startY+depth; z++) {
                    int xFinal = x;
                    int yFinal = y;
                    int zFinal = z;
                    if (wrapX) {
                        xFinal = Util.ModWrap(x, xDim);
                    }
                    if (wrapY) {
                        yFinal = Util.ModWrap(y, yDim);
                    }
                    if (wrapZ) {
                        zFinal = Util.ModWrap(z, zDim);
                    }
                    Action.Action(xFinal, yFinal, zFinal);
                }
            }
        }
    }

    int ApplyHood(int[] hood, int centerI, Coords3DAction Action){
        return ApplyHood(hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI),Action);
    }
    int ApplyHood(int[] hood, int centerX, int centerY, int centerZ, Coords3DAction Action){
        int ptCt = 0;
        int iStart = hood.length / 4;
        for (int i = iStart; i < hood.length; i += 3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(z, zDim)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            Action.Action(x,y,z);
        }
        return ptCt;
    }
    public boolean ContainsValidI(int[] hood, int centerI, Coords3DBool IsValid) {
        return ContainsValidI(hood, ItoX(centerI), ItoY(centerI),ItoZ(centerI), IsValid);
    }

    public boolean ContainsValidI(int[] hood, int centerX, int centerY,int centerZ, Coords3DBool IsValid) {
        int iStart = hood.length / 4;
        for (int i = iStart; i < hood.length; i += 3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(z, zDim)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            if(IsValid.Eval(x,y,z)){
                return true;
            }
        }
        return false;
    }

    public double DispX(double x1,double x2){
        if(wrapX){
            return Util.DispWrap(x1,x2,xDim);
        }
        else{
            return x2-x1;
        }
    }
    public double DispY(double y1,double y2){
        if(wrapY){
            return Util.DispWrap(y1,y2,yDim);
        }
        else{
            return y2-y1;
        }
    }
    public double DispZ(double z1,double z2){
        if(wrapY){
            return Util.DispWrap(z1,z2,zDim);
        }
        else{
            return z2-z1;
        }
    }
    public double Dist(double x1,double y1,double z1,double x2,double y2,double z2){
        return Math.sqrt(DistSquared(x1,y1,z1,x2,y2,z2));
    }
    public double DistSquared(double x1,double y1,double z1,double x2,double y2,double z2){
        double xDisp=DispX(x1,x2);
        double yDisp=DispY(y1,y2);
        double zDisp=DispZ(z1,z2);
        return xDisp*xDisp+yDisp*yDisp+zDisp*zDisp;

    }

    public void ResetTick(){
        tick=0;
    }
}

