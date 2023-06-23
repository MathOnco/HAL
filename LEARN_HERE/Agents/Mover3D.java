package LEARN_HERE.Agents;

import HAL.GridsAndAgents.AgentGrid3D;
import HAL.GridsAndAgents.AgentSQ3Dunstackable;
import HAL.Gui.GridWindow;
import HAL.Gui.OpenGL3DWindow;
import HAL.Util;

import java.util.Random;

/**
 * Created by Rafael on 10/29/2017.
 */
class MoveAgent extends AgentSQ3Dunstackable<Mover3D> {

}

public class Mover3D extends AgentGrid3D<MoveAgent> {
    public Mover3D(int x, int y, int z) {
        super(x, y,z, MoveAgent.class,false,false,false);//set to true for wraparound
    }

    public static void main(String[] args) {
        OpenGL3DWindow.MakeMacCompatible(args);

        Mover3D test=new Mover3D(10,10,10);
        OpenGL3DWindow win3D=new OpenGL3DWindow("3D",500,500,test.xDim,test.yDim,test.zDim);
        GridWindow win2D=new GridWindow("2D",test.xDim,test.yDim,20);
        MoveAgent ourHero=test.NewAgentSQ(5,5,5);
        Random rn=new Random();

        for (int i = 0; i < 10000; i++) {
            win2D.TickPause(10);
            ourHero.MoveSafeSQ(ourHero.Xsq()+(rn.nextInt(3)-1),ourHero.Ysq()+(rn.nextInt(3)-1),ourHero.Zsq()+(rn.nextInt(3)-1));//random movement

            win2D.Clear(Util.BLACK);
            win2D.SetPix(ourHero.Xsq(),ourHero.Ysq(), Util.HeatMapRGB(ourHero.Zsq(),0,test.zDim));//draw 2d

            win3D.ClearBox(Util.MAGENTA,Util.BLACK);
            win3D.Circle(ourHero.Xsq(),ourHero.Ysq(),ourHero.Zsq(),0.5,Util.GREEN);//draw 3d
            win3D.Update();
            if(win3D.IsClosed()){//quit if close button is clicked
                break;
            }
        }
        win3D.Close();//destroy guis
        win2D.Close();
    }
}
