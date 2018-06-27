package Framework.Gui;

public class PlotWindow extends UIPlot {
    UIWindow win;
    public PlotWindow(String name,int xPix, int yPix, boolean killOnClose, boolean active) {
        super(xPix,yPix,1,1,true);
        win=new UIWindow(name,killOnClose,active);
        win.AddCol(0,this);
        win.RunGui();
    }
}
