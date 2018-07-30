package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Util;

import java.util.Iterator;

/**
 * Created by bravorr on 5/17/17.
 */
public abstract class GridBase1D {
    public final int xDim;
    public final int length;
    public boolean wrapX;
    int tick;

    public GridBase1D(int xDim, boolean wrapX) {
        this.xDim = xDim;
        this.wrapX = wrapX;
        this.length = xDim;
    }

    /**
     * gets the index of the square at the specified coordinates with wrap around
     */
    public int WrapI(int x) {
        //wraps Coords to proper index
        if (In(x)) {
            return x;
        }
        return Util.ModWrap(x, xDim);
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
//                    retX=Util.ModWrap(retX,xDim);
//                }
//                if(wrapY&&!inY){
//                    retY=Util.ModWrap(retY,yDim);
//                }
//                ret[retCt]=I(retX,retY);
//                retCt++;
//            }
//        }
//        return retCt;
//    }

    public double DispX(double x1,double x2){
        if(wrapX){
            return Util.DispWrap(x1,x2,xDim);
        }
        else{
            return x2-x1;
        }
    }
    public double Dist(double x1,double x2){
        return Math.abs(x1-x2);
    }

    public void ApplyRectangle(int startX, int width, IndexAction Action) {
        for (int x = startX; x < startX+width; x++) {
            int xFinal=x;
            if(wrapX){
                xFinal=Util.ModWrap(x,xDim);
            }
                Action.Action(xFinal);
        }
    }

    int ApplyHood(int[] hood, int centerX,IndexAction Action) {
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i ++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            Action.Action(x);
        }
        return ptCt;
    }
    public int MapHood(int[] hood, int centerX,Coords1DBool Eval){
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i ++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if(Eval.Eval(x)) {
                hood[ptCt] = x;
                ptCt++;
            }
        }
        return ptCt;
    }
    public int MapHood(int[] hood, int centerX) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i ++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            hood[ptCt] = x;
            ptCt++;
        }
        return ptCt;
    }

    public boolean ContainsValidI(int[] hood, int centerX, IndexBool IsValid) {
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i ++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if(IsValid.Eval(x)){
                return true;
            }
        }
        return false;
    }


    public int ConvXsq(int x, GridBase1D other) {
        return (int)(((x+0.5) * other.xDim) / xDim);
    }

    public double ConvXpt(double x, GridBase1D other) {
        return x * other.xDim / xDim;
    }
    public void IncTick(){
        tick++;
    }
    public int GetTick(){
        return tick;
    }
    public void ResetTick(){tick=0;}

}

