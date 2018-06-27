package Examples.MarkModel3;

import Framework.Extensions.PDEGrid2DCoarse;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Util;

import java.io.Serializable;

public class Diff extends PDEGrid2DCoarse implements Serializable{
    private final static double inSide=2.0/3.0,outSide=1.0/3.0,inCorner=4.0/9.0,outCorner=5.0/18.0;
    public final Tissue g;
    public boolean active;
    public double diffRate;
    public double boundaryValue;
    public double vesselConc;
    public Diff(int xDim, int yDim, int spaceFactor, Tissue g, boolean active) {
        super(xDim, yDim, spaceFactor);
        this.g = g;
        this.active=active;
    }
    public void Diffuse(boolean isReflectve){
        if(g.DIFF_SPACE_SCALE==1){
            if (active) {
                if (isReflectve) {
                    grid.DiffusionADI(diffRate);
                } else {
                    grid.DiffusionADI(diffRate, boundaryValue);
                }
            }
        }
        else {
            if (active) {
                if (isReflectve) {
                    DiffusionADI(diffRate);
                } else {
                    DiffusionADI(diffRate, boundaryValue);
                }
            }
        }
    }
    public void SetVesselConc(int x,int y){
        if(g.DIFF_SPACE_SCALE==1){
            grid.Set(x,y,vesselConc);
        }
        else {
            if (active) {
                SetPartial(x, y, vesselConc, g.DIFF_SPACE_SCALE * g.DIFF_SPACE_SCALE);
            }
        }
    }
    public void SetVesselConcProp(int x,int y,double prop) {
        if(g.DIFF_SPACE_SCALE==1){
            grid.Set(x,y,vesselConc*prop);
        }
        if(active) {
            double current=Get(x,y);
            double difference=vesselConc-current;
            SetPartial(x,y, difference*(1-prop)+current,g.DIFF_SPACE_SCALE *g.DIFF_SPACE_SCALE);
        }
    }
    int InFallback(int val,int fallback,int dim){
        return(Util.InDim(val, dim))?val:fallback;
    }
    public double GetInterp(int x,int y) {
        if (g.DIFF_SPACE_SCALE == 1) {
            return grid.Get(x, y);
        } else {
            PDEGrid2D g = grid;
            final int xDiff = x / 3;
            final int yDiff = y / 3;
            final int xMod = x % 3;
            final int yMod = y % 3;
            switch (xMod) {
                case 0:
                    switch (yMod) {
                        case 0://left bottom
                            return g.Get(xDiff, yDiff) * inCorner +
                                    g.Get(InFallback(xDiff - 1, xDiff, g.xDim), yDiff) * outCorner +
                                    g.Get(xDiff, InFallback(yDiff - 1, yDiff, g.yDim)) * outCorner;
                        case 1://left middle
                            return g.Get(xDiff, yDiff) * inSide + g.Get(InFallback(xDiff - 1, xDiff, g.xDim), yDiff) * outSide;
                        case 2://left top
                            return g.Get(xDiff, yDiff) * inCorner +
                                    g.Get(InFallback(xDiff - 1, xDiff, g.xDim), yDiff) * outCorner +
                                    g.Get(xDiff, InFallback(yDiff + 1, yDiff, g.yDim)) * outCorner;
                        default:
                            throw new IllegalStateException("mod calculation did not work!");
                    }
                case 1:
                    switch (yMod) {
                        case 0://middle bottom
                            return g.Get(xDiff, yDiff) * inSide + g.Get(xDiff, InFallback(yDiff - 1, yDiff, g.yDim)) * outSide;
                        case 1://middle
                            return g.Get(xDiff, yDiff);
                        case 2://middle top
                            return g.Get(xDiff, yDiff) * inSide + g.Get(xDiff, InFallback(yDiff + 1, yDiff, g.yDim)) * outSide;
                        default:
                            throw new IllegalStateException("mod calculation did not work!");
                    }
                case 2:
                    switch (yMod) {
                        case 0://right bottom
                            return g.Get(xDiff, yDiff) * inCorner +
                                    g.Get(InFallback(xDiff + 1, xDiff, g.xDim), yDiff) * outCorner +
                                    g.Get(xDiff, InFallback(yDiff - 1, yDiff, g.yDim)) * outCorner;
                        case 1://right middle
                            return g.Get(xDiff, yDiff) * inSide + g.Get(InFallback(xDiff + 1, xDiff, g.xDim), yDiff) * outSide;
                        case 2://right top
                            return g.Get(xDiff, yDiff) * inCorner +
                                    g.Get(InFallback(xDiff + 1, xDiff, g.xDim), yDiff) * outCorner +
                                    g.Get(xDiff, InFallback(yDiff + 1, yDiff, g.yDim)) * outCorner;
                        default:
                            throw new IllegalStateException("mod calculation did not work!");
                    }

                default:
                    throw new IllegalStateException("mod calculation did not work!");
            }
        }
    }
}
