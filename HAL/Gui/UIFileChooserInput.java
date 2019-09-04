package HAL.Gui;

import HAL.Interfaces.*;
import HAL.Tools.FileIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * a gui item that allows the user to select an existing file, or create a new file
 */
public class UIFileChooserInput extends JButton implements HAL.Interfaces.MenuItem,GuiComp {
    JFileChooser browser;
    String labelText;
    JLabel label;
    int compX;
    int compY;
    UIWindow win;
    private final String initValue;
    public UIFileChooserInput(String label, int compX, int compY, String initVal){
        super();
        this.initValue=initVal;
        this.compX=compX;
        this.compY=compY;
        this.browser=new JFileChooser();
        this.labelText=label;
        this.label=new JLabel(labelText);
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret=browser.showSaveDialog(null);
                if(ret==JFileChooser.APPROVE_OPTION){
                    SetFile(browser.getSelectedFile());
                }
            }
        });
    }
    public UIFileChooserInput(String label, String initVal){
        this(label,1,2,initVal);
    }

    /**
     * sets the foreground and background of the UIFileChooserInput
     * @param foregroundColor color of the text if null the UIWindow color will be used
     * @param backgroundColor color of the background, if null the UIWindow color will be used
     */
    public UIFileChooserInput SetColor(Color foregroundColor, Color backgroundColor){
        if(backgroundColor!=null){
            setOpaque(true);
            setBackground(backgroundColor);
            label.setOpaque(true);
            label.setBackground(backgroundColor);
        }
        if(foregroundColor !=null) {
            setForeground(foregroundColor);
            label.setForeground(foregroundColor);
        }
        return this;
    }
    public UIFileChooserInput SetColor(int foregroundColor, int backgroundColor){
        SetColor(new Color(foregroundColor),new Color(backgroundColor));
        return this;
    }
    /**
     * ignore
     */
    @Override
    public void Set(String filePath) {
        File chosen=new File(filePath);
        if(!chosen.exists()) {
            FileIO maker=new FileIO(filePath,"w");
            maker.Close();
            chosen=new File(filePath);
        }
        SetFile(chosen);
    }
    /**
     * sets the selected file
     */
    public void SetFile(File chosen){
        String name=chosen.getName();
        if(name.length()>10) { name = name.substring(0, 10); }
        this.setText(name);
        this.browser.setSelectedFile(chosen);
    }

    /**
     * ignore
     */
    @Override
    public String Get() {
        return this.browser.getSelectedFile().getAbsolutePath();
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

    /**
     * ignore
     */
    @Override
    public int TypeID() {
        return 3;
    }

}
