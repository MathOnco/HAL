package Framework.Gui;

import Framework.Interfaces.GuiComp;
import Framework.Util;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * the UIPlot gui component is similar to the UIGrid but is specifically for generating plots.
 * It starts centered around 0,0. Ranging from -1 to 1 in x and y, but will automatically rescale to fit the values drawn on it
 */
public class UIPlot implements GuiComp{
    UIGrid disp;
    static final int GREY=Util.RGB(0.5,0.5,0.5);
    private int bkColor;
    private int axColor;
    PlotPoint first=null;
    PlotPoint last=null;
    double plotXstart;
    double plotYstart;
    double plotXend;
    double plotYend;
    double plotScaleX;
    double plotScaleY;
    NumberFormat form=new DecimalFormat("0.####E0");

    /**
     * @param xPix width of the UIPlot in pixels
     * @param yPix heigh of the UIPlot in pixels
     * @param scaleFactor the width and height in screen pixels of each UIGrid pixel
     * @param compX width on the gui GridBagLayout
     * @param compY height on the gui GridBagLayout
     * @param active
     */
    public UIPlot(int xPix, int yPix, int scaleFactor, int compX, int compY, boolean active) {
        this.disp=new UIGrid(xPix, yPix, scaleFactor, compX, compY, active);
        this.bkColor= Util.BLACK;
        this.axColor=GREY;
        plotXstart=-1;
        plotYstart=-1;
        plotXend=1;
        plotYend=1;
        SetScale();
    }
    public UIPlot(int xPix, int yPix, int scaleFactor, int compX, int compY) {
        this(xPix,yPix,scaleFactor,compX,compY,true);
    }
    public UIPlot(int xPix, int yPix, int scaleFactor,boolean active) {
        this(xPix,yPix,scaleFactor,1,1,active);
    }
    public UIPlot(int xPix, int yPix, int scaleFactor) {
        this(xPix,yPix,scaleFactor,1,1,true);
    }

    /**
     * call this once per step of your model, and the function will ensure that your model runs at the rate provided in
     * milliseconds. the function will take the amount time between calls into account to ensure a consistent tick
     * rate.
     */
    public void TickPause(int millis){
        disp.TickPause(millis);
    }

    /**
     * adds a line by iterating over the double arguments to generate x,y pairs. the line will be drawn with the color argument.
     * The PlotLine object returned can be subsequently added to
     */
    public PlotLine AddLine(int color,double... xys){
        PlotLine ret=new PlotLine(this,color);
        for (int i = 0; i < xys.length; i+=2) {
            ret.AddSegment(xys[0],xys[1]);
        }
        return ret;
    }

    /**
     * adds a line by iterating over the double arguments to generate x,y pairs. the line will be drawn with the color argument.
     * The PlotLine object returned can be subsequently added to
     * The DrawHood argument will be used to draw multi-pixel points
     */
    public PlotLine AddLine(int color,int[]drawHood,double... xys){
        PlotLine ret=new PlotLine(this,color);
        for (int i = 0; i < xys.length; i+=2) {
            ret.AddSegment(xys[0],xys[1],color,drawHood);
        }
        return ret;
    }

    /**
     * adds a point to the plot. the point will be 1 pixel in size and the color specified
     */
    public void AddPoint(double x,double y,int color){
        AddPoint(x,y,color,null);
    }

    /**
     * adds a point to the plot. the point will be drawn using the drawHood to set pixels around the draw point, and will be set to the color specified
     */
    public void AddPoint(double x,double y,int color,int[]drawHood){
        PlotPoint newPt=new PlotPoint(x,y,color,this,drawHood);
        SetPoint(newPt);
    }
    void SetAxes(int axColor){
        int xLine=Util.Bound(ConvX(0),0,disp.xDim-1);
        int yLine=Util.Bound(ConvY(0),0,disp.yDim-1);
        for (int y = 0; y < disp.yDim; y++) {
            disp.SetPix(xLine,y,axColor);
        }
        for (int x = 0; x < disp.xDim; x++) {
            disp.SetPix(x,yLine,axColor);
        }

    }
    void DrawAxesLabels(){
        String xStart=form.format(plotXstart);
        String yStart=form.format(plotYstart);
        String xEnd=form.format(plotXend);
        String yEnd=form.format(plotYend);
        int xLine=Util.Bound(ConvX(0),0,disp.xDim-xStart.length()*5);
        int yLine=Util.Bound(ConvY(0),10,disp.yDim-10);
        disp.SetString(yStart,xLine,5,Util.WHITE,Util.BLACK);
        disp.SetString(xStart,0,yLine+7,Util.WHITE,Util.BLACK);
        disp.SetString(yEnd,xLine,disp.yDim,Util.WHITE,Util.BLACK);
        disp.SetString(xEnd,disp.xDim-(yEnd.length()*4),yLine+7,Util.WHITE,Util.BLACK);
    }
    void Refresh(int bkColor,int axColor){
        disp.Clear(bkColor);
        SetAxes(axColor);
        PlotPoint curPt=first;
        while(curPt!=null){
            curPt.Draw();
            curPt=curPt.next;
        }
        DrawAxesLabels();
    }

    @Override
    public int compX() {
        return disp.compX();
    }

    @Override
    public int compY() {
        return disp.compY();
    }

    @Override
    public boolean IsActive() {
        return disp.IsActive();
    }

    @Override
    public void SetActive(boolean isActive) {
        disp.SetActive(isActive);
    }

    @Override
    public void _GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere) {
        disp._GetComps(putHere,coordsHere,compSizesHere);
    }
    void SetScale(){
        plotScaleX=1.0/((plotXend-plotXstart)/disp.xDim);
        plotScaleY=1.0/((plotYend-plotYstart)/disp.yDim);
    }
    int ConvX(double x){
        return (int)((x-plotXstart)*plotScaleX);
    }
    int ConvY(double y){
        return (int)((y-plotXstart)*plotScaleY);
    }
    void SetPoint(PlotPoint newPt){
        if(first==null){
            first=newPt;
        }
        if(last==null){
            last=newPt;
        }
        else{
            last.next=newPt;
            last=newPt;
        }
        boolean needRef=false;
        if(newPt.x>=(plotXend)){
            plotXend=newPt.x*1.3;
            needRef=true;
        }
        if(newPt.x<=plotXstart){
            plotXstart=newPt.x*1.3;
            needRef=true;
        }
        if(newPt.y>=plotYend){
            plotYend=newPt.y*1.3;
            needRef=true;
        }
        if(newPt.y<=plotYstart){
            plotYstart=newPt.y*1.3;
            needRef=true;
        }
        if(needRef){
            SetScale();
            Refresh(bkColor,axColor);
        }
        else {
            newPt.Draw();
        }
    }
}
