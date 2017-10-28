//package Framework.Gui;
//
//import Framework.Interfaces.GuiComp;
//import org.knowm.xchart.XChartPanel;
//import org.knowm.xchart.XYChart;
//
//import java.awt.*;
//import java.util.ArrayList;
//
//
////may want to make main class the panel, and take the chart as argument...
//class GuiXYChart extends XYChart implements GuiComp {
//    final XChartPanel panel;
//    final int compX;
//    final int compY;
//
//    public GuiXYChart(int width,int height){
//        this(width,height,1,1);
//    }
//    public GuiXYChart(int width,int height, int compX, int compY) {
//        super(width,height);
//        this.panel=new XChartPanel(this);
//        this.compX = compX;
//        this.compY = compY;
//    }
//
////    public GuiXYChart(XYSeriesRenderStyle mode,
////                      int width,
////                      int height,
////                      int markerSize,
////                      String title,
////                      double[] xData,
////                      double[] yData) {
////        this(mode, width, height,1,1);
////        AddData(title, xData, yData);
////        chart.getStyler().setMarkerSize(markerSize);
////        chart = null;
////    }
////
////
////
////    public void AddData(String title, double[] xData, double[] yData) {
////        chart.addSeries(title, xData, yData);
////    }
////
////    public void AddData(String title, double[] xData, double[] yData, int markerSize) {
////        chart.getStyler().setMarkerSize(markerSize);
////        chart.addSeries(title, xData, yData);
////    }
//
////    public void Display() {
////        new SwingWrapper(chart).displayChart();
////    }
//
//    @Override
//    public int compX() {
//        return compX;
//    }
//
//    @Override
//    public int compY() {
//        return compY;
//    }
//
//    @Override
//    public boolean IsActive() {
//        return true;
//    }
//
//    @Override
//    public void SetActive(boolean isActive) {
//    }
//
//    @Override
//    public void GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere) {
//        putHere.add(this.panel);
//        coordsHere.add(0);
//        coordsHere.add(0);
//        compSizesHere.add(compX);
//        compSizesHere.add(compY);
//
//    }
//}
