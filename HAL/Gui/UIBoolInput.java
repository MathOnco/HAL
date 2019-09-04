package HAL.Gui;

import HAL.Interfaces.GuiComp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * a menu item that can be toggled to true or false
 */
public class UIBoolInput extends JToggleButton implements HAL.Interfaces.MenuItem,GuiComp {
    int compX;
    int compY;
    private final String initVal;
    public UIBoolInput(String text, boolean initVal){
        this(text,initVal,1,1);
    }
    public UIBoolInput(String text, boolean initVal, int compX, int compY){
        super(text);
        this.initVal=Boolean.toString(initVal);
        this.compX=compX;
        this.compY=compY;
    }

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
     * returns the label associated with the boolean
     */
    @Override
    public String GetLabel() {
        return this.getText();
    }


    /**
     * sets the foreground and background of the UIBoolInput
     * @param foregroundColor color of the text if null the UIWindow color will be used
     * @param backgroundColor color of the background, if null the UIWindow color will be used
     */
    public UIBoolInput SetColor(Color foregroundColor, Color backgroundColor){
        if(backgroundColor!=null){
            setOpaque(true);
            setBackground(backgroundColor);
        }
        if(foregroundColor !=null) {
            setForeground(foregroundColor);
        }
        return this;
    }

    public UIBoolInput SetColor(int foregroundColor, int backgroundColor){
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
    public void _GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> sizesHere){
        putHere.add(this);
        coordsHere.add(0);
        coordsHere.add(0);
        sizesHere.add(compX);
        sizesHere.add(compY);
    }
    /**
     * ignore
     */
    @Override
    public int TypeID() { return 0; }

}
