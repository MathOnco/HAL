package Testing.OldTests;

/**
 * Created by bravorr on 7/18/17.
 */
public class DoubleAdditionTest {
    public static void main(String[] args) {
        double start=10000000;
        for (int i = 0; i < 10000000; i++) {
           start+=1;
        }
        System.out.println(start);
    }
}
