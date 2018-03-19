package Examples.MarkModel3.Drugs;

import Examples.MarkModel3.Cell;
import Examples.MarkModel3.Drug;
import Examples.MarkModel3.Tissue;
import Framework.Extensions.PDEGrid2DCoarse;
import Framework.Gui.GuiGrid;

import static Examples.MarkModel3.Tissue.NORMAL;
import static Examples.MarkModel3.Tissue.TUMOR;
import static Framework.Util.HeatMapRGB;
import static Framework.Util.RGB;

public class Chemo<C extends Cell<C,T>,T extends Tissue<C>> extends Drug<C,T> {

    boolean active=false;
    PDEGrid2DCoarse conc;
    final static int PURPLE =RGB(1,0,1);

    double PROB_KILL_NORMAL=2;
    double PROB_KILL_CANCER=8;
    double DIFF_RATE=1500;
    double ABSORPTION_RATE=0.03;
    double DECAY_RATE=0;
    double PUMP_PHENO_SCALE=0.5;
    double TOX_POWER=0.1;
    double TOX_DECAY=0.94;
    public Chemo() {
        super("Chemo",PURPLE);
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
        if (G().tick % 7 == 0) {
            return DefaultToxCalc(prevTox, intensity, TOX_POWER, TOX_DECAY);
        }
        if (G().tick % 7 == 1) {
            return DefaultToxCalc(prevTox, intensity / 2, TOX_POWER, TOX_DECAY);
        }
        return DefaultToxCalc(prevTox, 0, TOX_POWER, TOX_DECAY);
    }


    @Override
    public void OnStep(double intensity){
        if(intensity>0){
            active=true;
        }
        if(active&&G().tick%7==0){
            ReactionDiffusion(conc,DIFF_RATE,intensity,DECAY_RATE,(c)->ABSORPTION_RATE,1);
            System.out.println(conc.grid.GetAvg());
        }
        if(active&&G().tick%7==1){
            ReactionDiffusion(conc,DIFF_RATE,intensity/2,DECAY_RATE,(c)->ABSORPTION_RATE,1);
        }
        else if(active&&G().tick%7>1){
            ReactionDiffusion(conc,DIFF_RATE,0,DECAY_RATE,(c)->ABSORPTION_RATE,1);
        }
    }
    @Override
    public void BeforeDivision(C c, double intensity){
        if(active) {
            if (c.type == TUMOR) {
                //if (G().rn.Double() < c.GetInterp(conc)*PROB_KILL_CANCER*(1.0-c.pumpPheno*PUMP_PHENO_SCALE)) {
                //no pump pheno!
                if (G().rn.Double() < c.GetInterp(conc)*PROB_KILL_CANCER) {
                    c.Die(false);
                }
            }else if (c.type == NORMAL) {
                if (G().rn.Double() < c.GetInterp(conc)*PROB_KILL_NORMAL) {
                    c.Die(false);
                }
            }
        }
    }
}
