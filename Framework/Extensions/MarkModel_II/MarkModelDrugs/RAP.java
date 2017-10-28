package Framework.Extensions.MarkModel_II.MarkModelDrugs;

import Framework.Extensions.MarkModel_II.MarkModel_II;
import Framework.GridsAndAgents.PDEGrid2D;

/**
 * Created by Rafael on 10/25/2017.
 */
public class RAP extends Drug{
    private double RAP_BC_AND_VESSEL_CONC=0;
    private double RAP_CONVERSION_O2=1.39E-3;
    private double RAP_KILL_THRESH=0.1;
    public RAP(MarkModelPlusDrugs myModel){
        super(true,myModel,MarkModelPlusDrugs.RAP);
        this.boundaryCond=0;
        this.vesselConc=0;
        this.DIFF_RATE_BASE=0.5*1.1E2;
    }

    @Override
    void PostDiffusionStep(double drugIntensity){
        for (DrugCell c : G()) {
            if((c.type== MarkModel_II.NORMAL||c.type== MarkModel_II.TUMOR)&&conc.Get(c.Isq())<RAP_KILL_THRESH){
                c.Die(G().DISPOSE_PROB_APOP);
            }
        }
    }

    private void ConvertHAPtoRap(){
        PDEGrid2D oxygen=G().GetDiff(MarkModel_II.OXYGEN);
        PDEGrid2D hap=G().GetDrug(MarkModelPlusDrugs.HAP).GetConc();
        for (int i = 0; i < conc.length; i++) {
            if(oxygen.Get(i)<RAP_CONVERSION_O2) {
                hap.Add(i,conc.Get(i));
                conc.Set(i,0);
            }
        }
    }
    @Override
    void ADIFirstHalf(){
        ConvertHAPtoRap();
        super.ADIFirstHalf();
    }
    @Override
    void ADISecondHalf(){
        ConvertHAPtoRap();
        super.ADISecondHalf();
    }
}
