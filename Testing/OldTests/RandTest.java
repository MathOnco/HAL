package Testing.OldTests;

import HAL.Rand;

/**
 * Created by Rafael on 11/16/2017.
 */
public class RandTest {
    public static void main(String[] args) {
        Rand rng=new Rand(0);
        System.out.println(rng.Double());
        Rand rng2=new Rand(0);
        System.out.println(rng2.Double());
    }
}
