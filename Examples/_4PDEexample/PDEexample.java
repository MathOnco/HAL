package Examples._4PDEexample;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.GridWindow;
import HAL.Gui.UIGrid;
import HAL.Rand;
import HAL.Util;

import static HAL.Util.HeatMapRGB;
import static HAL.Util.RGB;


class SrcOrSink extends AgentSQ2Dunstackable<PDEexample>{
    int type;
    void Init(int type){
        this.type=type;
    }
    void Reaction(){
        G.diff.Set(Isq(),type==PDEexample.SRC?1:0);//set the local concentration to 1 if source, 0 if sink
    }
}

public class PDEexample extends AgentGrid2D<SrcOrSink> {
    public static int SRC=RGB(0,1,0),SINK=RGB(0,1,1);
    PDEGrid2D diff;
    Rand rn=new Rand();
    public PDEexample(int x, int y) {
        super(x, y, SrcOrSink.class,true,true);
        diff=new PDEGrid2D(x,y);//we add a PDEGrid to store the concentration of our diffusible
    }
    public void Setup(int nSinks,int sinkDist){
        for (int x = xDim/2; x < xDim/2+3; x++) {
            for (int y = yDim/2; y < yDim/2+3; y++) {
                NewAgentSQ(x, y).Init(SRC);//create source
            }
        }
        int[]sinkIs= Util.GenIndicesArray(length);
        rn.Shuffle(sinkIs);
        int sinksPlaced=0;
        for (int i = 0; i < sinkIs.length; i++) {
            int sinkI=sinkIs[i];
            if(DistSquared(ItoX(sinkI),ItoY(sinkI),xDim/2,yDim/2)>sinkDist*sinkDist){
                NewAgentSQ(sinkI).Init(SINK);//create sink
                sinksPlaced++;
                if(sinksPlaced==nSinks){
                    //IncTick(); //IncTick called to make sources and sinks appear during iteration
                    return;
                }
            }
        }
        //IncTick();//in case we never place enough sinks, we still call IncTick to make sure they appear during iteration
    }
    public void Step(int stepI){
        double advectionX=Math.sin(stepI*1.0/1000)*0.2;//sine and cosine based on timestep cause circular advection
        double advectionY=Math.cos(stepI*1.0/1000)*0.2;
        for (SrcOrSink srcOrSink : this) {
            srcOrSink.Reaction();
        }
        diff.Advection(advectionX,advectionY,0);
        double drate=(Math.sin(stepI*1.0/250)+1)*0.05;
        diff.Diffusion(drate);
        diff.Update();
    }
    public void Draw(UIGrid visSrcSinks, UIGrid visDiff){
        for (SrcOrSink srcOrSink : this) {
            visSrcSinks.SetPix(srcOrSink.Isq(),srcOrSink.type);//draw sources and sinks
        }
        for (int i = 0; i < length; i++) {//length of the Grid
            //visDiff.SetPix(i,SetAlpha(HeatMapRGB(diff.Get(i)*4),diff.Get(i)*4));
            visDiff.SetPix(i,HeatMapRGB(diff.Get(i)*4));
        }
    }

    public static void main(String[] args) {
        int x=400,y=400,scale=2;
        GridWindow visCells=new GridWindow(x,y,scale,true,null,false);
        GridWindow visDiff=new GridWindow(x,y,scale);
        //visCells.AddAlphaGrid(visDiff);//facilitates alpha blending
        PDEexample ex=new PDEexample(x,y);
        ex.Setup(100,10);
        int i=0;
        while(true){
            visCells.TickPause(100);//slows down simulation for presentation
            ex.Step(i);
            ex.Draw(visCells,visDiff);
            i++;
        }
    }
}
