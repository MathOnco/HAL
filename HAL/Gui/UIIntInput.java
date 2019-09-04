package HAL.Gui;

import HAL.Interfaces.*;
import HAL.Util;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * a menu item that takes int input
 */
public class UIIntInput extends JFormattedTextField implements HAL.Interfaces.MenuItem,GuiComp {
    static final DecimalFormat intFormat=new DecimalFormat("#");
    String labelText;
    JLabel label;
    int min;
    int max;
    int compX;
    int compY;
    private final String initVal;
    /**
     * @param label the label of the menuInt
     * @param initVal the starting value of the menuInt
     */
    public UIIntInput(String label, int initVal, int min, int max){
        this(label,initVal,min,max,10,1,2);
    }

    /**
     * @param label the label of the menuInt
     * @param initVal the starting value of the menuInt
     * @param nCols the number of digits that will fit on the display
     * @param compX the width on the gui GridBagLayout
     * @param compY the height on the gui GridBagLayout
     */
    public UIIntInput(String label, int initVal, int min, int max, int nCols, int compX, int compY){
        super(intFormat);
        this.initVal=Integer.toString(initVal);
        this.compX=compX;
        this.compY=compY;
        this.setColumns(nCols);
        this.min=min;
        this.max=max;
        this.labelText=label;
        if(label.length()>0){
            this.label=new JLabel(labelText);
        }
    }

    /**
     * sets the foreground and background of the UIIntInput
     * @param foregroundColor color of the text if null the UIWindow color will be used
     * @param backgroundColor color of the background, if null the UIWindow color will be used
     */
    public UIIntInput SetColor(Color foregroundColor, Color backgroundColor){
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

    public UIIntInput SetColor(int foregroundColor, int backgroundColor){
        SetColor(new Color(foregroundColor),new Color(backgroundColor));
        return this;
    }
    /**
     * ignore
     */
    @Override
    public int TypeID() { return 1; }

    /**
     * sets the value of the UIIntInput to the string provided
     */
    @Override
    public void Set(String val) { this.setText(Integer.toString(Util.Bound(Integer.parseInt(val),min,max))); }

    /**
     * ignore
     */
    @Override
    public String Get() {
        String val=Integer.toString(Util.Bound(Integer.parseInt(this.getText()),min,max));
        this.Set(val);
        return val;
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
    public int NEntries() { return 2; }

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
    public void SetActive(boolean isActive) {

    }

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
