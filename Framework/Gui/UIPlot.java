package Framework.Gui;

import Framework.Util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class UIPlot extends UIGrid {
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
    public UIPlot(int xPix, int yPix, int compX, int compY, boolean active) {
        super(xPix, yPix, 1, compX, compY, active);
        this.bkColor= Util.BLACK;
        this.axColor=GREY;
        plotXstart=-1;
        plotYstart=-1;
        plotXend=1;
        plotYend=1;
        SetScale();
    }
    void SetScale(){
        plotScaleX=1.0/((plotXend-plotXstart)/xDim);
        plotScaleY=1.0/((plotYend-plotYstart)/yDim);
    }
    int ConvX(double x){
        return (int)((x-plotXstart)*plotScaleX);
    }
    int ConvY(double y){
        return (int)((y-plotXstart)*plotScaleY);
    }
    void AddPoint(PlotPoint newPt){
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
            plotXend=newPt.x*1.1;
            needRef=true;
        }
        if(newPt.x<=plotXstart){
            plotXstart=newPt.x*1.1;
            needRef=true;
        }
        if(newPt.y>=plotYend){
            plotYend=newPt.y*1.1;
            needRef=true;
        }
        if(newPt.y<=plotYstart){
            plotYstart=newPt.y*1.1;
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
    public void Point(double x,double y,int color){
        Point(x,y,color,null);
    }
    public void Point(double x,double y,int color,int[]drawHood){
        PlotPoint newPt=new PlotPoint(x,y,color,this,drawHood);
        AddPoint(newPt);
    }
    void SetAxes(int axColor){
        int xLine=Util.Bound(ConvX(0),0,xDim-1);
        int yLine=Util.Bound(ConvY(0),0,yDim-1);
        for (int y = 0; y < yDim; y++) {
            SetPix(xLine,y,axColor);
        }
        for (int x = 0; x < xDim; x++) {
            SetPix(x,yLine,axColor);
        }

    }
    void DrawAxesLabels(){
        String xStart=form.format(plotXstart);
        String yStart=form.format(plotYstart);
        String xEnd=form.format(plotXend);
        String yEnd=form.format(plotYend);
        int xLine=Util.Bound(ConvX(0),0,xDim-xStart.length()*5);
        int yLine=Util.Bound(ConvY(0),10,yDim-10);
        DrawStringSingleLine(xStart,xLine,5,Util.WHITE,Util.BLACK);
        DrawStringSingleLine(yStart,0,yLine+7,Util.WHITE,Util.BLACK);
        DrawStringSingleLine(xEnd,xLine,yDim,Util.WHITE,Util.BLACK);
        DrawStringSingleLine(yEnd,xDim-(yEnd.length()*4),yLine+7,Util.WHITE,Util.BLACK);
    }
    void Refresh(int bkColor,int axColor){
        Clear(bkColor);
        SetAxes(axColor);
        PlotPoint curPt=first;
        while(curPt!=null){
            curPt.Draw();
            curPt=curPt.next;
        }
        DrawAxesLabels();
    }
}
