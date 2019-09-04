package HAL.Interfaces;

import HAL.Tools.FileIO;

public interface CustomSerialization {
    public void ToBinary(FileIO out);
    public void FromBinary(FileIO in);
}
