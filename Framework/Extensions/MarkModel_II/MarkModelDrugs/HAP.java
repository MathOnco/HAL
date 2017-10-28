package Framework.Extensions.MarkModel_II.MarkModelDrugs;

import Framework.Utils;

/**
 * Created by Rafael on 10/25/2017.
 */
public class HAP extends Drug{
    RAP rap;

    public HAP(MarkModelPlusDrugs myModel) {
        super("HAP",true,myModel, Utils.RGB256(217,100,78),MarkModelPlusDrugs.HAP);
        DIFF_RATE_BASE=1.1E2;

    }

    @Override
    void PreDiffusionStep(double drugIntensity) {
        this.vesselConc=drugIntensity;
        this.boundaryCond=drugIntensity/4;
    }
}
