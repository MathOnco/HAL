package Framework.Extensions.MarkModel_II.MarkModelDrugs;

import Framework.Utils;

/**
 * Created by Rafael on 10/25/2017.
 */
public class VesselStabilizer extends Drug{
    double VESSEL_STABILIZER_MAX_FACTOR=0.25;

    protected VesselStabilizer(MarkModelPlusDrugs myModel) {
        super("Stabilizer",false, myModel, Utils.RGB256(254,57,57),MarkModelPlusDrugs.VESSEL_STABILIZER);
    }

    @Override
    void PreDiffusionStep(double drugIntensity){
        G().VESSEL_DEGRADATION_RATE_BASE=drugIntensity*(1.0-VESSEL_STABILIZER_MAX_FACTOR)+VESSEL_STABILIZER_MAX_FACTOR;
    }
}
