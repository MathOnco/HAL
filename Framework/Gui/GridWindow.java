package Framework.Gui;

import Framework.Interfaces.GuiCloseAction;
import Framework.Interfaces.KeyResponse;

/**
 * Created by Rafael on 9/5/2017.
 */
public class GridWindow extends GuiGrid {
    private GuiWindow win;
    public GridWindow(String title, int xDim, int yDim, int scaleFactor, boolean killOnClose, boolean active) {
        super(xDim,yDim,scaleFactor,active);
        win=new GuiWindow(title, killOnClose, active);
        RunGui();
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor, boolean killOnClose) {
        super(xDim,yDim,scaleFactor,true);
        win=new GuiWindow(title, killOnClose);
        RunGui();
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor) {
        super(xDim,yDim,scaleFactor,true);
        win=new GuiWindow(title, true);
        RunGui();
    }
    public GridWindow(int xDim, int yDim, int scaleFactor) {
        super(xDim,yDim,scaleFactor,true);
        win=new GuiWindow("Grid Vis", true);
        RunGui();
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor, boolean killOnClose, GuiCloseAction closeAction, boolean active) {
        super(xDim,yDim,scaleFactor,active);
        win=new GuiWindow(title, killOnClose, closeAction, active);
        RunGui();
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor, boolean killOnClose, GuiCloseAction closeAction) {
        super(xDim,yDim,scaleFactor,true);
        win=new GuiWindow(title, killOnClose, closeAction, true);
        RunGui();
    }
    private void RunGui(){
        win.AddCol(0, this);
        win.RunGui();
    }
    public boolean IsKeyDown(char c){
        return win.IsKeyDown(c);
    }
    public boolean IsKeyDown(int keyCode){
        return win.IsKeyDown(keyCode);
    }
    public void AddKeyResponses(KeyResponse OnKeyDown, KeyResponse OnKeyUp){
        win.AddKeyResponses(OnKeyDown,OnKeyUp);
    }
    public void Dispose(){
        win.Dispose();
    }
}
