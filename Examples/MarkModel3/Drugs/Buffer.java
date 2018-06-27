package Examples.MarkModel3.Drugs;

import Examples.MarkModel3.Drug;
import Framework.Gui.UIGrid;

import static Framework.Util.PhToProtons;
import static Framework.Util.RGB;

/**
 * Created by Rafael on 1/4/2018.
 */
public class Buffer extends Drug {
    final static int GREEN=RGB(0,1,0);
    double VESSEL_ACID_MAX_DELTA=5;
    double TOX_POWER=0.035;
    double TOX_DECAY=0.9;
    public Buffer() {
        super("Buffer", GREEN);
    }

    @Override
    public void Draw(UIGrid vis) {
        return;
    }

    @Override
    public double ToxCalc(double prevTox, double intensity) {
        return DefaultToxCalc(prevTox,intensity,TOX_POWER,TOX_DECAY);
    }

    @Override
    public void OnStep(double intensity){
        G().acid.vesselConc=PhToProtons(G().VESSEL_PH+intensity*VESSEL_ACID_MAX_DELTA);
    }

}
