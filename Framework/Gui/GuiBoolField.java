package Framework.Gui;

import Framework.Interfaces.GuiComp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * a menu item that can be toggled to true or false
 */
public class GuiBoolField extends JToggleButton implements Framework.Interfaces.MenuItem,GuiComp {
    int compX;
    int compY;
    private final String initVal;
    public GuiBoolField(String text, boolean initVal){
        super(text);
        this.initVal=Boolean.toString(initVal);
        this.compX=1;
        this.compY=1;
    }
    public GuiBoolField(String text, boolean initVal, int compX, int compY){
        super(text);
        this.initVal=Boolean.toString(initVal);
        this.compX=compX;
        this.compY=compY;
    }

    /**
     * ignore
     */
    @Override
    public int TypeID() { return 0; }

    /**
     * sets the boolean value, true, false, t or f are all valid inputs
     */
    @Override
    public void Set(String val) {
        val=val.toLowerCase();
        boolean setVal;
        if(val.equals("true")||val.equals("t")){
            setVal=true;
        }else if(val.equals("false")||val.equals("f")){
            setVal=false;
        }
        else{
            throw new IllegalArgumentException(val+" cannot be interpreted as true or false");
        }
        this.setSelected(setVal);
    }

    /**
     * returns the current boolean value as a string
     */
    @Override
    public String Get() { return isSelected()?"true":"false"; }

    /**
     * returns teh label associated with the boolean
     */
    @Override
    public String GetLabel() {
        return this.getText();
    }


    /**
     * sets the foreground and background of the GuiBoolField
     * @param foregroundColor color of the text if null the GuiWindow color will be used
     * @param backgroundColor color of the background, if null the GuiWindow color will be used
     */
    public GuiBoolField SetColor(Color foregroundColor, Color backgroundColor){
        if(backgroundColor!=null){
            setOpaque(true);
            setBackground(backgroundColor);
        }
        if(foregroundColor !=null) {
            setForeground(foregroundColor);
        }
        return this;
    }

    public GuiBoolField SetColor(int foregroundColor, int backgroundColor){
        SetColor(new Color(foregroundColor),new Color(backgroundColor));
        return this;
    }
    /**
     * ignore
     */
    @Override
    public int NEntries() {
        return 1;
    }


    /**
     * ignore
     */
    @Override
    public <T extends Component> T GetEntry(int iEntry) {
        switch(iEntry){
            case 0: return (T)this;
            default: throw new IllegalArgumentException(iEntry+" does not match to an item!");
        }
    }

    @Override
    public String _GetInitValue() {
        return initVal;
    }

    /**
     * ignore
     */
    @Override
    public int compX(){ return compX; }

    /**
     * ignore
     */
    @Override
    public int compY(){ return compY; }

    @Override
    public boolean IsActive() {
        return true;
    }

    @Override
    public void SetActive(boolean isActive) { }

    /**
     * ignore
     */
    @Override
    public void GetComps(ArrayList<Component> putHere,ArrayList<Integer> coordsHere,ArrayList<Integer> sizesHere){
        putHere.add(this);
        coordsHere.add(0);
        coordsHere.add(0);
        sizesHere.add(compX);
        sizesHere.add(compY);
    }

}
