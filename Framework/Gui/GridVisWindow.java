package Framework.Gui;

import Framework.Interfaces.GuiCloseAction;

/**
 * Created by Rafael on 9/5/2017.
 */
public class GridVisWindow extends GuiGridVis{
    private GuiWindow win;
    public GridVisWindow(String title,int xDim,int yDim, int scaleFactor, boolean main, boolean active) {
        super(xDim,yDim,scaleFactor,active);
        win=new GuiWindow(title, main, active);
        RunGui();
    }
    public GridVisWindow(String title,int xDim,int yDim, int scaleFactor, boolean main) {
        super(xDim,yDim,scaleFactor,true);
        win=new GuiWindow(title, main);
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
    public GridVisWindow(String title,int xDim,int yDim, int scaleFactor, boolean main, GuiCloseAction closeAction, boolean active) {
        super(xDim,yDim,scaleFactor,active);
        win=new GuiWindow(title, main, closeAction, active);
    }
    public GridVisWindow(String title,int xDim,int yDim, int scaleFactor, boolean main, GuiCloseAction closeAction) {
        super(xDim,yDim,scaleFactor,true);
        win=new GuiWindow(title, main, closeAction, true);
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
