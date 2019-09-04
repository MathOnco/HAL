package HAL.Gui;

import HAL.Interfaces.*;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import static HAL.Util.*;


/**
 * a menu item that takes double input
 */
public class UIDoubleInput extends JFormattedTextField implements HAL.Interfaces.MenuItem,GuiComp {
    static final DecimalFormat doubleFormat=new DecimalFormat("#.##########################");
    private final String initValue;
    String labelText;
    JLabel label;
    double min;
    double max;
    int compX;
    int compY;


    /**
     * @param label the label of the menuDouble
     * @param initVal the starting value of the menuDouble
     * @param nCols the number of digits that will fit on the display
     * @param compX the width on the gui GridBagLayout
     * @param compY the height on the gui GridBagLayout
     */
    public UIDoubleInput(String label, double initVal, double min, double max, int nCols, int compX, int compY){
        super(doubleFormat);
        this.initValue=Double.toString(initVal);
        this.setColumns(nCols);
        this.min=min;
        this.max=max;
        this.labelText=label;
        this.label=new JLabel(labelText);
        this.compX=compX;
        this.compY=compY;
    }

    /**
     * @param label the label of the menuDouble
     * @param initVal the starting setting of the menuDouble
     */
    public UIDoubleInput(String label, double initVal, double min, double max){
        this(label,initVal,min,max,10,1,2);
    }

    /**
     * sets the foreground and background of the UIDoubleInput
     * @param foregroundColor color of the text if null the UIWindow color will be used
     * @param backgroundColor color of the background, if null the UIWindow color will be used
     */
    public UIDoubleInput SetColor(Color foregroundColor, Color backgroundColor){
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

    public UIDoubleInput SetColor(int foregroundColor, int backgroundColor){
        SetColor(new Color(foregroundColor),new Color(backgroundColor));
        return this;
    }
    /**
     * ignore
     */
    @Override
    public int TypeID() {
        return 2;
    }

    /**
     * ignore
     */
    @Override
    public void Set(String val) {
        this.setText(Double.toString(Bound(Double.parseDouble(val),min,max)));
    }

    /**
     * ignore
     */
    @Override
    public String Get() {
        String val=Double.toString(Bound(Double.parseDouble(this.getText()),min,max));
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
        return this.initValue;
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
    public void _GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere){
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
