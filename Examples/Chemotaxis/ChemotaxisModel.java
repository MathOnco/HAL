package Examples.Chemotaxis;

import HAL.GridsAndAgents.*;
import HAL.Gui.OpenGL2DWindow;
import HAL.Rand;
import HAL.Util;


class SphericalCow extends SphericalAgent2D<SphericalCow, ChemotaxisModel>{
    public void Step(){
        if(G.grass.Get(Isq())>0){
            G.grass.Add(Isq(),-G.EAT_RATE);
        }
        //chemotaxis
        double gradX=G.grass.GradientX(Xsq(),Ysq());
        double gradY=G.grass.GradientY(Xsq(),Ysq());
        double norm= Util.Norm(gradX,gradY);
        if(gradX!=0) {
            xVel += gradX / norm * G.CHEMOTAX_RATE;
        }
        if(gradY!=0) {
            yVel += gradY / norm * G.CHEMOTAX_RATE;
        }
        //random movement and forces
        G.rng.RandomPointInCircle(G.RANDOM_MOVE_RATE,G.randomMoveScratch);
        xVel+=G.randomMoveScratch[0];
        yVel+=G.randomMoveScratch[1];
        SumForces(radius,(overlap,other)->Math.pow(G.FORCE_SCALER*overlap, G.FORCE_EXPONENT));
        ForceMove();
        ApplyFriction(G.FRICTION);
    }
}

public class ChemotaxisModel extends AgentGrid2D<SphericalCow> {
    PDEGrid2D grass;
    double FORCE_EXPONENT=2;//these constants have been found to be rather stable, but tweak them and see what happens!
    double FORCE_SCALER=0.7;
    double FRICTION=0.5;
    double GROW_RATE=0.01;
    double EAT_RATE=1;
    double CHEMOTAX_RATE=0.1;
    double RANDOM_MOVE_RATE=0.1;
    double[]randomMoveScratch=new double[2];
    double COW_RAD=0.5;
    Rand rng=new Rand();
    OpenGL2DWindow win;

    public ChemotaxisModel(int x, int y) {
        super(x,y,SphericalCow.class);
        grass=new PDEGrid2D(x,y);
        grass.SetAll(1);
        grass.Update();
        win=new OpenGL2DWindow(500,500,x,y);
    }
    public void GrowGrass(){
        for (int i = 0; i < grass.length; i++) {
            grass.Add(i,GROW_RATE);
        }
        grass.Update();
        for (int i = 0; i < grass.length; i++) {
            if(grass.Get(i)>1){
                grass.Set(i,1);
            }
        }
        grass.Update();
    }
    public void Draw(){
        for (int i = 0; i < grass.length; i++) {
            win.SetPix(i,Util.RGB(0,grass.Get(i)/2,0));
        }
        for (SphericalCow cow : this) {
            win.Circle(cow.Xpt(),cow.Ypt(),cow.radius,Util.WHITE);
        }
        win.Update();
    }

    public static void main(String[] args) {
        OpenGL2DWindow.MakeMacCompatible(args);
        ChemotaxisModel m=new ChemotaxisModel(200,200);
        for (int i = 0; i < 500; i++) {
            m.NewAgentSQ(m.rng.Int(m.length)).radius=m.COW_RAD;
        }
        while(!m.win.IsClosed()){
            for (SphericalCow cow : m) {
                cow.Step();
            }
            m.GrowGrass();
            m.Draw();
            m.win.TickPause(0);
        }
        m.win.Close();
    }
}
