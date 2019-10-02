package LEARN_HERE.Agents;

import HAL.Gui.GridWindow;
import HAL.Interfaces.SerializableModel;
import static HAL.Util.*;

public class SaveLoadModel extends BirthDeath implements SerializableModel{
    //you can just add implements SerializableModel to a model
    static byte[]state;
    public SaveLoadModel(int x, int y) {
        super(x, y,RED);
    }

    @Override
    public void SetupConstructors() {
        _PassAgentConstructor(Cell.class);
    }
    public static void main(String[] args){
        //USE THE S KEY TO SAVE THE STATE, AND THE L KEY TO LOAD THE STATE
        SaveLoadModel model=new SaveLoadModel(100,100);
        GridWindow win=new GridWindow(100,100,10);
        win.AddKeyResponses(null,null);
        model.Setup(10);
        for (int i = 0; i < 100000; i++) {
            if(win.IsKeyDown('s')){
                state= SaveState(model);
            }
            if(win.IsKeyDown('l')){
                if(state!=null) {
                    model = LoadState(state);
                }
            }
            win.TickPause(10);
            model.Step();
            model.Draw(win);
        }
    }
}
