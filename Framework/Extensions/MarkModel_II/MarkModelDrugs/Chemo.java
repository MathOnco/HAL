package Framework.Extensions.MarkModel_II.MarkModelDrugs;

import Framework.Utils;

/**
 * Created by Rafael on 10/25/2017.
 */
public class Chemo extends Drug{
    private double CHEMO_MAX_KILL_PROB=0.9;
    private double CHEMO_HALF_MAX=0.25;

    double boundaryCond;

    public Chemo(MarkModelPlusDrugs myModel){
        super("Chemotherapy",true,myModel, Utils.RGB256(126,11,128),MarkModelPlusDrugs.CHEMO);
        this.DIFF_RATE_BASE =1E2;
        this.PreDiffusionStep(0);
    }

    public boolean CheckDivideKill(DrugCell c) {
        if (G().rn.nextDouble() < CHEMO_MAX_KILL_PROB * 2 * (1 / (1 + Math.exp(-(conc.Get(c.Isq()) / CHEMO_HALF_MAX)) - 0.5))) {
            return true;
        }
        return false;
    }


    @Override
    void PreDiffusionStep(double drugIntensity) {
        this.vesselConc=drugIntensity;
        this.boundaryCond=drugIntensity/2;
    }
}
