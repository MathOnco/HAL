package HAL.Interfaces;

import HAL.Rand;
import HAL.Util;

import static HAL.Util.*;

/**
 * holds functions that all 2D Grids share
 */
public abstract interface Grid2D {

    /**
     * gets the index of the square at the specified coordinates
     */
    default public int I(int x, int y) {
        //gets typeGrid index from location
        return x * Ydim() + y;
    }

    /**
     * gets the index of the square at the specified coordinates with wrap around
     */
    default public int WrapI(int x, int y) {
        //wraps Coords to proper index
        if (In(x, y)) {
            return I(x, y);
        }
        if(IsWrapX()) {
            x=Util.Wrap(x, Xdim());
        }
        if(IsWrapY()) {
            y=Util.Wrap(y, Xdim());
        }
        if(In(x,y)) {
            return I(x, y);
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
     * get the value of the given y component with wraparound
     */
    default public double WrapY(double y){
        return Util.Wrap(y,Ydim());
    }
    /**
     * get the value of the given x component with wraparound
     */
    default public int WrapX(int x){
        return Util.Wrap(x,Xdim());
    }
    /**
     * get the value of the given y component with wraparound
     */
    default public int WrapY(int y){
        return Util.Wrap(y,Ydim());
    }

    /**
     * gets the xDim component of the square at the specified index
     */
    default public int ItoX(int i) {
        return i / Ydim();
    }

    /**
     * gets the yDim component of the square at the specified index
     */
    default public int ItoY(int i) {
        return i % Ydim();
    }

    /**
     * gets the index of the square that contains the specified coordinates
     */
    default public int I(double x, double y) {
        //gets typeGrid index from location
        return (int) Math.floor(x) * Ydim() + (int) Math.floor(y);
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    default public boolean In(int x, int y) {
        return x >= 0 && x < Xdim() && y >= 0 && y < Ydim();
    }

    /**
     * returns whether the specified coordinates are inside the Grid bounds with wraparound
     */
    default public boolean InWrap(int x,int y) {
        if (IsWrapX() || InDim(x, Xdim()) && (IsWrapY() || InDim(y, Ydim()))) {
            return true;
        }
        return false;
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    default public boolean In(double x, double y) {
        int xInt = (int) Math.floor(x);
        int yInt = (int) Math.floor(y);
        return In(xInt, yInt);
    }

    /**
     * applies the action function to all positions in the rectangle, will use wraparound if appropriate
     */
    default public void ApplyRectangle(int startX, int startY, int width, int height, Coords2DAction Action) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                int xFinal = x;
                int yFinal = y;
                if (IsWrapX()) {
                    xFinal = Util.Wrap(x, Xdim());
                }
                if (IsWrapY()) {
                    yFinal = Util.Wrap(y, Ydim());
                }
                Action.Action(xFinal, yFinal);
            }
        }
    }

    /**
     * applies the action function to all positions in the neighborhood up to validCount, assumes the neighborhood is
     * already mapped
     */
    default public void ApplyHoodMapped(int[] hood, int validCount, IndexAction Action) {
        for (int i = 0; i < validCount; i++) {
            Action.Action(hood[i]);
        }
    }

    /**
     * applies the action function to all positions in the neighborhood
     */
    default public int ApplyHood(int[] hood, int centerI, Coords2DAction Action) {
        return ApplyHood(hood, ItoX(centerI), ItoY(centerI), Action);
    }

    /**
     * applies the action function to all positions in the neighborhood
     */
    default public int ApplyHood(int[] hood, int centerX, int centerY, Coords2DAction Action) {
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, Xdim())) {
                if (IsWrapX()) {
                    x = Util.Wrap(x, Xdim());
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, Ydim())) {
                if (IsWrapY()) {
                    y = Util.Wrap(y, Ydim());
                } else {
                    continue;
                }
            }
            Action.Action(x, y);
        }
        return ptCt;
    }
    /**
     * applies the action function to all positions in the neighborhood, includes the index in the neighborhood
     */
    default public int ApplyHoodWithIndex(int[] hood, int centerI, ICoords2DAction Action) {
        return ApplyHoodWithIndex(hood, ItoX(centerI), ItoY(centerI), Action);
    }

    /**
     * applies the action function to all positions in the neighborhood, includes the index in the neighborhood
     */
    default public int ApplyHoodWithIndex(int[] hood, int centerX, int centerY, ICoords2DAction Action) {
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, Xdim())) {
                if (IsWrapX()) {
                    x = Util.Wrap(x, Xdim());
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, Ydim())) {
                if (IsWrapY()) {
                    y = Util.Wrap(y, Ydim());
                } else {
                    continue;
                }
            }
            Action.Action((i-iStart)/2,x, y);
        }
        return ptCt;
    }

    /**
     * This function takes a neighborhood centered around the origin, translates the set of coordinates to be centered
     * around a particular central location, and computes which indices the translated coordinates map to. The function
     * returns the number of valid locations it set. this function differs from HoodToIs and CoordsToIs in that it takes
     * no ret[], MapHood instead puts the result of the mapping back into the hood array.
     */
    default public int MapHood(int[] hood, int iCenter) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        return MapHood(hood, ItoX(iCenter), ItoY(iCenter));
    }

    /**
     * This function is very similar to the previous definition of MapHood, only it additionally takes as argument an
     * EvaluationFunctoin. this function should take as argument (i,x,y) of a location and return a boolean that decides
     * whether that location should be included as a valid one.
     */
    default public int MapHood(int[] hood, int centerX, int centerY, IndexCoords2DBool Eval) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, Xdim())) {
                if (IsWrapX()) {
                    x = Util.Wrap(x, Xdim());
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, Ydim())) {
                if (IsWrapY()) {
                    y = Util.Wrap(y, Ydim());
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
    default public int MapHood(int[] hood, int iCenter, IndexCoords2DBool Eval) {
        return MapHood(hood, ItoX(iCenter), ItoY(iCenter), Eval);
    }

    /**
     * This function takes a neighborhood centered around the origin, translates the set of coordinates to be centered
     * around a particular central location, and computes which indices the translated coordinates map to. The function
     * returns the number of valid locations it set. this function differs from HoodToIs and CoordsToIs in that it takes
     * no ret[], MapHood instead puts the result of the mapping back into the hood array.
     */
    default public int MapHood(int[] hood, int centerX, int centerY) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, Xdim())) {
                if (IsWrapX()) {
                    x = Util.Wrap(x, Xdim());
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, Ydim())) {
                if (IsWrapY()) {
                    y = Util.Wrap(y, Ydim());
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
    default public int[] BoundaryIs() {
        int[] ret = new int[(Xdim() + Ydim()) * 2];
        for (int x = 0; x < Xdim(); x++) {
            ret[x] = I(x, 0);
            ret[x + Xdim()] = I(x, Ydim() - 1);
        }
        for (int y = 0; y < Ydim(); y++) {
            ret[y + Xdim() * 2] = I(0, y);
            ret[y + Xdim() * 2 + Ydim()] = I(Xdim() - 1, y);
        }
        return ret;
    }

    /**
     * returns whether a valid index exists in the neighborhood
     */
    default public boolean ContainsValidI(int[] hood, int centerI, Coords2DBool IsValid) {
        return ContainsValidI(hood, ItoX(centerI), ItoY(centerI), IsValid);
    }

    /**
     * returns whether a valid index exists in the neighborhood
     */
    default public boolean ContainsValidI(int[] hood, int centerX, int centerY, Coords2DBool IsValid) {
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, Xdim())) {
                if (IsWrapX()) {
                    x = Util.Wrap(x, Xdim());
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, Ydim())) {
                if (IsWrapY()) {
                    y = Util.Wrap(y, Ydim());
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
    default public int MapXsq(int x, Grid2D other) {
        return (int) (((x + 0.5) * other.Xdim()) / Xdim());
    }

    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    default public int MapYsq(int y, Grid2D other) {
        return (int) (((y + 0.5) * other.Ydim()) / Ydim());
    }

    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    default public int MapI(int i, Grid2D other) {
        int x = ItoX(i);
        int y = ItoY(i);
        return other.I(MapXsq(x, other), MapYsq(y, other));
    }

    /**
     * returns the position that x rescales to in the other grid
     */
    default public double MapXpt(double x, Grid2D other) {
        return x * other.Xdim() / Xdim();
    }

    /**
     * returns the position that y rescales to in the other grid
     */
    default public double MapYpt(double y, Grid2D other) {
        return y * other.Ydim() / Ydim();
    }


    /**
     * returns the set of indicies of squares that the line between (x1,y1) and (x2,y2) touches.
     */
    default public int AlongLineIs(double x1, double y1, double x2, double y2, int[] writeHere) {
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
     * gets a random index from the full neighborhood, if the index does not map, returns -1
     */

    default public int RandomHoodI(int[]hood, int centerX, int centerY, Rand rng){
        int i=rng.Int(hood.length/3);
        return GetHoodI(hood,centerX,centerY,i);
    }


    /**
     * gets a specified index from the hood after mapping to the center position
     */
    public default int GetHoodI(int[]hood,int centerX,int centerY,int entryIndex){
        int i=entryIndex*2+hood.length/3;
        int x=hood[i]+centerX;
        int y=hood[i+1]+centerY;
        if (!Util.InDim(x, Xdim())) {
            if (IsWrapX()) {
                x = Util.Wrap(x, Xdim());
            } else {
                return -1;
            }
        }
        if (!Util.InDim(y, Ydim())) {
            if (IsWrapY()) {
                y = Util.Wrap(y, Ydim());
            } else {
                return -1;
            }
        }
        return I(x,y);
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
     * gets the displacement from the first coorinate to the second. using wraparound if allowed over the given axis to
     * find the shortest displacement.
     */
    default public double DispY(double y1, double y2) {
        if (IsWrapY()) {
            return Util.DispWrap(y2, y1, Ydim());
        } else {
            return y2 - y1;
        }
    }

    /**
     * gets the distance between two positions with or without grid wrap around (if wraparound is enabled, the shortest
     * distance taking this into account will be returned)
     */
    default public double Dist(double x1, double y1, double x2, double y2) {
        double dx = DispX(x1, x2);
        double dy = DispY(y1, y2);
        return Norm(dx, dy);
    }


    default public double Dist(int i1, int i2) {
        int x1=ItoX(i1);
        int y1=ItoY(i1);
        int x2=ItoX(i2);
        int y2=ItoY(i2);
        return Dist(x1,y1,x2,y2);
    }

    /**
     * gets the distance squared between two positions with or without grid wrap around (if wraparound is enabled, the
     * shortest distance taking this into account will be returned) more efficient than the Dist function above as it
     * skips a square-root calculation.
     */
    default public double DistSquared(double x1, double y1, double x2, double y2) {
        double xDisp = DispX(x1, x2);
        double yDisp = DispY(y1, y2);
        return NormSquared(xDisp, yDisp);

    }

    default public void ForEdge(IndexCoords2DAction Action){
        int xDim=Xdim();
        int yDim=Ydim();
        for (int x = 0; x < xDim; x++) {
            int y=0;
            Action.Action(I(x,y),x,y);
            y=yDim-1;
            Action.Action(I(x,y),x,y);
        }
        for (int y = 1; y < yDim-1; y++) {
            int x=0;
            Action.Action(I(x,y),x,y);
            x=xDim-1;
            Action.Action(I(x,y),x,y);
        }
    }

    public int Xdim();

    public int Ydim();

    public int Length();

    public boolean IsWrapX();

    public boolean IsWrapY();

}

