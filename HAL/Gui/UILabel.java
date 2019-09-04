package HAL.Gui;

import HAL.Interfaces.GuiComp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * a gui item that presents text
 */
public class UILabel implements GuiComp {
    final public JLabel label;
    boolean active;
    int xDim;
    int yDim;

    /**
     * @param text label text
     * @param compX width on the gui GridBagLayout GridBagLayout
     * @param compY height on the gui GridBagLayout GridBagLayout
     */
    public UILabel(String text, int compX, int compY, boolean active){
        this.active=active;
        this.xDim=compX;
        this.yDim=compY;
        if(active){
            this.label=new JLabel(text);
            this.label.setPreferredSize((Dimension)this.label.getPreferredSize().clone());
        }
        else{
            this.label=null;
        }
    }
    public UILabel(String text, int compX, int compY){
        this(text,compX,compY,true);
    }

    /**
     * @param text label text
     */
    public UILabel(String text, boolean active){
        this(text,1,1,active);
    }
    public UILabel(String text){
        this(text,1,1,true);
    }
//    public void SetMinSize(){
//        double newW=Math.max(myDim.getWidth(),label.getPreferredSize().getWidth());
//        double newH=Math.max(myDim.getHeight(),label.getPreferredSize().getHeight());
//        myDim.setSize(newW,newH);
//        this.label.setPreferredSize(myDim);
//    }

    /**
     * gets the xDim component of the label
     */
    public int compX(){
        return xDim;
    }

    /**
     * gets the yDim component of the label
     */
    public int compY(){
        return yDim;
    }

    @Override
    public boolean IsActive() {
        return active;
    }

    @Override
    public void SetActive(boolean isActive) {

    }

    public String GetText(){
        if(active) {
            return label.getText();
        }
        return null;
    }
    public void SetText(String text){
        if(active){
            label.setText(text);
//            SetMinSize();
        }
    }

    /**
     * sets the foreground and background of the UILabel
     * @param foregroundColor color of the text if null the UIWindow color will be used
     * @param backgroundColor color of the background, if null the UIWindow color will be used
     */
    public UILabel SetColor(Color foregroundColor, Color backgroundColor){
        if(active) {
            if (backgroundColor != null) {
                label.setOpaque(true);
                label.setBackground(backgroundColor);
            }
            if (foregroundColor != null) {
                label.setForeground(foregroundColor);
            }
        }
        return this;
    }
    public UILabel SetColor(int foregroundColor, int backgroundColor){
        SetColor(new Color(foregroundColor),new Color(backgroundColor));
        return this;
    }
    /**
     * called by the UIWindow class to place the label
     */
    public void _GetComps(ArrayList<Component> putHere, ArrayList<Integer> putCoordsHere, ArrayList<Integer>compSizesHere){
        putHere.add(label);
        putCoordsHere.add(0);
        putCoordsHere.add(0);
        compSizesHere.add(xDim);
        compSizesHere.add(yDim);
    }
}
