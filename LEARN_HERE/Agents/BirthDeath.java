package LEARN_HERE.Agents;

import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Models.Raf.BirthDeathAgent2D;
import Framework.Gui.GridVisWindow;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.GuiGridVis;

import java.util.Random;

import static Framework.Utils.*;

/**
 * Created by Rafael on 9/5/2017.
 */

class Cell extends AgentSQ2Dunstackable<BirthDeath> {
    int color;

    public double GetDeathProb() {
        return 0.1;
    }

    public double GetBirthProb() {
        return 0.2;
    }
    public void Step() {
        if (G().rn.nextDouble() < GetDeathProb()) {
            Dispose();
        }
        if (G().rn.nextDouble() < GetBirthProb()) {
            int nOptions = G().HoodToEmptyIs(G().mooreHood, G().hoodIs, Xsq(), Ysq());
            if (nOptions > 0) {
                int newColor=color;
                newColor=SetRed(newColor,GetRed(newColor)+G().rn.nextDouble()*0.1-0.05);
                newColor=SetGreen(newColor,GetGreen(newColor)+G().rn.nextDouble()*0.1-0.05);
                newColor=SetBlue(newColor,GetBlue(newColor)+G().rn.nextDouble()*0.1-0.05);
                G().NewAgentSQ(G().hoodIs[G().rn.nextInt(nOptions)]).color=newColor;
            }
        }
    }
}

public class BirthDeath extends AgentGrid2D<Cell> {
    int GREY=RGB(0.5,0.5,0.5);
    int BLACK=RGB(0,0,0);
    double DEATH_PROB=0.1;
    double BIRTH_PROB=0.2;
    Random rn=new Random();
    int[]mooreHood=MooreHood(false);
    int[]hoodIs=new int[mooreHood.length];
    public BirthDeath(int x, int y, Class<Cell>cellClass) {
        super(x, y, cellClass);
    }
    public void Setup(double rad){
        int[]coords= CircleHood(true,rad);
        int[]Is=new int[coords.length/2];
        int nCoords= HoodToEmptyIs(coords,Is,xDim/2,yDim/2);
        for (int i = 0; i < nCoords ; i++) {
            NewAgentSQ(Is[i]).color=GREY;
        }
    }
    public void Step(GuiGridVis vis) {
        for (Cell c : this) {
            c.Step();
        }
        for (int i = 0; i < vis.length; i++) {
            Cell c = GetAgent(i);
            vis.SetPix(i, c == null ? BLACK : c.color);
        }
        CleanShuffInc(rn);
    }


    public static void main(String[] args) {
        GridVisWindow win=new GridVisWindow(100,100,10);
        BirthDeath t=new BirthDeath(100,100,Cell.class);
        t.Setup(10);
        for (int i = 0; i < 100000; i++) {
            win.TickPause(10);
            t.Step(win);
        }
    }
}
