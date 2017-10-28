package Framework.Extensions;

import Framework.GridsAndAgents.GridBase2D;

/**
 * Created by rafael on 8/25/17.
 */

public abstract class SpatialGame extends GridBase2D {
    public final double[] fitnesses;
    public SpatialGame(int x, int y, boolean wrapX, boolean wrapY){
        super(x,y,wrapX,wrapY);
        this.fitnesses=new double[length];
    }
    abstract public double CalcFitness(int i);
    abstract public double SetState(int i);
    public void SetFitnesses() {
        for (int i = 0; i < fitnesses.length; i++) {
            fitnesses[i] = CalcFitness(i);
        }
    }
    public void SetStates(){
        for (int i = 0; i < fitnesses.length; i++) {
            SetState(i);
        }
    }
}
