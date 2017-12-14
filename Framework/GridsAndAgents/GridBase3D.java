package Framework.GridsAndAgents;

import Framework.Interfaces.Coords3DToAction;
import Framework.Util;

/**
 * Created by bravorr on 5/17/17.
 */
public abstract class GridBase3D extends GridBase{
    public final int xDim;
    public final int yDim;
    public final int zDim;
    public final int length;
    public boolean wrapX;
    public boolean wrapY;
    public boolean wrapZ;
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
    /**
     * writes into ret the coordinates of SQs with the centerX and centerY coordinates added
     * returns the number of squares that were written into ret. any coordinates that fall out of bounds are not written
     * @param hood list of coordinates of the form [xDim,yDim,xDim,yDim,...]
     * @param ret list into which the displaced coordinates will be written
     * @param centerX xDim displacement of coordinates
     * @param centerY yDim displacement of coordinates
     * @param centerZ z displacement of coordinates
     * @param wrapX whether to wrap the coordinates that fall out of bounds in the X dimension
     * @param wrapY whether to wrap the coordinates that fall out of bounds in the Y dimension
     * @param wrapZ whether to wrap the coordinates that fall out of bounds in the Z dimension
     * @return the number of coordinates written into the ret array
     */
    public int HoodToIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ, boolean wrapX, boolean wrapY, boolean wrapZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        for (int i = 0; i < hood.length / 3; i++) {
            int x = hood[i * 3] + centerX;
            int y = hood[i * 3 + 1] + centerY;
            int z = hood[i * 3 + 2] + centerZ;
            if (!Util.InDim(xDim, x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(yDim, y)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(zDim, z)) {
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
    public int HoodToIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        for (int i = 0; i < hood.length / 3; i++) {
            int x = hood[i * 3] + centerX;
            int y = hood[i * 3 + 1] + centerY;
            int z = hood[i * 3 + 2] + centerZ;
            if (!Util.InDim(xDim, x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(yDim, y)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(zDim, z)) {
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

    public int HoodToAction(int[] hood, int centerX, int centerY, int centerZ, Coords3DToAction Action) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        for (int i = 0; i < hood.length / 3; i++) {
            int x = hood[i * 3] + centerX;
            int y = hood[i * 3 + 1] + centerY;
            int z = hood[i * 3 + 2] + centerZ;
            if (!Util.InDim(xDim, x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(yDim, y)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(zDim, z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            Action.Action(x,y,z);
            ptCt++;
        }
        return ptCt;
    }

    public int CoordsToIs(int[] coords, int[] ret, boolean wrapX, boolean wrapY, boolean wrapZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        for (int i = 0; i < coords.length / 3; i++) {
            int x = coords[i * 3];
            int y = coords[i * 3 + 1];
            int z = coords[i * 3 + 2];
            if (!Util.InDim(xDim, x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(yDim, y)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(zDim, z)) {
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
    public int CoordsToIs(int[] coords, int[] ret) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        for (int i = 0; i < coords.length / 3; i++) {
            int x = coords[i * 3];
            int y = coords[i * 3 + 1];
            int z = coords[i * 3 + 2];
            if (!Util.InDim(xDim, x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(yDim, y)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(zDim, z)) {
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
    public double DistSq(double x1, double y1, double z1, double x2, double y2, double z2, boolean wrapX, boolean wrapY, boolean wrapZ){
        return Util.DistSquared(x1,y1,z1,x2,y2,z2,xDim,yDim,zDim,wrapX,wrapY,wrapZ);
    }
    public double DistSq(double x1, double y1, double z1, double x2, double y2, double z2){
        return Util.DistSquared(x1,y1,z1,x2,y2,z2,xDim,yDim,zDim,wrapX,wrapY,wrapZ);
    }
    public int[]BoundaryIs(){
        int[] ret=new int[(xDim*yDim+xDim*zDim+yDim*zDim)*2];
        int side1=xDim*yDim;
        int side2=xDim*zDim;
        int side3=yDim*zDim;
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                ret[x*yDim+y]=I(x,y,0);
                ret[x*yDim+y+side1]=I(x,y,zDim-1);
            }
        }
        for (int x = 0; x < xDim; x++) {
            for (int z = 0; z < zDim; z++) {
                ret[x*zDim+z+side1*2]=I(x,0,z);
                ret[x*zDim+z+side1*2+side2]=I(x,yDim-1,z);
            }
        }
        for (int y = 0; y < yDim; y++) {
            for (int z = 0; z < zDim; z++) {
                ret[y*zDim+z+(side1+side2)*2]=I(0,y,z);
                ret[y*zDim+z+(side1+side2)*2+side3]=I(xDim-1,y,z);
            }
        }
        return ret;
    }
}
