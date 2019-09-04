package HAL.GridsAndAgents;

import HAL.Interfaces.Grid1D;
import HAL.Tools.MultinomialCalc;

import static HAL.Tools.Internal.PopulationGridPDEequations.Diffusion1;

public class PopulationGrid1D extends PopulationGridBase implements Grid1D {
    public final int xDim;
    public final int length;
    public boolean wrapX;

    public PopulationGrid1D(int xDim, boolean wrapX) {
        super(xDim);
        this.xDim=xDim;
        this.length=xDim;
        this.wrapX=wrapX;
    }
    public PopulationGrid1D(int xDim) {
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
    public void Diffusion(double diffRate,MultinomialCalc mn) {
        ApplyOccupied((i, ct) -> {
            Diffusion1(ct, i, this, diffRate, xDim, wrapX, null, mn);
        });
    }
}
