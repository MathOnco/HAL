package HAL.Gui;

import HAL.Interfaces.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * a menu item that takes string input
 */
public class UIStringInput extends JTextField implements HAL.Interfaces.MenuItem,GuiComp {
    String labelText;
    public JLabel label;
    int compX;
    int compY;
    int nCols;
    private final String initVal;


    /**
     * @param label the label of the menuString
     * @param initVal the starting value of the menuString
     * @param nCols the number of characters that will fit on the display
     * @param compX the width on the gui GridBagLayout
     * @param compY the height on the gui GridBagLayout
     */
    public UIStringInput(String label, String initVal, int nCols, int compX, int compY){
        super(nCols);
        this.initVal=initVal;
        this.nCols=nCols;
        this.compX=compX;
        this.compY=compY;
        this.labelText=label;
        this.label=new JLabel(labelText);
    }
    public UIStringInput(String label, int nCols, String initVal){
        this(label,initVal,nCols,1,2);
    }
    /**
     * @param label the label of the menuString
     * @param initVal the starting value of the menuString
     */
    public UIStringInput(String label, String initVal){
        this(label,initVal,10,1,2);
    }

    /**
     * sets the foreground and background of the UIStringInput
     * @param foregroundColor color of the text if null the UIWindow color will be used
     * @param backgroundColor color of the background, if null the UIWindow color will be used
     */
    public UIStringInput SetColor(Color foregroundColor, Color backgroundColor){
        if(backgroundColor!=null){
            setOpaque(true);
            setBackground(backgroundColor);
            label.setOpaque(true);
            label.setBackground(backgroundColor);
        }
        if(foregroundColor !=null) {
            setForeground(foregroundColor);
            label.setForeground(foregroundColor);
            setCaretColor(foregroundColor);
        }
        return this;
    }

    public UIStringInput SetColor(int foregroundColor, int backgroundColor){
        SetColor(new Color(foregroundColor),new Color(backgroundColor));
        return this;
    }
    /**
     * ignore
     */
    @Override
    public int TypeID() {
        return 3;
    }

    /**
     * ignore
     */
    @Override
    public void Set(String val) {
        if(val.length()>nCols){ val=val.substring(0,nCols); }
        this.setText(val);
    }

    /**
     * ignore
     */
    @Override
    public String Get() {
        return this.getText();
    }

    /**
     * ignore
     */
    @Override
    public String GetLabel() {
        return labelText;
    }

    /**
     * ignore
     */
    @Override
    public int NEntries() {
        return 2;
    }


    /**
     * ignore
     */
    @Override
    public <T extends Component> T GetEntry(int iEntry) {
        switch(iEntry){
            case 0: return (T)label;
            case 1: return (T)this;
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
    public int compX() {
        return compX;
    }

    /**
     * ignore
     */
    @Override
    public int compY() {
        return compY;
    }

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
    public void _GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere) {
        int labelEnd=compY/2;
        putHere.add(this.label);
        coordsHere.add(0);
        coordsHere.add(0);
        compSizesHere.add(compX);
        compSizesHere.add(labelEnd);
        putHere.add(this);
        coordsHere.add(0);
        coordsHere.add(labelEnd);
        compSizesHere.add(compX);
        compSizesHere.add(compY-labelEnd);
    }
}
