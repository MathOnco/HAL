package HAL.GridsAndAgents;

import HAL.Interfaces.Grid3D;
import HAL.Tools.MultinomialCalc;

import static HAL.Tools.Internal.PopulationGridPDEequations.Diffusion3;

public class PopulationGrid3D extends PopulationGridBase implements Grid3D{
    public final int xDim;
    public final int yDim;
    public final int zDim;
    public boolean wrapX;
    public boolean wrapY;
    public boolean wrapZ;

    public PopulationGrid3D(int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ) {
        super(xDim*yDim*zDim);
        this.xDim=xDim;
        this.yDim=yDim;
        this.zDim=zDim;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
        this.wrapZ=wrapZ;
    }
    public PopulationGrid3D(int xDim, int yDim, int zDim) {
        this(xDim,yDim,zDim,false,false,false);
    }

    public int Get(int x,int y,int z){
        return Get(I(x,y,z));
    }

    public void Add(int x,int y,int z,int val){
        Add(I(x,y,z),val);
    }
    public void Set(int x,int y,int z,int val){
        int i=I(x,y,z);
        Add(i,val-Get(i));
    }
    public void Move(int xFrom,int yFrom,int zFrom,int xTo,int yTo,int zTo,int val){
        Move(I(xFrom,yFrom,zFrom),I(xTo,yTo,zTo),val);
    }
    public <T extends PopulationGrid3D> void Move(int xFrom,int yFrom,int zFrom,int xTo,int yTo,int zTo,T gridTo,int val){
        Move(I(xFrom,yFrom,zFrom),I(xTo,yTo,zTo),gridTo,val);
    }

    @Override
    public int Xdim() {
        return xDim;
    }

    @Override
    public int Ydim() {
        return yDim;
    }

    @Override
    public int Zdim() {
        return zDim;
    }

    @Override
    public int Length() {
        return length;
    }

    @Override
    public boolean IsWrapX() {
        return wrapX;
    }

    @Override
    public boolean IsWrapY() {
        return wrapY;
    }

    @Override
    public boolean IsWrapZ() {
        return wrapZ;
    }

    public void Diffusion(double diffRate,MultinomialCalc mn){
        ApplyOccupied((i,ct)->{
            Diffusion3(ct,ItoX(i),ItoY(i),ItoZ(i),i,this,diffRate,xDim,yDim,zDim,wrapX,wrapY,wrapZ,null,mn);
        });
    }
}
