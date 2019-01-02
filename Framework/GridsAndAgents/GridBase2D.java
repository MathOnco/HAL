package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Util;

import java.io.Serializable;

import static Framework.Util.InDim;
import static Framework.Util.Norm;
import static Framework.Util.NormSquared;

/**
 * holds functions that all 2D Grids share
 */
public abstract class GridBase2D implements Serializable {
    public final int xDim;
    public final int yDim;
    public final int length;
    public boolean wrapX;
    public boolean wrapY;
    int tick;

    /**
     * gets the index of the square at the specified coordinates
     */
    public int I(int x, int y) {
        //gets typeGrid index from location
        return x * yDim + y;
    }

    /**
     * gets the index of the square at the specified coordinates with wrap around
     */
    public int WrapI(int x, int y) {
        //wraps Coords to proper index
        if (In(x, y)) {
            return I(x, y);
        }
        if(wrapX) {
            x=Util.Wrap(x,xDim);
        }
        if(wrapY) {
            y=Util.Wrap(y,xDim);
        }
        if(In(x,y)) {
            return I(x, y);
        }
        throw new IllegalArgumentException("cannot map to index in bounds!" );
    }

    /**
     * gets the xDim component of the square at the specified index
     */
    public int ItoX(int i) {
        return i / yDim;
    }

    /**
     * gets the yDim component of the square at the specified index
     */
    public int ItoY(int i) {
        return i % yDim;
    }

    /**
     * gets the index of the square that contains the specified coordinates
     */
    public int I(double x, double y) {
        //gets typeGrid index from location
        return (int) Math.floor(x) * yDim + (int) Math.floor(y);
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    public boolean In(int x, int y) {
        return x >= 0 && x < xDim && y >= 0 && y < yDim;
    }

    /**
     * returns whether the specified coordinates are inside the Grid bounds with wraparound
     */
    public boolean InWrap(int x,int y) {
        if (wrapX || InDim(x, xDim) && (wrapY || InDim(y, yDim))) {
            return true;
        }
        return false;
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    public boolean In(double x, double y) {
        int xInt = (int) Math.floor(x);
        int yInt = (int) Math.floor(y);
        return In(xInt, yInt);
    }

    /**
     * applies the action function to all positions in the rectangle, will use wraparound if appropriate
     */
    public void ApplyRectangle(int startX, int startY, int width, int height, Coords2DAction Action) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                int xFinal = x;
                int yFinal = y;
                if (wrapX) {
                    xFinal = Util.Wrap(x, xDim);
                }
                if (wrapY) {
                    yFinal = Util.Wrap(y, yDim);
                }
                Action.Action(xFinal, yFinal);
            }
        }
    }

    /**
     * applies the action function to all positions in the neighborhood up to validCount, assumes the neighborhood is
     * already mapped
     */
    public void ApplyHoodMapped(int[] hood, int validCount, IndexAction Action) {
        for (int i = 0; i < validCount; i++) {
            Action.Action(hood[i]);
        }
    }

    /**
     * applies the action function to all positions in the neighborhood
     */
    public int ApplyHood(int[] hood, int centerI, Coords2DAction Action) {
        return ApplyHood(hood, ItoX(centerI), ItoY(centerI), Action);
    }

    /**
     * applies the action function to all positions in the neighborhood
     */
    public int ApplyHood(int[] hood, int centerX, int centerY, Coords2DAction Action) {
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.Wrap(y, yDim);
                } else {
                    continue;
                }
            }
            Action.Action(x, y);
        }
        return ptCt;
    }

    /**
     * This function takes a neighborhood centered around the origin, translates the set of coordinates to be centered
     * around a particular central location, and computes which indices the translated coordinates map to. The function
     * returns the number of valid locations it set. this function differs from HoodToIs and CoordsToIs in that it takes
     * no ret[], MapHood instead puts the result of the mapping back into the hood array.
     */
    public int MapHood(int[] hood, int iCenter) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        return MapHood(hood, ItoX(iCenter), ItoY(iCenter));
    }

    /**
     * This function is very similar to the previous definition of MapHood, only it additionally takes as argument an
     * EvaluationFunctoin. this function should take as argument (i,x,y) of a location and return a boolean that decides
     * whether that location should be included as a valid one.
     */
    public int MapHood(int[] hood, int centerX, int centerY, IndexCoords2DBool Eval) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.Wrap(y, yDim);
                } else {
                    continue;
                }
            }
            int j = I(x, y);
            if (Eval.Eval(j, x, y)) {
                hood[ptCt] = j;
                ptCt++;
            }
        }
        return ptCt;
    }

    /**
     * This function is very similar to the previous definition of MapHood, only it additionally takes as argument an
     * EvaluationFunctoin. this function should take as argument (i,x,y) of a location and return a boolean that decides
     * whether that location should be included as a valid one.
     */
    public int MapHood(int[] hood, int iCenter, IndexCoords2DBool Eval) {
        return MapHood(hood, ItoX(iCenter), ItoY(iCenter), Eval);
    }

    /**
     * This function takes a neighborhood centered around the origin, translates the set of coordinates to be centered
     * around a particular central location, and computes which indices the translated coordinates map to. The function
     * returns the number of valid locations it set. this function differs from HoodToIs and CoordsToIs in that it takes
     * no ret[], MapHood instead puts the result of the mapping back into the hood array.
     */
    public int MapHood(int[] hood, int centerX, int centerY) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.Wrap(y, yDim);
                } else {
                    continue;
                }
            }
            hood[ptCt] = I(x, y);
            ptCt++;
        }
        return ptCt;
    }

    /**
     * returns a list of indices, where each index maps to one square on the boundary of the grid
     */
    public int[] BoundaryIs() {
        int[] ret = new int[(xDim + yDim) * 2];
        for (int x = 0; x < xDim; x++) {
            ret[x] = I(x, 0);
            ret[x + xDim] = I(x, yDim - 1);
        }
        for (int y = 0; y < yDim; y++) {
            ret[y + xDim * 2] = I(0, y);
            ret[y + xDim * 2 + yDim] = I(xDim - 1, y);
        }
        return ret;
    }

    /**
     * returns whether a valid index exists in the neighborhood
     */
    public boolean ContainsValidI(int[] hood, int centerI, Coords2DBool IsValid) {
        return ContainsValidI(hood, ItoX(centerI), ItoY(centerI), IsValid);
    }

    /**
     * returns whether a valid index exists in the neighborhood
     */
    public boolean ContainsValidI(int[] hood, int centerX, int centerY, Coords2DBool IsValid) {
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.Wrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (IsValid.Eval(x, y)) {
                return true;
            }
        }
        return false;
    }


    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    public int ConvXsq(int x, GridBase2D other) {
        return (int) (((x + 0.5) * other.xDim) / xDim);
    }

    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    public int ConvYsq(int y, GridBase2D other) {
        return (int) (((y + 0.5) * other.yDim) / yDim);
    }

    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    public int ConvI(int i, GridBase2D other) {
        int x = ItoX(i);
        int y = ItoY(i);
        return other.I(ConvXsq(x, other), ConvYsq(y, other));
    }

    /**
     * returns the position that x rescales to in the other grid
     */
    public double ConvXpt(double x, GridBase2D other) {
        return x * other.xDim / xDim;
    }

    /**
     * returns the position that y rescales to in the other grid
     */
    public double ConvYpt(double y, GridBase2D other) {
        return y * other.yDim / yDim;
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

    /**
     * returns the set of indicies of squares that the line between (x1,y1) and (x2,y2) touches.
     */
    public int AlongLineIs(double x1, double y1, double x2, double y2, int[] writeHere) {
        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);

        int x = (int) (Math.floor(x1));
        int y = (int) (Math.floor(y1));

        int n = 1;
        int x_inc, y_inc;
        double error;

        if (dx == 0) {
            x_inc = 0;
            error = Double.MAX_VALUE;
        } else if (x2 > x1) {
            x_inc = 1;
            n += (int) (Math.floor(x2)) - x;
            error = (Math.floor(x1) + 1 - x1) * dy;
        } else {
            x_inc = -1;
            n += x - (int) (Math.floor(x2));
            error = (x1 - Math.floor(x1)) * dy;
        }

        if (dy == 0) {
            y_inc = 0;
            error -= Double.MAX_VALUE;
        } else if (y2 > y1) {
            y_inc = 1;
            n += (int) (Math.floor(y2)) - y;
            error -= (Math.floor(y1) + 1 - y1) * dx;
        } else {
            y_inc = -1;
            n += y - (int) (Math.floor(y2));
            error -= (y1 - Math.floor(y1)) * dx;
        }

        int Count = 0;
        for (; n > 0; --n) {
            writeHere[Count] = I((int) Math.floor(x), (int) Math.floor(y));
            Count++;

            if (error > 0) {
                y += y_inc;
                error -= dx;
            } else {
                x += x_inc;
                error += dy;
            }
        }
        return Count;
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
     * gets the displacement from the first coorinate to the second. using wraparound if allowed over the given axis to
     * find the shortest displacement.
     */
    public double DispY(double y1, double y2) {
        if (wrapY) {
            return Util.DispWrap(y2, y1, yDim);
        } else {
            return y2 - y1;
        }
    }

    /**
     * gets the distance between two positions with or without grid wrap around (if wraparound is enabled, the shortest
     * distance taking this into account will be returned)
     */
    public double Dist(double x1, double y1, double x2, double y2) {
        double dx = DispX(x1, y1);
        double dy = DispY(x2, y2);
        return Norm(dx, dy);
    }

    /**
     * gets the distance squared between two positions with or without grid wrap around (if wraparound is enabled, the
     * shortest distance taking this into account will be returned) more efficient than the Dist function above as it
     * skips a square-root calculation.
     */
    public double DistSquared(double x1, double y1, double x2, double y2) {
        double xDisp = DispX(x1, x2);
        double yDisp = DispY(y1, y2);
        return NormSquared(xDisp, yDisp);

    }

    public GridBase2D(int xDim, int yDim, boolean wrapX, boolean wrapY) {
        this.xDim = xDim;
        this.yDim = yDim;
        this.wrapX = wrapX;
        this.wrapY = wrapY;
        this.length = xDim * yDim;
    }

}

