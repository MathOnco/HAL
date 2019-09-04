package Examples.ODEmodel;

import HAL.Gui.PlotLine;
import HAL.Gui.PlotWindow;
import HAL.Tools.ODESolver.ODESolver;

import java.util.ArrayList;

import static HAL.Util.BLUE;
import static HAL.Util.GREEN;

public class TumorGrowth {
    public final double tumorGrowthRate;
    public final double immuneDeathRate;
    public final double immuneKillTumorRate;
    public final double immuneRecrutimentRate;

    public final static int TUMOR=0,IMMUNE=1;

    public TumorGrowth(double tumorGrowthRate, double immuneDeathRate, double immuneKillTumorRate, double immuneRecrutimentRate) {
        this.tumorGrowthRate = tumorGrowthRate;
        this.immuneDeathRate = immuneDeathRate;
        this.immuneKillTumorRate = immuneKillTumorRate;
        this.immuneRecrutimentRate = immuneRecrutimentRate;
    }

    public void PopulationDerivatives(double t,double[]pops,double[]deltas){
        //tumor update
        deltas[TUMOR]=tumorGrowthRate*pops[TUMOR]-immuneKillTumorRate*pops[TUMOR]*pops[IMMUNE];
        deltas[IMMUNE]=-immuneDeathRate*pops[IMMUNE]+immuneRecrutimentRate*pops[TUMOR]*pops[IMMUNE];
    }

    public static void main(String[] args) {
        TumorGrowth model=new TumorGrowth(Math.sqrt(2),0.5,0.001,0.001);
        double[]startPops=new double[]{100,10};
        ArrayList<double[]> states=new ArrayList<>();
        states.add(startPops);
        ArrayList<Double> ts=new ArrayList<>();
        ts.add(0.0);
        PlotWindow win=new PlotWindow("Tumor Immune Example",250,250,4,0,0,1,1);
        PlotLine tumorLine=new PlotLine(win,GREEN);
        PlotLine immuneLine=new PlotLine(win,BLUE);
        ODESolver solver=new ODESolver();
        double dtStart=0.001;
        double errorTol=0.001;
        solver.Runge45(model::PopulationDerivatives,states,ts,10,dtStart,errorTol,0);
        for (int i = 0; i < states.size(); i++) {
            tumorLine.AddSegment(ts.get(i),states.get(i)[0]);
            immuneLine.AddSegment(ts.get(i),states.get(i)[1]);
            win.TickPause(100);
        }
        win.DrawAxesLabels();
        win.ToPNG("TumorImmuneModel.png");
    }
}
