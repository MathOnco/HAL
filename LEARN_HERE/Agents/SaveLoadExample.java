package LEARN_HERE.Agents;

import Framework.Gui.GridWindow;
import Framework.Tools.SerializableModel;

class SaveLoad extends BirthDeath implements SerializableModel{

    public SaveLoad(int x, int y) {
        super(x, y);
    }

    @Override
    public void SetupConstructors() {
        this._SetupAgentListConstructor(Cell.class);
    }
}

public class SaveLoadExample {
    static byte[]state=new byte[100000000];
    public static void main(String[] args) {
        GridWindow win=new GridWindow(100,100,10);
        win.AddKeyResponses((c,i)->{
            System.out.println(c+","+i+ "UP");
        },(c,i)->{
            System.out.println(c+","+i+" DOWN");
        });
        SaveLoad t=new SaveLoad(100,100);
        t.Setup(10);
        for (int i = 0; i < 100000; i++) {
            win.TickPause(10);
            t.Step(win);
            if(win.IsKeyDown('s')){
                System.out.println("s key is down");
                t.SaveState(state);
            }
            if(win.IsKeyDown('l')){
                System.out.println("l key is down");
                t.LoadState(state);
            }
        }
    }
}
