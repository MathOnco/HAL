package LEARN_HERE.Agents;

import HAL.GridsAndAgents.PopulationGrid2D;
import HAL.Gui.GridWindow;
import HAL.Rand;
import HAL.Tools.MultinomialCalc;
import HAL.Util;


public class PopGrid {
    public static void main(String[] args) {

        //generate the model objects
        PopulationGrid2D cells = new PopulationGrid2D(50, 50);
        Rand rng=new Rand();
        MultinomialCalc mn = new MultinomialCalc(rng);
        GridWindow win = new GridWindow(cells.xDim, cells.yDim,10);
        cells.Set(0, 0, 100000000);
        int[] hood = Util.VonNeumannHood(false);

        //main loop
        while (true) {
            cells.Update();

            //move cells
            for (int i : cells) {
                int numMoves = cells.MapHood(hood, i);
                mn.Setup(cells.Get(i));
                for (int j = 0; j < numMoves; j++) {
                    int movepop = mn.Sample(0.1);
                    cells.Add(i, -movepop);
                    cells.Add(hood[j], movepop);
                }
            }

            //draw cells
            win.TickPause(0);
            for(int i=0;i<cells.length;i++) {
                win.SetPix(i,Util.HeatMapRGB(cells.Get(i)/100000.0));
            }

        }
    }
}
