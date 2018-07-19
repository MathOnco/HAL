package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Util;

/**
 * Created by bravorr on 5/17/17.
 */
public abstract class GridBase2D{
    public final int xDim;
    public final int yDim;
    public final int length;
    public boolean wrapX;
    public boolean wrapY;
    int tick;

    public GridBase2D(int xDim, int yDim, boolean wrapX, boolean wrapY) {
        this.xDim = xDim;
        this.yDim = yDim;
        this.wrapX = wrapX;
        this.wrapY = wrapY;
        this.length = xDim * yDim;
    }

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
        return I(Util.ModWrap(x, xDim), Util.ModWrap(y, yDim));
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
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    public boolean In(double x, double y) {
        int xInt = (int) Math.floor(x);
        int yInt = (int) Math.floor(y);
        return In(xInt, yInt);
    }


    public void ApplyRectangle(int startX, int startY, int width, int height, Coords2DAction Action) {
        for (int x = startX; x < startX+width; x++) {
            for (int y = startY; y < startY+height; y++) {
                int xFinal=x;
                int yFinal=y;
                if(wrapX){
                    xFinal=Util.ModWrap(x,xDim);
                }
                if(wrapY){
                    yFinal=Util.ModWrap(y,yDim);
                }
                Action.Action(xFinal, yFinal);
            }
        }
    }

    int ApplyHood(int[] hood, int centerI, Coords2DAction Action){
        return ApplyHood(hood,ItoX(centerI),ItoY(centerI),Action);
    }
    int ApplyHood(int[] hood, int centerX, int centerY, Coords2DAction Action){
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
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
            Action.Action(x, y);
        }
        return ptCt;
    }

    public int MapHood(int[] hood, int iCenter) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        return MapHood(hood, ItoX(iCenter), ItoY(iCenter));
    }
    public int MapHood(int[] hood, int centerX,int centerY,IndexCoords2DBool Eval){
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
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
            int j=I(x,y);
            if(Eval.Eval(j,x,y)) {
                hood[ptCt] = j;
                ptCt++;
            }
        }
        return ptCt;
    }
    public int MapHood(int[] hood, int iCenter,IndexCoords2DBool Eval){
        return MapHood(hood,ItoX(iCenter),ItoY(iCenter),Eval);
    }

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
            hood[ptCt] = I(x, y);
            ptCt++;
        }
        return ptCt;
    }

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

    public boolean ContainsValidI(int[] hood, int centerI, Coords2DBool IsValid) {
        return ContainsValidI(hood, ItoX(centerI), ItoY(centerI), IsValid);
    }

    public boolean ContainsValidI(int[] hood, int centerX, int centerY, Coords2DBool IsValid) {
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
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
            if(IsValid.Eval(x,y)){
                return true;
            }
        }
        return false;
    }


    public int ConvXsq(int x, GridBase2D other) {
        return (int)(((x+0.5) * other.xDim) / xDim);
    }

    public int ConvYsq(int y, GridBase2D other) {
        return (int)(((y+0.5) * other.yDim) / yDim);
    }

    public int ConvI(int i, GridBase2D other) {
        int x = ItoX(i);
        int y = ItoY(i);
        return other.I(ConvXsq(x, other), ConvYsq(y, other));
    }

    public double ConvXpt(double x, GridBase2D other) {
        return x * other.xDim / xDim;
    }

    public double ConvYpt(double y, GridBase2D other) {
        return y * other.yDim / yDim;
    }
    public void IncTick(){
        tick++;
    }
    public int GetTick(){
        return tick;
    }

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
    public double Dist(double x1,double y1,double x2,double y2){
        return Math.sqrt(DistSquared(x1,y1,x2,y2));
    }
    public double DistSquared(double x1,double y1,double x2,double y2){
        double xDisp=DispX(x1,x2);
        double yDisp=DispY(y1,y2);
        return xDisp*xDisp+yDisp*yDisp;

    }

    public void ResetTick(){
        tick=0;
    }

}

