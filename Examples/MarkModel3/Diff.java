package Examples.MarkModel3;

import Framework.Extensions.PDEGrid2DCoarse;

import java.io.Serializable;

public class Diff extends PDEGrid2DCoarse implements Serializable{
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
        if(active) {
            if (isReflectve) {
                DiffusionADI(diffRate);
            } else {
                DiffusionADI(diffRate, boundaryValue);
            }
        }
    }
    public void SetVesselConc(int x,int y){
        if(active) {
            SetPartial(x,y, vesselConc,g.DIFF_SPACE_SCALE *g.DIFF_SPACE_SCALE);
        }
    }
    public void SetVesselConcProp(int x,int y,double prop)
    {
        if(active) {
            double current=Get(x,y);
            double difference=vesselConc-current;
            SetPartial(x,y, difference*(1-prop)+current,g.DIFF_SPACE_SCALE *g.DIFF_SPACE_SCALE);
        }
    }
}
