package Framework.Gui;

public class PlotLine {
    int color;
    final UIPlot myPlot;
    PlotPoint last;
    public PlotLine(UIPlot plot, int color){
        this.myPlot=plot;
        this.color=color;
    }
    public void AddSegment(double x,double y,int color,int[]pointHood){
        PlotPoint pt=new PlotPoint(x,y,color,myPlot,pointHood);
        if(last!=null) {
            pt.linePrev = last;
        }
        last=pt;
        myPlot.AddPoint(pt);
    }
    public void AddSegment(double x,double y){
        AddSegment(x,y,color,null);
    }
}
