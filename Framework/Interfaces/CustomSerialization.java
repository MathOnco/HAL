package Framework.Interfaces;

import Framework.Tools.FileIO;

public interface CustomSerialization {
    public void ToBinary(FileIO out);
    public void FromBinary(FileIO in);
}
