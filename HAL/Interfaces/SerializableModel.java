package HAL.Interfaces;

import java.io.*;

@FunctionalInterface
public interface SerializableModel extends Serializable{
    //must setup constructors of all grids that are serialized, used when loading
    public void SetupConstructors();//sets up the constructors for all Grid2Ds/Grid3Ds, use the _PassAgentConstructor() function to do this, if no Grid2Ds or Grid3Ds are being used, this function can be left empty
}
