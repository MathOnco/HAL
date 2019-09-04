package HAL.Gui;

import HAL.Util;

import java.io.Serializable;

// not meant to be used directly, created by the UIPlot object
class PlotPointInternal implements Serializable{
    int color;
    double x;
    double y;
    PlotPointInternal next;
    PlotPointInternal linePrev;
    final UIPlot win;
    int[]drawHood;
    public PlotPointInternal(double x, double y, int color, UIPlot win, int[]drawHood){
        this.win=win;
        Init(x,y,color,drawHood);
    }

    public void Init(double x,double y, int color, int[]drawHood){
        this.x=x;
        this.y=y;
        this.color=color;
        this.drawHood=drawHood;
        this.linePrev=null;
        this.next=null;

    }

    public void Draw(){
        int middleX=(int) ((x - win.plotXstart) * win.plotScaleX);
        int middleY=(int) ((y - win.plotYstart) * win.plotScaleY);
        if(drawHood==null) {
            win.grid.SetPix(middleX,middleY, color);
        }
        else{
            int nDraws=win.grid.MapHood(drawHood,middleX,middleY);
            for (int i = 0; i < nDraws; i++) {
                win.grid.SetPix(drawHood[i],color);
            }
        }
        if(linePrev!=null){
            int middleXprev=(int) ((linePrev.x - win.plotXstart) * win.plotScaleX);
            int middleYprev=(int) ((linePrev.y - win.plotYstart) * win.plotScaleY);
            Util.AlongLineAction(middleX,middleY,middleXprev,middleYprev,(x,y)->{
                win.grid.SetPix(x,y,color);
            });
        }
    }
}
