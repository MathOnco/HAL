package Testing;

/**
 * Created by Rafael on 10/3/2017.
 */

public class MullersRatchet{
    double[]pops;
    double[]update;

    public MullersRatchet(double[]pops,double[]update){
        this.pops=pops;
        this.update=update;
    }
    public void Step(int i){
        int popLen=pops.length/2;
        int currPop=i%2;
        for (int x = 0; x < popLen; x++) {
            for (int y = 0; y < popLen; y++) {
                pops[(currPop*popLen)+x]+=update[x*popLen+y]*pops[popLen-(currPop*popLen)+x];
            }
        }
    }
    public static void main(String[] args) {
        //ReplateRatchet mr=new ReplateRatchet()
    }
}
