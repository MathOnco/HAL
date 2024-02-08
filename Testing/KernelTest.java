package Testing;

import HAL.GridsAndAgents.Grid2Ddouble;
import HAL.Gui.GridWindow;
import HAL.Gui.UIGrid;
import HAL.Util;
import HAL.GridsAndAgents.KernelGrid;

import static HAL.Util.GaussianPDF;

public class KernelTest {

    static public void PullKernel(KernelGrid g, UIGrid win){
        for (int x = 0; x < win.xDim; x++) {
            for (int y = 0; y < win.yDim; y++) {
                win.SetPix((x+win.xDim/2)%win.xDim,(y+win.yDim/2)%win.yDim, Util.GreyScale(g.GetKernel(x,y)));
            }
        }
    }

    public static Grid2Ddouble SquareDomain(){
        Grid2Ddouble domain=new Grid2Ddouble(16,16);
        for (int x = 4; x < 13; x++) {
            for (int y = 4; y < 13; y++) {
                domain.Set(x,y,1.0);
            }
        }
        return domain;
    }

    public static Grid2Ddouble LineDomain(){
        Grid2Ddouble domain=new Grid2Ddouble(16,16);
        for (int x = 8; x < 9; x++) {
            for (int y = 4; y < 13; y++) {
                domain.Set(x,y,1.0);
            }
        }
        return domain;
    }

    static public void PullOut(Grid2Ddouble grid,UIGrid win){
        for (int i = 0; i < win.length; i++) {
            win.SetPix(i,Util.GreyScale(grid.Get(i)));
        }
    }

    public static void main(String[] args) {
        KernelGrid g= new KernelGrid((r)->r<5?GaussianPDF(0,2,r):0,16,true);
        GridWindow win =new GridWindow(g.xDim,g.yDim,10);
        Grid2Ddouble domain=LineDomain();
        PullKernel(g,win);
        g.Convolve(domain);
        win.ToPNG("KernelGridExampleKernel.png");
        PullOut(domain,win);
        win.ToPNG("KernelGridExampleStart.png");
        PullOut(g.out,win);
        win.ToPNG("KernelGridExampleEnd.png");
        win.Close();
    }
}
