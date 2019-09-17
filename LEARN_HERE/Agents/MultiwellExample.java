package LEARN_HERE.Agents;


import HAL.Tools.MultiWellExperiment.MultiWellExperiment;

import static HAL.Util.*;

public class MultiwellExample{
    public static void StepModel(BirthDeath model,int iWell){
        model.Step();
    }
    public static int DrawModel(BirthDeath model,int x,int y){
        Cell c = model.GetAgent(x,y);
        return c == null ? BLACK : c.color;
    }
    public static void main(String[] args){
        int x=100,y=100;
        BirthDeath[] models=new BirthDeath[]{new BirthDeath(x,y,RED),new BirthDeath(x,y,GREEN),new BirthDeath(x,y,BLUE),
        new BirthDeath(x,y,YELLOW),new BirthDeath(x,y,CYAN),new BirthDeath(x,y,MAGENTA)};
        for (BirthDeath model : models) {
            model.Setup(2);
        }
        MultiWellExperiment<BirthDeath> expt=new MultiWellExperiment<BirthDeath>(3,2,models,x,y, 5, WHITE, MultiwellExample::StepModel,MultiwellExample::DrawModel);
        expt.Run(200,false,100);
    }
}
