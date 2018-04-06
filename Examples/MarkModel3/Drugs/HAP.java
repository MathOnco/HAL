package Examples.MarkModel3.Drugs;

import Examples.MarkModel3.Cell;
import Examples.MarkModel3.CellAbsorption;
import Examples.MarkModel3.Drug;
import Examples.MarkModel3.Tissue;
import Framework.Extensions.PDEGrid2DCoarse;
import Framework.Gui.GuiGrid;

import static Examples.MarkModel3.Tissue.NORMAL;
import static Examples.MarkModel3.Tissue.TUMOR;
import static Framework.Util.Bound;
import static Framework.Util.RGB;

public class HAP<C extends Cell<C,T>,T extends Tissue<C>> extends Drug<C,T> {

    public PDEGrid2DCoarse conc;
    public PDEGrid2DCoarse concActivated;
    public double MAX_STEPS=20;
    public double STEADY_STATE=0.001;

    final static int RED =RGB(1,0,0);

    //unactivated drug
    double DIFF_RATE1=1500;
    double ABSORPTION_RATE1=0.01;
    double DECAY_RATE1=0;

    //activated drug
    double DIFF_RATE2=1500;
    double ABSORPTION_RATE2=0.01;
    double DECAY_RATE2=0;

    double ACTIVATION_RATE=0.1;

    double PUMP_PHENO_SCALE=0.5;
    double LETHALITY_START_O2=0.015;
    double TOX_POWER=0.04;
    double TOX_DECAY=0.9;
    double LETHALITY=1;

    public HAP() {
        super("HAP",RED);
    }
    public void Setup(){
        conc =GenDiff();
        concActivated =GenDiff();

    }
    @Override
    public void Draw(GuiGrid vis) {
        for (int i = 0; i < conc.grid.length; i++) {
            vis.SetPix(i,RGB(concActivated.grid.Get(i),0.0,conc.grid.Get(i)));
        }
    }

    @Override
    public double ToxCalc(double prevTox, double intensity) {
        return DefaultToxCalc(prevTox,intensity,TOX_POWER,TOX_DECAY);
    }

    public int ReactionDiffusionHAP(PDEGrid2DCoarse diff, PDEGrid2DCoarse activated, double diffRate1, double diffRate2, double vesselConc, double decayRate1, double decayRate2, CellAbsorption Absorption1, CellAbsorption Absorption2, double timeScale) {
        int i=0;
        do {
            i++;
            for (C v : G().vesselList) {
                diff.SetPartial(v.Xsq(), v.Ysq(), vesselConc, G().DIFF_TIMESTEP * timeScale);
            }
            if (decayRate1 > 0) {
                diff.grid.MulAll(Math.pow(1.0 - decayRate1, G().DIFF_TIMESTEP * timeScale));
            }
            if (decayRate2 > 0) {
                activated.grid.MulAll(Math.pow(1.0 - decayRate2, G().DIFF_TIMESTEP * timeScale));
            }
            if (Absorption1 != null) {
                for (C cell : G()) {
                    if (cell.type == NORMAL || cell.type == TUMOR) {
                        diff.Mul(cell.Xsq(), cell.Ysq(), Math.pow((1.0 - Absorption1.CalcAbsorption(cell)), G().DIFF_TIMESTEP * timeScale));
                    }
                }
            }
            if (Absorption2 != null) {
                for (C cell : G()) {
                    if (cell.type == NORMAL || cell.type == TUMOR) {
                        activated.Mul(cell.Xsq(), cell.Ysq(), Math.pow((1.0 - Absorption2.CalcAbsorption(cell)), G().DIFF_TIMESTEP * timeScale));
                    }
                }
            }
            if (diffRate1 > 0) {
//            System.out.println(diffRate* G().DIFF_TIMESTEP * timeScale / (G().DIFF_SPACE_SCALE * G().DIFF_SPACE_SCALE * G().SQUARE_DIAM * G().SQUARE_DIAM));
                diff.DiffusionADI(diffRate1 * G().DIFF_TIMESTEP * timeScale / (G().DIFF_SPACE_SCALE * G().DIFF_SPACE_SCALE * G().SQUARE_DIAM * G().SQUARE_DIAM));
            }
            if (diffRate2 > 0) {
//            System.out.println(diffRate* G().DIFF_TIMESTEP * timeScale / (G().DIFF_SPACE_SCALE * G().DIFF_SPACE_SCALE * G().SQUARE_DIAM * G().SQUARE_DIAM));
                activated.DiffusionADI(diffRate2 * G().DIFF_TIMESTEP * timeScale / (G().DIFF_SPACE_SCALE * G().DIFF_SPACE_SCALE * G().SQUARE_DIAM * G().SQUARE_DIAM));
            }
            //ACTIVATION
            for (int j = 0; j < diff.grid.length; j++) {
                if (G().oxygen.grid.Get(j) < LETHALITY_START_O2) {
                    double amountActivated=diff.grid.Get(j) * ACTIVATION_RATE;
                    activated.grid.Set(j, Bound(activated.grid.Get(j)+amountActivated,0,1));
                    diff.grid.Add(j,-amountActivated);
                }
            }
        }
            while (diff.grid.MaxDifference() > STEADY_STATE && activated.grid.MaxDifference() > STEADY_STATE && i < MAX_STEPS)
                ;
           // System.out.println(i);
//            if (i == MAX_STEPS) {
//                //System.out.println("drug " + name + " ran diffusion for max steps");
//            }
            return i;
        }


    public void OnStep(double intensity) {
            ReactionDiffusionHAP(conc,concActivated, DIFF_RATE1,DIFF_RATE2, intensity, DECAY_RATE1,DECAY_RATE2, (c) -> ABSORPTION_RATE1,(c)->ABSORPTION_RATE2, 1);
            //drug has to diffuse and decay
            //drug has to dissociate
            //activated drug is absorbed
    }

    //looks like activation?
    public double DeathProb(C c, double intensity){
        double o2=c.GetInterp(G().oxygen);
        if(o2<LETHALITY_START_O2){
                //return c.GetInterp(conc)*(1.0-o2/LETHALITY_START_O2)*(1.0-(c.pumpPheno*PUMP_PHENO_SCALE));
            //no pump pheno
            //return 1;
            return c.GetInterp(concActivated)*LETHALITY;
            }
        return 0;
    }
}
