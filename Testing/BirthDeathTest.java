package Testing;

import Models.Raf.BirthDeathAgent2D;
import Framework.Gui.GridVisWindow;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.GuiGridVis;

import java.util.Random;

import static Framework.Utils.*;

/**
 * Created by Rafael on 9/5/2017.
 */

class Cell extends BirthDeathAgent2D<Cell,BirthDeathTest> {

    @Override
    public double GetDeathProb() {
        return 0.1;
    }

    @Override
    public double GetBirthProb() {
        return 0.2;
    }
}

public class BirthDeathTest extends AgentGrid2D<Cell> {
    Random rn=new Random();
    int[]mooreHood=MooreHood(false);
    int[]mooreIs=new int[mooreHood.length];
    public BirthDeathTest(int x, int y, Class<Cell>cellClass) {
        super(x, y, cellClass);
    }
    public void Setup(double rad){
        int[]coords= CircleHood(true,rad);
        int[]Is=new int[coords.length/2];
        int nCoords= HoodToEmptyIs(coords,Is,xDim/2,yDim/2);
        for (int i = 0; i < nCoords ; i++) {
            Cell c=NewAgentSQ(Is[i]);
        }
    }
    public void Step(GuiGridVis vis){
        for (Cell c : this) {
            c.Step(mooreHood,mooreIs,rn);
        }
        for (int i = 0; i < vis.length; i++) {
            if(GetAgent(i)==null){
                vis.SetPix(i, RGB((double) 0, (double) 0, (double) 0));
            }
            else{
                vis.SetPix(i, RGB((double) 1, (double) 1, (double) 1));
            }
        }
        CleanShuffInc(rn);
    }

    public static void main(String[] args) {
        GridVisWindow win=new GridVisWindow(100,100,10);
        BirthDeathTest t=new BirthDeathTest(100,100,Cell.class);
        t.Setup(10);
        for (int i = 0; i < 100000; i++) {
            win.TickPause(10);
            t.Step(win);
        }
    }
}
