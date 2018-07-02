package LEARN_HERE.Agents;

import Framework.Gui.GridWindow;
import Framework.Util;

public class BirthDeathJumper {
    public static void main(String[] args) {
        BirthDeath bd1=new BirthDeath(100,100, Util.RED);
        BirthDeath bd2=new BirthDeath(100,100,Util.YELLOW);
        bd1.Setup(10);
        bd2.Setup(10);
        GridWindow vis1=new GridWindow(100,100);
        GridWindow vis2=new GridWindow(100,100);
        for (int i = 0; i < 10000; i++) {
            vis1.TickPause(0);
            bd1.Step(vis1);
            bd2.Step(vis2);
            int j=bd1.rn.Int(bd1.length);
            if(bd1.GetAgent(j)!=null) {
                System.out.println(bd1.GetAgent(j).G.color);
                bd1.GetAgent(j).Dispose();
            }
//            if(bd2.Pop()>0) {
//            }
        }
    }
}
