package Examples.PersianModel;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.GifMaker;
import HAL.Gui.GridWindow;
import HAL.Util;

//cells grow and mutate
class CellEx extends AgentSQ2Dunstackable<PersianCarpets>{

    public final static int COOPERATOR = 0, DEFECTOR = 1;

    // cell attributes: type; future_type; fitness
    int type;
    int future_type;
    double fitness;

    /*
        DetermineFitness()
            - loop through focal cell's neighborhood
            - calculate fitness based on payoff matrix
    */

    void DetermineFitness(){

        int nNeighbors = MapHood(G.hood);
        int [] neighborsTypes = new int[] {0,0};
        for (int i = 0; i < nNeighbors; i++) {
            neighborsTypes[G.GetAgent(G.hood[i]).type]++;
        }

        if (type == COOPERATOR) {
            fitness = G.payoff[0]*neighborsTypes[0] + G.payoff[1]*neighborsTypes[1];
        } else {
            fitness = G.payoff[2]*neighborsTypes[0] + G.payoff[3]*neighborsTypes[1];
        }
    }

    /*
        SetFutureType
            - Set focal cell's type to the same type
            as the cell with highest fitness in neighborhood
    */

    void SetFutureType(){
        double MaxFitness = 0; // assumes all elements of payoff are positive
        int future_type = this.type;
        int nNeighbors = MapHood(G.hood);
        for (int i = 0; i < nNeighbors; i++) {
            CellEx thisNeighbor = G.GetAgent(G.hood[i]);
            if (thisNeighbor.fitness > MaxFitness) {
                MaxFitness = thisNeighbor.fitness;
                future_type = thisNeighbor.type;
            }
        }
        this.future_type = future_type;
    }
}

public class PersianCarpets extends AgentGrid2D<CellEx> {

    double b = 1.61; // try b = 1.35, or b = 1.61

    static final int DIMENSION = 101;
    static final int SCALE_FACTOR = 5;
    static final int PAUSE = 50; // set a "pause" between timesteps (milliseconds)
    static final boolean SAVE_GIF = true;

    public double[] payoff = new double[]{1,0,b,0};
    int[] hood = Util.MooreHood(false);
    public GridWindow vis = new GridWindow(DIMENSION,DIMENSION,SCALE_FACTOR);//used for visualization;
    public GifMaker gifMaker = (SAVE_GIF) ? new GifMaker("persian_carpet.gif",100,true) : null;//used for visualization;

    /*
        Persian Carpet CONSTRUCTOR
            - initiate grid of size DIMENSION x DIMENSION
            - wrap-around is true (both dimensions)
            - initialize full grid of cooperators, with single defector
    */

    public PersianCarpets() {
        super(DIMENSION, DIMENSION, CellEx.class,true,true);

        // set the full domain to "Cooperator" type
        for (int i = 0; i < length; i++) {
            CellEx c = NewAgentSQ(i);
            c.type = CellEx.COOPERATOR;
        }

        // set the center cell to "Defector" type
        CellEx c1 = GetAgent((this.xDim-1)/2,(this.xDim-1)/2);
        c1.type = CellEx.DEFECTOR;
    }

    /*
        StepCells()
            - loop through each cell at every time step
            - update each cell's fitness
            - compare focal cell's fitness to neighborhood, switch to type of max fitness
    */

    public void StepCells(){
        for (CellEx c : this) { c.DetermineFitness(); }
        for (CellEx c : this) { c.SetFutureType(); }
        Draw();
        if (SAVE_GIF) { gifMaker.AddFrame(vis); }
        for (CellEx c : this) { c.type = c.future_type; }
    }

    /*
        Persian Carpet model
            - Cooperators vs Defectors
            - competing on a fitness landscape w/ payoff matrix
            - each cell updates to the type of cell w/ highest fitness in Moore neighborhood
            - synchronous updating
    */

    public static void main(String[]args){

        PersianCarpets grid=new PersianCarpets();

        grid.Draw();
        for (int tick = 0; tick < 200; tick++) {
            grid.StepCells();
            grid.Draw();
        }

        if (grid.SAVE_GIF) { grid.gifMaker.Close(); }
    }

    public void Draw() {
        for (int i = 0; i < vis.length; i++) {
            CellEx c = this.GetAgent(i);
            if (c!=null) {
                // set color based on cell's future type AND cell's previous type
                if ((c.type == CellEx.DEFECTOR) && (c.future_type == CellEx.DEFECTOR )) {
                    this.vis.SetPix(i,Util.RED);
                } else if ((c.type == CellEx.DEFECTOR) && (c.future_type == CellEx.COOPERATOR )) {
                    this.vis.SetPix(i,Util.GREEN);
                } else if ((c.type == CellEx.COOPERATOR) && (c.future_type == CellEx.DEFECTOR )) {
                    this.vis.SetPix(i,Util.YELLOW);
                } else {
                    this.vis.SetPix(i,Util.BLUE);
                }
            }
        }
        this.vis.TickPause(PAUSE);
    }
}