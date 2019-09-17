package HAL.Gui;

import HAL.Interfaces.DrawFunction;
import HAL.Interfaces.GuiComp;
import HAL.Util;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import static HAL.Util.BLACK;

/**
 * the UIPlot gui component is similar to the UIGrid but is specifically for generating plots.
 * It starts centered around 0,0. Ranging from -1 to 1 in x and y, but will automatically rescale to fit the values drawn on it
 */
public class UIPlot implements GuiComp,Serializable{
    public UIGrid grid;
    PlotPointInternal deadPoints=null;
    private int bkColor;
    private int axColor;
    PlotPointInternal first=null;
    PlotPointInternal last=null;
    int clearState=0;
    double plotXstart;
    double plotYstart;
    double plotXend;
    double plotYend;
    double plotScaleX;
    double plotScaleY;
    static final double RESCALE_FACTOR=0.3;
    NumberFormat form=new DecimalFormat("0.####E0");
    DrawFunction AdditionalDraws=null;
    boolean clickOn;
    UIGrid clickAlphaGrid=null;

    /**
     * @param xPix width of the UIPlot in pixels
     * @param yPix heigh of the UIPlot in pixels
     * @param scaleFactor the width and height in screen pixels of each UIGrid pixel
     * @param compX width on the gui GridBagLayout
     * @param compY height on the gui GridBagLayout
     * @param active
     */
    public UIPlot(int xPix, int yPix, int scaleFactor,double xMin,double yMin,double xMax,double yMax, int compX, int compY, boolean active) {
        this.grid =new UIGrid(xPix, yPix, scaleFactor, compX, compY, active);
        this.bkColor= BLACK;
        this.axColor=Util.WHITE;
        plotXstart=xMin;
        plotYstart=yMin;
        plotXend=xMax;
        plotYend=yMax;
        SetScale();
        Refresh(bkColor,axColor);
    }

    public void AddUpdateFn(DrawFunction AdditionalDraws){
        this.AdditionalDraws=AdditionalDraws;
        Refresh(bkColor,axColor);
    }

    public void ActivateClickCoords(int drawColor){
        final int clear=Util.RGBA(0,0,0,0);
        clickOn=false;
        clickAlphaGrid=new UIGrid(grid.xDim,grid.yDim,grid.scale);
        clickAlphaGrid.Clear(Util.RGBA(0,0,0,0));
        grid.AddAlphaGrid(clickAlphaGrid);
        grid.AddMouseListeners(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(clickOn){
                    clickOn=false;
                    clickAlphaGrid.Clear(clear);

                }
                else{
                    clickOn=true;
                    clickAlphaGrid.Clear(clear);
                    double x=grid.ClickXpt(e);
                    double y=grid.ClickYpt(e);
                    String drawStr=form.format(x)+","+form.format(y);
                    int xLeft=Math.min(Math.max(0,(int)(x-(drawStr.length()*4/2.0))),grid.xDim-drawStr.length()*4);
                    int yTop=Math.max(5,(int)y);
                    clickAlphaGrid.SetString(drawStr,xLeft,yTop,drawColor,clear);
                    clickAlphaGrid.SetPix(grid.ClickXsq(e),grid.ClickYsq(e),drawColor);
                }
            }
        });
    }

    public UIPlot(int xPix, int yPix, int scaleFactor,double xMin,double yMin,double xMax,double yMax, int compX, int compY) {
        this(xPix,yPix,scaleFactor,xMin,yMin,xMax,yMax,compX,compY,true);
    }
    public UIPlot(int xPix, int yPix, int scaleFactor,double xMin,double yMin,double xMax,double yMax,boolean active) {
        this(xPix,yPix,scaleFactor,xMin,yMin,xMax,yMax,1,1,active);
    }
    public UIPlot(int xPix, int yPix, int scaleFactor,double xMin,double yMin,double xMax,double yMax) {
        this(xPix,yPix,scaleFactor,xMin,yMin,xMax,yMax,1,1,true);
    }
    public UIPlot(int xPix, int yPix, int scaleFactor, int compX, int compY, boolean active) {
        this(xPix,yPix,scaleFactor,0,0,0,0,compX,compY,active);
    }
    public UIPlot(int xPix, int yPix, int scaleFactor, int compX, int compY) {
        this(xPix,yPix,scaleFactor,0,0,0,0,compX,compY,true);
    }
    public UIPlot(int xPix, int yPix, int scaleFactor,boolean active) {
        this(xPix,yPix,scaleFactor,0,0,0,0,1,1,active);
    }
    public UIPlot(int xPix, int yPix, int scaleFactor) {
        this(xPix,yPix,scaleFactor,0,0,0,0,1,1,true);
    }

//    public UIPlot(int xPix, int yPix, int scaleFactor, int compX, int compY, boolean active) {
//        this(xPix,yPix,scaleFactor,-1,-1,1,1,compX,compY,active);
//    }
//    public UIPlot(int xPix, int yPix, int scaleFactor, int compX, int compY) {
//        this(xPix,yPix,scaleFactor,-1,-1,1,1,compX,compY,true);
//    }
//    public UIPlot(int xPix, int yPix, int scaleFactor,boolean active) {
//        this(xPix,yPix,scaleFactor,-1,-1,1,1,1,1,active);
//    }
//    public UIPlot(int xPix, int yPix, int scaleFactor) {
//        this(xPix,yPix,scaleFactor,-1,-1,1,1,1,1,true);
//    }
    /**
     * call this once per step of your model, and the function will ensure that your model runs at the rate provided in
     * milliseconds. the function will take the amount time between calls into account to ensure a consistent tick
     * rate.
     */
    public void TickPause(int millis){
        grid.TickPause(millis);
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

    public void Clear(double xMin,double xMax,double yMin,double yMax){
        if(last!=null) {
            last.next = deadPoints;
        }
        deadPoints=first;
        first=null;
        last=null;
        plotXstart=xMin;
        plotYstart=yMin;
        plotXend=xMax;
        plotYend=yMax;
        SetScale();
        Refresh(bkColor,axColor);
        clearState++;
    }

    public void Clear(){
        if(last!=null) {
            last.next = deadPoints;
        }
        deadPoints=first;
        first=null;
        last=null;
        Refresh(bkColor,axColor);
        clearState++;
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

    public PlotLine AddLine(int color,double[]xs,double[]ys){
        PlotLine ret=new PlotLine(this,color);
        for (int i = 0; i < xs.length; i++) {
            ret.AddSegment(xs[i],ys[i],color);
        }
        return ret;
    }

    /**
     * adds a point to the plot. the point will be 1 pixel in size and the color specified
     */
    public void AddPoint(double x,double y,int color){
        AddPoint(x,y,color,null);
    }

    PlotPointInternal GenPoint(double x,double y,int color, int[]drawHood){
        PlotPointInternal newPt;
        if(deadPoints!=null){
            newPt = deadPoints;
            deadPoints=deadPoints.next;
            newPt.Init(x,y,color,drawHood);
        }else {
            newPt = new PlotPointInternal(x, y, color, this, drawHood);
        }
        return newPt;
    }

    /**
     * adds a point to the plot. the point will be drawn using the drawHood to set pixels around the draw point, and will be set to the color specified
     */
    public void AddPoint(double x,double y,int color,int[]drawHood){

        SetPoint(GenPoint(x,y,color,drawHood));
    }
    void SetAxes(int axColor){
        int xLine=Util.Bound(ConvX(0),0, grid.xDim-1);
        int yLine=Util.Bound(ConvY(0),0, grid.yDim-1);
        for (int y = 0; y < grid.yDim; y++) {
            grid.SetPix(xLine,y,axColor);
        }
        for (int x = 0; x < grid.xDim; x++) {
            grid.SetPix(x,yLine,axColor);
        }

    }
    public void DrawAxesLabels(){
        String xStart=form.format(plotXstart);
        String yStart=form.format(plotYstart);
        String xEnd=form.format(plotXend);
        String yEnd=form.format(plotYend);
        int xLine=Util.Bound(ConvX(0),0, grid.xDim-xStart.length()*5);
        int yLine=Util.Bound(ConvY(0),10, grid.yDim-10);
        grid.SetString(yStart,xLine,5, axColor, bkColor);
        grid.SetString(xStart,0,yLine+7, axColor, bkColor);
        grid.SetString(yEnd,xLine, grid.yDim, axColor, bkColor);
        grid.SetString(xEnd, grid.xDim-(xEnd.length()*4),yLine+7, axColor, bkColor);
    }
    void Refresh(int bkColor,int axColor){
        grid.Clear(bkColor);
        if(clickAlphaGrid!=null){
            clickAlphaGrid.Clear(Util.RGBA(0,0,0,0));
        }
        SetAxes(axColor);
        this.bkColor=bkColor;
        this.axColor=axColor;
        PlotPointInternal curPt=first;
        while(curPt!=null){
            curPt.Draw();
            curPt=curPt.next;
        }
        DrawAxesLabels();
        if(AdditionalDraws!=null){
            AdditionalDraws.Draw(grid);
        }
    }

    @Override
    public int compX() {
        return grid.compX();
    }

    @Override
    public int compY() {
        return grid.compY();
    }

    @Override
    public boolean IsActive() {
        return grid.IsActive();
    }

    @Override
    public void SetActive(boolean isActive) {
        grid.SetActive(isActive);
    }

    @Override
    public void _GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere) {
        grid._GetComps(putHere,coordsHere,compSizesHere);
    }
    void SetScale(){
        plotScaleX=1.0/((plotXend-plotXstart)/ (grid.xDim-1));
        plotScaleY=1.0/((plotYend-plotYstart)/ (grid.yDim-1));
    }
    int ConvX(double x){
        return (int)((x-plotXstart)*plotScaleX);
    }
    int ConvY(double y){
        return (int)((y-plotYstart)*plotScaleY);
    }
    void SetPoint(PlotPointInternal newPt){
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
        if(last.next==last){
            throw new IllegalStateException("problem found");
        }
        boolean needRef=false;
        if(newPt.x>(plotXend)){
            plotXend=newPt.x+(newPt.x-plotXstart)*RESCALE_FACTOR;
            needRef=true;
        }
        if(newPt.x<plotXstart){
            plotXstart=newPt.x-(plotXend-newPt.x)*RESCALE_FACTOR;
            needRef=true;
        }
        if(newPt.y>plotYend){
            plotYend=newPt.y+(newPt.y-plotYstart)*RESCALE_FACTOR;
            needRef=true;
        }
        if(newPt.y<plotYstart){
            plotYstart=newPt.y-(plotYend-newPt.y)*RESCALE_FACTOR;
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

    public void FitPointsY(double padY) {
        plotYstart = Double.MAX_VALUE;
        plotYend = Double.MIN_VALUE;
        PlotPointInternal pt = first;
        while (pt != null) {
            if (pt.y - padY < plotYstart) {
                plotYstart = pt.y - padY;
            }
            if (pt.y + padY > plotYend) {
                plotYend = pt.y + padY;
            }
        pt = pt.next;
        }
        SetScale();
        Refresh(bkColor, axColor);
    }

    public void FitPointsX(double padX){
        plotXstart=Double.MAX_VALUE;
        plotXend=Double.MIN_VALUE;
        PlotPointInternal pt=first;
        while(pt!=null){
            if(pt.x-padX<plotXstart){
                plotXstart=pt.x-padX;
            }
            if(pt.x+padX>plotXend){
                plotXend=pt.x+padX;
            }
            pt=pt.next;
        }
        SetScale();
        Refresh(bkColor,axColor);
    }

    //todo: change click coords readout when model resizes
    public void SetDims(double xMin,double xMax,double yMin,double yMax){
        plotYstart = yMax;
        plotYend = yMin;
        plotXstart=xMin;
        plotXend=xMax;
        EnsureFit(0,0);
        SetScale();
        Refresh(bkColor,axColor);
    }

    public void EnsureFit(double padX,double padY){
        PlotPointInternal pt=first;
        while(pt!=null){
            if(pt.x-padX<plotXstart){
                plotXstart=pt.x-padX;
            }
            if(pt.x+padX>plotXend){
                plotXend=pt.x+padX;
            }
            if (pt.y - padY < plotYstart) {
                plotYstart = pt.y - padY;
            }
            if (pt.y + padY > plotYend) {
                plotYend = pt.y + padY;
            }
            pt=pt.next;
        }
    }

    public void FitPoints(double padX,double padY){
        plotYstart = Double.MAX_VALUE;
        plotYend = Double.MIN_VALUE;
        plotXstart=Double.MAX_VALUE;
        plotXend=Double.MIN_VALUE;
        EnsureFit(padX,padY);
        SetScale();
        Refresh(bkColor,axColor);
    }
    public void ToPNG(String path) {
        grid.ToPNG(path);
    }
    public void ToJPG(String path) {
        grid.ToJPG(path);
    }
    public void ToGIF(String path) {
        grid.ToGIF(path);
    }
}
