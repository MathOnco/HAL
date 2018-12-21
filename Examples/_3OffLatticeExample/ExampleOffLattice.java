package Examples._3OffLatticeExample;

import Framework.GridsAndAgents.SphericalAgent2D;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.OpenGL2DWindow;
import Framework.Tools.FileIO;
import Framework.Tools.Internal.Gaussian;
import Framework.Rand;

import java.util.ArrayList;

import static Framework.Util.RGB256;

class CellOL extends SphericalAgent2D<CellOL,ExampleOffLattice>{
    int type;
    double forceSum;//used with contact inhibition calculation
    public void Init(int color){
        this.type =color;
        this.radius= G.RADIUS;
    }
    double ForceCalc(double overlap,CellOL other){
        if(overlap<0) {
            return 0;//if cells aren't actually overlapping, then there is no force response
        }
        return Math.pow(G.FORCE_SCALER*overlap, G.FORCE_EXPONENT);
    }
    public void CalcMove(){
        //sets x and y velocity components of cell
        //G.neighborList.clear();
        //G.GetAgentsRad(G.neighborList,G.neighborInfo,Xpt(),Ypt(),G.RADIUS*2);
        forceSum=SumForces(G.RADIUS*2,this::ForceCalc);
        //forceSum=SumForces(G.neighborList,G.neighborInfo,this::ForceCalc);
    }
    public boolean CanDivide(double div_bias,double inhib_weight){
        return G.rn.Double()<Math.tanh(div_bias-forceSum*inhib_weight);
    }
    public void MoveDiv(){
        //move cell and reduce x and y velocity components by friction constant
        ForceMove();
        ApplyFriction(G.FRICTION);
        //compute whether division can occur, using the constants
        if((type == ExampleOffLattice.PURPLE &&CanDivide(G.PURP_DIV_BIAS, G.PURP_INHIB_WEIGHT))||(type == ExampleOffLattice.PINK &&CanDivide(G.PINK_DIV_BIAS, G.PINK_INHIB_WEIGHT))){
            Divide(radius*2.0/3.0, G.divCoordStorage, G.rn).Init(type);
        }
    }
}

public class ExampleOffLattice extends AgentGrid2D<CellOL> {
    static final int WHITE=RGB256(248,255,252), PURPLE =RGB256(77,0,170), PINK =RGB256(222,0,109),CYTOPLASM=RGB256(191,156,147);
    double RADIUS=0.5;

    double FORCE_EXPONENT=2;//these constants have been found to be rather stable, but tweak them and see what happens!
    double FORCE_SCALER=0.7;
    double FRICTION=0.5;

    double PURP_DIV_BIAS =0.01;//grid holds onto phenotype differences, if drift were included these would probably be moved to individual cells.
    double PINK_DIV_BIAS =0.02;
    double PURP_INHIB_WEIGHT =0.015;
    double PINK_INHIB_WEIGHT =0.05;
    ArrayList<CellOL> neighborList=new ArrayList<>();
    ArrayList<double[]> neighborInfo=new ArrayList<>();
    double[]divCoordStorage=new double[2];
    Rand rn=new Rand(0);
    Gaussian gn =new Gaussian();
    FileIO out;

    public ExampleOffLattice(int x, int y) {
        super(x, y, CellOL.class,true,true);
    }
    public ExampleOffLattice(int x, int y,String outFileName) {
        super(x, y, CellOL.class,true,true);
        out=new FileIO(outFileName,"w");
    }
    public static void main(String[] args) {
        int x=60,y=60;
        //to record output, call the constructor with an output filename
        ExampleOffLattice ex=new ExampleOffLattice(x,y,"PopOut.csv");
        //ExampleOffLattice ex=new ExampleOffLattice(x,y);
        OpenGL2DWindow vis=new OpenGL2DWindow("Off Lattice Example", 1000,1000,x,y);
        ex.Setup(50,5,0.5);
        int i=0;
        while(i<10000&&!vis.IsClosed()) {//check for click on close button on window
            vis.TickPause(0);
            ex.StepCells();
            ex.DrawCells(vis);
            i++;
        }
        if(ex.out!=null){
            ex.out.Close();//be sure to call Close when finished writing output to make sure everything is recorded.
        }
        vis.Close();
    }
    public void Setup(double initPop,double initRadius,double propRed){
        for (int i = 0; i < initPop; i++) {
            rn.RandomPointInCircle(initRadius,divCoordStorage);
            //create a new agent, and set the type depending on a comparison with the random number generator
            NewAgentPT(divCoordStorage[0]+xDim/2.0,divCoordStorage[1]+yDim/2.0).Init(rn.Double()<propRed? PURPLE : PINK);
        }
    }
    public void DrawCells(OpenGL2DWindow vis){
        vis.Clear(WHITE);
        for (CellOL cell : this) {
            //draw "cytoplasm" of cell
            vis.Circle(cell.Xpt(),cell.Ypt(),cell.radius,CYTOPLASM);
        }
        for (CellOL cell : this) {
            //draw colored "nucleus" on top of cytoplasm
            vis.Circle(cell.Xpt(), cell.Ypt(), cell.radius / 3, cell.type);
        }
        vis.Update();
    }
    public void StepCells(){
        for (CellOL cell : this) {
            cell.CalcMove();//calculation of forces before any agents move, for simultaneous movement
        }
        for (CellOL cell : this) {
            cell.MoveDiv();//movement and division
        }
        if(out!=null){
            //if an output file has been generated, write to it
            RecordOut(out);
        }
    }
    public void RecordOut(FileIO writeHere){
        int ctPurp=0,ctPink=0;
        for (CellOL cell : this) {
            if(cell.type ==PURPLE){
                ctPurp++;
            } else{
                ctPink++;
            }
        }
        //population of one timestep per line
        writeHere.Write(ctPink+","+ctPurp+"\n");
    }
}
