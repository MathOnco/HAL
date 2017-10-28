package Framework.Extensions;

public interface Module {
    int GetNumNeededProps();//should return the number of needed props
    void SetModPropID(int propID,int modulePropIndex);//used to store a label for each propID
}
