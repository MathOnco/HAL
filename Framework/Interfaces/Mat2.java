package Framework.Interfaces;

import Framework.Util;

/**
 * Created by rafael on 7/3/17.
 */

public interface Mat2 {
    int Xdim();

    int Ydim();

    default boolean WrapX() {
        return false;
    };
    default boolean WrapY() {
        return false;
    };

    default int Length(){
        return Xdim()*Ydim();
    }

    default boolean In(int x, int y) {
        return x >= 0 && x < Xdim() && y >= 0 && y < Ydim();
    }
    default boolean In(double x, double y) {
        return x >= 0 && x < Xdim() && y >= 0 && y < Ydim();
    }

    default int SQwrapI(int x, int y) {
        //wraps Coords to proper index
        if (In(x, y)) {
            return I(x, y);
        }
        return I(Util.ModWrap(x, Xdim()), Util.ModWrap(y, Ydim()));
    }

    /**
     * gets the xDim component of the square at the specified index
     */
    default int ItoX(int i) {
        return i / Ydim();
    }

    /**
     * gets the yDim component of the square at the specified index
     */
    default int ItoY(int i) {
        return i % Ydim();
    }

    default int I(int x, int y) {
        return x * Ydim() + y;
    }

    default int I(double x, double y) {
        return (int) (x * Ydim() + y);
    }

    default int I(float x, float y) {
        return (int) (x * Ydim() + y);
    }
    default double DistSq(double x1, double y1, double x2, double y2, boolean wrapX, boolean wrapY){
        return Util.DistSquared(x1,y1,x2,y2, Xdim(), Ydim(), wrapX,wrapY);
    }
    default double DistSq(double x1, double y1, double x2, double y2){
        return Util.DistSquared(x1,y1,x2,y2, Xdim(), Ydim(), WrapX(),WrapY());
    }
    /**
     * writes into ret the coordinates of SQs with the centerX and centerY coordinates added
     * returns the number of squares that were written into ret. any coordinates that fall out of bounds are not written
     * @param SQs list of coordinates of the form [xDim,yDim,xDim,yDim,...]
     * @param ret list into which the displaced coordinates will be written
     * @param centerX xDim displacement of coordinates
     * @param centerY yDim displacement of coordinates
     * @param wrapX whether to wrap the coordinates that fall out of bounds in the X direction
     * @param wrapY whether to wrap the coordinates that fall out of bounds in the Y direction
     * @return the number of coordinates written into the ret array
     */
    default int HoodToLocalIs(int[] SQs, int[] ret, int centerX, int centerY, boolean wrapX, boolean wrapY){
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt=0;
        for(int i=0;i<SQs.length/2;i++) {
            int x = SQs[i * 2] + centerX;
            int y = SQs[i * 2 + 1] + centerY;
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
            ret[ptCt]=I(x,y);
            ptCt++;
        }
        return ptCt;
    }
    default int HoodToLocalIs(int[] SQs, int[] ret, int centerX, int centerY){
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt=0;
        for(int i=0;i<SQs.length/2;i++) {
            int x = SQs[i * 2] + centerX;
            int y = SQs[i * 2 + 1] + centerY;
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
            ret[ptCt]=I(x,y);
            ptCt++;
        }
        return ptCt;
    }

}
