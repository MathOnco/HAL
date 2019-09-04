package HAL.Tools;

public class BoolSet {
    private final long[] data;
    final int LBITS=Long.SIZE;
    public final int length;
    public BoolSet(int capacity){
        this.length=capacity;
        data =new long[capacity/LBITS+1];
    }
    public BoolSet(long[]data,int capacity){
        this.data=new long[data.length];
        this.length=capacity;
        System.arraycopy(data,0,this.data,0,data.length);
    }
    public BoolSet Copy(){
        return new BoolSet(data,this.length);
    }
    public long[] GetData(){
        return data;
    }
    public void Set(int i,boolean val){
        int block=i/LBITS;
        int iblock=i%LBITS;
        if(val) {
            data[block] = data[block] | (1L << iblock);
        }else{
            data[block] = data[block] & ~(1L << iblock);
        }
    }
    public boolean Get(int i){
        int block=i/LBITS;
        int iblock=i%LBITS;
        return (data[block] & (1L << iblock)) != 0;
    }
    public void SetSafe(int i,boolean val){
        if(i>=length||i<0){
            throw new IndexOutOfBoundsException("set index "+i+" is out of BoolSet bounds, length "+length);
        }
        Set(i,val);
    }
    public boolean GetSafe(int i){
        if(i>=length||i<0){
            throw new IndexOutOfBoundsException("get index "+i+" is out of BoolSet bounds, length "+length);
        }
        return Get(i);
    }

    public static void main(String[] args) {
        BoolSet ex=new BoolSet(100);
        ex.SetSafe(85,true);
        System.out.println(ex.GetSafe(85));
    }
}
