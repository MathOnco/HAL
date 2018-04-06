package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Rand;
import Framework.Util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by bravorr on 5/17/17.
 */
public class GridBase2D{
    public final int xDim;
    public final int yDim;
    public final int length;
    public boolean wrapX;
    public boolean wrapY;
    public ArrayList<int[]> usedHoodIs = new ArrayList<>();
    ArrayList<IsIterator> usedIterIs = new ArrayList<>();

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

    /**
     * writes into ret the coordinates of SQs with the centerX and centerY coordinates added
     * returns the number of squares that were written into ret. any coordinates that fall out of bounds are not written
     *
     * @param hood    list of coordinates of the form [xDim,yDim,xDim,yDim,...]
     * @param ret     list into which the displaced coordinates will be written
     * @param centerX xDim displacement of coordinates
     * @param centerY yDim displacement of coordinates
     * @param wrapX   whether to wrap the coordinates that fall out of bounds in the X direction
     * @param wrapY   whether to wrap the coordinates that fall out of bounds in the Y direction
     * @return the number of coordinates written into the ret array
     */
    public int HoodToIs(int[] hood, int[] ret, int centerX, int centerY, boolean wrapX, boolean wrapY) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
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
            ret[ptCt] = I(x, y);
            ptCt++;
        }
        return ptCt;
    }

    public int HoodToEvalIs(int[] hood, int[] ret, int centerX, int centerY, IndexToBool Eval, boolean wrapX, boolean wrapY) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
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
            int j = I(x, y);
            if (Eval.Eval(j)) {
                ret[ptCt] = j;
                ptCt++;
            }
        }
        return ptCt;
    }

    int[] GetFreshHoodIs(int hoodLen) {
        int[] Is;
        int nFound;
        if (usedHoodIs.size() == 0) {
            Is = new int[hoodLen];
        } else {
            Is = usedHoodIs.remove(usedHoodIs.size() - 1);
            if (Is.length < hoodLen / 2) {
                Is = new int[hoodLen];
            }
        }
        return Is;
    }

    public void ApplyRectangle(CoordsIAction Action) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Action.Action(x, y, I(x, y));
            }
        }
    }

    public void ApplyRectangle(int startX, int startY, int width, int height, CoordsIAction Action) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Action.Action(x, y, I(x, y));
            }
        }
    }

    public int IndexFromHood(int[] hood, int centerI, Rand rn, IndexToBool IsValid) {
        return IndexFromHood(hood, ItoX(centerI), ItoY(centerI), rn, IsValid);
    }

    public int IndexFromHood(int[] hood, int centerX, int centerY, Rand rn, IndexToBool IsValid) {
        int[] inds = GetFreshHoodIs(hood.length);
        int nOptions = HoodToEvalIs(hood, inds, centerX, centerY, IsValid, wrapX, wrapY);
        if (nOptions == 0) {
            return -1;
        }
        if (nOptions == 1) {
            return inds[0];
        } else {
            return inds[rn.Int(nOptions)];
        }
    }

    public int IndexFromHood(int[] hood, int centerI, Rand rn, IndexToBool IsValid, IntToBool ValidCount) {
        return IndexFromHood(hood, ItoX(centerI), ItoY(centerI), rn, IsValid, ValidCount);
    }

    public int IndexFromHood(int[] hood, int centerX, int centerY, Rand rn, IndexToBool IsValid, IntToBool ValidCount) {
        int[] inds = GetFreshHoodIs(hood.length);
        int nOptions = HoodToEvalIs(hood, inds, centerX, centerY, IsValid, wrapX, wrapY);
        if (!ValidCount.Eval(nOptions) || nOptions == 0) {
            return -1;
        }
        if (nOptions == 1) {
            return inds[0];
        } else {
            return inds[rn.Int(nOptions)];
        }
    }

    public int IndexFromHood(int[] hood, int centerI, Rand rn) {
        return IndexFromHood(hood, ItoX(centerI), ItoY(centerI), rn);
    }

    public int IndexFromHood(int[] hood, int centerX, int centerY, Rand rn) {
        int[] inds = GetFreshHoodIs(hood.length);
        int nOptions = HoodToIs(hood, inds, centerX, centerY);
        if (nOptions == 0) {
            return -1;
        }
        if (nOptions == 1) {
            return inds[0];
        } else {
            return inds[rn.Int(nOptions)];
        }
    }

    public int ApplyHood(int[] hood, int centerI, IndexAction Action) {
        return ApplyHood(-1, hood, ItoX(centerI), ItoY(centerI), null, null, Action, null, wrapX, wrapY);
    }

    public int ApplyHood(int[] hood, int centerX, int centerY, IndexAction Action) {
        return ApplyHood(-1, hood, centerX, centerY, null, null, Action, null, wrapX, wrapY);
    }

    public int ApplyHood(int[] hood, int centerI, IndexToBool IsValidIndex, IndexAction Action) {
        return ApplyHood(-1, hood, ItoX(centerI), ItoY(centerI), null, IsValidIndex, Action, null, wrapX, wrapY);
    }

    public int ApplyHood(int[] hood, int centerX, int centerY, IndexToBool IsValidIndex, IndexAction Action) {
        return ApplyHood(-1, hood, centerX, centerY, null, IsValidIndex, Action, null, wrapX, wrapY);
    }

    public int ApplyHood(int nActions, int[] hood, int centerI, Rand rn, IndexAction Action) {
        return ApplyHood(nActions, hood, ItoX(centerI), ItoY(centerI), rn, null, Action, null, wrapX, wrapY);
    }

    public int ApplyHood(int nActions, int[] hood, int centerX, int centerY, Rand rn, IndexAction Action) {
        return ApplyHood(nActions, hood, centerX, centerY, rn, null, Action, null, wrapX, wrapY);
    }

    public int ApplyHood(int nActions, int[] hood, int centerI, Rand rn, IndexToBool IsValidIndex, IndexAction Action) {
        return ApplyHood(nActions, hood, ItoX(centerI), ItoY(centerI), rn, IsValidIndex, Action, null, wrapX, wrapY);
    }

    public int ApplyHood(int nActions, int[] hood, int centerX, int centerY, Rand rn, IndexToBool IsValidIndex, IndexAction Action) {
        return ApplyHood(nActions, hood, centerX, centerY, rn, IsValidIndex, Action, null, wrapX, wrapY);
    }

    public int ApplyHood(int nActions, int[] hood, int centerI, Rand rn, IndexToBool IsValidIndex, IndexAction Action, boolean wrapX, boolean wrapY) {
        return ApplyHood(nActions, hood, ItoX(centerI), ItoY(centerI), rn, IsValidIndex, Action, null, wrapX, wrapY);
    }

    public int ApplyHood(int nActions, int[] hood, int centerX, int centerY, Rand rn, IndexToBool IsValidIndex, IndexAction Action, boolean wrapX, boolean wrapY) {
        return ApplyHood(nActions, hood, centerX, centerY, rn, IsValidIndex, Action, null, wrapX, wrapY);
    }

    public int ApplyHood(int[] hood, int centerI, Rand rn, IntToInt GetNumActions, IndexAction Action) {
        return ApplyHood(-1, hood, ItoX(centerI), ItoY(centerI), rn, null, Action, GetNumActions, wrapX, wrapY);
    }

    public int ApplyHood(int[] hood, int centerX, int centerY, Rand rn, IntToInt GetNumActions, IndexAction Action) {
        return ApplyHood(-1, hood, centerX, centerY, rn, null, Action, GetNumActions, wrapX, wrapY);
    }

    public int ApplyHood(int[] hood, int centerI, Rand rn, IntToInt GetNumActions, IndexToBool IsValidIndex, IndexAction Action) {
        return ApplyHood(-1, hood, ItoX(centerI), ItoY(centerI), rn, IsValidIndex, Action, GetNumActions, wrapX, wrapY);
    }

    public int ApplyHood(int[] hood, int centerX, int centerY, Rand rn, IntToInt GetNumActions, IndexToBool IsValidIndex, IndexAction Action) {
        return ApplyHood(-1, hood, centerX, centerY, rn, IsValidIndex, Action, GetNumActions, wrapX, wrapY);
    }

    public int ApplyHood(int[] hood, int centerI, Rand rn, IntToInt GetNumActions, IndexToBool IsValidIndex, IndexAction Action, boolean wrapX, boolean wrapY) {
        return ApplyHood(-1, hood, ItoX(centerI), ItoY(centerI), rn, IsValidIndex, Action, GetNumActions, wrapX, wrapY);
    }

    public int ApplyHood(int[] hood, int centerX, int centerY, Rand rn, IntToInt GetNumActions, IndexToBool IsValidIndex, IndexAction Action, boolean wrapX, boolean wrapY) {
        return ApplyHood(-1, hood, centerX, centerY, rn, IsValidIndex, Action, GetNumActions, wrapX, wrapY);
    }

    int ApplyHood(int nActions, int[] hood, int centerX, int centerY, Rand rn, IndexToBool IsValidIndex, IndexAction Action, IntToInt GetNumActions, boolean wrapX, boolean wrapY) {
        int nFound;
        int[] Is = GetFreshHoodIs(hood.length);
        if (IsValidIndex != null) {
            nFound = HoodToEvalIs(hood, Is, centerX, centerY, IsValidIndex, wrapX, wrapY);
        } else {
            nFound = HoodToIs(hood, Is, centerX, centerY, wrapX, wrapY);
        }
        if (GetNumActions != null) {
            nActions = GetNumActions.Eval(nFound);
            nActions = nActions < 0 ? 0 : nActions;
        }
        if (nActions < 0 || nFound <= nActions) {
            for (int i = 0; i < nFound; i++) {
                Action.Action(Is[i], nFound);
            }
        } else {
            for (int i = 0; i < nActions; i++) {
                int iRand = rn.Int(nFound - i);
                Action.Action(Is[iRand], nFound);
                Is[iRand] = Is[nFound - i - 1];
            }
        }
        usedHoodIs.add(Is);
        return nFound;
    }
//    public int HoodValidAction(int[]hood, int centerX, int centerY, IndexToBool ValidPosition, IndexAndCountAction Action, boolean wrapX, boolean wrapY){
//        if(actionIs.length<hood.length/2){
//            actionIs=new int[hood.length/2];
//        }
//        int ptCt=0;
//        for(int i=0;i<hood.length/2;i++) {
//            int x = hood[i * 2] + centerX;
//            int y = hood[i * 2 + 1] + centerY;
//            if (!Util.InDim(xDim, x)) {
//                if (wrapX) {
//                    x = Util.ModWrap(x, xDim);
//                } else {
//                    continue;
//                }
//            }
//            if (!Util.InDim(yDim, y)) {
//                if (wrapY) {
//                    y = Util.ModWrap(y, yDim);
//                } else {
//                    continue;
//                }
//            }
//            int j= I(x,y);
//            if(ValidPosition==null||ValidPosition.Eval(j)) {
//                this.actionIs[ptCt] = j;
//                ptCt++;
//            }
//        }
//        for (int i = 0; i < ptCt; i++) {
//            Action.Action(this.actionIs[i],ptCt);
//        }
//        return ptCt;
//    }
//    public int HoodValidAction(int[]hood, int centerI, IndexToBool ValidPosition, IndexAndCountAction Action, boolean wrapX, boolean wrapY){
//        return HoodValidAction(hood,ItoX(centerI),ItoY(centerI),ValidPosition,Action,wrapX,wrapY);
//    }
//    public int HoodValidAction(int[]hood,int centerI,IndexToBool ValidPosition,IndexAndCountAction Action){
//        return HoodValidAction(hood,ItoX(centerI),ItoY(centerI),ValidPosition,Action,wrapX,wrapY);
//    }
//    public int HoodValidAction(int[]hood,int centerX,int centerY,IndexToBool ValidPosition,IndexAndCountAction Action){
//        return HoodValidAction(hood,centerX,centerY,ValidPosition,Action,wrapX,wrapY);
//    }
//    public int HoodSingleAction(int[]hood, int centerX, int centerY, Rand rn, IndexToBool ValidPosition, IndexAndCountAction Action, boolean wrapX, boolean wrapY){
//        if(actionIs.length<hood.length/2){
//            actionIs=new int[hood.length/2];
//        }
//        int ptCt=0;
//        for(int i=0;i<hood.length/2;i++) {
//            int x = hood[i * 2] + centerX;
//            int y = hood[i * 2 + 1] + centerY;
//            if (!Util.InDim(xDim, x)) {
//                if (wrapX) {
//                    x = Util.ModWrap(x, xDim);
//                } else {
//                    continue;
//                }
//            }
//            if (!Util.InDim(yDim, y)) {
//                if (wrapY) {
//                    y = Util.ModWrap(y, yDim);
//                } else {
//                    continue;
//                }
//            }
//            int j= I(x,y);
//            if(ValidPosition==null||ValidPosition.Eval(j)) {
//                this.actionIs[ptCt] = j;
//                ptCt++;
//            }
//        }
//        if(ptCt==0){
//            return ptCt;
//        }
//        if(ptCt==1){
//            Action.Action(this.actionIs[0],ptCt);
//        }
//        else{
//            Action.Action(this.actionIs[rn.Int(ptCt)],ptCt);
//        }
//        return ptCt;
//    }
//    public int HoodSingleAction(int[]hood, int centerI, Rand rn, IndexToBool ValidPosition, IndexAndCountAction Action, boolean wrapX, boolean wrapY) {
//        return HoodSingleAction(hood,ItoX(centerI),ItoY(centerI),rn, ValidPosition, Action,wrapX,wrapY);
//    }
//    public int HoodSingleAction(int[]hood, int centerI,Rand rn, IndexToBool ValidPosition, IndexAndCountAction Action) {
//        return HoodSingleAction(hood,ItoX(centerI),ItoY(centerI),rn, ValidPosition, Action,wrapX,wrapY);
//    }
//    public int HoodSingleAction(int[]hood, int centerX,int centerY,Rand rn, IndexToBool ValidPosition, IndexAndCountAction Action) {
//        return HoodSingleAction(hood,centerX,centerY,rn, ValidPosition, Action,wrapX,wrapY);
//    }

    //    public int HoodAction(int[] hood, int centerX,int centerY, LocalIndexAction Action,boolean wrapX,boolean wrapY){
//        //moves coordinates to be around origin
//        //if any of the coordinates are outside the bounds, they will not be added
//        if(actionIs.length<hood.length/2){
//            actionIs=new int[hood.length/2];
//        }
//        int ptCt=0;
//        for(int i=0;i<hood.length/2;i++) {
//            int x = hood[i * 2] + centerX;
//            int y = hood[i * 2 + 1] + centerY;
//            if (!Util.InDim(xDim, x)) {
//                if (wrapX) {
//                    x = Util.ModWrap(x, xDim);
//                } else {
//                    continue;
//                }
//            }
//            if (!Util.InDim(yDim, y)) {
//                if (wrapY) {
//                    y = Util.ModWrap(y, yDim);
//                } else {
//                    continue;
//                }
//            }
//            Action.Action(I(x,y));
//            ptCt++;
//        }
//        return ptCt;
//    }
//    public int HoodAction(int[] hood, int centerI, LocalIndexAction Action,boolean wrapX,boolean wrapY){
//        return HoodAction(hood,ItoX(centerI),ItoY(centerI),Action,wrapX,wrapY);
//    }
//    public int HoodAction(int[] hood, int centerI, LocalIndexAction Action){
//        return HoodAction(hood,ItoX(centerI),ItoY(centerI),Action,wrapX,wrapY);
//    }
//    public int HoodAction(int[] hood, int centerX,int centerY, LocalIndexAction Action){
//        return HoodAction(hood,centerX,centerY,Action,wrapX,wrapY);
//    }
    public int HoodToIs(int[] hood, int[] ret, int centerX, int centerY) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
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
            ret[ptCt] = I(x, y);
            ptCt++;
        }
        return ptCt;
    }

    public int HoodToIs(int[] hood, int[] ret, int iCenter) {
        return HoodToIs(hood, ret, ItoX(iCenter), ItoY(iCenter));
    }

    public int MapHood(int[] hood, int iCenter) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        return MapHood(hood, ItoX(iCenter), ItoY(iCenter));
    }
    public int MapHood(int[] hood, int centerX,int centerY,Coords2DToBool Eval){
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
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
            int j=I(x,y);
            if(Eval.Eval(j,x,y)) {
                hood[ptCt] = j;
                ptCt++;
            }
        }
        return ptCt;
    }
    public int MapHood(int[] hood, int iCenter,Coords2DToBool Eval){
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
            hood[ptCt] = I(x, y);
            ptCt++;
        }
        return ptCt;
    }

    public double DistSquared(double x1, double y1, double x2, double y2, boolean wrapX, boolean wrapY) {
        return Util.DistSquared(x1, y1, x2, y2, xDim, yDim, wrapX, wrapY);
    }

    public double DistSquared(double x1, double y1, double x2, double y2) {
        return Util.DistSquared(x1, y1, x2, y2, xDim, yDim, wrapX, wrapY);
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

    public boolean ContainsValidI(int[] hood, int centerI, IndexToBool IsValid) {
        return ContainsValidI(hood, ItoX(centerI), ItoY(centerI), IsValid);
    }

    public boolean ContainsValidI(int[] hood, int centerX, int centerY, IndexToBool IsValid) {
        int[] is = GetFreshHoodIs(hood.length);
        int ct = HoodToIs(hood, is, centerX, centerY);
        int ret = 0;
        for (int i = 0; i < ct; i++) {
            if (IsValid.Eval(is[i])) {
                return true;
            }
        }
        return false;
    }

    public int HoodCount(int[] hood, int centerI, IndexToBool IsValid) {
        return HoodCount(hood, centerI, IsValid);
    }

    public int HoodCount(int[] hood, int centerX, int centerY, IndexToBool IsValid) {
        int[] is = GetFreshHoodIs(hood.length);
        int ct = HoodToIs(hood, is, centerX, centerY);
        int ret = 0;
        for (int i = 0; i < ct; i++) {
            if (IsValid.Eval(is[i])) {
                ret++;
            }
        }
        return ret;
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


    private class IsIterator implements Iterator<Integer>, Iterable<Integer> {
        final GridBase2D myGrid;
        int[] myIs;
        int numIs;
        int iCount;

        IsIterator(GridBase2D grid) {
            myGrid = grid;
        }

        public void Setup(GridBase2D grid, int[] hood, int centerX, int centerY) {
            iCount = 0;
            myIs = myGrid.GetFreshHoodIs(hood.length);
            numIs = myGrid.HoodToIs(hood, myIs, centerX, centerY);

        }

        @Override
        public boolean hasNext() {
            if (iCount == numIs) {
                myGrid.usedHoodIs.add(myIs);
                return false;
            }
            return true;
        }

        @Override
        public Integer next() {
            int ret = myIs[iCount];
            iCount++;
            return ret;
        }

        @Override
        public Iterator<Integer> iterator() {
            return this;
        }
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
}

