package Framework.Gui;

import Framework.Interfaces.GuiCloseAction;
import Framework.Interfaces.KeyResponse;

/**
 * Created by Rafael on 9/5/2017.
 */
public class GridWindow extends UIGrid {
    private UIWindow win;
    public GridWindow(int xDim, int yDim) {
        this("",xDim,yDim,1,true,null,true);
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor, boolean killOnClose, GuiCloseAction closeAction, boolean active) {
        super(xDim,yDim,scaleFactor,active);
        win=new UIWindow(title, killOnClose,closeAction, active);
        RunGui();
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor, boolean killOnClose) {
        this(title,xDim,yDim,scaleFactor,killOnClose,null,true);
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor, boolean killOnClose, boolean active) {
        this(title,xDim,yDim,scaleFactor,killOnClose,null,active);
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor) {
        this(title,xDim,yDim,scaleFactor,true,null,true);
    }
    public GridWindow(int xDim, int yDim, int scaleFactor) {
        this("",xDim,yDim,scaleFactor,true,null,true);
    }
    public GridWindow(int xDim, int yDim, int scaleFactor, boolean killOnClose, boolean active) {
        this("",xDim,yDim,scaleFactor,killOnClose,null,active);
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor, boolean killOnClose, GuiCloseAction closeAction) {
        this(title,xDim,yDim,scaleFactor,killOnClose,closeAction,true);
    }
    /**
     * returns whether the given key is currently pressed
     */
    public boolean IsKeyDown(char c){
        return win.IsKeyDown(c);
    }

    /**
     * returns whether the given key is currently pressed
     */
    public boolean IsKeyDown(int keyCode){
        return win.IsKeyDown(keyCode);
    }

    /**
     * takes 2 key response functions that will be called whenever a key is pressed or released
     */
    public void AddKeyResponses(KeyResponse OnKeyDown, KeyResponse OnKeyUp){
        win.AddKeyResponses(OnKeyDown,OnKeyUp);
    }

    /**
     * disposes of the GridWindow.
     */
    public void Close(){
        win.Close();
    }

    private void RunGui(){
        win.AddCol(0, this);
        win.RunGui();
    }

}
