package Framework.Gui;

import Framework.Interfaces.GuiCloseAction;

/**
 * Created by Rafael on 9/5/2017.
 */
public class GridVisWindow extends GuiGridVis{
    private GuiWindow win;
    public GridVisWindow(String title,int xDim,int yDim, int scaleFactor, boolean killOnClose, boolean active) {
        super(xDim,yDim,scaleFactor,active);
        win=new GuiWindow(title, killOnClose, active);
        RunGui();
    }
    public GridVisWindow(String title,int xDim,int yDim, int scaleFactor, boolean killOnClose) {
        super(xDim,yDim,scaleFactor,true);
        win=new GuiWindow(title, killOnClose);
        RunGui();
    }
    public GridVisWindow(String title,int xDim,int yDim, int scaleFactor) {
        super(xDim,yDim,scaleFactor,true);
        win=new GuiWindow(title, true);
        RunGui();
    }
    public GridVisWindow(int xDim,int yDim, int scaleFactor) {
        super(xDim,yDim,scaleFactor,true);
        win=new GuiWindow("Grid Vis", true);
        RunGui();
    }
    public GridVisWindow(String title,int xDim,int yDim, int scaleFactor, boolean killOnClose, GuiCloseAction closeAction, boolean active) {
        super(xDim,yDim,scaleFactor,active);
        win=new GuiWindow(title, killOnClose, closeAction, active);
    }
    public GridVisWindow(String title,int xDim,int yDim, int scaleFactor, boolean killOnClose, GuiCloseAction closeAction) {
        super(xDim,yDim,scaleFactor,true);
        win=new GuiWindow(title, killOnClose, closeAction, true);
        RunGui();
    }
    private void RunGui(){
        win.AddCol(0, this);
        win.RunGui();
    }
    public void TickPause(int millis){
        win.TickPause(millis);
    }
    public void Dispose(){
        win.Dispose();
    }
}
