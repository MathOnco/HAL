package Framework.Extensions.MarkModel_II.MarkModelDrugs;

import Framework.Util;


/**
 * Created by Rafael on 10/25/2017.
 */
public class AntiAngio extends Drug{
    public int ANGIO_PROP;
    private double ANGIO_RATE_BASE;
    protected AntiAngio(MarkModelPlusDrugs myModel) {
        super("AntiAngio",false, myModel, Util.RGB256(250,162,13),MarkModelPlusDrugs.ANTI_ANGIO);
        ANGIO_RATE_BASE=G().ANGIO_RATE;
    }
    @Override
    void PreDiffusionStep(double drugIntensity){
        G().ANGIO_RATE=(1-drugIntensity)*ANGIO_RATE_BASE;
    }
}
