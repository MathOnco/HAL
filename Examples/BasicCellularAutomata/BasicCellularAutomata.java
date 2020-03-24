package Examples.BasicCellularAutomata;
import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.GifMaker;
import HAL.Gui.GridWindow;
import HAL.Util;

public class BasicCellularAutomata extends AgentGrid2D<BasicCell> {

    public static final int RULE = 35; // must be between 0 and 255
    public static final int DOMAIN_SIZE = 200;
    public GridWindow vis;

    public static void main(String[]args){

        BasicCellularAutomata grid=new BasicCellularAutomata();
        grid.Draw();

        GifMaker gif = new GifMaker("Examples/BasicCellularAutomata/rule"+Integer.toString(RULE)+".gif",10,true);
        gif.AddFrame(grid.vis);

        for (int tick = 2; tick <= grid.yDim; tick++) {
            grid.vis.TickPause(0); // pause for n milliseconds
            grid.StepCells(tick);
            grid.Draw();
            gif.AddFrame(grid.vis);
        }
        gif.Close();
        grid.vis.Close();
    }

    // constructor for the grid
    public BasicCellularAutomata() {
        super(DOMAIN_SIZE*2-1, DOMAIN_SIZE, BasicCell.class,false,false);

        // begin with a single cell in top middle
        this.NewAgentSQ((xDim-1)/2,yDim-1);

        //used for visualization
        int scale_factor = (xDim > 300) ? 1 : 3;
        vis=new GridWindow(xDim,yDim,scale_factor);
    }


    // step through all cells in a given row, determine if they're dead/alive
    public void StepCells(int row){
        int middle_x = (xDim-1)/2; // only need to "check" the middle section
        for (int x = middle_x-(row-1); x <= middle_x+(row-1); x++) {
            // create new cell in row, column
            BasicCell c = this.NewAgentSQ(x,yDim-row);

            // delete the cell if the rule doesn't call for a cell in that location
            if (getBit(RULE,c.DetermineNeighbors()) == 0) { c.Dispose(); }
        }
    }

    public void Draw() {
        for (int i = 0; i < vis.length; i++) {
            BasicCell c = this.GetAgent(i);
            this.vis.SetPix(i, (c!=null) ? Util.BLACK : Util.WHITE);
        }
    }

    // function to determine ith bit of integer
    public static int getBit(int integer, int i) {
        return (integer >> (7-i)) & 1;
    }
}

class BasicCell extends AgentSQ2Dunstackable<BasicCellularAutomata>{

    // a function which returns the neighbors' (row above) states,
    // returns an integer of the case explained by rule, for example:
    // http://mathworld.wolfram.com/Rule30.html
    public int DetermineNeighbors() {
        BasicCell above_left_cell = (this.Xsq()-1 >=0) ? G.GetAgent(this.Xsq()-1,this.Ysq()+1) : null;
        BasicCell above_center_cell = G.GetAgent(this.Xsq(),this.Ysq()+1);
        BasicCell above_right_cell = (this.Xsq()+1 <= G.xDim-1) ? G.GetAgent(this.Xsq()+1,this.Ysq()+1) : null;

        if ((above_left_cell != null) && (above_center_cell != null) && (above_right_cell != null)) { return 0; }
        if ((above_left_cell != null) && (above_center_cell != null) && (above_right_cell == null)) { return 1; }
        if ((above_left_cell != null) && (above_center_cell == null) && (above_right_cell != null)) { return 2; }
        if ((above_left_cell != null) && (above_center_cell == null) && (above_right_cell == null)) { return 3; }
        if ((above_left_cell == null) && (above_center_cell != null) && (above_right_cell != null)) { return 4; }
        if ((above_left_cell == null) && (above_center_cell != null) && (above_right_cell == null)) { return 5; }
        if ((above_left_cell == null) && (above_center_cell == null) && (above_right_cell != null)) { return 6; }
        return 7; // triple null case
    }
}