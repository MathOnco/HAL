package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Util;

import java.io.Serializable;

import static Framework.Util.InDim;
import static Framework.Util.NormSquared;

/**
 * holds functions that all 3D Grids share
 */
public abstract class GridBase3D implements Serializable {
    public final int xDim;
    public final int yDim;
    public final int zDim;
    public final int length;
    public boolean wrapX;
    public boolean wrapY;
    public boolean wrapZ;
    int tick;

    /**
     * gets the index of the square at the specified coordinates with wrap around
     */
    public int WrapI(int x, int y, int z) {
        //wraps Coords to proper index
        if (In(x, y,z)) {
            return I(x, y,z);
        }
        if(wrapX) {
            x=Util.Wrap(x,xDim);
        }
        if(wrapY) {
            y=Util.Wrap(y,xDim);
        }
        if(wrapZ) {
            z=Util.Wrap(z,zDim);
        }
        if(In(x,y,z)) {
            return I(x, y,z);
        }
        throw new IllegalArgumentException("cannot map to index in bounds!" );
    }


    /**
     * gets the index of the voxel at the specified coordinates
     */
    public int I(int x, int y, int z) {
        //gets typeGrid index from location
        return x * yDim * zDim + y * zDim + z;
    }

    /**
     * gets the xDim component of the voxel at the specified index
     */
    public int ItoX(int i) {
        return i / (yDim * zDim);
    }

    /**
     * gets the yDim component of the voxel at the specified index
     */
    public int ItoY(int i) {
        return (i / zDim) % yDim;
    }

    /**
     * gets the z component of the voxel at the specified index
     */
    public int ItoZ(int i) {
        return i % zDim;
    }

    /**
     * gets the index of the square that contains the specified coordinates
     */
    public int I(double x, double y, double z) {
        //gets typeGrid index from location
        return (int) Math.floor(x) * yDim * zDim + (int) Math.floor(y) * zDim + (int) Math.floor(z);
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    public boolean In(int x, int y, int z) {
        if (x >= 0 && x < xDim && y >= 0 && y < yDim && z >= 0 && z < zDim) {
            return true;
        }
        return false;
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    public boolean In(double x, double y, double z) {
        int xInt = (int) Math.floor(x);
        int yInt = (int) Math.floor(y);
        int zInt = (int) Math.floor(z);
        return In(xInt, yInt, zInt);
    }
    /**
     * returns whether the specified coordinates are inside the Grid bounds with wraparound
     */
    public boolean InWrap(int x,int y,int z) {
        if (wrapX || InDim(x, xDim) && (wrapY || InDim(y, yDim))&& (wrapZ || InDim(z, zDim))) {
            return true;
        }
        return false;
    }
    /**
     * returns whether the specified coordinates are inside the Grid bounds with wraparound
     */
    public boolean InWrap(double x,double y,double z) {
        if (wrapX || InDim(x, xDim) && (wrapY || InDim(y, yDim))&& (wrapZ || InDim(z, zDim))) {
            return true;
        }
        return false;
    }
    /**
     * This function is very similar to the previous definition of MapHood, only it additionally takes as argument an
     * EvaluationFunctoin. this function should take as argument (i,x,y) of a location and return a boolean that decides
     * whether that location should be included as a valid one.
     */
    public int MapHood(int[] hood, int centerX, int centerY, int centerZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 4;
        for (int i = iStart; i < hood.length; i += 3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
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
            if (!Util.InDim(z, zDim)) {
                if (wrapZ) {
                    z = Util.Wrap(z, zDim);
                } else {
                    continue;
                }
            }
            hood[ptCt] = I(x, y, z);
            ptCt++;
        }
        return ptCt;
    }

    /**
     * This function is very similar to the previous definition of MapHood, only it additionally takes as argument an
     * EvaluationFunctoin. this function should take as argument (i,x,y) of a location and return a boolean that decides
     * whether that location should be included as a valid one.
     */
    public int MapHood(int[] hood, int centerX, int centerY, int centerZ, IndexCoords3DBool Eval) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 4;
        for (int i = iStart; i < hood.length; i += 3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
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
            if (!Util.InDim(z, zDim)) {
                if (wrapZ) {
                    z = Util.Wrap(z, zDim);
                } else {
                    continue;
                }
            }
            int j = I(x, y, z);
            if (Eval.Eval(j, x, y, z)) {
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
    public int MapHood(int[] hood, int centerI) {
        return MapHood(hood, ItoX(centerI), ItoY(centerI), ItoZ(centerI));
    }

    /**
     * This function takes a neighborhood centered around the origin, translates the set of coordinates to be centered
     * around a particular central location, and computes which indices the translated coordinates map to. The function
     * returns the number of valid locations it set. this function differs from HoodToIs and CoordsToIs in that it takes
     * no ret[], MapHood instead puts the result of the mapping back into the hood array.
     */
    public int MapHood(int[] hood, int centerI, IndexCoords3DBool Eval) {
        return MapHood(hood, ItoX(centerI), ItoY(centerI), ItoZ(centerI), Eval);
    }

    /**
     * returns a list of indices, where each index maps to one square on the boundary of the grid
     */
    public int[] BoundaryIs() {
        int[] ret = new int[(xDim * yDim + xDim * zDim + yDim * zDim) * 2];
        int side1 = xDim * yDim;
        int side2 = xDim * zDim;
        int side3 = yDim * zDim;
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                ret[x * yDim + y] = I(x, y, 0);
                ret[x * yDim + y + side1] = I(x, y, zDim - 1);
            }
        }
        for (int x = 0; x < xDim; x++) {
            for (int z = 0; z < zDim; z++) {
                ret[x * zDim + z + side1 * 2] = I(x, 0, z);
                ret[x * zDim + z + side1 * 2 + side2] = I(x, yDim - 1, z);
            }
        }
        for (int y = 0; y < yDim; y++) {
            for (int z = 0; z < zDim; z++) {
                ret[y * zDim + z + (side1 + side2) * 2] = I(0, y, z);
                ret[y * zDim + z + (side1 + side2) * 2 + side3] = I(xDim - 1, y, z);
            }
        }
        return ret;
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
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    public int ConvXsq(int x, GridBase3D other) {
        return (int) (((x + 0.5) * other.xDim) / xDim);
    }

    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    public int ConvYsq(int y, GridBase3D other) {
        return (int) (((y + 0.5) * other.yDim) / yDim);
    }

    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    public int ConvZsq(int z, GridBase3D other) {
        return (int) (((z + 0.5) * other.zDim) / zDim);
    }

    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    public int ConvI(int i, GridBase3D other) {
        int x = ItoX(i);
        int y = ItoY(i);
        int z = ItoZ(i);
        return other.I(ConvXsq(x, other), ConvYsq(y, other), ConvZsq(z, other));
    }

    /**
     * returns the position that x rescales to in the other grid
     */
    public double ConvXpt(double x, GridBase3D other) {
        return x * other.xDim / xDim;
    }

    /**
     * returns the position that y rescales to in the other grid
     */
    public double ConvYpt(double y, GridBase3D other) {
        return y * other.yDim / yDim;
    }

    /**
     * returns the position that z rescales to in the other grid
     */
    public double ConvZpt(double z, GridBase3D other) {
        return z * other.zDim / zDim;
    }

    /**
     * applies the action function to all positions in the rectangle, will use wraparound if appropriate
     */
    public void ApplyRectangle(int startX, int startY, int startZ, int width, int height, int depth, Coords3DAction Action) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                for (int z = startZ; z < startY + depth; z++) {
                    int xFinal = x;
                    int yFinal = y;
                    int zFinal = z;
                    if (wrapX) {
                        xFinal = Util.Wrap(x, xDim);
                    }
                    if (wrapY) {
                        yFinal = Util.Wrap(y, yDim);
                    }
                    if (wrapZ) {
                        zFinal = Util.Wrap(z, zDim);
                    }
                    Action.Action(xFinal, yFinal, zFinal);
                }
            }
        }
    }

    /**
     * applies the action function to all positions in the neighborhood
     */
    int ApplyHood(int[] hood, int centerI, Coords3DAction Action) {
        return ApplyHood(hood, ItoX(centerI), ItoY(centerI), ItoZ(centerI), Action);
    }

    /**
     * applies the action function to all positions in the neighborhood
     */
    int ApplyHood(int[] hood, int centerX, int centerY, int centerZ, Coords3DAction Action) {
        int ptCt = 0;
        int iStart = hood.length / 4;
        for (int i = iStart; i < hood.length; i += 3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
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
            if (!Util.InDim(z, zDim)) {
                if (wrapZ) {
                    z = Util.Wrap(z, zDim);
                } else {
                    continue;
                }
            }
            Action.Action(x, y, z);
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
     * returns whether a valid index exists in the neighborhood
     */
    public boolean ContainsValidI(int[] hood, int centerI, Coords3DBool IsValid) {
        return ContainsValidI(hood, ItoX(centerI), ItoY(centerI), ItoZ(centerI), IsValid);
    }

    /**
     * returns whether a valid index exists in the neighborhood
     */
    public boolean ContainsValidI(int[] hood, int centerX, int centerY, int centerZ, Coords3DBool IsValid) {
        int iStart = hood.length / 4;
        for (int i = iStart; i < hood.length; i += 3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
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
            if (!Util.InDim(z, zDim)) {
                if (wrapZ) {
                    z = Util.Wrap(z, zDim);
                } else {
                    continue;
                }
            }
            if (IsValid.Eval(x, y, z)) {
                return true;
            }
        }
        return false;
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
     * gets the displacement from the first coorinate to the second. using wraparound if allowed over the given axis to
     * find the shortest displacement.
     */
    public double DispZ(double z1, double z2) {
        if (wrapY) {
            return Util.DispWrap(z2, z1, zDim);
        } else {
            return z2 - z1;
        }
    }

    /**
     * gets the distance between two positions with or without grid wrap around (if wraparound is enabled, the shortest
     * distance taking this into account will be returned)
     */
    public double Dist(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(DistSquared(x1, y1, z1, x2, y2, z2));
    }

    /**
     * gets the distance squared between two positions with or without grid wrap around (if wraparound is enabled, the
     * shortest distance taking this into account will be returned) more efficient than the Dist function above as it
     * skips a square-root calculation.
     */
    public double DistSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        double xDisp = DispX(x1, x2);
        double yDisp = DispY(y1, y2);
        double zDisp = DispZ(z1, z2);
        return NormSquared(xDisp, yDisp, zDisp);

    }

    public GridBase3D(int x, int y, int z, boolean wrapX, boolean wrapY, boolean wrapZ) {
        xDim = x;
        yDim = y;
        zDim = z;
        length = x * y * z;
        this.wrapX = wrapX;
        this.wrapY = wrapY;
        this.wrapZ = wrapZ;
    }

}

