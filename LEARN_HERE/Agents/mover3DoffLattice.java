package LEARN_HERE.Agents;

import Framework.GridsAndAgents.AgentGrid3D;
import Framework.GridsAndAgents.AgentPT3D;
import Framework.Gui.GridWindow;
import Framework.Gui.OpenGL3DWindow;
import Framework.Rand;
import Framework.Util;

import static Framework.Util.RGB;

/**
 * Created by Rafael on 10/29/2017.
 */
class MoveAgentoffLattice extends AgentPT3D<mover3DoffLattice> {

}

public class mover3DoffLattice extends AgentGrid3D<MoveAgentoffLattice> {
    public mover3DoffLattice(int x, int y, int z) {
        super(x, y,z, MoveAgentoffLattice.class,false,false,false);//set to true for wraparound
    }

    public static void main(String[] args) {
        int BLUE=RGB(0,0,1);
        int RED=RGB(1,0,0);

        mover3DoffLattice test=new mover3DoffLattice(10,10,10);
        OpenGL3DWindow win3D=new OpenGL3DWindow("3D",500,500,test.xDim,test.yDim,test.zDim);
        GridWindow win2D=new GridWindow("2D",test.xDim,test.yDim,20);
        MoveAgentoffLattice ourHero=test.NewAgentSQ(5,5,5);
        Rand rn=new Rand();
        double[]moveCoordScratch=new double[3];

        for (int i = 0; i < 10000; i++) {
            win2D.TickPause(10);
            rn.RandomPointInSphere(0.2,moveCoordScratch);
            ourHero.MoveSafePT(ourHero.Xpt()+moveCoordScratch[0],ourHero.Ypt()+moveCoordScratch[1],ourHero.Zpt()+moveCoordScratch[2]);//random movement

            win2D.Clear(BLUE);
            win2D.SetPix(ourHero.Xsq(),ourHero.Ysq(), Util.HeatMapRGB(ourHero.Zsq(),0,test.zDim));//draw 2d

            win3D.Clear(BLUE);
            win3D.Circle(ourHero.Xpt(),ourHero.Ypt(),ourHero.Zpt(),0.5,RED);//draw 3d
            win3D.Update();
            if(win3D.IsClosed()){//quit if close button is clicked
                break;
            }
        }
        win3D.Close();//destroy guis
        win2D.Close();
    }
}
