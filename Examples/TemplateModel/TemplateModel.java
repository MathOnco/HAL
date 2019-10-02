package Examples.TemplateModel;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.GridWindow;

import static HAL.Util.BLACK;
import static HAL.Util.WHITE;

class TemplateCell extends AgentSQ2Dunstackable<TemplateModel>{

}

public class TemplateModel extends AgentGrid2D<TemplateCell> {
    GridWindow win;
    public TemplateModel(int x, int y) {
        super(x, y, TemplateCell.class);
        win=new GridWindow(x,y,10);
    }
    public void Draw(){
        for (int i = 0; i < length; i++) {
            if(GetAgent(i)==null){
                win.SetPix(i,BLACK);
            }
            else{
                win.SetPix(i,WHITE);
            }
        }
    }

    public static void main(String[]args){
        TemplateModel model=new TemplateModel(20,20);
        model.NewAgentSQ(10,10);
        model.Draw();
    }
}

