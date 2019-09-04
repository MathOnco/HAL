package HAL.Tools.Internal;

import HAL.Rand;

import java.io.Serializable;

/**
 * Created by Rafael on 11/16/2017.
 */


public class Gaussian implements Serializable{
    double V1=0,V2=0,S=0,phase=0;
    public double Sample(double mean, double stdDev, Rand rn) {
        double X;
        if (phase == 0) {
            do {
                double U1 = rn.Double();
                double U2 = rn.Double();

                V1 = 2 * U1 - 1;
                V2 = 2 * U2 - 1;
                S = V1 * V1 + V2 * V2;
            } while (S >= 1 || S == 0);

            X = V1 * Math.sqrt(-2 * Math.log(S) / S);
        } else {
            X = V2 * Math.sqrt(-2 * Math.log(S) / S);
        }
        phase = 1 - phase;
        return X * stdDev + mean;
    }
}
