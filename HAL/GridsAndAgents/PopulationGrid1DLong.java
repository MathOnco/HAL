package HAL.GridsAndAgents;

import HAL.Interfaces.Grid1D;
import HAL.Tools.MultinomialCalcLong;

import static HAL.Tools.Internal.PopulationGridPDEequations.Diffusion1L;


public class PopulationGrid1DLong extends PopulationGridLongBase implements Grid1D {
    public final int xDim;
    public final int length;
    public boolean wrapX;

    public PopulationGrid1DLong(int xDim, boolean wrapX) {
        super(xDim);
        this.xDim=xDim;
        this.length=xDim;
        this.wrapX=wrapX;
    }
    public PopulationGrid1DLong(int xDim) {
        this(xDim,false);
    }

    @Override
    public int Xdim() {
        return xDim;
    }

    @Override
    public int Length() {
        return length;
    }

    @Override
    public boolean IsWrapX() {
        return wrapX;
    }
    public void Diffusion(double diffRate, MultinomialCalcLong mn) {
        ApplyOccupied((i, ct) -> {
            Diffusion1L(ct, i, this, diffRate, xDim, wrapX, null, mn);
        });
    }
}
