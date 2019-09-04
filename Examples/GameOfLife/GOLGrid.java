package Examples.GameOfLife;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.UIGrid;
import HAL.Gui.UILabel;
import HAL.Gui.UIWindow;

import java.util.Random;

import static Examples.GameOfLife.GOLGrid.*;
import static HAL.Util.*;

/**
 * Created by rafael on 4/16/17.
 */

class GOLAgent extends AgentSQ2Dunstackable<GOLGrid> {
    final int[] state =new int[2];
    int GetCurrState(){
        //modular division used to determine current state entry
        return state[G.tick%2];
    }
    void SetNextState(int state){
        //modular division used to determine next state entry
        this.state[(G.tick+1)%2]=state;
    }
    public void Step(){
        int countNeighbors=0;
        int hoodLen=MapHood(G.mooreHood);
        for (int i = 0; i < hoodLen; i++) {
            if(G.GetAgent(G.mooreHood[i]).GetCurrState()==LIVE){
                countNeighbors++;
            }
        }
        //classic game of life rules
        if((GetCurrState()==LIVE&&(countNeighbors==2||countNeighbors==3))||(GetCurrState()==DEAD&&countNeighbors==3)){
            G.liveCt++;
            SetNextState(LIVE);
        }
        else{
            SetNextState(DEAD);
        }
    }
}

public class GOLGrid extends AgentGrid2D<GOLAgent> {
    public int liveCt;
    final UIGrid vis;
    final int[] mooreHood;
    final int runTicks;
    final int refreshRateMS;
    int tick;
    final static int LIVE = RGB(1,0,0);
    final static int DEAD = RGB(0,0,0);
    GOLGrid(int x, int y, double livingProb, int runTicks, int refreshRateMS, UIGrid vis){
        super(x,y,GOLAgent.class,true,true);
        this.vis=vis;
        Random rn=new Random();
        for (int i = 0; i < length; i++) {
            GOLAgent a= NewAgentSQ(i);
            a.SetNextState(rn.nextDouble() < livingProb?LIVE:DEAD);
        }
        mooreHood=MooreHood(false);
        this.runTicks=runTicks;
        this.refreshRateMS=refreshRateMS;
    }
    public void StepAgents(){
        tick++;
        liveCt =0;//used to total population
        for (GOLAgent a : this) {
            a.Step();
        };
    }
    public void Run(UILabel tickCt, UILabel popCt){
        for (int i = 0; i < runTicks; i++) {
            StepAgents();
            tickCt.SetText("Tick "+tick);
            popCt.SetText("Population "+liveCt);
            for (int j = 0; j < length; j++) {
                vis.SetPix(j,GetAgent(j).GetCurrState());
            }
        }
    }
    public static void main(String[] args){
        int xDim=1000;
        int yDim=1000;
        double livingProb=0.35;
        int runTicks=10000000;
        int refreshRate=0;
        UIWindow gui=new UIWindow("GOL with Agents",true);
        UILabel tickCt=new UILabel("Tick:______________");
        UILabel popCt=new UILabel("Population:________________");
        UIGrid vis=new UIGrid(xDim,yDim,1,2,1);
        gui.AddCol(0, tickCt);
        gui.AddCol(1, popCt);
        gui.AddCol(0, vis);
        GOLGrid gol=new GOLGrid(xDim,yDim,livingProb,runTicks,refreshRate,vis);
        gui.RunGui();
        vis.SetActive(true);
        gol.Run(tickCt,popCt);
        gui.Close();
    }
}
