package HAL.Gui;

import HAL.Interfaces.GuiCloseAction;
import HAL.Interfaces.KeyResponse;

/**
 * The GridWindow is a stand-alone window with a UIGrid embedded, it allows drawwing to it by setting pixels
 */
public class GridWindow extends UIGrid {
    private UIWindow win;


    /**
     * Creates a new GridWindow
     * @param title the title that will appear at the top of the window (default "")
     * @param xDim the length in UIGrid squares
     * @param yDim the height in UIGrid squares
     * @param scaleFactor the conversion factor between a pixel width/height and the length/height of a UIGrid square (default 1)
     * @param killOnClose whether the program should exit when the window is closed (default true)
     * @param CloseAction function that runs when the window is closed (default null)
     * @param active if set to false, the GridWindow will not actually render and its methods will be skipped (default true)
     */
    public GridWindow(String title, int xDim, int yDim, int scaleFactor, boolean killOnClose, GuiCloseAction CloseAction, boolean active) {
        super(xDim,yDim,scaleFactor,active);
        win=new UIWindow(title, killOnClose,CloseAction, active);
        RunGui();
    }

    /**
     * the below constructors are variants of the above constructor with default values for some of the arguments
     */
    public GridWindow(int xDim, int yDim) {
        this("",xDim,yDim,1,true,null,true);
    }
    public GridWindow(int xDim, int yDim,boolean active) {
        this("",xDim,yDim,1,true,null,active);
    }
    public GridWindow(int xDim, int yDim, int scaleFactor) {
        this("",xDim,yDim,scaleFactor,true,null,true);
    }
    public GridWindow(int xDim, int yDim, int scaleFactor,boolean active) {
        this("",xDim,yDim,scaleFactor,true,null,active);
    }
    public GridWindow(int xDim, int yDim, int scaleFactor, boolean killOnClose, GuiCloseAction CloseAction) {
        this("",xDim,yDim,scaleFactor,killOnClose,CloseAction,true);
    }
    public GridWindow(int xDim, int yDim, int scaleFactor, boolean killOnClose,GuiCloseAction CloseAction, boolean active) {
        this("",xDim,yDim,scaleFactor,killOnClose,CloseAction,active);
    }
    public GridWindow(String title,int xDim, int yDim) {
        this(title,xDim,yDim,1,true,null,true);
    }
    public GridWindow(String title, int xDim, int yDim,boolean active) {
        this(title,xDim,yDim,1,true,null,active);
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor) {
        this(title,xDim,yDim,scaleFactor,true,null,true);
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor,boolean active) {
        this(title,xDim,yDim,scaleFactor,true,null,active);
    }
    public GridWindow(String title, int xDim, int yDim, int scaleFactor, boolean killOnClose, GuiCloseAction CloseAction) {
        this(title,xDim,yDim,scaleFactor,killOnClose,CloseAction,true);
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

    public boolean IsClosed(){
        return win.IsClosed();
    }

}
