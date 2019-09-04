package HAL.Interfaces;

public interface RNG {
    public int Int(int bound);
    public double Double(double bound);
    public double Double();
    public long Long(long bound);
    public boolean Bool();
}
