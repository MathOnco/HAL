package Testing.OldTests;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2D;
import HAL.Gui.GridWindow;
import HAL.Interfaces.SerializableModel;
import HAL.Util;

class Cell extends AgentSQ2D<SerializableModelTest> {
}

public class SerializableModelTest extends AgentGrid2D<Cell> implements SerializableModel{
    public void Draw(GridWindow win){
        for (int i = 0; i < length; i++) {
            Cell c=GetAgent(i);
            if(c!=null){
                win.SetPix(i, Util.RGB256(256-c.Age()/10,0,c.Age()/10));
            }
            else{
                win.SetPix(i,Util.BLACK);
            }
        }
    }
    public SerializableModelTest(int x, int y) {
        super(x, y, Cell.class);
    }

    public static void main(String[] args) {
        GridWindow win=new GridWindow(100,100,5);
        SerializableModelTest smt=new SerializableModelTest(100,100);
        byte[] out=null;
        for (int i = 0; i < 10000; i++) {
            if(i==1000){out=Util.SaveState(smt);}
            if(i==9000){smt=Util.LoadState(out);}
            smt.NewAgentSQ(smt.GetTick());
            win.TickPause(1);
            smt.Draw(win);
            smt.IncTick();
        }
    }

    @Override
    public void SetupConstructors() {
        this._PassAgentConstructor(Cell.class);
    }
}
