package Examples.MarkModel3.Drugs;

import Examples.MarkModel3.Drug;
import Framework.Extensions.PDEGrid2DCoarse;
import Framework.Gui.GuiGrid;

import static Framework.Util.HeatMapRGB;
import static Framework.Util.RGB;

/**
 * Created by Rafael on 11/28/2017.
 */
public class AntiAngio extends Drug {
    PDEGrid2DCoarse conc;
    final static int RED =RGB(1,0,0);

    double ANTIANGIO_EXP=0.025;
    double DIFF_RATE=2000;
    double ABSORPTION_RATE=0.05;
    double DECAY_RATE=0;
    double TOX_POWER=0.04;
    double TOX_DECAY=0.9;
    public AntiAngio(double antiangioExp, double diffRate, double absorptionRate, double decayRate) {
        super("AntiAngio",RED);
        ANTIANGIO_EXP=antiangioExp;
        DIFF_RATE=diffRate;
        ABSORPTION_RATE=absorptionRate;
        DECAY_RATE=decayRate;
    }
    public AntiAngio() {
        super("AntiAngio",RED);
    }
    public void Setup(){
        conc =GenDiff();
    }

    @Override
    public void Draw(GuiGrid vis) {
        vis.DrawGridDiff(conc.grid,(v)->HeatMapRGB(v));
    }

    @Override
    public double ToxCalc(double prevTox, double intensity) {
        return DefaultToxCalc(prevTox,intensity,TOX_POWER,TOX_DECAY);
    }


    public void OnStep(double intensity){
           ReactionDiffusion(conc,DIFF_RATE,intensity,DECAY_RATE,(c)->ABSORPTION_RATE,1);
    }
    public double VesselDivisionProb(int iUnscaled,double intensity){
        return 1.0-Math.pow(conc.grid.Get(iUnscaled),ANTIANGIO_EXP);
    }
}
