package Testing;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.GridVisWindow;
import Framework.Extensions.SphericalAgent2D;
import Framework.Gui.GuiGridVis;
import Framework.Utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Rafael on 9/29/2017.
 */

class Particle extends SphericalAgent2D<Particle,ParticleExample>{
    void Init(){
        radius=0.3;
    }
    void Step1(){
        G().neighbors.clear();
       SumForces(radius*2,G().neighbors,(double overlap)->{
           return overlap;
       });
    }
    void Step2(){
        ForceMove();
    }
}

public class ParticleExample extends AgentGrid2D<Particle> {
    ArrayList<Particle> neighbors=new ArrayList<>();
    Random rn=new Random();

    public ParticleExample(int x, int y){
        super(x, y, Particle.class);
        for (int i = 0; i < 100; i++) {
            Particle p=NewAgentPT(rn.nextDouble()*xDim,rn.nextDouble()*yDim);
            p.Init();
        }
    }
    public void Draw(GuiGridVis vis){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Particle p=GetAgent(x/5,y/5);
                if(p!=null){
                    vis.SetPix(x, y,Utils.RGB((double) 1, (double) 1, (double) 1));
                }
                else{
                    vis.SetPix(x, y, Utils.RGB((double) 0, (double) 0, (double) 0));
                }
            }
        }
    }
    public void Step(){
        for (Particle p: this) {
            p.Step1();
        }
        for (Particle p: this) {
            p.Step2();
        }
        IncTick();
    }

    public static void main(String[] args) {
        GridVisWindow win = new GridVisWindow("particles",50,50,1);
        ParticleExample ex=new ParticleExample(10,10);
        for (int i = 0; i < 1000; i++) {
            win.TickPause(100);
            ex.Step();
            ex.Draw(win);
        }
    }
}
