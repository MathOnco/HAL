package Framework.Gui;

/**
 * this class is used to add segments one at a time to a PlotLine on a UIPlot/PlotWindow
 */
public class PlotLine {
    int color;
    final UIPlot myPlot;
    PlotPoint last;

    /**
     * establishes the UIPlot that will contain the PlotLine, and the color of the segment lines
     */
    public PlotLine(UIPlot plot, int color){
        this.myPlot=plot;
        this.color=color;
    }

    /**
     * adds a segment to the current line, the color and pointHood are used to draw the point at the end of the segment
     */
    public void AddSegment(double x,double y,int color,int[]pointHood){
        PlotPoint pt=new PlotPoint(x,y,color,myPlot,pointHood);
        if(last!=null) {
            pt.linePrev = last;
        }
        last=pt;
        myPlot.SetPoint(pt);
    }

    /**
     * adds a segment to the current line that will end at the specified coordinates
     */
    public void AddSegment(double x,double y,int color){
        AddSegment(x,y,color,null);
    }

    /**
     * adds a segment to the current line that will end at the specified coordinates
     */
    public void AddSegment(double x,double y){
        AddSegment(x,y,color,null);
    }
}
