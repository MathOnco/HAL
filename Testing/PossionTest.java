//package Testing;
//
//import Framework.Utils;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class PossionTest {
//    @Test
//    public void FactorialTest() {
//        assertEquals(Utils.Factorial(0), 1);
//        assertEquals(Utils.Factorial(1), 1);
//        assertEquals(Utils.Factorial(2), 2);
//        assertEquals(Utils.Factorial(3), 6);
//    }
//
//    @Test
//    public void SamplePoissonTest() {
//        double lambda = 1;
//        assertEquals(
//                Utils.PoissonProb(0, lambda),
//                Math.pow(Math.E, -lambda),
//                10e-5);
//        assertEquals(
//                Utils.PoissonProb(1, lambda),
//                0.367879441171442,
//                10e-5);
//        assertEquals(
//                Utils.PoissonProb(2, lambda),
//                0.183939720585721,
//                10e-5);
//
//        lambda = 2;
//        assertEquals(
//                Utils.PoissonProb(2, lambda),
//                0.270670566473225,
//                10e-5);
//    }
//}
