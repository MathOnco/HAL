package HAL.Gui;

import HAL.Interfaces.*;
import HAL.Interfaces.MenuItem;
import HAL.Tools.Internal.KeyRecorder;
import HAL.Tools.Internal.ParamSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * a JFrame wrapper that holds menu and gui items
 * the window that the other gui items sit in
 */


public class UIWindow {
    boolean alive;
    KeyRecorder kr;
    final boolean active;
    final boolean killOnClose;
    private boolean isClosed;
    final private GridBagConstraints gbc;
    public final JFrame frame;
    public final JPanel panel;
    final ArrayList<GuiComp> comps;
    final ArrayList<Integer> compCoords;
    final ArrayList<Component> subComps;
    final ArrayList<Integer> subCompCoords;
    final ArrayList<Integer> subCompSizes;
    protected BufferedImage drawBuff;
    protected Graphics drawGraphics;
    final int[] locs;
    final GuiCloseAction closeAction;
    final TickTimer tt=new TickTimer();
    KeyboardFocusManager keyManager;
    final ParamSet params;
    /**
     * @param title the title that will appear at the top of the window
     * @param killOnClose whether the program should terminate on closing the window
     * @param CloseAction function that will run when the window is closed
     * @param active if set to false, the UIWindow will not actually render and its methods will be skipped (default true)
     */
    public UIWindow(String title, boolean killOnClose, GuiCloseAction CloseAction, boolean active){
        this.active=active;
        this.killOnClose=killOnClose;
        if(active) {
            this.frame = new JFrame();
            this.comps = new ArrayList<>();
            this.compCoords = new ArrayList<>();
            this.subComps = new ArrayList<>();
            this.subCompCoords = new ArrayList<>();
            this.subCompSizes = new ArrayList<>();
            this.frame.setResizable(false);//fixes window size
            this.frame.setLocationRelativeTo(null);//puts window in middle of screen
            this.closeAction = CloseAction;
            if (killOnClose) {
                this.frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (CloseAction != null) {
                            CloseAction.Action(e);
                        }
                        frame.setVisible(false);
                        frame.dispose();
                        isClosed=true;
                        System.exit(0);
                    }
                });
            } else {
                this.frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (alive) {
                            alive = false;
                            if (CloseAction != null) {
                                CloseAction.Action(e);
                            }
                            frame.setVisible(false);
                            frame.dispose();
                            isClosed=true;
                        }
                    }
                });
            }
            this.frame.setTitle(title);
            panel = new JPanel();
            gbc = new GridBagConstraints();
            gbc.weightx=1;
            gbc.weighty=1;

            panel.setLayout(new GridBagLayout());
            this.frame.add(panel);
            this.locs = new int[1000 * 1000];
            for (int i = 0; i < 1000 * 1000; i++) {
                locs[i] = -1;
            }
            this.kr=new KeyRecorder();
        }
        else{
            this.frame=null;
            this.comps=null;
            this.compCoords=null;
            this.subComps=null;
            this.subCompCoords=null;
            this.panel=null;
            this.locs=null;
            this.subCompSizes=null;
            this.closeAction=null;
            this.gbc=null;
        }
        params = new ParamSet();
    }
    /**
     * the below constructors are variants of the above constructor with default values for some of the arguments
     */
    public UIWindow() {
        this("",true,null,true);
    }
    public UIWindow(boolean active) {
        this("",true,null,active);
    }
    public UIWindow(boolean killOnClose, GuiCloseAction CloseAction){
        this("",killOnClose,CloseAction,true);
    }
    public UIWindow(boolean killOnClose,GuiCloseAction CloseAction,boolean active) {
        this("",killOnClose,CloseAction,active);
    }
    public UIWindow(String title) {
        this(title,true,null,true);
    }
    public UIWindow(String title,boolean active) {
        this(title,true,null,active);
    }
    public UIWindow(String title,boolean killOnClose, GuiCloseAction CloseAction){
        this(title,killOnClose,CloseAction,true);
    }
    public void TickPause(int millis){
        if(active) {
            tt.TickPause(millis);
        }
    }

    /**
     * sets the gui background color
     * @param backgroundColor default color of any empty space on the gui
     */
    public void SetColor(int backgroundColor){
        if(active) {
            this.panel.setOpaque(true);
            this.panel.setBackground(new Color(backgroundColor));
        }
    }

    public void SetParamChangeAction(ParamSetChangeAction paramChangeAction){
        this.params.SetParamChangeAction(paramChangeAction);
    }
    /**
     * Disables or enables all interacton with the UIWindow
     * @param onOff whether to enable or disable the gui
     */
    public void GreyOut(boolean onOff){
        if(active) {
            this.frame.setEnabled(!onOff);
            this.panel.setEnabled(!onOff);
            for (GuiComp gc : comps) {
                subComps.clear();
                subCompCoords.clear();
                subCompSizes.clear();
                gc._GetComps(subComps, subCompCoords, subCompSizes);
                for (Component sc : subComps) {
                    sc.setEnabled(!onOff);
                }
            }
            frame.repaint();
        }
    }
    public boolean IsKeyDown(char c){
        if(active) {
            return kr.IsPressed(c);
        }
        return false;
    }
    public boolean IsKeyDown(int keyCode){
        if(active){
            return kr.IsPressed(keyCode);
        }
        return false;
    }

    public void AddKeyResponses(KeyResponse OnKeyDown, KeyResponse OnKeyUp){
        if(active) {
            keyManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            UIWindow win = this;
            keyManager.addKeyEventDispatcher(new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    if (e.getID() == e.KEY_PRESSED && kr.KeyPress(e.getKeyCode())) {
                        if (OnKeyDown != null) {
                            OnKeyDown.Response(e.getKeyChar(), e.getKeyCode());
                        }
                    } else if (e.getID() == e.KEY_RELEASED) {
                        kr.KeyRelease(e.getKeyCode());
                        if (OnKeyUp != null) {
                            OnKeyUp.Response(e.getKeyChar(), e.getKeyCode());
                        }
                    }
                    return true;
                }

            });
        }
    }

    void PlaceComponent(GuiComp comp,int x,int y,int w,int h){
        int iComp=comps.size();
        comps.add(comp);
        compCoords.add(x);
        compCoords.add(y);
        for(int i=x;i<x+w;i++){
            for(int j=y;j<y+h;j++){
                locs[CtoI(i,j)]=iComp;
            }
        }
    }

    /**
     * starts the gui thread and reveals the gui to the user
     */
    public void RunGui(){
        if(alive){
            throw new IllegalStateException("RunGui has already been called on this window!");
        }
        if(active) {
            alive = true;
            for (int i = 0; i < comps.size(); i++) {
                int compX = compCoords.get(i * 2);
                int compY = compCoords.get(i * 2 + 1);
                GuiComp comp = comps.get(i);
                subComps.clear();
                subCompCoords.clear();
                subCompSizes.clear();
                comp._GetComps(subComps, subCompCoords, subCompSizes);
                for (int j = 0; j < subComps.size(); j++) {
                    Component subComp = subComps.get(j);
                    int subX = subCompCoords.get(j * 2);
                    int subY = subCompCoords.get(j * 2 + 1);
                    int subW = subCompSizes.get(j * 2);
                    int subH = subCompSizes.get(j * 2 + 1);
                    AddComponent(subComp, compX + subX, compY + subY, subW, subH);
                }
            }
            this.frame.pack();
            this.frame.setVisible(true);
            panel.setVisible(true);
        }
    }
    public boolean IsActive(){
        return !isClosed&&active;
    }
    public boolean IsRunning(){
        return alive;
    }

    public boolean IsClosed(){
        return isClosed;
    }

    public void SetParamValues(String[]vals){
        params.SetVals(vals);
    }
    public int GetInt(String label){
        return params.GetInt(label);
    }
    public double GetDouble(String label){
        return params.GetDouble(label);
    }
    public String GetString(String label){
        return params.GetString(label);
    }
    public boolean GetBool(String label){
        return params.GetBool(label);
    }
    public void SetLables(String[] labels){
        params.SetLabels(labels);
    }
    public void SetParam(String label,String value){
        params.Set(label,value);
    }
    public void SetValsAndLabels(String[]labels,String[]vals){
        SetValsAndLabels(labels,vals);
    }
    public String[] ValueStrings(){
        return params.ValueStrings();
    }
    public String[] LabelStrings(){
        return params.LabelStrings();
    }
    /**
     * ingore
     */
    void AddComponent(Component comp, int x, int y, int w, int h) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        panel.add(comp,gbc);
        comp.setVisible(true);
//        if(obj instanceof VisualizerOld){
//            ((VisualizerOld)obj).createBufferStrategy(3);
//        }
        if(comp instanceof PaintPanel){
            ((PaintPanel)comp).g=(Graphics2D)(((PaintPanel)comp).getGraphics());
        }
        if(comp instanceof MenuItem){
            MenuItem paramComp=((MenuItem)comp);
            this.params.AddGuiMenuItem(paramComp);
        }
    }

    void SetParams(String[] labelArr, String[] vals) {
        for (int i = 0; i < labelArr.length; i++) {
            params.labels.put(labelArr[i],i);
        }
    }

    int CtoI(int x,int y){ return x*1000+y; }
    int CtoLocVal(int x,int y){
        if(x<0||x>=1000||y<0||y>=1000){ throw new IllegalArgumentException("going for coord outside UIWindow range"); }
        return this.locs[x*1000+y];
    }

    /**
     * adds component by dropping down into the specified column
     * think connect 4 or tetris
     * @param col column to drop from. the left end of the component will occupy this column
     * @param comp component to be added
     */
    public void AddCol(int col, GuiComp comp){
        if(!active){
            comp.SetActive(false);
        }
        if(active) {
            if (comp.IsActive()) {
                int w = comp.compX();
                int h = comp.compY();
                int found = 0;
                for (int y = 999; y >= 0; y--) {
                    for (int x = col; x < col + w; x++) {
                        if (CtoLocVal(x, y) != -1) {
                            found = y + 1;
                            break;
                        }
                    }
                    if (found != 0) {
                        break;
                    }
                }
                PlaceComponent(comp, col, found, w, h);
            }
        }
    }

    /**
     * shortcut function creates a UILabel with the text argument, adds it to the column, then returns the created UILabel
     */
    public UILabel AddCol(int col,String text){
        UILabel ret=new UILabel(text);
        AddCol(col,ret);
        return ret;
    }

    /**
     * destroys the gui with the window closing event
     */
    public void Close(){
        if(active) {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
    }
//    private void SaveImage(String filePath,String type){
//        if(active) {
//           // if(drawBuff==null||drawBuff.getHeight()!=panel.getHeight()||drawBuff.getWidth()!=panel.getWidth()) {
//           //     drawBuff = new BufferedImage(panel.getHeight(), panel.getWidth(), BufferedImage.TYPE_INT_RGB);
//           // }
//            Dimension size=frame.getSize();
//            drawBuff=(BufferedImage)frame.createImage(size.width,size.height);
//            drawGraphics=drawBuff.getGraphics();
//            frame.paint(drawGraphics);
//            drawGraphics.dispose();
//            frame.repaint();
//            try {
//                ImageIO.write(drawBuff, type, new File(filePath));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    public void ToPNG(String filePath) {
//        SaveImage(filePath,"png");
//    }
//    public void ToJPG(String filePath) {
//        SaveImage(filePath,"jpg");
//    }
//    public void ToGIF(String filePath) {
//        SaveImage(filePath,"gif");
//    }


//    public void paintComponent(Graphics g) {
//        panel.paintComponent(g);
//    }
}

