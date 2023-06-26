package HAL.Interfaces;

import HAL.Rand;
import HAL.Util;

import static HAL.Util.InDim;

/**
 * holds functions that all 1D Grids share
 */
public interface Grid1D {

    /**
     * gets the index of the square at the specified coordinates with wrap around
     */
    default public int WrapI(int x) {
        //wraps Coords to proper index
        if (In(x)) {
            return x;
        }
        if(IsWrapX()) {
            return Util.Wrap(x, Xdim());
        }
        throw new IllegalArgumentException("cannot map to index in bounds!" );
    }

    /**
     * get the value of the given x component with wraparound
     */
    default public double WrapX(double x){
        return Util.Wrap(x,Xdim());
    }
    /**
     * get the value of the given x component with wraparound
     */
    default public int WrapX(int x){
        return Util.Wrap(x,Xdim());
    }


    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    default public boolean In(int x) {
        return x >= 0 && x < Xdim();
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    default public boolean In(double x) {
        int xInt = (int) Math.floor(x);
        return In(xInt);
    }

    /**
     * returns whether the specified coordinates are inside the Grid bounds with wraparound
     */
    default public boolean InWrap(int x){
        return IsWrapX() || InDim(x, Xdim());
    }

    /**
     * returns whether the specified coordinates are inside the Grid bounds with wraparound
     */
    default public boolean InWrap(double x){
        return IsWrapX() || InDim(x, Xdim());
    }

    /**
     * gets the displacement from the first coorinate to the second. using wraparound if allowed over the given axis to
     * find the shortest displacement.
     */
    default public double DispX(double x1, double x2) {
        if (IsWrapX()) {
            return Util.DispWrap(x2, x1, Xdim());
        } else {
            return x2 - x1;
        }
    }

    /**
     * gets the distance between two positions with or without grid wrap around (if wraparound is enabled, the shortest
     * distance taking this into account will be returned)
     */
    default public double Dist(double x1, double x2) {
        return Math.abs(DispX(x1, x2));
    }

    default public double Dist(int x1, int x2){
        return Math.abs(DispX(x1,x2));
    }
    /**
     * gets the distance between two positions squared with or without grid wrap around
     */
    default public double DistSquared(double x1, double x2) {
        double dist = DispX(x1, x2);
        return dist * dist;
    }

    /**
     * applies the action function to all positions in the rectangle, will use wraparound if appropriate
     */
    default public void ApplyRectangle(int startX, int width, IndexAction Action) {
        for (int x = startX; x < startX + width; x++) {
            int xFinal = x;
            if (IsWrapX()) {
                xFinal = Util.Wrap(x, Xdim());
            }
            Action.Action(xFinal);
        }
    }

    /**
     * applies the action function to all positions in the neighborhood
     */
    default int ApplyHood(int[] hood, int centerX, IndexAction Action) {
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, Xdim())) {
                if (IsWrapX()) {
                    x = Util.Wrap(x, Xdim());
                } else {
                    continue;
                }
            }
            Action.Action(x);
        }
        return ptCt;
    }

    /**
     * applies the action function to all positions in the neighborhood, includes the index in the neighborhood
     */
    default int ApplyHoodWithIndex(int[] hood, int centerX, Coords2DAction Action) {
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, Xdim())) {
                if (IsWrapX()) {
                    x = Util.Wrap(x, Xdim());
                } else {
                    continue;
                }
            }
            Action.Action(i,x);
        }
        return ptCt;
    }

    /**
     * applies the action function to all positions in the neighborhood up to validCount, assumes the neighborhood is
     * already mapped
     */
    default void ApplyHoodMapped(int[] hood, int validCount, IndexAction Action) {
        for (int i = 0; i < validCount; i++) {
            Action.Action(hood[i]);
        }
    }

    /**
     * This function is very similar to the previous definition of MapHood, only it additionally takes as argument an
     * EvaluationFunctoin. this function should take as argument (i,x,y) of a location and return a boolean that decides
     * whether that location should be included as a valid one.
     */
    default public int MapHood(int[] hood, int centerX, Coords1DBool Eval) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, Xdim())) {
                if (IsWrapX()) {
                    x = Util.Wrap(x, Xdim());
                } else {
                    continue;
                }
            }
            if (Eval.Eval(x)) {
                hood[ptCt] = x;
                ptCt++;
            }
        }
        return ptCt;
    }

    /**
     * This function takes a neighborhood centered around the origin, translates the set of coordinates to be centered
     * around a particular central location, and computes which indices the translated coordinates map to. The function
     * returns the number of valid locations it set. this function differs from HoodToIs and CoordsToIs in that it takes
     * no ret[], MapHood instead puts the result of the mapping back into the hood array.
     */
    default public int MapHood(int[] hood, int centerX) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, Xdim())) {
                if (IsWrapX()) {
                    x = Util.Wrap(x, Xdim());
                } else {
                    continue;
                }
            }
            hood[ptCt] = x;
            ptCt++;
        }
        return ptCt;
    }

    /**
     * gets a random index from the full neighborhood, if the index does not map, returns -1
     */
    public default int RandomHoodI(int[] hood, int centerX, Rand rng){
        int i=rng.Int(hood.length/2);
        return GetHoodI(hood,centerX,i);
    }


    /**
     * gets a specified index from the hood after mapping to the center position
     */
    public default int GetHoodI(int[]hood,int centerX,int entryIndex){
        int i=entryIndex+hood.length/2;
        int x = hood[i] + centerX;
        if (!Util.InDim(x, Xdim())) {
            if (IsWrapX()) {
                x = Util.Wrap(x, Xdim());
            } else {
                return -1;
            }
        }
        return x;
    }

    /**
     * returns whether a valid index exists in the neighborhood
     */
    default public boolean ContainsValidI(int[] hood, int centerX, IndexBool IsValid) {
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, Xdim())) {
                if (IsWrapX()) {
                    x = Util.Wrap(x, Xdim());
                } else {
                    continue;
                }
            }
            if (IsValid.Eval(x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    default public int MapXsq(int x, Grid1D other) {
        return (int) (((x + 0.5) * other.Xdim()) / Xdim());
    }

    /**
     * returns the position that x rescales to in the other grid
     */
    default public double MapXpt(double x, Grid1D other) {
        return x * other.Xdim() / Xdim();
    }

    default public void ForEdge(IntToVoid Action){
        Action.Eval(0);
        Action.Eval(Length()-1);
    }

//    /**
//     * gets the indices of the squares that lie within a given radius of a position
//     * argument array must be large enough to fit all indices in the maximum case, something like (rad*2)^2
//     */
//    public int SQsInRad(final int[] ret,final boolean wrapX,final boolean wrapY,final double centerX,final double centerY,final double rad){
//        int retCt=0;
//        for (int x = (int)Math.floor(centerX-rad); x <(int)Math.ceil(centerX+rad) ; x++) {
//            for (int y = (int)Math.floor(centerY-rad); y <(int)Math.ceil(centerY+rad) ; y++) {
//                int retX=x; int retY=y;
//                boolean inX=Util.InDim(xDim,retX);
//                boolean inY=Util.InDim(yDim,retY);
//                if((!wrapX&&!inX)||(!wrapY&&!inY)){
//                    continue;
//                }
//                if(wrapX&&!inX){
//                    retX=Util.Wrap(retX,xDim);
//                }
//                if(wrapY&&!inY){
//                    retY=Util.Wrap(retY,yDim);
//                }
//                ret[retCt]=I(retX,retY);
//                retCt++;
//            }
//        }
//        return retCt;
//    }

    public int Xdim();
    public int Length();
    public boolean IsWrapX();

}

