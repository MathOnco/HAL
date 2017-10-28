package Framework.Extensions.MarkModel_II.MarkModelDrugs;

import Framework.Extensions.Module;
import Framework.GridsAndAgents.PDEGrid2D;

import java.io.Serializable;

/**
 * Created by Rafael on 10/25/2017.
 */
public abstract class Drug implements Module,Serializable {
    public final String name;
    public final int id;
    public final int color;
    double toxicity;
    public final boolean diffusing;
    public boolean controllable;
    private final MarkModelPlusDrugs myModel;
    protected double maxDif;
    protected double DIFF_RATE_BASE;
    protected double DIFF_RATE;
    protected double boundaryCond;
    protected double vesselConc;
    protected final PDEGrid2D conc;

    protected Drug(boolean diffusing,MarkModelPlusDrugs myModel,int id) {
        this.color=0;
        this.name=null;
        this.myModel=myModel;
        this.diffusing=diffusing;
        this.conc=diffusing?new PDEGrid2D(G().xDim,G().yDim):null;
        this.controllable=false;
        this.id=id;
    }
    protected Drug(String name,boolean diffusing,MarkModelPlusDrugs myModel,int color,int id) {
        this.color=color;
        this.myModel=myModel;
        this.conc=diffusing?new PDEGrid2D(G().xDim,G().yDim):null;
        this.name=name;
        this.diffusing=diffusing;
        this.controllable=true;
        this.id=id;
    }

    @Override
    public int GetNumNeededProps(){ return 0; }
    @Override
    public void SetModPropID(int propID,int modulePropIndex){}
    protected PDEGrid2D GetConc(){
        return conc;
    }
    double GetDiffRateBase(){
        return DIFF_RATE_BASE;
    }
    void SetDiffRate(){
        DIFF_RATE = DIFF_RATE_BASE *G().DIFF_TIMESTEP/(G().SQUARE_DIAM*G().SQUARE_DIAM);
    }
    MarkModelPlusDrugs G(){
        return myModel;
    }
    public void SetTox(double drugIntensity){
        toxicity=drugIntensity;
    }
    public double GetTox(){
        return toxicity;
    }
    public double GetMaxDif(){
        return maxDif;
    }
    void PreDiffusionStep(double drugIntensity){};
    void PostDiffusionStep(double drugIntensity){};
    void VesselReaction(DrugCell c){
        conc.Set(c.Isq(),vesselConc);
    }
    void ADIFirstHalf(){
        if(!G().REFLECTIVE_BOUNDARY) {
            conc.DiffusionADIHalfX(DIFF_RATE, true, boundaryCond);
        }else{
            conc.DiffusionADIHalfX(DIFF_RATE, false, 0);
        }
    }
    void ADISecondHalf(){
        if(!G().REFLECTIVE_BOUNDARY) {
            conc.DiffusionADIHalfY(DIFF_RATE, true, boundaryCond);
        }else{
            conc.DiffusionADIHalfY(DIFF_RATE, false, 0);
        }
        maxDif=conc.MaxDifInternalScaled(G().EPS);
    }
}
