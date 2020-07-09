package Examples.ColorBarGenerator;

import HAL.Gui.*;
import HAL.Util;

public class ColorBarGenerator{
    static final int TICKS_EXTRA_SPACE=8*4+2;

    public static void main(String[] args){
        UIWindow win=new UIWindow();
        win.AddCol(0,new UIIntInput("width",5,0,100));
        win.AddCol(0,new UIIntInput("height",50,1,1000));
        win.AddCol(0,new UIIntInput("scalefactor",2,1,10));
        win.AddCol(0,new UIDoubleInput("min",0.0,-Double.MAX_VALUE,Double.MAX_VALUE));
        win.AddCol(0,new UIDoubleInput("max",1.0,-Double.MAX_VALUE,Double.MAX_VALUE));
        win.AddCol(0,new UIIntInput("ticks",2,2,1000));
        win.AddCol(0,new UIComboBoxInput("color function",0,new String[]{"HeatMapRGB","HeatMapRBG","HeatMapGRB","HeatMapGBR","HeatMapBRG","HeatMapBGR","HeatMapJet","GreyScale"}));
        win.AddCol(0,new UIButton("Show",true,(e)->{
            double min=win.GetDouble("min");
            double max=win.GetDouble("max");
            GridWindow draw=new GridWindow(win.GetInt("width")+TICKS_EXTRA_SPACE,win.GetInt("height"),win.GetInt("scalefactor"),false,null);
            Util.DrawColorBar(draw,0,0,win.GetInt("width"),win.GetInt("height"),win.GetDouble("min"),win.GetDouble("max"),win.GetInt("ticks"),(double in)->{
                    switch(win.GetInt("color function")){
                        case 0: return Util.HeatMapRGB(in,min,max);
                        case 1: return Util.HeatMapRBG(in,min,max);
                        case 2: return Util.HeatMapGRB(in,min,max);
                        case 3: return Util.HeatMapGBR(in,min,max);
                        case 4: return Util.HeatMapBRG(in,min,max);
                        case 5: return Util.HeatMapBGR(in,min,max);
                        case 6: return Util.HeatMapJet(in,min,max);
                        case 7: return Util.GreyScale(in,min,max);
                        default: return Util.WHITE;
                    }
            });
        }));
        win.RunGui();
    }
}
