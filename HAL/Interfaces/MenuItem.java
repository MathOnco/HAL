package HAL.Interfaces;

/**
 * Created by rafael on 11/25/16.
 */
public interface MenuItem{
    public int TypeID();//bool,int,double,String
    public void Set(String val);
    public String Get();
    public String GetLabel();
    public int NEntries();
    public <T extends java.awt.Component> T GetEntry(int iEntry);
    public String _GetInitValue();
}
