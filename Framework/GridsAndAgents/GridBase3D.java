package Framework.GridsAndAgents;

import Framework.Interfaces.Coords3DToAction;
import Framework.Interfaces.IndexAction;
import Framework.Interfaces.IndexToBool;
import Framework.Interfaces.IntToInt;
import Framework.Rand;
import Framework.Util;

import java.util.ArrayList;

/**
 * Created by bravorr on 5/17/17.
 */
public abstract class GridBase3D extends GridBase{
    public final int xDim;
    public final int yDim;
    public final int zDim;
    public final int length;
    public boolean wrapX;
    public boolean wrapY;
    public boolean wrapZ;
    public ArrayList<int[]> hoodIs=new ArrayList<>();
    int ihoodIs=0;
    GridBase3D(int x,int y,int z,boolean wrapX,boolean wrapY,boolean wrapZ){
        xDim=x;
        yDim=y;
        zDim=z;
        length=x*y*z;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
        this.wrapZ=wrapZ;
    }
    public int WrapI(int x, int y, int z){
        //wraps Coords to proper index
        if(In(x,y,z)) { return I(x,y,z);}
        return I(Util.ModWrap(x,xDim), Util.ModWrap(y,yDim), Util.ModWrap(z,zDim));
    }

    /**
     * gets the index of the voxel at the specified coordinates
     */
    public int I(int x, int y, int z){
        //gets typeGrid index from location
        return x*yDim*zDim+y*zDim+z;
    }

    /**
     * gets the xDim component of the voxel at the specified index
     */
    public int ItoX(int i){
        return i/(yDim*zDim);
    }

    /**
     * gets the yDim component of the voxel at the specified index
     */
    public int ItoY(int i){return (i/zDim)%yDim;}

    /**
     * gets the z component of the voxel at the specified index
     */
    public int ItoZ(int i){
        return i%zDim;
    }

    /**
     * gets the index of the square that contains the specified coordinates
     */
    public int I(double x, double y, double z){
        //gets typeGrid index from location
        return (int)Math.floor(x)*yDim*zDim+(int)Math.floor(y)*zDim+(int)Math.floor(z);
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    public boolean In(int x, int y, int z){
        if(x>=0&&x<xDim&&y>=0&&y<yDim&&z>=0&&z<zDim){
            return true;
        }
        return false;
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    public boolean In(double x, double y, double z){
        int xInt=(int)Math.floor(x);
        int yInt=(int)Math.floor(y);
        int zInt=(int)Math.floor(z);
        return In(xInt,yInt,zInt);
    }
    /**
     * writes into ret the coordinates of SQs with the centerX and centerY coordinates added
     * returns the number of squares that were written into ret. any coordinates that fall out of bounds are not written
     * @param hood list of coordinates of the form [xDim,yDim,xDim,yDim,...]
     * @param ret list into which the displaced coordinates will be written
     * @param centerX xDim displacement of coordinates
     * @param centerY yDim displacement of coordinates
     * @param centerZ z displacement of coordinates
     * @param wrapX whether to wrap the coordinates that fall out of bounds in the X dimension
     * @param wrapY whether to wrap the coordinates that fall out of bounds in the Y dimension
     * @param wrapZ whether to wrap the coordinates that fall out of bounds in the Z dimension
     * @return the number of coordinates written into the ret array
     */
    public int HoodToEvalIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ,IndexToBool Eval, boolean wrapX, boolean wrapY, boolean wrapZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart=hood.length/4;
        for (int i = iStart; i < hood.length; i+=3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
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
            if (!Util.InDim(zDim, z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            int j= I(x,y,z);
            if(Eval.Eval(j)) {
                ret[ptCt] = j;
                ptCt++;
            }
        }
        return ptCt;
    }
    public int MapHood(int[] hood, int centerX, int centerY, int centerZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart=hood.length/4;
        for (int i = iStart; i < hood.length; i+=3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
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
            if (!Util.InDim(zDim, z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            hood[ptCt]= I(x,y,z);
            ptCt++;
        }
        return ptCt;
    }
    public int MapHood(int[] hood, int centerI) {
        return MapHood(hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI));
    }
    public int HoodToIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ, boolean wrapX, boolean wrapY, boolean wrapZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart=hood.length/4;
        for (int i = iStart; i < hood.length; i+=3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
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
            if (!Util.InDim(zDim, z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            ret[ptCt]= I(x,y,z);
            ptCt++;
        }
        return ptCt;
    }
    public int HoodToIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart=hood.length/4;
        for (int i = iStart; i < hood.length; i+=3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
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
            if (!Util.InDim(zDim, z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            ret[ptCt]= I(x,y,z);
            ptCt++;
        }
        return ptCt;
    }

    public int HoodToAction(int[] hood, int centerX, int centerY, int centerZ, Coords3DToAction Action) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        int iStart=hood.length/4;
        for (int i = iStart; i < hood.length; i+=3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
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
            if (!Util.InDim(zDim, z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            Action.Action(x,y,z);
            ptCt++;
        }
        return ptCt;
    }

    public int CoordsToIs(int[] coords, int[] ret, boolean wrapX, boolean wrapY, boolean wrapZ) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        for (int i = 0; i < coords.length / 3; i++) {
            int x = coords[i * 3];
            int y = coords[i * 3 + 1];
            int z = coords[i * 3 + 2];
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
            if (!Util.InDim(zDim, z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            ret[ptCt]= I(x,y,z);
            ptCt++;
        }
        return ptCt;
    }
    public int CoordsToIs(int[] coords, int[] ret) {
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt = 0;
        for (int i = 0; i < coords.length / 3; i++) {
            int x = coords[i * 3];
            int y = coords[i * 3 + 1];
            int z = coords[i * 3 + 2];
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
            if (!Util.InDim(zDim, z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            ret[ptCt]= I(x,y,z);
            ptCt++;
        }
        return ptCt;
    }
    public double DistSq(double x1, double y1, double z1, double x2, double y2, double z2, boolean wrapX, boolean wrapY, boolean wrapZ){
        return Util.DistSquared(x1,y1,z1,x2,y2,z2,xDim,yDim,zDim,wrapX,wrapY,wrapZ);
    }
    public double DistSq(double x1, double y1, double z1, double x2, double y2, double z2){
        return Util.DistSquared(x1,y1,z1,x2,y2,z2,xDim,yDim,zDim,wrapX,wrapY,wrapZ);
    }
    public int[]BoundaryIs(){
        int[] ret=new int[(xDim*yDim+xDim*zDim+yDim*zDim)*2];
        int side1=xDim*yDim;
        int side2=xDim*zDim;
        int side3=yDim*zDim;
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                ret[x*yDim+y]= I(x,y,0);
                ret[x*yDim+y+side1]= I(x,y,zDim-1);
            }
        }
        for (int x = 0; x < xDim; x++) {
            for (int z = 0; z < zDim; z++) {
                ret[x*zDim+z+side1*2]= I(x,0,z);
                ret[x*zDim+z+side1*2+side2]= I(x,yDim-1,z);
            }
        }
        for (int y = 0; y < yDim; y++) {
            for (int z = 0; z < zDim; z++) {
                ret[y*zDim+z+(side1+side2)*2]= I(0,y,z);
                ret[y*zDim+z+(side1+side2)*2+side3]= I(xDim-1,y,z);
            }
        }
        return ret;
    }

    public int IsInHood(int[]hood, int centerX, int centerY,int centerZ, int nActions, IndexToBool IsValidIndex, IndexAction Action, boolean wrapX, boolean wrapY,boolean wrapZ, Rand rn){
        int[]Is;
        int nFound;
        if(hoodIs.size()<= ihoodIs){
            Is=new int[hood.length];
            hoodIs.add(Is);
        }
        else{
            Is=hoodIs.get(ihoodIs);
            if(Is.length<hood.length/2){
                Is=new int[hood.length];
                hoodIs.set(ihoodIs,Is);
            }
        }
        ihoodIs++;
        if(IsValidIndex!=null){
            nFound=HoodToEvalIs(hood,Is,centerX,centerY,centerZ,IsValidIndex,wrapX,wrapY,wrapZ);
        }
        else {
            nFound = HoodToIs(hood, Is, centerX, centerY,centerZ, wrapX, wrapY,wrapZ);
        }
        if(nActions<=0||nFound<=nActions){
            for (int i = 0; i < nFound; i++) {
                Action.Action(Is[i],nFound);
            }
        }
        else{
            for (int i = 0; i < nActions; i++) {
                int iRand = rn.Int(nFound - i);
                Action.Action(Is[iRand], nFound);
                Is[iRand]=Is[nFound-i-1];
            }
        }
        ihoodIs--;
        return nFound;
    }
    public int ConvXsq(int x,GridBase3D other){
        return x*other.xDim/xDim;
    }
    public int ConvYsq(int y,GridBase3D other){
        return y*other.yDim/yDim;
    }
    public int ConvZsq(int z,GridBase3D other){
        return z*other.zDim/zDim;
    }
    public int ConvI(int i,GridBase3D other){
        int x=ItoX(i);
        int y=ItoY(i);
        int z=ItoZ(i);
        return other.I(ConvXsq(x,other),ConvYsq(y,other),ConvZsq(z,other));
    }
    public double ConvXpt(double x,GridBase3D other){
        return x*other.xDim/xDim;
    }
    public double ConvYpt(double y,GridBase3D other){
        return y*other.yDim/yDim;
    }
    public double ConvZpt(double z,GridBase3D other){
        return z*other.zDim/zDim;
    }
    int[]GetFreshHoodIs(int hoodLen){
        int[]Is;
        int nFound;
        if(hoodIs.size()<= ihoodIs){
            Is=new int[hoodLen];
            hoodIs.add(Is);
        }
        else{
            Is=hoodIs.get(ihoodIs);
            if(Is.length<hoodLen/2){
                Is=new int[hoodLen];
                hoodIs.set(ihoodIs,Is);
            }
        }
        return Is;
    }

    public int ApplyHood(int[]hood, int centerI, IndexAction Action){
        return ApplyHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), null, null,Action, null, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int[]hood, int centerX, int centerY, int centerZ, IndexAction Action){
        return ApplyHood(-1, hood,centerX,centerY,centerZ, null, null,Action, null, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int[] hood, int centerI, IndexToBool IsValidIndex, IndexAction Action){
        return ApplyHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), null, IsValidIndex,Action, null, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int[] hood, int centerX, int centerY, int centerZ, IndexToBool IsValidIndex, IndexAction Action){
        return ApplyHood(-1, hood,centerX,centerY,centerZ, null, IsValidIndex,Action, null, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int nActions, int[] hood, int centerI, Rand rn, IndexAction Action){
        return ApplyHood(nActions, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, null,Action, null, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int nActions, int[] hood, int centerX, int centerY, int centerZ, Rand rn, IndexAction Action){
        return ApplyHood(nActions, hood,centerX,centerY,centerZ, rn, null,Action, null, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int nActions, int[] hood, int centerI, Rand rn, IndexToBool IsValidIndex, IndexAction Action){
        return ApplyHood(nActions, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, IsValidIndex,Action, null, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int nActions, int[] hood, int centerX, int centerY, int centerZ, Rand rn, IndexToBool IsValidIndex, IndexAction Action){
        return ApplyHood(nActions, hood,centerX,centerY,centerZ, rn, IsValidIndex,Action, null, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int nActions, int[] hood, int centerI, Rand rn, IndexToBool IsValidIndex, IndexAction Action, boolean wrapX, boolean wrapY, boolean wrapZ) {
        return ApplyHood(nActions, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, IsValidIndex,Action, null, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood( int[] hood, int centerI, Rand rn,IntToInt GetNumActions, IndexAction Action){
        return ApplyHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, null,Action, GetNumActions, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int[] hood, int centerX, int centerY, int centerZ, Rand rn,IntToInt GetNumActions,  IndexAction Action){
        return ApplyHood(-1, hood,centerX,centerY,centerZ, rn, null,Action, GetNumActions, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int[] hood, int centerI, Rand rn,IntToInt GetNumActions,  IndexToBool IsValidIndex, IndexAction Action){
        return ApplyHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, IsValidIndex,Action, GetNumActions, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int[] hood, int centerX, int centerY, int centerZ, Rand rn,IntToInt GetNumActions,  IndexToBool IsValidIndex, IndexAction Action){
        return ApplyHood(-1, hood,centerX,centerY,centerZ, rn, IsValidIndex,Action, GetNumActions, wrapX,wrapY,wrapZ);
    }
    public int ApplyHood(int[] hood, int centerI, Rand rn,IntToInt GetNumActions,  IndexToBool IsValidIndex, IndexAction Action, boolean wrapX, boolean wrapY, boolean wrapZ) {
        return ApplyHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, IsValidIndex,Action, GetNumActions, wrapX,wrapY,wrapZ);
    }
    int ApplyHood(int nActions, int[] hood, int centerX, int centerY, int centerZ, Rand rn, IndexToBool IsValidIndex, IndexAction Action, IntToInt GetNumActions, boolean wrapX, boolean wrapY, boolean wrapZ){
        int nFound;
        int[]Is=GetFreshHoodIs(hood.length);
        ihoodIs++;
        if(IsValidIndex!=null){
            nFound=HoodToEvalIs(hood,Is,centerX,centerY,centerZ,IsValidIndex,wrapX,wrapY,wrapZ);
        }
        else {
            nFound = HoodToIs(hood, Is, centerX, centerY,centerZ, wrapX, wrapY,wrapZ);
        }

        if(GetNumActions!=null){
            nActions=GetNumActions.Eval(nFound);
            nActions=nActions<0?0:nActions;
        }

        if(nActions<0||nFound<=nActions){
            for (int i = 0; i < nFound; i++) {
                Action.Action(Is[i],nFound);
            }
        }
        else{
            for (int i = 0; i < nActions; i++) {
                int iRand = rn.Int(nFound - i);
                Action.Action(Is[iRand], nFound);
                Is[iRand]=Is[nFound-i-1];
            }
        }
        ihoodIs--;
        return nFound;
    }
}

