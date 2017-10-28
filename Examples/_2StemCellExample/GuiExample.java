package Examples._2StemCellExample;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.*;
import Framework.Gui.TickTimer;
import Framework.Utils;
import java.util.Random;

import static Framework.Utils.RGB;

class CACell extends AgentSQ2Dunstackable<StemCellCA> {
    int divs;
    boolean stem;
    void Init(int divs, boolean stem){
        //win agent properties
        this.divs=divs;
        this.stem=stem;

        //draw agent on vis if it exists
        if(G().vis!=null) {
            if (stem) {
                G().vis.SetPix(Xsq(), Ysq(), StemCellCA.RED);
            }
            else{
                G().vis.SetPix(Xsq(), Ysq(), RGB(0,0,(divs+1.0)/(G().maxDivs)));
            }
        }
    }
    void Die(){
        //win visActions square to black if visActions exists
        if(G().vis!=null) {
            G().vis.SetPix(Xsq(), Ysq(), StemCellCA.BLACK);
        }
        Dispose();
    }
    //requires location to place the child into
    void Divide(int iChildLoc) {
        boolean stemChild=false;
        int divsChild=divs;
        if(stem&&G().rn.nextDouble()<G().stemDivProb){ stemChild=true; }
        else{ divsChild--; }
        G().NewAgentSQ(iChildLoc).Init(divsChild,stemChild);
        if(!stem) {
            divs--;
        }
    }

    void Step(){
        //random death
        if(G().rn.nextDouble()<G().deathProb){
            Die();
            return;
        }
        //check if division event will occur
        if(G().rn.nextDouble()<G().divProb){
            //get moore neighborhood around cell, ignores indices that fall outside the bounds of the model
            int checkLen=HoodToIs(G().mooreHood,G().localIs);
            int openings=0;
            //get indices of all moore neighborhood locations that do not have cells occupying them
            for(int i=0;i<checkLen;i++){
                int iLoc=G().localIs[i];
                if(G().GetAgent(iLoc)==null){
                    G().divIs[openings]=iLoc;
                    openings++;
                }
            }
            //can only divide if there is space
            if(openings>0){
                //die if out of divisions
                if(divs==0){
                    Die();
                    return;
                }
                //choose a random location to divide into
                int divI=G().divIs[G().rn.nextInt(openings)];
                Divide(divI);
            }
        }
    }
}

class StemCellCA extends AgentGrid2D<CACell> {
    static final int WHITE =RGB(1,1,1),BLACK=RGB(0,0,0), RED =RGB(1,0,0);
    //model specific params
    final double divProb;
    final double deathProb;
    final double stemDivProb;
    final int maxDivs;

    //value containers used in cell division function
    final int[] divIs=new int[8];
    final int[] localIs=new int[8];
    final int[] mooreHood= Utils.MooreHood(false);

    final GuiGridVis vis;
    final Random rn;

    StemCellCA(int xDim, int yDim, double divProb, double deathProb, double stemDivProb, int maxDivs, GuiGridVis vis){
        //typeGrid constructor, passes the agent class which is used to make agents when NewAgentPT is called
        super(xDim,yDim,CACell.class,true,true);
        this.vis=vis;
        this.divProb=divProb;
        this.deathProb=deathProb;
        this.stemDivProb=stemDivProb;
        this.maxDivs=maxDivs;
        rn=new Random();

        //add a first agent to the middle of the world
        CACell c=NewAgentSQ(xDim/2,yDim/2);
        c.Init(maxDivs,true);
    }
    public static void main(String[] args){
        //main menu gui defined
        GuiWindow win=new GuiWindow("StemCellCA menu",true);
        //ParamSet stores all menu options
        win.AddCol(0, new GuiDoubleField("DIV_PROB",1.0/24,0,1).SetColor(WHITE, BLACK));
        win.AddCol(0, new GuiDoubleField("DEATH_PROB",1.0/1000,0,1).SetColor(WHITE, BLACK));
        win.AddCol(0, new GuiDoubleField("stemDivProb",7.0/10,0,1).SetColor(WHITE, BLACK));
        win.AddCol(0, new GuiIntField("maxDivs",11,1,100).SetColor(WHITE, BLACK));
        win.AddCol(1, new GuiIntField("visScale",5,0,10).SetColor(WHITE, BLACK));
        win.AddCol(1, new GuiIntField("runTicks",20000,0,1000000).SetColor(WHITE, BLACK));
        win.AddCol(1, new GuiIntField("TimeStep",0,0,1000).SetColor(WHITE, BLACK));
        win.AddCol(1, new GuiIntField("worldDims",200,0,1000).SetColor(WHITE, BLACK));
        //Run button definition, includes run button action
        win.SetColor(BLACK);
        win.AddCol(0, new GuiButton("Run",true,(clickEvent)->{//inline function defines what happens when the button is clicked
            //greys out the gui while the model is running
            win.GreyOut(true);
            final int[] runDuration = {win.GetInt("runTicks")};//array is used so the value can be rewin on close
            //visualization gui defined, with close event that causes the model to stop execution
            GuiWindow visGui=new GuiWindow("StemCellCA",false,(closeEvent)->{
                runDuration[0]=0;
                win.GreyOut(false);
            }, true);
            //defines visualization window, wins dimensions and how many gui squares the visualization window will take up
            GuiGridVis vis=new GuiGridVis(win.GetInt("worldDims"),win.GetInt("worldDims"),win.GetInt("visScale"),2,1, true);
            GuiLabel tickLabel=new GuiLabel("TimeStep                       ");
            GuiLabel popLabel=new GuiLabel("Population                       ");
            //visGui contains a label that displays the tick
            visGui.AddCol(0, tickLabel);
            visGui.AddCol(1, popLabel);
            visGui.AddCol(0, vis);
            //defines the typeGrid that will be run, pulling typeGrid initialization values from the menuwin
            StemCellCA runGrid=new StemCellCA(win.GetInt("worldDims"),win.GetInt("worldDims"),win.GetDouble("DIV_PROB"),
                    win.GetDouble("DEATH_PROB"),win.GetDouble("stemDivProb"),win.GetInt("maxDivs"),vis);
            long timeStep=win.GetInt("TimeStep");
            TickTimer timer=new TickTimer();
            //starts the visualization gui
            visGui.RunGui();
            while(runGrid.GetTick()< runDuration[0]){
                //iterates over all cells and calls their step function
                for(CACell c:runGrid){
                    c.Step();
                }
                //increments tick, cleans and shuffles the agentlist
                runGrid.CleanShuffInc(runGrid.rn);
                //imposes a tick rate
                timer.TickPause(timeStep);
                //displays the current tick
                tickLabel.SetText("Timestep "+runGrid.GetTick());
                popLabel.SetText("Population "+runGrid.GetPop());
            }
            //destroys visGui when run is complete, calling close event
            visGui.Dispose();
        }));
        //starts the main gui
        win.RunGui();
    }
}

