package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Util;

import java.io.Serializable;

import static Framework.Util.InDim;

/**
 * holds functions that all 1D Grids share
 */
public abstract class GridBase1D implements Serializable {
    public final int xDim;
    public final int length;
    public boolean wrapX;
    int tick;

    /**
     * gets the index of the square at the specified coordinates with wrap around
     */
    public int WrapI(int x) {
        //wraps Coords to proper index
        if (In(x)) {
            return x;
        }
        if(wrapX) {
            return Util.Wrap(x,xDim);
        }
        throw new IllegalArgumentException("cannot map to index in bounds!" );
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    public boolean In(int x) {
        return x >= 0 && x < xDim;
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    public boolean In(double x) {
        int xInt = (int) Math.floor(x);
        return In(xInt);
    }

    /**
     * returns whether the specified coordinates are inside the Grid bounds with wraparound
     */
    public boolean InWrap(int x){
        return wrapX || InDim(x, xDim);
    }

    /**
     * returns whether the specified coordinates are inside the Grid bounds with wraparound
     */
    public boolean InWrap(double x){
        return wrapX || InDim(x, xDim);
    }

    /**
     * gets the displacement from the first coorinate to the second. using wraparound if allowed over the given axis to
     * find the shortest displacement.
     */
    public double DispX(double x1, double x2) {
        if (wrapX) {
            return Util.DispWrap(x2, x1, xDim);
        } else {
            return x2 - x1;
        }
    }

    /**
     * gets the distance between two positions with or without grid wrap around (if wraparound is enabled, the shortest
     * distance taking this into account will be returned)
     */
    public double Dist(double x1, double x2) {
        return Math.abs(DispX(x1, x2));
    }

    /**
     * gets the distance between two positions squared with or without grid wrap around
     */
    public double DistSquared(double x1, double x2) {
        double dist = DispX(x1, x2);
        return dist * dist;
    }

    /**
     * applies the action function to all positions in the rectangle, will use wraparound if appropriate
     */
    public void ApplyRectangle(int startX, int width, IndexAction Action) {
        for (int x = startX; x < startX + width; x++) {
            int xFinal = x;
            if (wrapX) {
                xFinal = Util.Wrap(x, xDim);
            }
            Action.Action(xFinal);
        }
    }

    /**
     * applies the action function to all positions in the neighborhood
     */
    int ApplyHood(int[] hood, int centerX, IndexAction Action) {
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
                } else {
                    continue;
                }
            }
            Action.Action(x);
        }
        return ptCt;
    }

    /**
     * applies the action function to all positions in the neighborhood up to validCount, assumes the neighborhood is
     * already mapped
     */
    void ApplyHoodMapped(int[] hood, int validCount, IndexAction Action) {
        for (int i = 0; i < validCount; i++) {
            Action.Action(hood[i]);
        }
    }

    /**
     * This function is very similar to the previous definition of MapHood, only it additionally takes as argument an
     * EvaluationFunctoin. this function should take as argument (i,x,y) of a location and return a boolean that decides
     * whether that location should be included as a valid one.
     */
    public int MapHood(int[] hood, int centerX, Coords1DBool Eval) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
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
    public int MapHood(int[] hood, int centerX) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
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
     * returns whether a valid index exists in the neighborhood
     */
    public boolean ContainsValidI(int[] hood, int centerX, IndexBool IsValid) {
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
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
    public int ConvXsq(int x, GridBase1D other) {
        return (int) (((x + 0.5) * other.xDim) / xDim);
    }

    /**
     * returns the position that x rescales to in the other grid
     */
    public double ConvXpt(double x, GridBase1D other) {
        return x * other.xDim / xDim;
    }

    /**
     * increments the internal grid tick counter by 1, used with the Age() and BirthTick() functions to get age
     * information about the agents on an AgentGrid. can otherwise be used as a counter with the other grid types.
     */
    public void IncTick() {
        tick++;
    }

    /**
     * gets the current grid timestep.
     */
    public int GetTick() {
        return tick;
    }

    /**
     * sets the tick to 0.
     */
    public void ResetTick() {
        tick = 0;
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

    public GridBase1D(int xDim, boolean wrapX) {
        this.xDim = xDim;
        this.wrapX = wrapX;
        this.length = xDim;
    }

}

