package Framework.Gui;

import Framework.Util;

//first PlotPoint holds the last PlotPoint
//PlotPoints hold connections to the next PlotPoint and the first plotpoint
class PlotPoint{
    int color;
    double x;
    double y;
    PlotPoint next;
    PlotPoint linePrev;
    final UIPlot win;
    int[]drawHood;
    public PlotPoint(double x, double y, int color, UIPlot win, int[]drawHood){
        this.x=x;
        this.y=y;
        this.color=color;
        this.win=win;
        this.drawHood=drawHood;
    }
    public PlotPoint(double x,double y,int color,UIPlot win){
        this.x=x;
        this.y=y;
        this.color=color;
        this.win=win;
        this.drawHood=null;
    }
    public void Draw(){
        int middleX=(int) ((x - win.plotXstart) * win.plotScaleX);
        int middleY=(int) ((y - win.plotYstart) * win.plotScaleY);
        if(drawHood==null) {
            win.SetPix(middleX,middleY, color);
        }
        else{
            int nDraws=win.MapHood(drawHood,middleX,middleY);
            for (int i = 0; i < nDraws; i++) {
                win.SetPix(drawHood[i],color);
            }
        }
        if(linePrev!=null){
            int middleXprev=(int) ((linePrev.x - win.plotXstart) * win.plotScaleX);
            int middleYprev=(int) ((linePrev.y - win.plotYstart) * win.plotScaleY);
            Util.AlongLineAction(middleX,middleY,middleXprev,middleYprev,(x,y)->{
                win.SetPix(x,y,color);
            });
        }
    }
}
