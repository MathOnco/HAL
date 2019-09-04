package HAL.Gui;

import HAL.Interfaces.GuiCloseAction;

public class PlotWindow extends UIPlot {
    UIWindow win;

    /**
     * the PlotWindow object component is similar to the GridWindow object but specifically for generating plots.
     * it is composed of a UIPlot embedded in a UIWindow.
     * It starts centered around 0,0. Ranging from -1 to 1 in x and y, but will automatically rescale to fit the values drawn on it
     */
    public PlotWindow(String title, int xPix, int yPix, int scale, boolean killOnClose, GuiCloseAction CloseAction, boolean active) {
        super(xPix,yPix, scale, 1,1, true);
        win=new UIWindow(title,killOnClose,CloseAction,active);
        win.AddCol(0,this);
        win.RunGui();
    }
    public PlotWindow(int xPix, int yPix) {
        this("",xPix,yPix,1,true,null,true);
    }
    public PlotWindow(int xPix, int yPix,boolean active) {
        this("",xPix,yPix,1,true,null,active);
    }
    public PlotWindow(int xPix, int yPix,int scaleFactor) {
        this("",xPix,yPix,scaleFactor,true,null,true);
    }
    public PlotWindow(int xPix, int yPix,int scaleFactor,boolean active) {
        this("",xPix,yPix,scaleFactor,true,null,active);
    }
    public PlotWindow(int xPix, int yPix,int scaleFactor,boolean killOnClose,GuiCloseAction CloseAction) {
        this("",xPix,yPix,scaleFactor,killOnClose,CloseAction,true);
    }
    public PlotWindow(int xPix, int yPix,int scaleFactor,boolean killOnClose,GuiCloseAction CloseAction,boolean active) {
        this("",xPix,yPix,scaleFactor,killOnClose,CloseAction,active);
    }
    public PlotWindow(String title,int xPix, int yPix) {
        this(title,xPix,yPix,1,true,null,true);
    }
    public PlotWindow(String title,int xPix, int yPix,boolean active) {
        this(title,xPix,yPix,1,true,null,active);
    }
    public PlotWindow(String title,int xPix, int yPix,int scaleFactor) {
        this(title,xPix,yPix,scaleFactor,true,null,true);
    }
    public PlotWindow(String title,int xPix, int yPix,int scaleFactor,boolean active) {
        this(title,xPix,yPix,scaleFactor,true,null,active);
    }
    public PlotWindow(String title,int xPix, int yPix,int scaleFactor,boolean killOnClose,GuiCloseAction CloseAction) {
        this(title,xPix,yPix,scaleFactor,killOnClose,CloseAction,true);
    }
    public PlotWindow(String title, int xPix, int yPix, int scale,double xMin,double yMin,double xMax,double yMax, boolean killOnClose, GuiCloseAction CloseAction, boolean active) {
        super(xPix,yPix, scale,xMin,yMin,xMax,yMax, 1,1, true);
        win=new UIWindow(title,killOnClose,CloseAction,active);
        win.AddCol(0,this);
        win.RunGui();
    }
    public PlotWindow(int xPix, int yPix,double xMin,double yMin,double xMax,double yMax) {
        this("",xPix,yPix,1,xMin,yMin,xMax,yMax,true,null,true);
    }
    public PlotWindow(int xPix, int yPix,double xMin,double yMin,double xMax,double yMax,boolean active) {
        this("",xPix,yPix,1,xMin,yMin,xMax,yMax,true,null,active);
    }
    public PlotWindow(int xPix, int yPix,int scaleFactor,double xMin,double yMin,double xMax,double yMax) {
        this("",xPix,yPix,scaleFactor,xMin,yMin,xMax,yMax,true,null,true);
    }
    public PlotWindow(int xPix, int yPix,int scaleFactor,double xMin,double yMin,double xMax,double yMax,boolean active) {
        this("",xPix,yPix,scaleFactor,xMin,yMin,xMax,yMax,true,null,active);
    }
    public PlotWindow(int xPix, int yPix,int scaleFactor,double xMin,double yMin,double xMax,double yMax,boolean killOnClose,GuiCloseAction CloseAction) {
        this("",xPix,yPix,scaleFactor,xMin,yMin,xMax,yMax,killOnClose,CloseAction,true);
    }
    public PlotWindow(int xPix, int yPix,int scaleFactor,double xMin,double yMin,double xMax,double yMax,boolean killOnClose,GuiCloseAction CloseAction,boolean active) {
        this("",xPix,yPix,scaleFactor,xMin,yMin,xMax,yMax,killOnClose,CloseAction,active);
    }
    public PlotWindow(String title,int xPix, int yPix,double xMin,double yMin,double xMax,double yMax) {
        this(title,xPix,yPix,1,xMin,yMin,xMax,yMax,true,null,true);
    }
    public PlotWindow(String title,int xPix, int yPix,double xMin,double yMin,double xMax,double yMax,boolean active) {
        this(title,xPix,yPix,1,xMin,yMin,xMax,yMax,true,null,active);
    }
    public PlotWindow(String title,int xPix, int yPix,int scaleFactor,double xMin,double yMin,double xMax,double yMax) {
        this(title,xPix,yPix,scaleFactor,xMin,yMin,xMax,yMax,true,null,true);
    }
    public PlotWindow(String title,int xPix, int yPix,int scaleFactor,double xMin,double yMin,double xMax,double yMax,boolean active) {
        this(title,xPix,yPix,scaleFactor,xMin,yMin,xMax,yMax,true,null,active);
    }
    public PlotWindow(String title,int xPix, int yPix,int scaleFactor,double xMin,double yMin,double xMax,double yMax,boolean killOnClose,GuiCloseAction CloseAction) {
        this(title,xPix,yPix,scaleFactor,xMin,yMin,xMax,yMax,killOnClose,CloseAction,true);
    }

    public boolean IsClosed(){
        return win.IsClosed();
    }
}
