package Framework.Gui;

import Framework.Interfaces.GuiComp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * a gui item that presents text
 */
public class GuiLabel implements GuiComp {
    final public JLabel label;
    boolean active;
    int xDim;
    int yDim;

    /**
     * @param text label text
     * @param compX width on the gui GridBagLayout GridBagLayout
     * @param compY height on the gui GridBagLayout GridBagLayout
     */
    public GuiLabel(String text, int compX, int compY,boolean active){
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
    public GuiLabel(String text, int compX, int compY){
        this.active=true;
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

    /**
     * @param text label text
     */
    public GuiLabel(String text,boolean active){
        this.active=active;
        this.xDim=1;
        this.yDim=1;
        if(active){
            this.label=new JLabel(text);
            this.label.setPreferredSize((Dimension)this.label.getPreferredSize().clone());
        }
        else{
            this.label=null;
        }
    }
    public GuiLabel(String text){
        this.active=true;
        this.xDim=1;
        this.yDim=1;
        if(active){
            this.label=new JLabel(text);
            this.label.setPreferredSize((Dimension)this.label.getPreferredSize().clone());
        }
        else{
            this.label=null;
        }
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
     * sets the foreground and background of the GuiLabel
     * @param foregroundColor color of the text if null the GuiWindow color will be used
     * @param backgroundColor color of the background, if null the GuiWindow color will be used
     */
    public GuiLabel SetColor(Color foregroundColor, Color backgroundColor){
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
    public GuiLabel SetColor(int foregroundColor, int backgroundColor){
        SetColor(new Color(foregroundColor),new Color(backgroundColor));
        return this;
    }
    /**
     * called by the GuiWindow class to place the label
     */
    public void GetComps(ArrayList<Component> putHere,ArrayList<Integer> putCoordsHere,ArrayList<Integer>compSizesHere){
        putHere.add(label);
        putCoordsHere.add(0);
        putCoordsHere.add(0);
        compSizesHere.add(xDim);
        compSizesHere.add(yDim);
    }
}
