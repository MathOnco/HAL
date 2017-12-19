package Framework.Gui;

import Framework.GridsAndAgents.*;
import Framework.Interfaces.*;
import Framework.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class PaintPanel extends JPanel {

    final static int BLACK= Util.RGB(0,0,0);
    final BufferedImage buff;
    final int xDim;
    final int yDim;
    public final int scaleX;
    public final int scaleY;
    Graphics2D g;
    final ArrayList<GuiGrid> alphaGrids;
    PaintPanel(BufferedImage buff,int xDim,int yDim,int scaleX,int scaleY){
        this.buff = buff;
        this.xDim = xDim;
        this.yDim = yDim;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.alphaGrids=new ArrayList<>();
        this.setVisible(true);
        this.setPreferredSize(new Dimension((int)Math.ceil(xDim*scaleX),(int)Math.ceil(yDim*scaleY)));
        this.setMaximumSize(new Dimension((int)Math.ceil(xDim*scaleX),(int)Math.ceil(yDim*scaleY)));
        this.setMinimumSize(new Dimension((int)Math.ceil(xDim*scaleX),(int)Math.ceil(yDim*scaleY)));
    }
    @Override
    public void paintComponent(Graphics g){
        ((Graphics2D)g).drawImage(buff.getScaledInstance(scaleX *xDim,-scaleY *yDim,Image.SCALE_FAST),null,null);
        for (int i = 0; i < alphaGrids.size(); i++) {
            GuiGrid alphaGrid=alphaGrids.get(i);
         ((Graphics2D)g).drawImage(alphaGrid.buff.getScaledInstance(alphaGrid.scale *alphaGrid.xDim,-alphaGrid.scale *alphaGrid.yDim,Image.SCALE_FAST),null,null);
        }
        repaint();
    }

}

/**
 * a gui item that is used to efficiently visualize in 2 dimensions
 * uses an array of pixels whose color values are individually set
 */
public class GuiGrid implements GuiComp{
    boolean active;
    final PaintPanel panel;
    final public int xDim;
    final public int yDim;
    final public int length;
    public final int scale;
    public final int compX;
    public final int compY;
    final BufferedImage buff;
    protected BufferedImage scaledBuff;
    protected Graphics2D scaledG;
    final int[] data;
    TickTimer tickTimer=new TickTimer();

    public void TickPause(int millis){
        if(active){
            tickTimer.TickPause(millis);
        }
    }

    /**
     * @param gridW width of the GuiGrid in pixels
     * @param gridH height of the GuiGrid in pixels
     * @param scaleFactor the width and height in screen pixels of each GuiGrid pixel
     * @param compX width on the gui GridBagLayout
     * @param compY height on the gui GridBagLayout
     * @param active
     */
    public GuiGrid(int gridW, int gridH, int scaleFactor, int compX, int compY, boolean active){
        this.active= active;
        xDim=gridW;
        yDim=gridH;
        length=gridH*gridW;
        this.compX=compX;
        this.compY=compY;
        scale =scaleFactor;
        if(this.active) {
            buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_ARGB);
            panel=new PaintPanel(buff,xDim,yDim, scaleFactor,scaleFactor);
            data=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
            for (int i = 0; i < length; i++) {
                SetPix(i, Util.RGB((double) 0, (double) 0, (double) 0));
            }
        }
        else{
           panel=null;
           buff=null;
           data=null;
        }
    }
    public GuiGrid(int gridW, int gridH, int scaleFactor, int compX, int compY){
        this.active= true;
        xDim=gridW;
        yDim=gridH;
        length=gridH*gridW;
        this.compX=compX;
        this.compY=compY;
        scale =scaleFactor;
        if(this.active) {
            buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_ARGB);
            panel=new PaintPanel(buff,xDim,yDim, scale, scale);
            data=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
            for (int i = 0; i < length; i++) {
                SetPix(i, Util.RGB((double) 0, (double) 0, (double) 0));
            }
        }
        else{
            panel=null;
            buff=null;
            data=null;
        }
    }

    /**
     * @param gridW width of the GuiGrid in pixels
     * @param gridH height of the GuiGrid in pixels
     * @param scaleFactor the width and height in screen pixels of each GuiGrid pixel
     */
    public GuiGrid(int gridW, int gridH, int scaleFactor, boolean active){
        this.active=active;
        xDim=gridW;
        yDim=gridH;
        length=gridH*gridW;
        this.compX=1;
        this.compY=1;
        scale =scaleFactor;
        if(active) {
            buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_ARGB);
            panel=new PaintPanel(buff,xDim,yDim, scale, scale);
            data=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
            for (int i = 0; i < length; i++) {
                SetPix(i, Util.RGB((double) 0, (double) 0, (double) 0));
            }
        }
        else{
            panel=null;
            buff=null;
            data=null;
        }
    }
    public GuiGrid(int gridW, int gridH, int scaleFactor){
        this.active=true;
        xDim=gridW;
        yDim=gridH;
        length=gridH*gridW;
        this.compX=1;
        this.compY=1;
        scale =scaleFactor;
        if(active) {
            buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_ARGB);
            panel=new PaintPanel(buff,xDim,yDim, scale, scale);
            data=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
            for (int i = 0; i < length; i++) {
                SetPix(i, Util.RGB((double) 0, (double) 0, (double) 0));
            }
        }
        else{
            panel=null;
            buff=null;
            data=null;
        }
    }
//    /**
//     * @param gridW width of the GuiGrid in pixels
//     * @param gridH height of the GuiGrid in pixels
//     * @param compX width on the gui GridBagLayout
//     * @param compY height on the gui GridBagLayout
//     * @param active
//     */
//    public GuiGrid(int gridW, int gridH, int scaleX,int scaleY, int compX, int compY, boolean active){
//        this.active= active;
//        xDim=gridW;
//        yDim=gridH;
//        length=gridH*gridW;
//        this.compX=compX;
//        this.compY=compY;
//        this.scaleX =scaleX;
//        this.scaleY=scaleY;
//        if(this.active) {
//            buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_ARGB);
//            panel=new PaintPanel(buff,xDim,yDim, scaleX,scaleY);
//            buf=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
//            for (int i = 0; i < length; i++) {
//                SetPix(i,Util.RGB((double) 0, (double) 0, (double) 0));
//            }
//        }
//        else{
//            panel=null;
//            buff=null;
//            buf=null;
//        }
//    }
//    public GuiGrid(int gridW, int gridH, int scaleX,int scaleY, int compX, int compY){
//        this.active= true;
//        xDim=gridW;
//        yDim=gridH;
//        length=gridH*gridW;
//        this.compX=compX;
//        this.compY=compY;
//        this.scaleX =scaleX;
//        this.scaleY =scaleY;
//        if(this.active) {
//            buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_ARGB);
//            panel=new PaintPanel(buff,xDim,yDim, scaleX,scaleY);
//            buf=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
//            for (int i = 0; i < length; i++) {
//                SetPix(i,Util.RGB((double) 0, (double) 0, (double) 0));
//            }
//        }
//        else{
//            panel=null;
//            buff=null;
//            buf=null;
//        }
//    }
//
//    /**
//     * @param gridW width of the GuiGrid in pixels
//     * @param gridH height of the GuiGrid in pixels
//     */
//    public GuiGrid(int gridW, int gridH, int scaleX,int scaleY,boolean active){
//        this.active=active;
//        xDim=gridW;
//        yDim=gridH;
//        length=gridH*gridW;
//        this.compX=1;
//        this.compY=1;
//        this.scaleX =scaleX;
//        this.scaleY =scaleY;
//        if(active) {
//            buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_ARGB);
//            panel=new PaintPanel(buff,xDim,yDim, scaleX,scaleY);
//            buf=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
//            for (int i = 0; i < length; i++) {
//                SetPix(i,Util.RGB((double) 0, (double) 0, (double) 0));
//            }
//        }
//        else{
//            panel=null;
//            buff=null;
//            buf=null;
//        }
//    }
//    public GuiGrid(int gridW, int gridH, int scaleX,int scaleY){
//        this.active=true;
//        xDim=gridW;
//        yDim=gridH;
//        length=gridH*gridW;
//        this.compX=1;
//        this.compY=1;
//        this.scaleX =scaleX;
//        this.scaleY =scaleY;
//        if(active) {
//            buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_ARGB);
//            panel=new PaintPanel(buff,xDim,yDim, scaleX,scaleY);
//            buf=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
//            for (int i = 0; i < length; i++) {
//                SetPix(i,Util.RGB((double) 0, (double) 0, (double) 0));
//            }
//        }
//        else{
//            panel=null;
//            buff=null;
//            buf=null;
//        }
//    }
    public void SetPix(int x, int y, int color){
        if(active) {
            data[(yDim - y - 1) * xDim + x] = color;
        }
    }
    public void SetPix(int i, int color){
        if(active) {
            SetPix(i / yDim, i % yDim, color);
        }
    }
    //this function allows for inactivating the computational effort of generating the color with the active boolean
    public void SetPix(int x,int y,ColorIntGenerator ColorFunction){
        if(active){
            SetPix(x,y,ColorFunction.GenColorInt());
        }
    }
    //this function allows for inactivating the computational effort of generating the color with the active boolean
    public void SetPix(int i,ColorIntGenerator ColorFunction){
        if(active){
            SetPix(i,ColorFunction.GenColorInt());
        }
    }
    public int GetPix(int x, int y){
         return data[(yDim - y - 1) * xDim + x];

    }
    public int GetPix(int i){
         return GetPix(i / yDim, i % yDim);
    }
    public void Clear(int color){
        if(active){
            Arrays.fill(data,color);
        }
    }
    //    public void DrawStamp(float[] stampColors,double xMid,double yMid,int xSize,int ySize){
//        int xStart=
//    }

    public void AddAlphaGrid(GuiGrid overlay){
        this.panel.alphaGrids.add(overlay);
    }
    public void PlotSegment(double x1, double y1, double x2, double y2, int color){
        Util.AlongLineCoords(x1,y1,x2,y2,(int x, int y)->{this.SetPix(x,y,color); });
    }
    public void PlotSegment(double x1, double y1, double x2, double y2, int color, double scale){
        if(scale<=0){
            throw new IllegalArgumentException("scale must be >0! scale: "+scale);
        }
        Util.AlongLineCoords(x1*scale,y1*scale,x2*scale,y2*scale,(int x, int y)->{this.SetPix(x,y,color); });
    }
    public void PlotSegment(double x1, double y1, double x2, double y2, int color, double scaleX, double scaleY){
        if(scaleX<0||scaleY<0){
            throw new IllegalArgumentException("scaleX and scaleY must be >=0! scaleX: "+scaleX+" scaleY: "+scaleY);
        }
        Util.AlongLineCoords(x1*scaleX,y1*scaleY,x2*scaleX,y2*scaleY,(int x, int y)->{this.SetPix(x,y,color); });
    }
    public void PlotLine(double[]xys,int color){
        PlotLine(xys, color, 0,xys.length);
    }
    public void PlotLine(double[] xys, int color, double scale){
        PlotLine(xys, color, 0,xys.length, scale,scale);
    }
    public void PlotLine(double[] xys, int color, int startPoint, int endPoint, double scale){
        PlotLine(xys, color, startPoint,endPoint, scale,scale);
    }
    public void PlotLine(double[] xys, int color, int startPoint, int endPoint){
        if(xys.length<4){
            throw new IllegalArgumentException("xys array too short, must define at least 2 points! length: "+xys.length);
        }
        if(xys.length%2!=0){
            throw new IllegalArgumentException("xys array should have even length! length: "+xys.length);
        }
        if(startPoint<0||startPoint>xys.length/2||endPoint<0||endPoint>xys.length/2||startPoint>=endPoint){
            throw new IllegalArgumentException("invalid startPoint or endPoint for plotting "+xys.length/2+" points! startPoint: "+startPoint+" endPoint: "+endPoint);
        }
        for (int i = 0; i < xys.length/2-1; i++) {
            Util.AlongLineCoords(xys[i*2],xys[i*2+1],xys[(i+1)*2],xys[(i+1)*2+1],(int x, int y)->{this.SetPix(x,y,color); });
        }
    }
    public void PlotLine(double[] xys, int color, int startPoint, int endPoint, double scaleX, double scaleY){
        if(scaleX<0||scaleY<0){
            throw new IllegalArgumentException("scaleX and scaleY must be >=0! scaleX: "+scaleX+" scaleY: "+scaleY);
        }
        if(xys.length<4){
            throw new IllegalArgumentException("xys array too short, must define at least 2 points! length: "+xys.length);
        }
        if(xys.length%2!=0){
            throw new IllegalArgumentException("xys array should have even length! length: "+xys.length);
        }
        if(startPoint<0||startPoint>xys.length/2||endPoint<0||endPoint>xys.length/2||startPoint>=endPoint){
            throw new IllegalArgumentException("invalid startPoint or endPoint for plotting "+xys.length/2+" points! startPoint: "+startPoint+" endPoint: "+endPoint);
        }
        for (int i = 0; i < xys.length/2-1; i++) {
            Util.AlongLineCoords(xys[i*2]*scaleX,xys[i*2+1]*scaleY,xys[(i+1)*2]*scaleX,xys[(i+1)*2+1]*scaleY,(int x, int y)->{this.SetPix(x,y,color); });
        }
    }
    public void PlotLine(double[]xs,double[]ys,int color){
        PlotLine(xs,ys, color, 0,xs.length);
    }
    public void PlotLine(double[] xs, double[] ys, int color, double scale){
        PlotLine(xs,ys, color, 0,xs.length, scale,scale);
    }
    public void PlotLine(double[] xs, double[] ys, int color, int startPoint, int endPoint, double scale){
        PlotLine(xs,ys, color, startPoint,endPoint, scale,scale);
    }
    public void PlotLine(double[] xs, double[] ys, int color, int startPoint, int endPoint){
        if(xs.length!=ys.length){
            throw new IllegalArgumentException("xs and ys must have the same length! xs.length: "+xs.length+" ys.length: "+ys.length);
        }
        if(xs.length<2){
            throw new IllegalArgumentException("arrays too short, must define at least 2 points! length: "+xs.length);
        }
        if(startPoint<0||startPoint>xs.length||endPoint<0||endPoint>xs.length||startPoint>=endPoint-1){
            throw new IllegalArgumentException("invalid startPoint or endPoint for plotting "+xs.length+" points! startPoint: "+startPoint+" endPoint: "+endPoint);
        }
        for (int i = startPoint; i < endPoint-1; i++) {
            Util.AlongLineCoords(xs[i],ys[i],xs[i+1],ys[i+1],(int x, int y)->{this.SetPix(x,y,color);});
        }
    }
    public void PlotLine(double[] xs, double[] ys, int color, int startPoint, int endPoint, double scaleX, double scaleY){
        if(scaleX<0||scaleY<0){
            throw new IllegalArgumentException("scaleX and scaleY must be >=0! scaleX: "+scaleX+" scaleY: "+scaleY);
        }
        if(xs.length!=ys.length){
            throw new IllegalArgumentException("xs and ys must have the same length! xs.length: "+xs.length+" ys.length: "+ys.length);
        }
        if(xs.length<2){
            throw new IllegalArgumentException("arrays too short, must define at least 2 points! length: "+xs.length);
        }
        if(startPoint<0||startPoint>xs.length||endPoint<0||endPoint>xs.length||startPoint>=endPoint-1){
            throw new IllegalArgumentException("invalid startPoint or endPoint for plotting "+xs.length+" points! startPoint: "+startPoint+" endPoint: "+endPoint);
        }
        for (int i = startPoint; i < endPoint-1; i++) {
            Util.AlongLineCoords(xs[i]*scaleX,ys[i]*scaleY,xs[i+1]*scaleX,ys[i+1]*scaleY,(int x, int y)->{this.SetPix(x,y,color);});
        }
    }

    /**
     * gets the xDim component of the vis window
     */
    @Override
    public int compX(){return compX;}
    /**
     * gets the yDim component of the vis window
     */
    @Override
    public int compY(){return compY;}

    @Override
    public boolean IsActive() {
        return active;
    }

    @Override
    public void SetActive(boolean isActive) {
        this.active=isActive;
    }

    /**
     * called by the GuiWindow class to place the vis window
     */
    @Override
    public void GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere) {
        putHere.add(panel);
        coordsHere.add(0);
        coordsHere.add(0);
        compSizesHere.add(compX);
        compSizesHere.add(compY);
    }

    public int I(int x,int y){
       return x*yDim+y;
    }
    public double ClickXpt(MouseEvent e){
        return e.getX()*1.0/ scale;
    };
    public int ClickXsq(MouseEvent e){
        return e.getX()/ scale;
    };
    public double ClickYpt(MouseEvent e){
        return (yDim-1)-e.getY()*1.0/ scale;
    };
    public int ClickYsq(MouseEvent e){
        return (yDim-1)-e.getY()/ scale;
    };
    public void AddMouseListeners(MouseListener mouseListener, MouseMotionListener motionListener){
        if(mouseListener!=null) {
            this.panel.addMouseListener(mouseListener);
        }
        if(motionListener!=null) {
            this.panel.addMouseMotionListener(motionListener);
        }
    }

    public int ItoX(int i){
       return  i/yDim;
    }
    public int ItoY(int i){
       return i%yDim;
    }
    public void DrawShapeHood(int i, int[]Shapehood, int[]ColorInts){
            int ptCt=0;
            for(int k=0;k<Shapehood.length/2;k++) {
                int x = Shapehood[k * 2] + ItoX(k);
                int y = Shapehood[k * 2 + 1] + ItoY(k);
                if (!Util.InDim(xDim, x)||!Util.InDim(yDim, y)) {
                        continue;
                }
                SetPix(x, y, ColorInts[k]);
                ptCt++;
        }
    }
    public<A extends AgentBaseSpatial,G extends AgentGrid2D<A>> void DrawGridShapeHood(G g, AgentToInts<A> AgentToShapeHood, AgentToInts<A> AgentToColors, int BackgroundColorInt){
        int scaleFactor=Math.min(xDim/g.xDim,yDim/g.yDim);

    }

    public void DrawGridDiff(PDEGrid2D drawMe, DoubleToInt ColorFn){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    SetPix(x, y, ColorFn.DoubleToInt(drawMe.Get(x,y)));
                }
            }
        }
    }

    public void DrawGridDiffXY(PDEGrid3D drawMe, DoubleToInt ColorFn){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    double sum = 0;
                    for (int z = 0; z < drawMe.zDim; z++) {
                        sum += drawMe.Get(x, y, z);
                    }
                    SetPix(x, y, ColorFn.DoubleToInt(sum/drawMe.zDim));
                }
            }
        }
    }
    public void DrawGridDiffYZ(PDEGrid3D drawMe, DoubleToInt ColorFn){
        if(active) {
            for (int y = 0; y < drawMe.yDim; y++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    double sum = 0;
                    for (int x = 0; x < drawMe.xDim; x++) {
                        sum += drawMe.Get(x, y, z);
                    }
                    SetPix(y, z, ColorFn.DoubleToInt(sum/drawMe.xDim));
                }
            }
        }
    }
    public void DrawGridDiffXZ(PDEGrid3D drawMe, DoubleToInt ColorFn){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    double sum = 0;
                    for (int y = 0; y < drawMe.yDim; y++) {
                        sum += drawMe.Get(x, y, z);
                    }
                    SetPix(x, z, ColorFn.DoubleToInt(sum / drawMe.yDim));
                }
            }
        }
    }

    public <Q extends AgentBaseSpatial,T extends AgentGrid2D<Q>>void DrawAgents(T drawMe, AgentToColorInt<Q> ColorFn, int backgroundColor){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    Q a = drawMe.GetAgent(x, y);
                    if (a != null) {
                        SetPix(x, y, ColorFn.AgentToColor(a));
                    } else {
                        SetPix(x, y, backgroundColor);
                    }
                }
            }
        }
    }
//    public <Q extends AgentBaseSpatial,T extends AgentGrid2D<Q>>void DrawAgentDensity(T drawMe, int maxDensity,String colorOrder){
//        if(active) {
//            for (int x = 0; x < drawMe.xDim; x++) {
//                for (int y = 0; y < drawMe.yDim; y++) {
//                    //SetColorHeatBound(x,y,drawMe.PopAt(x,y)*1.0/maxDensity,colorOrder);
//                }
//            }
//        }
//        throw new IllegalStateException("this function should be deleted");
//    }
//    public <Q extends AgentBaseSpatial,T extends AgentGrid2D<Q>>void DrawAgentDensity(T drawMe, int maxDensity){
//        if(active) {
//            for (int x = 0; x < drawMe.xDim; x++) {
//                for (int y = 0; y < drawMe.yDim; y++) {
//                    //SetColorHeatBound(x,y,drawMe.PopAt(x,y)*1.0/maxDensity);
//                }
//            }
//        }
//        throw new IllegalStateException("this function should be deleted");
//    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid2D<Q>>void DrawAgents(T drawMe, AgentToColorInt<Q> ColorFn){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    Q a = drawMe.GetAgent(x, y);
                    if (a != null) {
                        SetPix(x, y, ColorFn.AgentToColor(a));
                    }
                }
            }
        }
    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawClosestAgentsXY(T drawMe, AgentToColorInt<Q> ColorFn, int backgroundColor){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    for (int z = 0; z <= drawMe.zDim; z++) {
                        if (z == drawMe.zDim) {
                            SetPix(x, y, backgroundColor);
                        } else {
                            Q a = drawMe.GetAgent(x, y, z);
                            if (a != null) {
                                SetPix(x, y, ColorFn.AgentToColor(a));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawClosestAgentsXZ(T drawMe, AgentToColorInt<Q> ColorFn, int backgroundColor){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int y = 0; y <= drawMe.yDim; y++) {
                        if (y == drawMe.yDim) {
                            SetPix(x, z, backgroundColor);
                        } else {
                            Q a = drawMe.GetAgent(x, y, z);
                            if (a != null) {
                                SetPix(x, z, ColorFn.AgentToColor(a));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawClosestAgentsYZ(T drawMe, AgentToColorInt<Q> ColorFn, int backgroundColor){
        if(active) {
            for (int y = 0; y < drawMe.yDim; y++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int x = 0; x <= drawMe.xDim; x++) {
                        if (x == drawMe.yDim) {
                            SetPix(y, z, backgroundColor);
                        } else {
                            Q a = drawMe.GetAgent(x, y, z);
                            if (a != null) {
                                SetPix(y, z, ColorFn.AgentToColor(a));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawFurthestAgentsXY(T drawMe, AgentToColorInt<Q> ColorFn, int backgroundColor){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    for (int z = drawMe.zDim-1; z >= -1; z++) {
                        if (z == -1) {
                            SetPix(x, y, backgroundColor);
                        } else {
                            Q a = drawMe.GetAgent(x, y, z);
                            if (a != null) {
                                SetPix(x, y, ColorFn.AgentToColor(a));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawFurthestAgentsXZ(T drawMe, AgentToColorInt<Q> ColorFn, int backgroundColor){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int y = drawMe.yDim-1; y >= -1; y++) {
                        if (y == -1) {
                            SetPix(x, z, backgroundColor);
                        } else {
                            Q a = drawMe.GetAgent(x, y, z);
                            if (a != null) {
                                SetPix(x, z, ColorFn.AgentToColor(a));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawFurthestAgentsYZ(T drawMe, AgentToColorInt<Q> ColorFn, int backgroundColor){
        if(active) {
            for (int y = 0; y < drawMe.yDim; y++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int x = drawMe.xDim-1; x >= 0; x++) {
                        Q a = drawMe.GetAgent(x, y, z);
                        if (a != null) {
                            SetPix(y, z, ColorFn.AgentToColor(a));
                            break;
                        }
                    }
                }
            }
        }
    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawClosestAgentsXY(T drawMe, AgentToColorInt<Q> ColorFn){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    for (int z = 0; z < drawMe.zDim; z++) {
                        Q a = drawMe.GetAgent(x, y, z);
                        if (a != null) {
                            SetPix(x, y, ColorFn.AgentToColor(a));
                            break;
                        }
                    }
                }
            }
        }
    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawClosestAgentsXZ(T drawMe, AgentToColorInt<Q> ColorFn){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int y = 0; y < drawMe.yDim; y++) {
                        Q a = drawMe.GetAgent(x, y, z);
                        if (a != null) {
                            SetPix(x, z, ColorFn.AgentToColor(a));
                            break;
                        }
                    }
                }
            }
        }
    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawClosestAgentsYZ(T drawMe, AgentToColorInt<Q> ColorFn){
        if(active) {
            for (int y = 0; y < drawMe.yDim; y++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int x = 0; x < drawMe.xDim; x++) {
                        Q a = drawMe.GetAgent(x, y, z);
                        if (a != null) {
                            SetPix(y, z, ColorFn.AgentToColor(a));
                            break;
                        }
                    }
                }
            }
        }
    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawFurthestAgentsXY(T drawMe, AgentToColorInt<Q> ColorFn){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    for (int z = drawMe.zDim - 1; z >= 0; z++) {
                        Q a = drawMe.GetAgent(x, y, z);
                        if (a != null) {
                            SetPix(x, y, ColorFn.AgentToColor(a));
                            break;
                        }
                    }
                }
            }
        }
    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawFurthestAgentsXZ(T drawMe, AgentToColorInt<Q> ColorFn){
        if(active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int y = drawMe.yDim - 1; y >= 0; y++) {
                        Q a = drawMe.GetAgent(x, y, z);
                        if (a != null) {
                            SetPix(x, z, ColorFn.AgentToColor(a));
                            break;
                        }
                    }
                }
            }
        }
    }
    public <Q extends AgentBaseSpatial,T extends AgentGrid3D<Q>>void DrawFurthestAgentsYZ(T drawMe, AgentToColorInt<Q> ColorFn) {
        if (active) {
            for (int y = 0; y < drawMe.yDim; y++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int x = drawMe.xDim - 1; x >= 0; x++) {
                        Q a = drawMe.GetAgent(x, y, z);
                        if (a != null) {
                            SetPix(y, z, ColorFn.AgentToColor(a));
                            break;
                        }
                    }
                }
            }
        }
    }

    void SetupScaledBuff(){
        if(scaledBuff==null||scaledBuff.getHeight()!=panel.scaleX *yDim||scaledBuff.getWidth()!=panel.scaleX *xDim) {
            scaledBuff = new BufferedImage(panel.scaleX * xDim, panel.scaleX * yDim, BufferedImage.TYPE_INT_ARGB);
            scaledG=scaledBuff.createGraphics();
        }
        scaledG.drawImage((panel.buff.getScaledInstance(panel.scaleX *xDim,-panel.scaleX *yDim,Image.SCALE_FAST)),0,0,null);
    }
    /**
     * called by the GuiWindow to draw the vis
     */
    public void ToPNG(String path){
        if(active) {
            SetupScaledBuff();
            File out = new File(path);
            try {
                ImageIO.write(scaledBuff, "png", out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public void ToJPG(String path){
        if(active) {
            SetupScaledBuff();
            File out = new File(path);
            try {
                ImageIO.write(scaledBuff, "jpg", out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public void ToGIF(String path) {
        if (active) {
            SetupScaledBuff();
            File out = new File(path);
            try {
                ImageIO.write(scaledBuff, "gif", out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
