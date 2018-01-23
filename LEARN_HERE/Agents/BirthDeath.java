package LEARN_HERE.Agents;

import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.Gui.GridWindow;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.GuiGrid;
import Framework.Rand;

import static Framework.Util.*;

/**
 * Created by Rafael on 9/5/2017.
 */

class Cell extends AgentSQ2Dunstackable<BirthDeath> {

    public void Step() {
        if (G().rn.Double() < G().DEATH_PROB) {
            Dispose();
        }
        if (G().rn.Double() < G().BIRTH_PROB) {
            int nOptions = G().HoodToEmptyIs(G().mooreHood, G().hoodIs, Xsq(), Ysq());
            if(nOptions>0) {
                G().NewAgentSQ(G().hoodIs[G().rn.Int(nOptions)]);
            }
        }
    }
}

public class BirthDeath extends AgentGrid2D<Cell> {
    int BLACK=RGB(0,0,0);
    double DEATH_PROB=0.01;
    double BIRTH_PROB=0.2;
    Rand rn=new Rand();
    int[]mooreHood=MooreHood(false);
    int[]hoodIs=new int[mooreHood.length];
    public BirthDeath(int x, int y) {
        super(x, y, Cell.class);
    }
    public void Setup(double rad){
        int[]coords= CircleHood(true,rad);
        int[]Is=new int[coords.length/2];
        int nCoords= HoodToEmptyIs(coords,Is,xDim/2,yDim/2);
        for (int i = 0; i < nCoords ; i++) {
            NewAgentSQ(Is[i]);
        }
    }
    public void Step(GuiGrid vis) {
        for (Cell c : this) {
            c.Step();
        }
        for (int i = 0; i < vis.length; i++) {
            Cell c = GetAgent(i);
            vis.SetPix(i, c == null ? BLACK : RGB(1,c.Age()/100.0,0));
        }
        CleanShuffInc(rn);
    }


    public static void main(String[] args) {
        BirthDeath t=new BirthDeath(100,100);
        GridWindow win=new GridWindow(100,100,10);
        t.Setup(10);
        for (int i = 0; i < 100000; i++) {
            win.TickPause(10);
            t.Step(win);
        }
    }
}
