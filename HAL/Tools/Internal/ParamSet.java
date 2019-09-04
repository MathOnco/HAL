package HAL.Tools.Internal;

import HAL.Gui.UIWindow;
import HAL.Interfaces.MenuItem;
import HAL.Interfaces.ParamSetChangeAction;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.*;

/*PLAN
GetPix Base Version Working
Refine Syntax and Methods
*/


/**
 * the ParamSet class is used to keep track of all of the items in a menu
 * it can be queried to get the current values of the menu
 * values and labels can also be loaded as arrays of strings, allowing for the menuset to be used without a gui
 */
public class ParamSet {
    UIWindow win;
    final int typeID = 1;//integer
    boolean column;
    final ArrayList<MenuItem> items;
    public HashMap<String,Integer>labels;
    public ArrayList<String>vals;
    ParamSetChangeAction ChangeAction;

    public ParamSet() {
        this(null,null);
    }

    /**
     * @param labelArr array of all label names
     * @param vals array of values, must be in the same order as the labels array
     */
    public ParamSet(String[] labelArr, String[] vals){
        labels=new HashMap<>();
        if(labelArr!=null) {
            for (int i = 0; i < labelArr.length; i++) {
                labels.put(labelArr[i], i);
            }
        }
        if(vals!=null){
            this.vals=new ArrayList<String>(Arrays.asList(vals));
        }
        else {
            this.vals = new ArrayList<>();
        }
        this.items = new ArrayList<MenuItem>();
        this.ChangeAction =null;
    }
    /**
     * gets the boolean value from the set with the specified label
     */
    public boolean GetBool(String label){
        String val=vals.get(labels.get(label)).toLowerCase();
        boolean setVal=false;
        if(val.equals("true")||val.equals("t")){
            setVal=true;
        }else if(val.equals("false")||val.equals("f")) {
            setVal = false;
        }
        return setVal;
    }
    /**
     * gets the integer value from the set with the specified label
     */
    public int GetInt(String label){
        return (int)Double.parseDouble(vals.get(labels.get(label)));
    }
    /**
     * gets the double value from the set with the specified label
     */
    public double GetDouble(String label){
        return Double.parseDouble(vals.get(labels.get(label)));
    }
    /**
     * gets the string value from the set with the specified label
     */
    public String GetString(String label){
        return vals.get(labels.get(label));
    }

    /**
     * sets all values with the vals array provided
     */
    public void SetVals(String[] vals){
        if(vals.length==labels.size()) {
            this.vals = new ArrayList<String>(Arrays.asList(vals));
        }
        else{
            throw new IllegalArgumentException("Values array has wrong size!");
        }
    }

    /**
     * sets the value at the specified label to the specified value
     */
    public void Set(String label,String value){
        vals.set(labels.get(label),value);
    }

    /**
     * sets all labels with the labels array provided
     */
    public void SetLabels(String[] labels){
        for(int i=0;i<labels.length;i++){
            this.labels.put(labels[i],i);
            if(vals.size()<=i){
                vals.add("");
            }
        }
    }
    public void SetParamChangeAction(ParamSetChangeAction ChangeAction){
        this.ChangeAction=ChangeAction;
    }


    /**
     * sets both the vals and labels with the arrays provided
     */
    public void SetValsAndLabels(String[]labels,String[] vals) {
        for(int i=0;i<labels.length;i++){
            Set(labels[i],vals[i]);
        }
    }

    /**
     * adds a new gui menu item to the set
     */
    public MenuItem AddGuiMenuItem(MenuItem addMe) {
        String name = addMe.GetLabel();
        labels.put(addMe.GetLabel(),items.size());
        items.add(addMe);
        addMe.Set(addMe._GetInitValue());
        vals.add(addMe._GetInitValue());
        //((java.awt.Component)addMe).addPropertyChangeListener(new PropertyChangeListener() {
        ((java.awt.Component)addMe).addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                String label=addMe.GetLabel();
                String val=addMe.Get();
                if(ChangeAction !=null)
                ChangeAction.Action(label,val,GetString(label));
                Set(label,val);
            }
        });
        return addMe;
    }

    /**
     * set all gui values with the vals array provided
     */
    public void SetGuiAll(String[] vals) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).Set(vals[i]);
        }
        SetVals(vals.clone());
    }

    /**
     * sets the gui component with the provided label to val
     */
    public void SetGui(String label, String val){
        items.get(labels.get(label)).Set(val);
        Set(label,val);
    }

    /**
     * returns the values in the set as an array of strings
     */
    public String[] ValueStrings() {
        String[] elems = new String[vals.size()];
        for (int i = 0; i < vals.size(); i++) {
            elems[i] = vals.get(i);
        }
        return elems;
    }

    /**
     * returns the labels in the set as an array of strings
     */
    public String[] LabelStrings() {
        String[] newLabels = new String[labels.size()];
        for (Map.Entry<String,Integer> entry:labels.entrySet()) {
            newLabels[entry.getValue()] = entry.getKey();
        }
        return newLabels;
    }
}
