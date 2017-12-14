package Examples._2StemCellExample;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.*;
import Framework.Tools.FileIO;
import Framework.Rand;
import Framework.Util;

import java.util.Arrays;

import static Framework.Util.RGB;

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
                G().vis.SetPix(Xsq(), Ysq(), RGB(0,0,(divs+1.0)/(G().MAX_DIVS)));
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
        if(stem&&G().rn.Double()<G().STEM_DIV_PROB){ stemChild=true; }
        else{ divsChild--; }
        G().NewAgentSQ(iChildLoc).Init(divsChild,stemChild);//create a new cell and initialize it
        if(!stem) {
            divs--;
        }
    }

    void Step(){
        G().cellCts[stem?0:1]++;//add 1 to either 0th or 1st entry, depending on whether cell is stem
        //random death
        if(G().rn.Double()<G().DEATH_PROB){
            Die();
            return;
        }
        //check if division event will occur
        if(G().rn.Double()<G().DIV_PROB){
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
                int divI=G().divIs[G().rn.Int(openings)];
                Divide(divI);
            }
        }
    }
}

class StemCellCA extends AgentGrid2D<CACell> {
    static final int WHITE =RGB(1,1,1),BLACK=RGB(0,0,0), RED =RGB(1,0,0);
    double DIV_PROB;
    double DEATH_PROB;
    double STEM_DIV_PROB;
    int MAX_DIVS;//models telomere shortening

    //value containers used in cell division function
    final int[] divIs=new int[8];
    final int[] localIs=new int[8];
    final int[] mooreHood= Util.MooreHood(false);
    final int[] cellCts=new int[2];
    int RUN_DURATION;
    int TICK_PAUSE;
    FileIO outFile;

    GuiGrid vis;
    GuiLabel tickLabel;
    GuiLabel popLabel;
    final Rand rn;
    public void Init(double DIV_PROB, double DEATH_PROB, double STEM_DIV_PROB, int MAX_DIVS, int TICK_PAUSE, int RUN_DURATION, String outFileName, boolean record, GuiGrid vis, GuiLabel tickLabel, GuiLabel popLabel, GuiWindow win){
        this.Reset();
        this.DIV_PROB =DIV_PROB;
        this.DEATH_PROB =DEATH_PROB;
        this.STEM_DIV_PROB =STEM_DIV_PROB;
        this.MAX_DIVS=MAX_DIVS;
        this.TICK_PAUSE=TICK_PAUSE;
        this.RUN_DURATION=RUN_DURATION;
        this.outFile=record?new FileIO(outFileName,"w"):null;
        this.vis=vis;
        this.tickLabel=tickLabel;
        this.popLabel=popLabel;
        vis.Clear(BLACK);//clear the visualization for a new round
    }
    public void Run(){
        while(GetTick()< RUN_DURATION){
            if(GetPop()==0){
                //seed a new agent if grid is empty
                NewAgentSQ(xDim/2,yDim/2).Init(MAX_DIVS,true);
            }
            //iterates over all cells and calls their step function
            Arrays.fill(cellCts,0);
            for(CACell c:this){
                c.Step();
            }
            if(outFile!=null&&!outFile.IsClosed()){
                outFile.Write(Util.ArrToString(cellCts,",")+"\n");
            }
            //increments tick, cleans and shuffles the agentlist
            CleanShuffInc(rn);
            //imposes a tick rate
            vis.TickPause(TICK_PAUSE);
            //displays the current tick
            tickLabel.SetText("Timestep "+GetTick());
            popLabel.SetText("Population "+GetPop());
        }
    }

    StemCellCA(int xDim, int yDim){
        //typeGrid constructor, passes the agent class which is used to make agents when NewAgentPT is called
        super(xDim,yDim,CACell.class,true,true);
        rn=new Rand();
    }
    public static void main(String[] args){
        //main menu gui defined
        GuiWindow win=new GuiWindow("StemCellCA menu",true);
        //ParamSet stores all menu options
        win.AddCol(0, new GuiFileChooserField("OUTPUT_FILE","buf.csv").SetColor(RED,BLACK));
        win.AddCol(0, new GuiDoubleField("DIV_PROB",1.0/24,0,1).SetColor(WHITE, BLACK));
        win.AddCol(0, new GuiDoubleField("DEATH_PROB",1.0/1000,0,1).SetColor(WHITE, BLACK));
        win.AddCol(1, new GuiDoubleField("STEM_DIV_PROB",7.0/10,0,1).SetColor(WHITE, BLACK));
        win.AddCol(1, new GuiIntField("MAX_DIVS",11,1,100).SetColor(RED, BLACK));
        win.AddCol(1, new GuiIntField("RUN_TICKS",20000,0,1000000).SetColor(WHITE, BLACK));
        win.AddCol(1, new GuiIntField("TICK_PAUSE",0,0,1000).SetColor(WHITE, BLACK));
        //Run button definition, includes run button action
        win.SetColor(BLACK);
        final StemCellCA runGrid=new StemCellCA(200,200);
        GuiGrid vis=new GuiGrid(runGrid.xDim,runGrid.yDim,5,2,1,true);
        GuiLabel tickLabel=new GuiLabel("TimeStep                       ");
        GuiLabel popLabel=new GuiLabel("Population                       ");
        win.AddCol(0,new GuiBoolField("Record",false).SetColor(RED,WHITE));
        win.AddCol(0, new GuiButton("Run",true,(clickEvent)->{//inline function defines what happens when the run button is clicked
            //greys out the menu gui while the model is running
            win.GreyOut(true);
            GuiWindow visGui=new GuiWindow("StemCellCA",false,(closeEvent)->{//guiwindow inline function defines what happens when the agent visualization gui is closed
                win.GreyOut(false);//allow interaction with the menu gui again
                if(runGrid.outFile!=null) {
                    runGrid.outFile.Close();//make sure to close the file, even if execution is cut off
                    System.out.println("closed file "+runGrid.outFile.fileName);
                }
                runGrid.RUN_DURATION=0;//stop the model if the visualization is closed
            });
            //visGui contains a label that displays the tick and another that displays the population
            visGui.AddCol(0, tickLabel);
            visGui.AddCol(1, popLabel);
            visGui.AddCol(0, vis);
            runGrid.Init(win.GetDouble("DIV_PROB"),win.GetDouble("DEATH_PROB"),win.GetDouble("STEM_DIV_PROB"),win.GetInt("MAX_DIVS"),win.GetInt("TICK_PAUSE"),win.GetInt("RUN_TICKS"),win.GetString("OUTPUT_FILE"),win.GetBool("Record"),vis,tickLabel,popLabel,win);
            visGui.RunGui();
            runGrid.Run();
            visGui.Dispose();
        }));
        //starts the main gui
        win.RunGui();
    }
}

