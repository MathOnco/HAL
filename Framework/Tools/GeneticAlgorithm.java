package Framework.Tools;

import Framework.Interfaces.GAMutantToString;
import Framework.Interfaces.GAMutationFunction;
import Framework.Interfaces.GAScoreFunction;
import Framework.Interfaces.Sortable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static Framework.Util.*;

/**
 * Created by rafael on 4/8/17.
 */
public class GeneticAlgorithm <T> implements Sortable {
    GAMutationFunction<T> Mutator;
    GAScoreFunction<T> Scorer;
    T[] generation;
    double[] scores;

    /**
     * Genetic algorithm class runs a GA by alternating between the Scorer function and Mutator functoin
     * @param Mutator function that handles mutation
     * @param Scorer function that handles scoring
     */
    public GeneticAlgorithm(GAMutationFunction<T> Mutator,GAScoreFunction<T> Scorer){
        this.Mutator=Mutator;
        this.Scorer=Scorer;
    }

    void ScoreGAGeneration(T[] prevGen,int nThreads) {
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        scores = new double[prevGen.length];
        generation= Arrays.copyOf(prevGen,prevGen.length);
        MutantRunner[] runners = new MutantRunner[generation.length];
        Thread[] threads = new Thread[generation.length];
        for (int i = 0; i < generation.length; i++) {
            runners[i] = new MutantRunner<>(Scorer, prevGen[i]);
            threads[i] = new Thread(runners[i]);
        }
        for (Thread t : threads) {
            executor.execute(t);
        }
        executor.shutdown();
        while (!executor.isTerminated()) ;
        for (int i = 0; i < generation.length; i++) {
            scores[i] = runners[i].score;
            generation[i]=prevGen[i];
        }
        QuickSort(this, true);
    }

    /**
     * runs the genetic algorithm, starting with the first generation and creating nGensToScore-1 additional ones
     * puts the results in generationOut and scoresOut
     * @param firstGen the first generation, will be scored, and if nGensToScore > 1, mutated to create the next generation
     * @param nGensToScore how many loops of scoring to run. the last generation to be created will be scored but not mutated
     * @param nThreads number of threads to run
     * @param generationOut empty arraylist that will be used to store each generation
     * @param scoresOut empty arraylist that willl be used to store the scores for each generation
     */
    public void RunGA(T[] firstGen,int nGensToScore,int nThreads,ArrayList<T[]> generationOut, ArrayList<double[]> scoresOut){
        generationOut.clear();
        scoresOut.clear();
        T[] currGen=firstGen;
        for (int i = 0; i < nGensToScore; i++) {
            ScoreGAGeneration(currGen,nThreads);
            generationOut.add(generation);
            scoresOut.add(scores);
            if(i!=nGensToScore-1){
                currGen=Mutator.CreateNextGen((T[])generation,scores,i);
            }
        }
    }

    /**
     * runs the genetic algorithm, starting with the first generation and creating nGensToScore-1 additional ones
     * puts the results in generationOut and scoresOut
     * @param firstGen the first generation, will be scored, and if nGensToScore > 1, mutated to create the next generation
     * @param nGensToScore how many loops of scoring to run. the last generation to be created will be scored but not mutated
     * @param nThreads number of threads to run
     * @param generationStringsOut empty arraylist that will be used to store the string result of each generation
     * @param scoresOut empty arraylist that willl be used to store the scores for each generation
     * @param ToString function that takes in a mutant and returns a string with info to be stored
     */
    public void RunGAOutputStrings(T[] firstGen, int nGensToScore, int nThreads, ArrayList<String[]> generationStringsOut, ArrayList<double[]> scoresOut, GAMutantToString<T> ToString){
        generationStringsOut.clear();
        scoresOut.clear();
        T[] currGen=firstGen;
        for (int i = 0; i < nGensToScore; i++) {
            ScoreGAGeneration(currGen,nThreads);
            String[] outStrings=new String[generation.length];
            for (int j = 0; j < generation.length; j++) {
                outStrings[j]=ToString.MutantToString(generation[j]);
            }
            generationStringsOut.add(outStrings);
            scoresOut.add(scores);
            if(i!=nGensToScore-1){
                currGen=Mutator.CreateNextGen((T[])generation,scores,i);
            }
        }
    }

    /**
     * ignore
     */
    @Override
    public double Compare(int iFirst, int iSecond) {
        return scores[iFirst] - scores[iSecond];
    }

    /**
     * ignore
     */
    @Override
    public void Swap(int iFirst, int iSecond) {
        T temp= generation[iFirst];
        generation[iFirst] = generation[iSecond];
        generation[iSecond] = temp;
        double tempScore= scores[iFirst];
        scores[iFirst] = scores[iSecond];
        scores[iSecond] = tempScore;
    }

    /**
     * ignore
     */
    @Override
    public int Length() {
        return generation.length;
    }
}

/**
 * ignore
 */
class MutantRunner<T> implements Runnable{
    T mutant;
    double score;
    GAScoreFunction<T> Scorer;
    public MutantRunner(GAScoreFunction<T> Scorer,T myMut){
        mutant=myMut;
        this.Scorer=Scorer;
    }

    @Override
    public void run(){
        score=Scorer.ScoreMutant(mutant);
    }
}
