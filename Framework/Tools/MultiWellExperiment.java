package Framework.Tools;

import Framework.Gui.GifMaker;
import Framework.Gui.GridWindow;
import Framework.Interfaces.DrawWell;
import Framework.Interfaces.StepWell;
import Framework.Util;

public class MultiWellExperiment <T>{
    public final int visXdim;
    public final int visYdim;
    public final int wellsX;
    public final int wellsY;
    int borderColor;
    GridWindow win;
    public T[]wells;
    DrawWell<T> Drawer;
    StepWell<T> Stepper;
    int[]wellStarts;
    int nProcs;


    public MultiWellExperiment(int numWellsX, int numWellsY, T[]models, int visXdim, int visYdim, int borderColor, int scaleFactor, StepWell<T> StepFn, DrawWell<T> ColorFn){
        this.visYdim = visYdim;
        this.visXdim = visXdim;
        this.wellsX=numWellsX;
        this.wellsY=numWellsY;
        this.borderColor=borderColor;
        this.wells=models;
        this.Drawer=ColorFn;
        this.Stepper=StepFn;
        if(models.length>numWellsX*numWellsY){
            throw new IllegalArgumentException("model models passed than can be displayed, max: "+numWellsX*numWellsY+" passed: "+models.length);
        }
        SetWellStarts();
        win =new GridWindow("MultiWell",visXdim*numWellsX+numWellsX-1,visYdim*numWellsY+numWellsY-1,scaleFactor,false);
        win.Clear(borderColor);
    }
    void SetWellStarts(){
        nProcs=Runtime.getRuntime().availableProcessors();
        int perWell=wells.length/nProcs;
        int extras=wells.length-perWell*nProcs;
        wellStarts=new int[nProcs];
        for (int i = 1; i < wellStarts.length; i++) {
            if(extras>0){
                wellStarts[i]=wellStarts[i-1]+perWell+1;
                extras--;
            }else{
                wellStarts[i]=wellStarts[i-1]+perWell;
            }
        }
    }
    public void LoadWells(T[]models){
        wells=models;
        if(models.length>wellsX*wellsY){
            throw new IllegalArgumentException("model models passed than can be displayed, max: "+wellsX*wellsY+" passed: "+models.length);
        }
        SetWellStarts();
    }
    public void StepWell(int iWell,int tick){
        Stepper.Step(wells[iWell],iWell,tick);
    }
    public void DrawWell(int i){
        int xStart=(i/wellsX)*(visXdim+1);
        int yStart=(i%wellsY)*(visYdim+1);
        win.SetRect(xStart,yStart, visXdim, visYdim,(x, y)->Drawer.GetPixColor(wells[i],x,y));

    }
    public void StepMultiThread(int tick){
                Util.MultiThread(nProcs,nProcs,(iThread) -> {
                    int start=wellStarts[iThread];
                    int end=iThread<wellStarts.length-1?wellStarts[iThread+1]:wells.length;
                    for (int i = start; i < end; i++) {
                        StepWell(i, tick);
                        DrawWell(i);
                    }
                });
    }
    public void Step(int tick){
            for (int j = 0; j < wells.length; j++) {
                StepWell(j,tick);
                DrawWell(j);
            }
    }
    public void Run(int numTicks,boolean multiThread,int tickPause){
        for (int i = 0; i < numTicks; i++) {
            win.TickPause(tickPause);
            if (multiThread) {
                StepMultiThread(i);
            } else {
                Step(i);
            }
        }
        win.Close();
    }
    public void RunGIF(int numTicks,String outFileName,int recordPeriod,boolean multiThread){
        GifMaker gif=new GifMaker(outFileName,0,true);
        for (int i = 0; i < numTicks; i++) {
            if(multiThread){
                StepMultiThread(i);
            }else{
                Step(i);
            }
            if(i%recordPeriod==0){
                gif.AddFrame(win);
            }
        }
        gif.Close();
        win.Close();
    }
}
