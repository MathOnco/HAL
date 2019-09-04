package HAL.Gui;

import HAL.Interfaces.GuiComp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * a menu item that facilitates choosing from a set of options
 */
public class UIComboBoxInput extends JComboBox implements HAL.Interfaces.MenuItem,GuiComp {
    String labelText;
    JLabel label;
    int compX;
    int compY;
    private final String initVal;

    /**
     * @param label   the label of the comboBox
     * @param initVal the starting setting of the combo box
     * @param options the array of options. the setting of the combo box corresponds to the index of the option in the
     *                array
     * @param compX   the width on the gui GridBagLayout
     * @param compY   the height on the gui GridBagLayout
     */
    public UIComboBoxInput(String label, int initVal, String[] options, int compX, int compY) {
        super(options);
        this.initVal = Integer.toString(initVal);
        this.labelText = label;
        this.label = new JLabel(labelText);
        this.compX = compX;
        this.compY = compY;
    }

    /**
     * @param label   the label of the comboBox
     * @param initVal the starting setting of the combo box
     * @param options the array of options. the setting of the combo box corresponds to the index of the option in the
     *                array
     */
    public UIComboBoxInput(String label, int initVal, String[] options) {
        this(label, initVal, options, 1, 2);
    }

    /**
     * sets the foreground and background of the UIComboBoxInput
     *
     * @param foregroundColor color of the text if null the UIWindow color will be used
     * @param backgroundColor color of the background, if null the UIWindow color will be used
     */
    public UIComboBoxInput SetColor(Color foregroundColor, Color backgroundColor) {
        if (backgroundColor != null) {
            setOpaque(true);
            setBackground(backgroundColor);
            label.setOpaque(true);
            label.setBackground(backgroundColor);
        }
        if (foregroundColor != null) {
            setForeground(foregroundColor);
            label.setForeground(foregroundColor);
        }
        return this;
    }

    public UIComboBoxInput SetColor(int foregroundColor, int backgroundColor) {
        SetColor(new Color(foregroundColor), new Color(backgroundColor));
        return this;
    }

    /**
     * ignore
     */
    @Override
    public String Get() {
        return Integer.toString(this.getSelectedIndex());
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
        return label != null ? 2 : 1;
    }

    /**
     * ignore
     */
    @Override
    public <T extends Component> T GetEntry(int iEntry) {
        switch (iEntry) {
            case 0:
                return (T) label;
            case 1:
                return (T) this;
            default:
                throw new IllegalArgumentException(iEntry + " does not match to an item!");
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
    public int TypeID() {
        return 1;
    }

    /**
     * ignore
     */
    @Override
    public void Set(String val) {
        this.setSelectedIndex(Integer.parseInt(val));
    }


    /**
     * ignore
     */
    @Override
    public void _GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere) {
        int labelEnd = compY / 2;
        putHere.add(this.label);
        coordsHere.add(0);
        coordsHere.add(0);
        compSizesHere.add(compX);
        compSizesHere.add(labelEnd);
        putHere.add(this);
        coordsHere.add(0);
        coordsHere.add(labelEnd);
        compSizesHere.add(compX);
        compSizesHere.add(compY - labelEnd);
    }
}
