package Examples.TemplateModel;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.GridWindow;

import static HAL.Util.*;

class GenericCell extends AgentSQ2Dunstackable<AgentGrid2D<GenericCell>>{

}

public class GenericModel {
    //this example uses the standard AgentGrid2D, rather than an extension
    AgentGrid2D<GenericCell> genericGrid;
    GridWindow win;
    public GenericModel(int x,int y){
        genericGrid =new AgentGrid2D<>(x,y,GenericCell.class);
        win=new GridWindow(x,y,10);

    }
    public static void main(String[] args) {
        GenericModel model=new GenericModel(20,20);
        model.genericGrid.NewAgentSQ(10,10);
        model.Draw();
    }

    public void Draw(){
        for (int i = 0; i < genericGrid.length; i++) {
            if(genericGrid.GetAgent(i)==null){
                win.SetPix(i,BLACK);
            }
            else{
                win.SetPix(i,WHITE);
            }
        }

    }
}
