package Framework.Extensions.MarkModel_II.MarkModelDrugs;

import Framework.Utils;

import static Framework.Extensions.MarkModel_II.MarkModel_II.ACID;
import static Framework.Utils.PhToProtons;

/**
 * Created by Rafael on 10/25/2017.
 */
public class Bicarb extends Drug{
    private double DRUG_TO_PH=0.0666;

    protected Bicarb( MarkModelPlusDrugs myModel) {
        super("Bicarb",true, myModel, Utils.RGB256(23,114,70),MarkModelPlusDrugs.BICARB);
    }

    @Override
    void PreDiffusionStep(double drugIntensity) {
        G().vesselConcs[ACID]=PhToProtons(G().VESSEL_PH+drugIntensity*DRUG_TO_PH);
        G().diffRates[ACID]=G().DIFF_RATE_PROTON*G().DIFF_SCALE_FACTOR*(1+drugIntensity);
    }
}
