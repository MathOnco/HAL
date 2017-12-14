package Framework.Interfaces;

import Framework.Util;

/**
 * Created by rafael on 7/3/17.
 */

public interface Mat3 {
    int Xdim();

    int Ydim();

    int Zdim();

    default int Length(){
       return Xdim()*Ydim()*Zdim();
    }

    default boolean WrapX() {
        return false;
    };
    default boolean WrapY() {
        return false;
    };
    default boolean WrapZ() {
        return false;
    };

    /**
     * gets the Xdim() component of the voxel at the specified index
     */
    default int ItoX(int i){
        return i/(Ydim()*Zdim());
    }

    /**
     * gets the Ydim() component of the voxel at the specified index
     */
    default int ItoY(int i){return (i/Zdim())%Ydim();}

    /**
     * gets the z component of the voxel at the specified index
     */
    default int ItoZ(int i){
        return i%Zdim();
    }

    /**
     * gets the index of the square that contains the specified coordinates
     */
    default int I(double x, double y, double z){
        //gets typeGrid index from location
        return (int)Math.floor(x)*Ydim()*Zdim()+(int)Math.floor(y)*Ydim()+(int)Math.floor(z);
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    default boolean In(int x, int y, int z){
        if(x>=0&&x<Xdim()&&y>=0&&y<Ydim()&&z>=0&&z<Zdim()){
            return true;
        }
        return false;
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    default boolean In(double x, double y, double z){
        int xInt=(int)Math.floor(x);
        int yInt=(int)Math.floor(y);
        int zInt=(int)Math.floor(z);
        return In(xInt,yInt,zInt);
    }
    /**
     * writes into ret the coordinates of SQs with the centerX and centerY coordinates added
     * returns the number of squares that were written into ret. any coordinates that fall out of bounds are not written
     * @param SQs list of coordinates of the form [Xdim(),Ydim(),Xdim(),Ydim(),...]
     * @param ret list into which the displaced coordinates will be written
     * @param centerX Xdim() displacement of coordinates
     * @param centerY Ydim() displacement of coordinates
     * @param centerZ z displacement of coordinates
     * @param wrapX whether to wrap the coordinates that fall out of bounds in the X dimension
     * @param wrapY whether to wrap the coordinates that fall out of bounds in the Y dimension
     * @param wrapZ whether to wrap the coordinates that fall out of bounds in the Z dimension
     * @return the number of coordinates written into the ret array
     */
    default int SQstoLocalIs(int[] SQs, int[] ret, int centerX, int centerY, int centerZ, boolean wrapX,boolean wrapY,boolean wrapZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        for (int i = 0; i < SQs.length / 3; i++) {
            int x = SQs[i * 3] + centerX;
            int y = SQs[i * 3 + 1] + centerY;
            int z = SQs[i * 3 + 2] + centerZ;
            if (!Util.InDim(Xdim(), x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, Xdim());
                } else {
                    continue;
                }
            }
            if (!Util.InDim(Ydim(), y)) {
                if (wrapY) {
                    y = Util.ModWrap(y, Ydim());
                } else {
                    continue;
                }
            }
            if (!Util.InDim(Zdim(), z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, Ydim());
                } else {
                    continue;
                }
            }
            ret[ptCt]= I(x,y,z);
            ptCt++;
        }
        return ptCt;
    }
    default int SQstoLocalIs(int[] SQs, int[] ret, int centerX, int centerY, int centerZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        for (int i = 0; i < SQs.length / 3; i++) {
            int x = SQs[i * 3] + centerX;
            int y = SQs[i * 3 + 1] + centerY;
            int z = SQs[i * 3 + 2] + centerZ;
            if (!Util.InDim(Xdim(), x)) {
                if (WrapX()) {
                    x = Util.ModWrap(x, Xdim());
                } else {
                    continue;
                }
            }
            if (!Util.InDim(Ydim(), y)) {
                if (WrapY()) {
                    y = Util.ModWrap(y, Ydim());
                } else {
                    continue;
                }
            }
            if (!Util.InDim(Zdim(), z)) {
                if (WrapZ()) {
                    z = Util.ModWrap(z, Ydim());
                } else {
                    continue;
                }
            }
            ret[ptCt]= I(x,y,z);
            ptCt++;
        }
        return ptCt;
    }
    default double DistSq(double x1, double y1, double z1, double x2, double y2, double z2, boolean wrapX, boolean wrapY, boolean wrapZ){
        return Util.DistSquared(x1,y1,z1,x2,y2,z2,Xdim(),Ydim(),Zdim(),wrapX,wrapY,wrapZ);
    }
    default double DistSq(double x1, double y1, double z1, double x2, double y2, double z2){
        return Util.DistSquared(x1,y1,z1,x2,y2,z2,Xdim(),Ydim(),Zdim(),WrapX(),WrapY(),WrapZ());
    }

}
