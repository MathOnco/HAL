package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Rand;
import Framework.Util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by bravorr on 5/17/17.
 */
public class GridBase1D {
    public final int xDim;
    public final int length;
    public boolean wrapX;
    int tick;
    public ArrayList<int[]> usedHoodIs = new ArrayList<>();
    ArrayList<IsIterator> usedIterIs = new ArrayList<>();

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

    /**
     * writes into ret the coordinates of SQs with the centerX and centerY coordinates added
     * returns the number of squares that were written into ret. any coordinates that fall out of bounds are not written
     *
     * @param hood    list of coordinates of the form [xDim,yDim,xDim,yDim,...]
     * @param ret     list into which the displaced coordinates will be written
     * @param centerX xDim displacement of coordinates
     * @param wrapX   whether to wrap the coordinates that fall out of bounds in the X direction
     * @return the number of coordinates written into the ret array
     */
    public int HoodToIs(int[] hood, int[] ret, int centerX, boolean wrapX) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i ++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(xDim, x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            ret[ptCt] = x;
            ptCt++;
        }
        return ptCt;
    }
    public double Dist(double x1,double x2,boolean wrapX){
        if(wrapX){
            return Util.DistWrap(x1,x2,xDim);
        }
        else{
            return Math.abs(x1-x2);
        }
    }

    public int HoodToEvalIs(int[] hood, int[] ret, int centerX, IndexToBool Eval, boolean wrapX) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            if (!Util.InDim(xDim, x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (Eval.Eval(x)) {
                ret[ptCt] = x;
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

    public void ApplyRectangle(Coords1DAction Action) {
        for (int x = 0; x < xDim; x++) {
                Action.Action(x);
        }
    }

    public void ApplyRectangle(int startX, int width, Coords1DAction Action) {
        for (int x = 0; x < width; x++) {
                Action.Action(x);
        }
    }

    public int IndexFromHood(int[] hood, int centerX, Rand rn, IndexToBool IsValid) {
        int[] inds = GetFreshHoodIs(hood.length);
        int nOptions = HoodToEvalIs(hood, inds, centerX, IsValid, wrapX);
        if (nOptions == 0) {
            return -1;
        }
        if (nOptions == 1) {
            return inds[0];
        } else {
            return inds[rn.Int(nOptions)];
        }
    }

    public int IndexFromHood(int[] hood, int centerX, Rand rn, IndexToBool IsValid, IntToBool ValidCount) {
        int[] inds = GetFreshHoodIs(hood.length);
        int nOptions = HoodToEvalIs(hood, inds, centerX, IsValid, wrapX);
        if (!ValidCount.Eval(nOptions) || nOptions == 0) {
            return -1;
        }
        if (nOptions == 1) {
            return inds[0];
        } else {
            return inds[rn.Int(nOptions)];
        }
    }

    public int IndexFromHood(int[] hood, int centerX, Rand rn) {
        int[] inds = GetFreshHoodIs(hood.length);
        int nOptions = HoodToIs(hood, inds, centerX);
        if (nOptions == 0) {
            return -1;
        }
        if (nOptions == 1) {
            return inds[0];
        } else {
            return inds[rn.Int(nOptions)];
        }
    }

    public int ApplyHood(int[] hood, int centerX, IndexAction Action) {
        return ApplyHood(-1, hood, centerX, null, null, Action, null, wrapX);
    }

    public int ApplyHood(int[] hood, int centerX, int centerY, IndexToBool IsValidIndex, IndexAction Action) {
        return ApplyHood(-1, hood, centerX, null, IsValidIndex, Action, null, wrapX);
    }

    public int ApplyHood(int nActions, int[] hood, int centerX, int centerY, Rand rn, IndexAction Action) {
        return ApplyHood(nActions, hood, centerX, rn, null, Action, null, wrapX);
    }

    public int ApplyHood(int nActions, int[] hood, int centerX, int centerY, Rand rn, IndexToBool IsValidIndex, IndexAction Action) {
        return ApplyHood(nActions, hood, centerX, rn, IsValidIndex, Action, null, wrapX);
    }

    public int ApplyHood(int nActions, int[] hood, int centerX, int centerY, Rand rn, IndexToBool IsValidIndex, IndexAction Action, boolean wrapX) {
        return ApplyHood(nActions, hood, centerX, rn, IsValidIndex, Action, null, wrapX);
    }

    public int ApplyHood(int[] hood, int centerX, int centerY, Rand rn, IntToInt GetNumActions, IndexAction Action) {
        return ApplyHood(-1, hood, centerX, rn, null, Action, GetNumActions, wrapX);
    }

    public int ApplyHood(int[] hood, int centerX, int centerY, Rand rn, IntToInt GetNumActions, IndexToBool IsValidIndex, IndexAction Action) {
        return ApplyHood(-1, hood, centerX, rn, IsValidIndex, Action, GetNumActions, wrapX);
    }


    public int ApplyHood(int[] hood, int centerX, Rand rn, IntToInt GetNumActions, IndexToBool IsValidIndex, IndexAction Action, boolean wrapX) {
        return ApplyHood(-1, hood, centerX, rn, IsValidIndex, Action, GetNumActions, wrapX);
    }

    int ApplyHood(int nActions, int[] hood, int centerX, Rand rn, IndexToBool IsValidIndex, IndexAction Action, IntToInt GetNumActions, boolean wrapX) {
        int nFound;
        int[] Is = GetFreshHoodIs(hood.length);
        if (IsValidIndex != null) {
            nFound = HoodToEvalIs(hood, Is, centerX, IsValidIndex, wrapX);
        } else {
            nFound = HoodToIs(hood, Is, centerX, wrapX);
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
    public int HoodToIs(int[] hood, int[] ret, int centerX) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i ++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(xDim, x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            ret[ptCt] = x;
            ptCt++;
        }
        return ptCt;
    }

    public int MapHood(int[] hood, int centerX,Coords1DToBool Eval){
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart = hood.length / 2;
        for (int i = iStart; i < hood.length; i ++) {
            int x = hood[i] + centerX;
            if (!Util.InDim(xDim, x)) {
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
            if (!Util.InDim(xDim, x)) {
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

    public boolean ContainsValidI(int[] hood, int centerX, IndexToBool IsValid) {
        int[] is = GetFreshHoodIs(hood.length);
        int ct = HoodToIs(hood, is, centerX);
        int ret = 0;
        for (int i = 0; i < ct; i++) {
            if (IsValid.Eval(is[i])) {
                return true;
            }
        }
        return false;
    }

    public int HoodCount(int[] hood, int centerX, IndexToBool IsValid) {
        int[] is = GetFreshHoodIs(hood.length);
        int ct = HoodToIs(hood, is, centerX);
        int ret = 0;
        for (int i = 0; i < ct; i++) {
            if (IsValid.Eval(is[i])) {
                ret++;
            }
        }
        return ret;
    }


    public int ConvXsq(int x, GridBase1D other) {
        return (int)(((x+0.5) * other.xDim) / xDim);
    }

    public double ConvXpt(double x, GridBase1D other) {
        return x * other.xDim / xDim;
    }

    private class IsIterator implements Iterator<Integer>, Iterable<Integer> {
        final GridBase1D myGrid;
        int[] myIs;
        int numIs;
        int iCount;

        IsIterator(GridBase1D grid) {
            myGrid = grid;
        }

        public void Setup(GridBase1D grid, int[] hood, int centerX, int centerY) {
            iCount = 0;
            myIs = myGrid.GetFreshHoodIs(hood.length);
            numIs = myGrid.HoodToIs(hood, myIs, centerX);

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
    int GetTick(){
        return tick;
    }
    void SetTick(int tickValue){
        tick=tickValue;
    }
    void IncTick(){
        tick++;
    }
}

