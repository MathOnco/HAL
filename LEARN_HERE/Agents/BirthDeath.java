package LEARN_HERE.Agents;

import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.Gui.GridWindow;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.UIGrid;
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
            int nOptions = G().MapEmptyHood(G().mooreHood, Xsq(), Ysq());
            if(nOptions>0) {
                G().NewAgentSQ(G().mooreHood[G().rn.Int(nOptions)]);
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
    public BirthDeath(int x, int y) {
        super(x, y, Cell.class);
    }
    public void Setup(double rad){
        int[]coords= CircleHood(true,rad);
        int nCoords= MapHood(coords,xDim/2,yDim/2);
        for (int i = 0; i < nCoords ; i++) {
            NewAgentSQ(coords[i]);
        }
    }
    public void Step(UIGrid vis) {
        for (Cell c : this) {
            c.Step();
        }
        for (int i = 0; i < vis.length; i++) {
            Cell c = GetAgent(i);
            vis.SetPix(i, c == null ? BLACK : RGB(1,1,0));
        }
        CleanAgents();
        ShuffleAgents(rn);
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
