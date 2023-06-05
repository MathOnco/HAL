package Examples.GasPopulationGrid;


import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentPT2D;
import HAL.Gui.GridWindow;
import HAL.Rand;

import static HAL.Util.HeatMapRGB;

class Particle extends AgentPT2D<Gas> {
    public void BrownianMotion(){
        //loads moveCoords with a random x,y pair
        G.rng.RandomPointInCircle(0.5, G.moveCoords);
        //displaces the particle, particle will not move if movement would cause it to move out of the grid
        MoveSafePT(Xpt()+ G.moveCoords[0],Ypt()+ G.moveCoords[1]);
    }
}

public class Gas extends AgentGrid2D<Particle> {
    Rand rng=new Rand();
    double moveRad=50;
    double[]moveCoords=new double[2];
    double displayColorMax=1;
    public GridWindow display;
    public Gas(int x, int y) {
        super(x, y, Particle.class);
        display=new GridWindow("density",xDim,yDim,5);
    }
    public void Draw(){
        for (int i = 0; i < length; i++) {
            //sets each pixel of the display based on the density at the same model grid index
            //0 cells will be colored black, 4 or more cells will be colored white
            display.SetPix(i,HeatMapRGB(PopAt(i),0,4));
        }
    }
    public static void main(String[] args) {
        Gas gas=new Gas(100,100);
        for (int i = 0; i < 10000; i++) {
            gas.NewAgentPT(gas.xDim/2,gas.yDim/2);
        }
        for (int i = 0; i < 100000; i++) {
            gas.display.TickPause(0);
            for (Particle particle : gas) {
                particle.BrownianMotion();
            }
            gas.Draw();
            if(i%10000==0){
                gas.display.ToPNG("gas"+i+".png");
            }
        }
        gas.display.Close();
    }
}
