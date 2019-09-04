package Testing.OldTests;

import HAL.GridsAndAgents.SphericalAgent2D;
import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Rand;

import java.util.ArrayList;
import java.util.Random;

import static HAL.Util.*;

/**
 * Created by Rafael on 8/2/2017.
 */

class OnLatticeGrid extends AgentGrid2D<OnLatticeCell> {
    Rand rn;
    int[] mooreHood=MooreHood(false);
    OnLatticeGrid(int x,int y,int pop){
        super(x,y,OnLatticeCell.class);
        int[] popIs=new int[length];
        rn=new Rand();
        for (int i = 0; i < length; i++) {
            popIs[i]=i;
        }
        rn.Shuffle(popIs, length, pop);
        for (int i = 0; i < pop; i++) {
            NewAgentSQ(popIs[i]);
        }
    }
    void Step(){
        for (OnLatticeCell c : this) {
            int nLocs=MapHood(mooreHood,c.Isq());
            c.chosenI=mooreHood[rn.Int(nLocs)];
        }
    }
}

class OnLatticeCell extends AgentSQ2Dunstackable<OnLatticeGrid> {
    int chosenI;
}

class OffLatticeCell extends SphericalAgent2D<OffLatticeCell,OffLatticeGrid> {}

class OffLatticeGrid extends AgentGrid2D<OffLatticeCell> {
    Random rn;
    double cellRad=0.3;
    ArrayList<OffLatticeCell> cellScratch;
    double interactionRad=cellRad*2;
    double forceExp=2;
    double forceMul=1;
    double friction=0.1;
    OffLatticeGrid(int x,int y,int pop){
        super(x,y,OffLatticeCell.class);
        rn=new Random();
        for (int i = 0; i < pop; i++) {
            double xChild=rn.nextDouble()*xDim;
            double yChild=rn.nextDouble()*yDim;
            NewAgentPT(xChild,yChild);
        }
    }
    void Step(){
        for (OffLatticeCell c : this) {
            c.SumForces(cellRad*2,(overlap,agent)->Math.abs(Math.pow(overlap/interactionRad,forceExp))*forceMul);
        }
        for (OffLatticeCell c : this) {
            c.ApplyFriction(friction);
            c.ForceMove();
        }
    }
}


public class OnVsOffLatticeSpeedTest {//compares looking around an on-lattice neighborhood vs resolving forces
    public static void main(String[] args) {
        int[]pops=new int[]{10,100,1000,2000,3000};
        for (int j = 0; j < pops.length; j++) {
            System.out.println("testing" + pops[j]);
            OnLatticeGrid olg = new OnLatticeGrid(100, 100, pops[j]);
            double start = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++) {
                olg.Step();
            }
            System.out.println(System.currentTimeMillis() - start);
            OffLatticeGrid flg = new OffLatticeGrid(100, 100, pops[j]);
            start = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++) {
                flg.Step();
            }
            System.out.println(System.currentTimeMillis() - start);
        }
    }
}
