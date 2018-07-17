package LEARN_HERE.Agents;

import Framework.Gui.GridWindow;
import Framework.Tools.MultiWellExperiment;

import static Framework.Util.*;

public class MultiwellExample{
    public static void StepModel(BirthDeath model,int iWell,int iTick){
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
        MultiWellExperiment<BirthDeath> expt=new MultiWellExperiment<BirthDeath>(3,2,models,x,y,WHITE,5,MultiwellExample::StepModel,MultiwellExample::DrawModel);
        //USE THE S KEY TO SAVE THE STATE, AND THE L KEY TO LOAD THE STATE
        expt.Run(200,false,100);
    }
}
