package Examples.MarkModel3.Drugs;

import Examples.MarkModel3.Drug;
import Framework.Gui.UIGrid;

import static Framework.Util.RGB;
import static Framework.Util.RGBA256;

/**
 * Created by Rafael on 11/29/2017.
 */
public class VesselStabilizer extends Drug {
    //TODO: may want to make vessel stabilizer effect exponential
    //turn down max to something less than 1
    static final int BLANK=RGBA256(0,0,0,0);
    double TOX_POWER=0.02;
    double TOX_DECAY=0.9;
    double MAX_STABILITY=0.9;
    public VesselStabilizer() {
        super("VesselStabilizer",RGB(1,1,0));
    }

    @Override
    public void OnStep(double intensity){
        G().VESSEL_DEGRADE_PROB =G().VESSEL_DEGRADE_PROB_BASE *(1.0-intensity*MAX_STABILITY);
    }

    @Override
    public double ToxCalc(double prevTox, double intensity) {
        return DefaultToxCalc(prevTox,intensity,TOX_POWER,TOX_DECAY);
    }

    @Override
    public void Draw(UIGrid vis) {
        return;
    }
}
