package Examples._06OffLatticeExample;

import Framework.Extensions.SphericalAgent2D;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.Vis2DOpenGL;
import Framework.Tools.FileIO;
import Framework.Utils;

import java.util.ArrayList;
import java.util.Random;

import static Framework.Utils.RGB256;

class CellOL extends SphericalAgent2D<CellOL,ExampleOffLattice>{
    int type;
    double forceSum;
    public void Init(int color){
        this.type =color;
        this.radius=G().RADIUS;
    }
    double ForceCalc(double overlap){
        if(overlap<0) {
            return 0;
        }
        return Math.pow(G().FORCE_SCALER*overlap,G().FORCE_EXPONENT);
    }
    public void CalcMove(){
        //sets x and y velocity components of cell
        forceSum=SumForces(G().RADIUS*2,G().neighborList,this::ForceCalc);
    }
    public boolean CanDivide(double div_bias,double inhib_weight){
        return G().rn.nextDouble()<Math.tanh(div_bias-forceSum*inhib_weight);
    }
    public void MoveDiv(){
        //move cell and reduce x and y velocity components by friction constant
        ForceMove();
        xVel*=G().FRICTION;
        yVel*=G().FRICTION;
        //compute whether division can occur, using the constants
        if((type == ExampleOffLattice.PURPLE &&CanDivide(G().PURP_DIV_BIAS,G().PURP_INHIB_WEIGHT))||(type == ExampleOffLattice.PINK &&CanDivide(G().PINK_DIV_BIAS,G().PINK_INHIB_WEIGHT))){
            Divide(radius*2.0/3.0,G().divCoordStorage,G().rn).Init(type);
        }
    }
}

public class ExampleOffLattice extends AgentGrid2D<CellOL> {
    static final int WHITE=RGB256(248,255,252), PURPLE =RGB256(77,0,170), PINK =RGB256(222,0,109),CYTOPLASM=RGB256(191,156,147);
    double RADIUS=0.5;
    double FORCE_EXPONENT=2;
    double FORCE_SCALER=0.7;
    double FRICTION=0.5;
    double PURP_DIV_BIAS =0.01;
    double PINK_DIV_BIAS =0.02;
    double PURP_INHIB_WEIGHT =0.015;
    double PINK_INHIB_WEIGHT =0.05;
    ArrayList<CellOL> neighborList=new ArrayList<>();
    double[]divCoordStorage=new double[2];
    Random rn=new Random();
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
        Vis2DOpenGL vis=new Vis2DOpenGL(1000,1000,x,y,"Off Lattice Example");
        ex.Setup(50,5,0.5);
        for (int i = 0; i < 10000; i++) {
            if(vis.CheckClosed()){
                break;
            }
            vis.TickPause(0);
            ex.StepCells();
            ex.DrawCells(vis);
        }
        if(ex.out!=null){
            ex.out.Close();
        }
        vis.Dispose();
    }
    public void Setup(double initPop,double initRadius,double propRed){
        for (int i = 0; i < initPop; i++) {
            Utils.RandomPointInCircle(initRadius,divCoordStorage,rn);
            //create a new agent, and set the type depending on a comparison with the random number generator
            NewAgentPT(divCoordStorage[0]+xDim/2.0,divCoordStorage[1]+yDim/2.0).Init(rn.nextDouble()<propRed? PURPLE : PINK);
        }
    }
    public void DrawCells(Vis2DOpenGL vis){
        vis.Clear(WHITE);
        for (CellOL cell : this) {
            //draw "cytoplasm" of cell
            vis.Circle(cell.Xpt(),cell.Ypt(),cell.radius,CYTOPLASM);
        }
        for (CellOL cell : this) {
            //draw colored "nucleus" on top of cytoplasm
            vis.Circle(cell.Xpt(), cell.Ypt(), cell.radius / 3, cell.type);
        }
        vis.Show();
    }
    public void StepCells(){
        for (CellOL cell : this) {
            cell.CalcMove();
        }
        for (CellOL cell : this) {
            cell.MoveDiv();
        }
        if(out!=null){
            //if an output file has been generated, write to it
            RecordOut(out);
        }
        IncTick();
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
