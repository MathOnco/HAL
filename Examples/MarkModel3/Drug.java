package Examples.MarkModel3;

import Framework.Extensions.PDEGrid2DCoarse;
import Framework.Gui.GuiGrid;

import java.io.Serializable;

import static Examples.MarkModel3.Tissue.NORMAL;
import static Examples.MarkModel3.Tissue.TUMOR;


/**
 * Created by marybravo on 11/23/17.
 */
public abstract class Drug<C extends Cell,T extends Tissue<C>> implements Serializable{
    private T g;
    public final String name;
    private int index;
    public final int color;
    public double MAX_STEPS=100;
    public double STEADY_STATE=0.001;

    public Drug(String name, int color) {
        this.name = name;
        this.color=color;
    }

    /*
    every step [set vessel concs]
    every cell step?
    every cell division?
    every cell death?
    every vessel birth?
    every vessel death?
    for every vessel?
    affect prob death
    affect prob birth
    */
    public void _Init(T myTissue){
        this.g=myTissue;
        this.index=myTissue.allDrugs.size();
    }
    //once at setup (after constants are set)
    public void Setup(){}
    public double DefaultToxCalc(double prevTox,double intensity,double toxPower,double toxDecay){
        return (prevTox+toxPower*intensity)*toxDecay;
    }
    abstract public void Draw(GuiGrid vis);

    public abstract double ToxCalc(double prevTox,double intensity);

    public T G(){
        return g;
    }
    public int I(){
        return index;
    }

    public int ReactionDiffusion(PDEGrid2DCoarse diff, double diffRate, double vesselConc, double decayRate, CellAbsorption Absorption, double timeScale) {
        int i=0;
        do {
            i++;
            for (C v : G().vesselList) {
                diff.SetPartial(v.Xsq(), v.Ysq(), vesselConc, G().DIFF_TIMESTEP * timeScale);
            }
            if (decayRate > 0) {
                diff.grid.MulAll(Math.pow(1.0 - decayRate, G().DIFF_TIMESTEP * timeScale));
            }
            if (Absorption != null) {
                for (C cell : G()) {
                    if (cell.type == NORMAL || cell.type == TUMOR) {
                        diff.Mul(cell.Xsq(), cell.Ysq(), Math.pow((1.0 - Absorption.CalcAbsorption(cell)), G().DIFF_TIMESTEP * timeScale));
                    }
                }
            }
            if (diffRate > 0) {
//            System.out.println(diffRate* G().DIFF_TIMESTEP * timeScale / (G().DIFF_SPACE_SCALE * G().DIFF_SPACE_SCALE * G().SQUARE_DIAM * G().SQUARE_DIAM));
                diff.DiffusionADI(diffRate * G().DIFF_TIMESTEP * timeScale / (G().DIFF_SPACE_SCALE * G().DIFF_SPACE_SCALE * G().SQUARE_DIAM * G().SQUARE_DIAM));
            }
        }while (diff.grid.MaxDifference()>STEADY_STATE&&i<MAX_STEPS);
        System.out.println(i);
        if(i==MAX_STEPS){
            System.out.println("drug "+name+" ran diffusion for max steps");
        }
        return i;
    }

    public PDEGrid2DCoarse GenDiff(){
        return new PDEGrid2DCoarse(G().xDim/G().DIFF_SPACE_SCALE,G().yDim/G().DIFF_SPACE_SCALE,G().DIFF_SPACE_SCALE);
    }

    //once every timestep
    public void OnStep(double intensity){}

    //once every diff step

    //once every cell/vessel diff step
    public void OnCellDiffStep(C c,double intensity){}

    //once every cell/vessel step
    public void OnCellStep(C c,double intensity){}

    //birth/death events
    public void BeforeDivision(C c, double intensity){}

    //influence birth/death probs
    public double DeathProb(C c,double intensity){return 1;}
    public double VesselDivisionProb(int iUnscaled,double intensity){return 1;}
}
