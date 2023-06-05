package Examples.MultiwellOutbreak;
import HAL.Tools.MultiWellExperiment.MultiWellExperiment;

import static HAL.Util.*;

public class MultiOutbreak {

    // used for "StepFn" argument of MultiWellExperiment
    //  - arguments: model, well index
    //  - update the model argument for one timestep.
    public static void StepModel(OutbreakWorld model,int iWell){
        model.StepCells();
    }

    // used for "ColorFn" argument of MultiWellExperiment
    //  - arguments: model, x, and y
    //  - used to set one pixel of the visualization.
    public static int DrawModel(OutbreakWorld model,int x,int y){
        Person c = model.GetAgent(x,y);
        return OutbreakWorld.ReturnColor(c.type);
    }
    public static void main(String[] args){


        // how many model simulations would you like to run?
        int x_dimension = 7;
        int y_dimension = 3;

        OutbreakWorld[] models=new OutbreakWorld[x_dimension*y_dimension];

        // set up all models
        for (int row = 0; row < x_dimension; row++) {
            for (int col = 0; col < y_dimension; col++) {

                OutbreakWorld model = new OutbreakWorld();

                model.QUARANTINE_RATE_SYMPTOMATIC = ((double) row / 20.0);
                model.QUARANTINE_RATE_ASYMPTOMATIC = ((double) row / 20.0);

                models[row*y_dimension + col] = model;
            }
        }

        MultiWellExperiment<OutbreakWorld> expt=new MultiWellExperiment<>(x_dimension,y_dimension,models,models[0].DIMENSION,models[0].DIMENSION, 1, WHITE, MultiOutbreak::StepModel, MultiOutbreak::DrawModel);
        expt.RunGIF(80, "multi_world_outbreak.gif",4,false);
    }
}




