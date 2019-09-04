package HAL.Gui;

import java.io.Serializable;

/**
 * this class is used to add segments one at a time to a PlotLine on a UIPlot/PlotWindow
 */
public class PlotLine implements Serializable{
    int color;
    final UIPlot myPlot;
    PlotPointInternal last;
    PlotPointInternal first;
    int clearState;

    /**
     * establishes the UIPlot that will contain the PlotLine, and the color of the segment lines
     */
    public PlotLine(UIPlot plot, int color){
        this.myPlot=plot;
        this.color=color;
        this.clearState=myPlot.clearState;
    }

    /**
     * adds a segment to the current line, the color and pointHood are used to draw the point at the end of the segment
     */
    public void AddSegment(double x,double y,int color,int[]pointHood){
        if(myPlot.clearState>clearState){
            last=null;
            first=null;
            clearState=myPlot.clearState;
        }
        PlotPointInternal pt=myPlot.GenPoint(x,y,color,pointHood);
        if(first==null){
            first=pt;
        }
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
