package Examples;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GridVisWindow;
import Framework.Gui.GuiGridVis;
import Framework.Utils;

import java.util.Random;

import static Framework.Utils.*;


class SrcOrSink extends AgentSQ2Dunstackable<PDEexample>{
    int type;
    void Init(int type){
        this.type=type;
    }
    void Reaction(){
        G().diff.Set(Isq(),type== PDEexample.SRC?1:0);
    }
}

public class PDEexample extends AgentGrid2D<SrcOrSink> {
    public static int SRC=RGB(0,1,0),SINK=RGB(0,1,1);
    PDEGrid2D diff;
    Random rn=new Random();
    public PDEexample(int x, int y) {
        super(x, y, SrcOrSink.class,true,true);
        diff=new PDEGrid2D(x,y);
    }
    public void Setup(int nSinks,int sinkDist){
        NewAgentSQ(xDim/2,yDim/2).Init(SRC);//create source
        int[]sinkIs=Utils.GenIndicesArray(length);
        Utils.Shuffle(sinkIs,rn);//shuffle sink placement locations
        int sinksPlaced=0;
        for (int i = 0; i < sinkIs.length; i++) {
            int sinkI=sinkIs[i];
            if(DistSquared(ItoX(sinkI),ItoY(sinkI),xDim/2,yDim/2)>sinkDist*sinkDist){
                NewAgentSQ(sinkI).Init(SINK);//create sink
                sinksPlaced++;
                if(sinksPlaced==nSinks){
                    IncTick(); //IncTick called to make sources and sinks appear
                    return;
                }
            }
        }
        IncTick();
    }
    public void Step(int stepI){
        double advectionX=Math.sin(stepI*1.0/1000)*0.2;
        double advectionY=Math.cos(stepI*1.0/1000)*0.2;
        for (SrcOrSink srcOrSink : this) {
            srcOrSink.Reaction();
        }
        diff.Advection(advectionX,advectionY);
        diff.Diffusion((Math.sin(stepI*1.0/1000)+1)*0.12);
    }
    public void Draw(GuiGridVis visSrcSinks,GuiGridVis visDiff){
        for (SrcOrSink srcOrSink : this) {
            visSrcSinks.SetPix(srcOrSink.Isq(),srcOrSink.type);//draw sources and sinks
        }
        for (int i = 0; i < length; i++) {//length of the Grid
            visDiff.SetPix(i,SetAlpha(HeatMapRGB(diff.Get(i)*4),0.3));
        }
    }

    public static void main(String[] args) {
        int x=400,y=400,scale=2;
        GridVisWindow visCells=new GridVisWindow(x,y,scale);
        GuiGridVis visDiff=new GuiGridVis(x,y,scale);
        visCells.AddAlphaGrid(visDiff);//facilitates alpha blending
        PDEexample ex=new PDEexample(x,y);
        ex.Setup(10,10);
        int i=0;
        while(true){
            visCells.TickPause(0);//slows down simulation for presentation
            ex.Step(i);
            ex.Draw(visCells,visDiff);
            i++;
        }
    }
}
