package Framework.Gui;

import Framework.Util;

// not meant to be used directly, created by the UIPlot object
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
            win.disp.SetPix(middleX,middleY, color);
        }
        else{
            int nDraws=win.disp.MapHood(drawHood,middleX,middleY);
            for (int i = 0; i < nDraws; i++) {
                win.disp.SetPix(drawHood[i],color);
            }
        }
        if(linePrev!=null){
            int middleXprev=(int) ((linePrev.x - win.plotXstart) * win.plotScaleX);
            int middleYprev=(int) ((linePrev.y - win.plotYstart) * win.plotScaleY);
            Util.AlongLineAction(middleX,middleY,middleXprev,middleYprev,(x,y)->{
                win.disp.SetPix(x,y,color);
            });
        }
    }
}
