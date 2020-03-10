package HAL;

import HAL.Interfaces.RNG;
import HAL.Tools.Internal.Binomial;
import HAL.Tools.Internal.Gaussian;
import HAL.Tools.Internal.SplittableRN;

import java.io.Serializable;

import static HAL.Util.DistSquared;
import static HAL.Util.Norm;

/**
 * contains functions for random value generation and distribution sampling.
 */
public class Rand implements Serializable {
    public final RNG rn;
    public final Gaussian gn;
    public final Binomial bn;

    /**
     * creates a Rand object using the RNG argument as its internal generator
     */
    public Rand(RNG rng) {
        this.rn = rng;
        this.gn = new Gaussian();
        this.bn = new Binomial();
    }

    public Rand(long seed) {
        this.rn = new SplittableRN(seed);
        this.gn = new Gaussian();
        this.bn = new Binomial();
    }

    public Rand() {
        this.rn = new SplittableRN();
        this.gn = new Gaussian();
        this.bn = new Binomial();
    }

    /**
     * returns a random integer from 0 up to (not including) bound
     */
    public int Int(int bound) {
        if (bound == 1) {
            return 0;
        }
        return rn.Int(bound);
    }

    /**
     * returns a random double from 0 up to bound
     */
    public double Double(double bound) {
        return rn.Double(bound);
    }
    public double Double(int bound) {
        return rn.Double(bound);
    }

    /**
     * returns a random double from 0 to 1
     */
    public double Double() {
        return rn.Double();
    }

    /**
     * returns a random long from 0 up to (not including) bound
     */
    public long Long(long bound) {
        return rn.Long(bound);
    }

    /**
     * returns a random boolean value (true or false)
     */
    public boolean Bool() {
        return rn.Bool();
    }

    /**
     * returns a random number from the binomial distribution
     */
    public long Binomial(long n, double p) {
        return bn.SampleLong(n, p, this);
    }

    /**
     * returns a random number from the binomial distribution (number of heads from n weighted coin flips with probability p of heads)
     */
    public int Binomial(int n, double p) {
        return bn.SampleIntFast(n, p, this);
    }

    /**
     * returns a random number from the binomial distribution (number of heads from n weighted coin flips with probability p of heads)
     */
    public double Gaussian(double mean, double stdDev) {
        return gn.Sample(mean, stdDev, this);
    }

    public void Multinomial(double[] probabilities, int n, int[] ret) {
        double pSum = 1;
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] != 0) {
                if(probabilities[i]-pSum==0){
                    ret[i]=n;
                    for (; i < probabilities.length; i++) {
                        ret[i]=0;
                    }
                    return;
                }
                int ni = bn.SampleInt(n, probabilities[i] / pSum, this);
                ret[i] = ni;
                n -= ni;
                pSum -= probabilities[i];
            }
            else{
                ret[i]=0;
            }
        }
    }

    public void Multinomial(double[] probabilities, long n, long[] ret) {
        double pSum = 1;
        if (probabilities.length == 1) {
            ret[0] = n;
            return;
        }
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] == 1) {
                ret[i] = n;
                return;
            }
            if (probabilities[i] != 0) {
                long ni = bn.SampleLong(n, probabilities[i] / pSum, this);
                ret[i] = ni;
                n -= ni;
                pSum -= probabilities[i];
            }
        }
    }


    /**
     * gets a random point on the surface of a sphere centered at 0,0,0, with the provided radius. the x,y,z coords are
     * put in the double[] ret
     */
    public void RandomPointOnSphereEdge(double radius, double[] ret) {
        double x = gn.Sample(0, radius, this);
        double y = gn.Sample(0, radius, this);
        double z = gn.Sample(0, radius, this);
        double norm = Norm(x, y, z);
        ret[0] = (x * radius) / norm;
        ret[1] = (y * radius) / norm;
        ret[2] = (z * radius) / norm;
    }

    public void RandomPointInSphere(double radius, double[] ret) {
        do {
            ret[0] = rn.Double();
            ret[1] = rn.Double();
            ret[2] = rn.Double();
        } while (DistSquared(ret[0], ret[1], ret[2], 0.5, 0.5, 0.5) > 0.25);
        double retScale = radius * 2;
        ret[0] = (ret[0] - 0.5) * retScale;
        ret[1] = (ret[1] - 0.5) * retScale;
        ret[2] = (ret[2] - 0.5) * retScale;
    }

    /**
     * gets a random point on the surface of a circle centered at 0,0, with the provided radius. the x,y coords are put
     * in the double[] ret
     */
    public void RandomPointOnCircleEdge(double radius, double[] ret) {
        double x = gn.Sample(0, radius, this);
        double y = gn.Sample(0, radius, this);
        double norm = Norm(x, y);
        ret[0] = (x * radius) / norm;
        ret[1] = (y * radius) / norm;
    }

    public void RandomPointInCircle(double radius, double[] ret) {
        double r = Math.sqrt(rn.Double()) * radius;
        double a = rn.Double() * Math.PI * 2;
        ret[0] = r * Math.cos(a);
        ret[1] = r * Math.sin(a);
    }


    //OTHER COORDINATE FUNCTIONS


    //MATH FUNCTIONS

    /**
     * Samples a discrete random variable from the probabilities provided
     *
     * @param probs an array of probabilities. should sum to 1
     * @return the index of the probability bin that was sampled
     */
    public int RandomVariable(double[] probs) {
        double rand = rn.Double();
        for (int i = 0; i < probs.length; i++) {
            rand -= probs[i];
            if (rand <= 0) {
                return i;
            }
        }
        return -1;
    }

    public int RandomVariable(double[] probs, int start, int end) {
        double rand = rn.Double();
        for (int i = start; i < end; i++) {
            rand -= probs[i];
            if (rand <= 0) {
                return i;
            }
        }
        return -1;
    }

    public int RandomVariable(double[] probs, double rand, int start, int end) {
        for (int i = start; i < end; i++) {
            rand -= probs[i];
            if (rand <= 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Fills out with random doubles between min and max inclusive
     *
     * @param out the array the random doubles should be written to. the length of the input array defines the number of
     *            doubles to be generated
     */
    public void RandomDS(double[] out, double min, double max) {
        for (int i = 0; i < out.length; i++) {
            out[i] = rn.Double() * (max - min) + min;
        }
    }

    /**
     * Fills out with random integers between min (inclusive) and max (exclusive)
     *
     * @param out the array the random doubles should be written to. the length of the input array defines the number of
     *            doubles to be generated
     */
    public void RandomIS(int[] out, int min, int max) {
        for (int i = 0; i < out.length; i++) {
            out[i] = rn.Int(max - min) + min;
        }
    }


    /**
     * Samples and exponential distribution with the argument rate parameter
     * equivalent to the timing of the next poisson event with the same rate parameter
     */
    public double ExponentialDist(double rate){
        return -Math.log(Double())/rate;
    }


    /**
     * Shuffles an array of integers
     *
     * @param arr              array to be shuffled
     * @param sampleSize       number of elements from array that shuffling can deltas
     * @param numberOfShuffles number of elements that will be shuffled, should not exceed lenToShuffle
     */
    public void Shuffle(int[] arr, int sampleSize, int numberOfShuffles) {
        for (int i = 0; i < numberOfShuffles; i++) {
            int iSwap = rn.Int(sampleSize - i) + i;
            int swap = arr[iSwap];
            arr[iSwap] = arr[i];
            arr[i] = swap;
        }
    }

    public void Shuffle(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int iSwap = rn.Int(arr.length - i) + i;
            int swap = arr[iSwap];
            arr[iSwap] = arr[i];
            arr[i] = swap;
        }
    }
    public void Shuffle(int[] arr,int numberOfShuffles) {
        Shuffle(arr,arr.length,numberOfShuffles);
    }

    /**
     * Shuffles an array of doubles
     *
     * @param arr              array to be shuffled
     * @param sampleSize       number of elements from array that shuffling can deltas
     * @param numberOfShuffles number of elements that will be shuffled, should not exceed lenToShuffle
     */
    public void Shuffle(double[] arr, int sampleSize, int numberOfShuffles) {
        for (int i = 0; i < numberOfShuffles; i++) {
            int iSwap = rn.Int(sampleSize - i) + i;
            double swap = arr[iSwap];
            arr[iSwap] = arr[i];
            arr[i] = swap;
        }
    }
    public void Shuffle(double[] arr,int numberOfShuffles) {
        Shuffle(arr,arr.length,numberOfShuffles);
    }

    public void Shuffle(double[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int iSwap = rn.Int(arr.length - i) + i;
            double swap = arr[iSwap];
            arr[iSwap] = arr[i];
            arr[i] = swap;
        }
    }

    /**
     * Shuffles an array of objects
     *
     * @param arr              array to be shuffled
     * @param sampleSize       number of elements from array that shuffling can deltas
     * @param numberOfShuffles number of elements that will be shuffled, should not exceed lenToShuffle
     */
    public void Shuffle(Object[] arr, int sampleSize, int numberOfShuffles) {
        for (int i = 0; i < numberOfShuffles; i++) {
            int iSwap = rn.Int(sampleSize - i) + i;
            Object swap = arr[iSwap];
            arr[iSwap] = arr[i];
            arr[i] = swap;
        }
    }

    public void Shuffle(Object[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int iSwap = rn.Int(arr.length - i) + i;
            Object swap = arr[iSwap];
            arr[iSwap] = arr[i];
            arr[i] = swap;
        }
    }
}
