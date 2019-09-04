package HAL.GridsAndAgents;

import HAL.Interfaces.Grid2D;
import HAL.Tools.MultinomialCalc;

import static HAL.Tools.Internal.PopulationGridPDEequations.Diffusion2;

public class PopulationGrid2D extends PopulationGridBase implements Grid2D {
    public final int xDim;
    public final int yDim;
    public boolean wrapX;
    public boolean wrapY;

    public PopulationGrid2D(int xDim, int yDim, boolean wrapX, boolean wrapY) {
        super(xDim*yDim);
        this.xDim=xDim;
        this.yDim=yDim;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
    }

    public PopulationGrid2D(int xDim, int yDim) {
        this(xDim,yDim,false,false);
    }
    public int Get(int x,int y){
        return Get(I(x,y));
    }
    public void Set(int x,int y,int val){
        int i=I(x,y);
        Add(i,val-agents[i]);
    }
    public void Add(int x,int y,int val){
        Add(I(x,y),val);
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
    public void Diffusion(double diffRate,MultinomialCalc mn) {
        ApplyOccupied((i, ct) -> {
            Diffusion2(ct, ItoX(i), ItoY(i), i, this, diffRate, xDim, yDim, wrapX, wrapY, null, mn);
        });
    }
}
