package HAL;

import HAL.GridsAndAgents.Grid2Dint;
import HAL.Gui.UIGrid;
import HAL.Interfaces.*;
import HAL.Interfaces.SerializableModel;
import HAL.Tools.FileIO;
import HAL.Tools.Internal.SweepRun;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A collection of helpful static utility functions
 * recommended import: import static Util.*
 */
public final class Util {

    public static final double DOUBLE_EPSILON = 2.22E-16;
    private static Scanner inputReader=null;
    private static DecimalFormat fmt=new DecimalFormat("0.#E0#");

    /**
     * returns a color integer based on the RGB components passed in. color values should be scaled from 0 to 1
     */
    public static int RGB(double r, double g, double b) {
        int ri = Bound((int) (r * 256), 0, 255);
        int gi = Bound((int) (g * 256), 0, 255);
        int bi = Bound((int) (b * 256), 0, 255);
        return (0xff000000) | (ri << 16) | (gi << 8) | (bi);
    }

    /**
     * returns a color integer based on the RGBA components passed in. color values should be scaled from 0 to 1. the
     * alpha value is used with the AddAlphaGrid UIGrid function to blend colors together from multiple grids
     */
    public static int RGBA(double r, double g, double b, double a) {
        int ri = Bound((int) (r * 256), 0, 255);
        int gi = Bound((int) (g * 256), 0, 255);
        int bi = Bound((int) (b * 256), 0, 255);
        int ai = Bound((int) (a * 256), 0, 255);
        return (ai << 24) | (ri << 16) | (gi << 8) | (bi);
    }

    /**
     * returns a color integer based on the RGB components passed in. color values should be scaled from 0 to 255.
     */
    public static int RGB256(int r, int g, int b) {
        r = Bound(r, 0, 255);
        g = Bound(g, 0, 255);
        b = Bound(b, 0, 255);
        return (0xff000000) | (r << 16) | (g << 8) | (b);
    }

    /**
     * returns a color integer based on the RGB components passed in. color values should be scaled from 0 to 255. the
     * alpha value is used with the AddAlphaGrid UIGrid function to blend colors together from multiple grids
     */
    public static int RGBA256(int r, int g, int b, int a) {
        r = Bound(r, 0, 255);
        g = Bound(g, 0, 255);
        b = Bound(b, 0, 255);
        a = Bound(a, 0, 255);
        return (a << 24) | (r << 16) | (g << 8) | (b);
    }

    /**
     * returns the Red component value of the color as an integer from 0 to 255
     */
    public static int GetRed256(int color) {
        return (color & 0x00ff0000) >> 16;
    }

    /**
     * returns the Green component value of the color as an integer from 0 to 255
     */
    public static int GetGreen256(int color) {
        return (color & 0x0000ff00) >> 8;
    }

    /**
     * returns the Blue component value of the color as an integer from 0 to 255
     */
    public static int GetBlue256(int color) {
        return color & 0x000000ff;
    }

    /**
     * returns the Alpha component value of the color as an integer from 0 to 255
     */
    public static int GetAlpha256(int color) {
        return color >>> 24;
    }

    /**
     * returns the Red component value of the color as an integer from 0 to 1
     */
    public static double GetRed(int color) {
        return ((color & 0x00ff0000) >> 16) / 255.0;
    }

    /**
     * returns the Green component value of the color as an integer from 0 to 1
     */
    public static double GetGreen(int color) {
        return ((color & 0x0000ff00) >> 8) / 255.0;
    }

    /**
     * returns the Blue component value of the color as an integer from 0 to 1
     */
    public static double GetBlue(int color) {
        return (color & 0x000000ff) / 255.0;
    }

    /**
     * returns the Alpha component value of the color as an integer from 0 to 1
     */
    public static double GetAlpha(int color) {
        return (color >>> 24) / 255.0;
    }

    /**
     * returns a new color integer based on the input color but with the red component changed to the r argument
     */
    public static int SetRed(int color, double r) {
        int ri = Bound((int) (r * 256), 0, 255);
        return color & 0xff00ffff | (ri << 16);
    }

    /**
     * returns a new color integer based on the input color but with the green component changed to the r argument
     */
    public static int SetGreen(int color, double g) {
        int gi = Bound((int) (g * 256), 0, 255);
        return color & 0xffff00ff | (gi << 8);
    }

    /**
     * returns a new color integer based on the input color but with the blue component changed to the r argument
     */
    public static int SetBlue(int color, double b) {
        int bi = Bound((int) (b * 256), 0, 255);
        return color & 0xffffff00 | (bi);
    }

    /**
     * returns a new color integer based on the input color but with the alpha component changed to the r argument
     */
    public static int SetAlpha(int color, double a) {
        a = Math.max(a, 0);
        int ai = Bound((int) (a * 256), 0, 255);
        return color & 0x00ffffff | (ai << 24);
    }

    /**
     * returns a new color integer based on the input color but with the red component changed to the r argument
     */
    public static int SetRed256(int color, int r) {
        r = Bound(r, 0, 255);
        return color & 0xff00ffff | (r << 16);
    }

    /**
     * returns a new color integer based on the input color but with the green component changed to the r argument
     */
    public static int SetGreen256(int color, int g) {
        g = Bound(g, 0, 255);
        return color & 0xffff00ff | (g << 8);
    }

    /**
     * returns a new color integer based on the input color but with the blue component changed to the r argument
     */
    public static int SetBlue256(int color, int b) {
        b = Bound(b, 0, 255);
        return color & 0xffffff00 | (b);
    }

    /**
     * returns a new color integer based on the input color but with the alpha component changed to the r argument
     */
    public static int SetAlpha256(int color, int a) {
        a = Bound(a, 0, 255);
        return color & 0x00ffffff | (a << 24);
    }

    /**
     * returns the inverse of the input color
     */
    public static int InvertColor(int color){
        return RGB256(255-GetRed256(color),255-GetGreen256(color),255-GetBlue256(color));
    }

    //a set of default colors
    final public static int RED = RGB(1, 0, 0), GREEN = RGB(0, 1, 0), BLUE = RGB(0, 0, 1), BLACK = RGB(0, 0, 0), WHITE = RGB(1, 1, 1), YELLOW = RGB(1, 1, 0), CYAN = RGB(0, 1, 1), MAGENTA = RGB(1, 0, 1);

    //a set of categorical colors based on d3 category20
    final private static int CC0 = RGB256(56, 116, 177), CC1 = RGB256(198, 56, 44), CC2 = RGB256(79, 159, 57), CC3 = RGB256(189, 190, 58), CC4 = RGB256(142, 102, 186), CC5 = RGB256(240, 134, 39), CC6 = RGB256(83, 187, 206), CC7 = RGB256(214, 123, 191), CC8 = RGB256(133, 88, 76), CC9 = RGB256(178, 197, 230), CC10 = RGB256(243, 156, 151), CC11 = RGB256(166, 222, 144), CC12 = RGB256(220, 220, 147), CC13 = RGB256(194, 174, 211), CC14 = RGB256(246, 191, 126), CC15 = RGB256(169, 216, 228), CC16 = RGB256(238, 184, 209), CC17 = RGB256(190, 157, 146), CC18 = RGB256(199, 199, 199), CC19 = RGB256(127, 127, 127);


    //generates a list of categorical colors
    public static int[]CategoricalColors(int startIndex,int endIndex){
        int[]ret=new int[endIndex-startIndex];
        for (int i = startIndex; i < endIndex; i++) {
            ret[i-startIndex]=CategorialColor(i);
        }
        return ret;
    }

    /**
     * returns a color from the d3 category20 color set based on the index argument
     */
    public static int CategorialColor(int index) {
        if (index < 0 || index > 20) {
            throw new IllegalArgumentException("index outside color category range [0-19] index: " + index);
        }
        switch (index) {
            case 0:
                return CC0;     // blue
            case 1:
                return CC1;       //red
            case 2:
                return CC2;       // green
            case 3:
                return CC3;      //yellow
            case 4:
                return CC4;     //purple
            case 5:
                return CC5;     //orange
            case 6:
                return CC6;      //cyan
            case 7:
                return CC7;     //pink
            case 8:
                return CC8;     // brown
            case 9:
                return CC9;     // light blue
            case 10:
                return CC10;    // light red
            case 11:
                return CC11;    // light green
            case 12:
                return CC12;    // light yellow
            case 13:
                return CC13;    // light purple
            case 14:
                return CC14;    // light orange
            case 15:
                return CC15;    // light cyan
            case 16:
                return CC16;    // light pink
            case 17:
                return CC17;    // light brown
            case 18:
                return CC18;    // light gray
            case 19:
                return CC19;    // gray
            default:
                throw new IllegalArgumentException("index outside color category range index: " + index);
        }
    }

    /**
     * returns a color int by mapping values from 0 to 1 with a heatmap with colors black, red, orange, yellow, white
     */
    public static int HeatMapRGB(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c1, c2, c3);
    }

    /**
     * returns a color int by mapping values from 0 to 1 with a heatmap with colors black, red, pink, magenta, white
     */
    public static int HeatMapRBG(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c1, c3, c2);
    }

    /**
     * returns a color int by mapping values from 0 to 1 with a heatmap with colors black, green, lime, yellow, white
     */
    public static int HeatMapGRB(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c2, c1, c3);
    }

    /**
     * returns a color int by mapping values from 0 to 1 with a heatmap with colors black, green, greenblue, cyan,
     * white
     */
    public static int HeatMapGBR(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c3, c1, c2);
    }

    /**
     * returns a color int by mapping values from 0 to 1 with a heatmap with colors black, blue, purple, pink, white
     */
    public static int HeatMapBRG(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c2, c3, c1);
    }

    /**
     * returns a color int by mapping values from 0 to 1 with a heatmap with colors black, blue, light blue, cyan,
     * white
     */
    public static int HeatMapBGR(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c3, c2, c1);
    }

    /**
     * returns a color int by mapping values from min to max with a heatmap with colors black, red, orange, yellow,
     * white
     */
    public static int HeatMapRGB(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        return HeatMapRGB(val);
    }

    /**
     * returns a color int by mapping values from min to max with a heatmap with colors black, red, pink, magenta,
     * white
     */
    public static int HeatMapRBG(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        return HeatMapBGR(val);
    }

    /**
     * returns a color int by mapping values from min to max with a heatmap with colors black, green, lime, yellow,
     * white
     */
    public static int HeatMapGRB(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        return HeatMapGRB(val);
    }

    /**
     * returns a color int by mapping values from min to max with a heatmap with colors black, green, greenblue, cyan,
     * white
     */
    public static int HeatMapGBR(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        return HeatMapGBR(val);
    }

    /**
     * returns a color int by mapping values from min to max with a heatmap with colors black, blue, purple, pink,
     * white
     */
    public static int HeatMapBRG(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        return HeatMapBRG(val);
    }

    /**
     * returns a color int by mapping values from min to max with a heatmap with colors black, blue, light blue, cyan,
     * white
     */
    public static int HeatMapBGR(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        return HeatMapBGR(val);
    }

    public static int HeatMapJet(double val) {
        if(val<=0){
            return RGB(0,0,0.5);
        }
        if(val>=1){
            return RGB(0.5,0,0);
        }
        double c1=1.5-Math.abs(0.75-val)*4;
        double c2=1.5-Math.abs(0.5-val)*4;
        double c3=1.5-Math.abs(0.25-val)*4;
        return RGB(c1,c2,c3);
    }


    public static int HeatMapJet(double val,double min,double max){
        val=Scale0to1(val,min,max);
        return HeatMapJet(val);
    }
    public static int HeatMapParula(double val){
        if(val<=0){
            return RGB(0.2,0.2,0.5);
        }
        if(val>=1){
            return RGB(1.0,1.0,0.0);
        }
        if(val<0.125){
            return RGB(0.2-val*1.6,0.2+val*0.8,0.5+val*2);
        }
        return RGB((val-0.375)*2,0.2+val*0.8,0.9-val*1.143);
    }

    public static int HeatMapParula(double val, double min, double max){
        val=Scale0to1(val,min,max);
        return HeatMapParula(val);
    }

    /**
     * interpoloates value from 0 to 1 to between any pair of colors
     */
    public static int ColorMap(double val, int minColor, int maxColor) {
        return ColorMap(val, 0, 1, minColor, maxColor);
    }

    /**
     * interpoloates value from min to max to between any pair of colors
     */
    public static int ColorMap(double val, double min, double max, int minColor, int maxColor) {
        if (val <= min) {
            return minColor;
        }
        if (val >= max) {
            return maxColor;
        }
        val = (val - min) / (max - min);
        return RGB256(InterpComp(val, GetRed256(minColor), GetRed256(maxColor)), InterpComp(val, GetGreen256(minColor), GetGreen256(maxColor)), InterpComp(val, GetBlue256(minColor), GetBlue256(maxColor)));
    }

    /**
     * interpoloates coordinate pairs from 0 to 1 to between a box with any 4 corner colors
     */
    public static int ColorMap2D(double valx, double valy, int bottomLeft, int bottomRight, int topLeft, int topRight) {
        return RGB(
                Interpolate2D(valx, valy, GetRed(bottomLeft), GetRed(bottomRight), GetRed(topLeft), GetRed(topRight)),
                Interpolate2D(valx, valy, GetGreen(bottomLeft), GetGreen(bottomRight), GetGreen(topLeft), GetGreen(topRight)),
                Interpolate2D(valx, valy, GetBlue(bottomLeft), GetBlue(bottomRight), GetBlue(topLeft), GetBlue(topRight))
        );
    }

    /**
     * returns the RGBA components of the color as a string
     */
    public static String ColorString(int color) {
        return "r: " + GetRed256(color) + ", g: " + GetGreen256(color) + ", b: " + GetBlue256(color) + ", a: " + GetAlpha256(color);
    }


    //TODO check alpha value of return

    /**
     * generates a color int based on the HSB color space
     */
    public static int HSBColor(double hue, double saturation, double brightness) {
        return Color.HSBtoRGB((float) hue, (float) saturation, (float) brightness);
    }

    /**
     * puts the HSB components [hue, saturation, brightness] of the RGB color into the ret array
     */
    public static void ColorToHSB(int color, float[] ret) {
        Color.RGBtoHSB(GetRed256(color), GetGreen256(color), GetBlue256(color), ret);
    }

    /**
     * generates an RGB color from the YCbCr color space
     */
    public static int YCbCrColor(double y, double cb, double cr) {
        double yd = (double) (Bound(y * 256, 0, 255));
        double cbd = (double) (Bound(cb * 256, 0, 255));
        double crd = (double) (Bound(cr * 256, 0, 255));
        int r = (int) (yd + 1.402 * (crd - 128));
        int g = (int) (yd - 0.344136 * (cbd - 128) - 0.714136 * (crd - 128));
        int b = (int) (yd + 1.772 * (cbd - 128));
        return RGB256(r, g, b);
    }

    /**
     * generates an RGB color from the CbCr plane with x,y coordinates assumed to be from 0 to 1
     */
    public static int CbCrPlaneColor(double x, double y) {
        double ycbcrY = 0.5;
        return YCbCrColor(ycbcrY, x, y);
    }

    public static int HsLuvColor(double x,double y){
        double xScaled =(Bound(y,0,0.99999999))*2;
        double yScaled=(0.99999999-Bound(x,0,0.99999999))*2;
        int xInt=(int)xScaled;
        int yInt=(int)yScaled;
        xScaled=xScaled-xInt;
        yScaled=yScaled-yInt;
        double r=0;
        double g=0;
        double b=0;
        switch (xInt){
            case 0: switch (yInt){
                case 0://bottom left
                    r=Interpolate2D(xScaled,yScaled,89,200,60,158);
                    g=Interpolate2D(xScaled,yScaled,60,60,199,157);
                    b=Interpolate2D(xScaled,yScaled,255,255,255,158);
                    break;
                case 1://top left
                    r=Interpolate2D(xScaled,yScaled,60,158,60,255);
                    g=Interpolate2D(xScaled,yScaled,199,157,255,247);
                    b=Interpolate2D(xScaled,yScaled,255,158,85,60);
                    break;
            }break;
            case 1: switch (yInt){
                case 0://bottom right
                    r=Interpolate2D(xScaled,yScaled,200,255,158,255);
                    g=Interpolate2D(xScaled,yScaled,60,60,157,60);
                    b=Interpolate2D(xScaled,yScaled,255,230,158,106);
                    break;
                case 1://top right
                    r=Interpolate2D(xScaled,yScaled,158,255,255,255);
                    g=Interpolate2D(xScaled,yScaled,157,60,247,132);
                    b=Interpolate2D(xScaled,yScaled,158,106,60,60);
                    break;
            }break;
        }
        return RGB256((int)r,(int)g,(int)b);
    }

    public static int GreyScale(double val,double min, double max){
        double colorVal=(val-min)/(max-min);
        return RGB(colorVal,colorVal,colorVal);
    }

    /**
     * generates a greyscale color, val is assumed to be in the range 0 to 1
     */
    public static int GreyScale(double val){
        return RGB(val,val,val);
    }

    /**
     * gets the max value from an array
     */
    public static double ArrayMax(double[] arr) {
        double max = Double.MIN_VALUE;
        for (double val : arr) {
            max = max < val ? val : max;
        }
        return max;
    }

    /**
     * gets the max value from an array
     */
    public static int ArrayMax(int[] arr) {
        int max = Integer.MIN_VALUE;
        for (int val : arr) {
            max = max < val ? val : max;
        }
        return max;
    }

    /**
     * gets the min value from an array
     */
    public static double ArrayMin(double[] arr) {
        double min = Double.MAX_VALUE;
        for (double val : arr) {
            min = min < val ? min : val;
        }
        return min;
    }

    /**
     * gets the min value from an array
     */
    public static int ArrayMin(int[] arr) {
        int min = Integer.MAX_VALUE;
        for (int val : arr) {
            min = min < val ? min : val;
        }
        return min;
    }

    /**
     * sums the array
     */
    public static double ArraySum(double[] arr) {
        double sum = 0;
        for (double val : arr) {
            sum += val;
        }
        return sum;
    }

    /**
     * sums the array
     */
    public static double ArraySquaredSum(double[] arr) {
        double sum = 0;
        for (double val : arr) {
            sum += val*val;
        }
        return sum;
    }

    /**
     * sums the array
     */
    public static int ArraySum(int[] arr) {
        int sum = 0;
        for (int val : arr) {
            sum += val;
        }
        return sum;
    }

    /**
     * sums the array
     */
    public static long ArraySum(long[] arr) {
        long sum = 0;
        for (long val : arr) {
            sum += val;
        }
        return sum;
    }

    /**
     * returns the mean value of the provided array
     */
    static public double ArrayMean(double[] a) {
        double tot = 0;
        for (int i = 0; i < a.length; i++) {
            tot += a[i];
        }
        return tot / a.length;
    }

    /**
     * returns the standard deviation value of the provided array
     */
    static public double ArrayStdDev(double[] a){
        double tot = 0;
        double mean=ArrayMean(a);
        for (int i = 0; i < a.length; i++) {
            double dev=a[i]-mean;
            tot+=dev*dev;
        }
        return Math.sqrt(tot/a.length);

    }
    /**
     * returns the mean value of the provided array
     */
    static public double ArrayMean(int[] a) {
        double tot = 0;
        for (int i = 0; i < a.length; i++) {
            tot += a[i];
        }
        return tot / a.length;
    }

    /**
     * returns the standard deviation value of the provided array
     */
    static public double ArrayStdDev(int[] a){
        double tot = 0;
        double mean=ArrayMean(a);
        for (int i = 0; i < a.length; i++) {
            double dev=a[i]-mean;
            tot+=dev*dev;
        }
        return Math.sqrt(tot/a.length);

    }
    /**
     * returns the mean value of the provided array
     */
    static public double ArrayMean(long[] a) {
        double tot = 0;
        for (int i = 0; i < a.length; i++) {
            tot += a[i];
        }
        return tot / a.length;
    }

    /**
     * returns the standard deviation value of the provided array
     */
    static public double ArrayStdDev(long[] a){
        double tot = 0;
        double mean=ArrayMean(a);
        for (int i = 0; i < a.length; i++) {
            double dev=a[i]-mean;
            tot+=dev*dev;
        }
        return Math.sqrt(tot/a.length);

    }
    /**
     * prints an array to a string, using the .toString function, and separating entries with the delim argument
     */
    public static <T> String ArrToString(ArrayList<String> arr, String delim) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.size() - 1; i++) {
            sb.append(arr.get(i) + delim);
        }
        sb.append(arr.get(arr.size() - 1));
        return sb.toString();
    }

    /**
     * prints an array between start and end indices to a string, using the .toString function, and separating entries
     * with the delim argument
     */
    public static <T> String ArrToString(ArrayList<String> arr, String delim, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end-1; i++) {
            sb.append(arr.get(i) + delim);
        }
        sb.append(arr.get(arr.size() - 1));
        return sb.toString();
    }

    /**
     * prints an array to a string, using the .toString function, and separating entries with the delim argument
     */
    public static <T> String ArrToString(T[] arr, String delim) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[arr.length - 1]);
        return sb.toString();
    }

    /**
     * prints an array between start and end indices to a string, using the .toString function, and separating entries
     * with the delim argument
     */
    public static <T> String ArrToString(T[] arr, String delim, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[end - 1]);
        return sb.toString();
    }

    public static double[]ArrayListToArrayDouble(ArrayList<Double>in){
        double[]out=new double[in.size()];
        for (int i = 0; i < out.length; i++) {
            out[i]=in.get(i);
        }
        return out;
    }

    public static int[]ArrayListToArrayInt(ArrayList<Integer>in){
        int[]out=new int[in.size()];
        for (int i = 0; i < out.length; i++) {
            out[i]=in.get(i);
        }
        return out;
    }

    public  static <T> T[]ArrayListToArrayObject(ArrayList<T>in){
        T[]out=(T[])new Object[in.size()];
        for (int i = 0; i < out.length; i++) {
            out[i]=in.get(i);
        }
        return out;
    }
    public  static String[]ArrayListToArrayString(ArrayList<String>in){
        String[]out=new String[in.size()];
        for (int i = 0; i < out.length; i++) {
            out[i]=in.get(i);
        }
        return out;
    }

    public static double InterpolateLinear(double x, double startX, double endX, double startY, double endY){
        if(x>endX||x<startX){
            throw new IllegalArgumentException("x:"+x+" must be between startX:"+startX+" and endX:"+endX);
        }
        return (startY*(endX-x)+endY*(x-startX))/(endX-startX);
    }

    /**
     * interpolates value from 0 to 1 to be between min and max
     */
    public static double Interpolate(double val, double min, double max) {
        val = Util.Bound(val, 0, 1);
        return (max - min) * val + min;
    }
    public static double InterpolateNoBound(double val, double min, double max) {
        return (max - min) * val + min;
    }

//    /**
//     * interpolates value from 0 to 1 to be between min and max
//     */
//    public static double Interpolate(double position,double leftPoint,double rightPoint, double leftVal, double rightVal) {
//        position-=leftPoint/(rightPoint-leftPoint);
//        return (leftVal - rightVal) * position + leftVal;
//
//    }

    /**
     * interpolates value from 0 to 1 to be between min and max
     */
    public static double Interpolate2D(double x, double y, double bottomLeft, double bottomRight, double topLeft, double topRight) {
        x = Util.Bound(x, 0, 1);
        y = Util.Bound(y, 0, 1);
        double bottom = (bottomRight - bottomLeft) * x + bottomLeft;
        double top = (topRight - topLeft) * x + topLeft;
        return (top - bottom) * y + bottom;
    }
    public static double Interpolate2DNoBound(double x, double y, double bottomLeft, double bottomRight, double topLeft, double topRight) {
        double bottom = (bottomRight - bottomLeft) * x + bottomLeft;
        double top = (topRight - topLeft) * x + topLeft;
        return (top - bottom) * y + bottom;
    }

    /**
     * gets the value from a single bit of an int32
     */
    public static boolean GetBit(int v, int i) {
        return v >> i % 1 != 0;
    }

    /**
     * gets the value from a single bit of an long64
     */
    public static boolean GetBit(long v, int i) {
        return v >> i % 1 != 0;
    }

    /**
     * returns a version of the int v with the bit at index i flipped
     */
    public static int FlipBit(int v, int i) {
        return v ^ (1 << i);
    }

    /**
     * returns a version of the long v with the bit at index i flipped
     */
    public static long FlipBit(long v, int i) {
        return v ^ (1 << i);
    }

    /**
     * returns a version of the int v with the bit at position i set to val
     */
    public static int SetBit(int v, int i, boolean val) {
        return val ? v | (1 << i) : v & ~(1 << i);
    }

    /**
     * returns a version of the long v with the bit at position i set to val
     */
    public static long SetBit(long v, int i, boolean val) {
        return val ? v | (1 << i) : v & ~(1 << i);
    }

    /**
     * prints an array using delim to separate the entries
     */
    public static String ArrToString(double[] arr, String delim) {
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[arr.length - 1]);
        return sb.toString();
    }

    /**
     * prints an array using delim to separate the entries, beginning at index start and ending at index end
     */
    public static String ArrToString(double[] arr, String delim, int start, int end) {
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[end - 1]);
        return sb.toString();
    }

    /**
     * prints an array using delim to separate the entries
     */
    public static String ArrToString(float[] arr, String delim) {
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[arr.length - 1]);
        return sb.toString();
    }

    /**
     * prints an array using delim to separate the entries, beginning at index start and ending at index end
     */
    public static String ArrToString(float[] arr, String delim, int start, int end) {
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[end - 1]);
        return sb.toString();
    }

    /**
     * prints an array using delim to separate the entries
     */
    public static String ArrToString(int[] arr, String delim) {
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[arr.length - 1]);
        return sb.toString();
    }

    /**
     * prints an array using delim to separate the entries, beginning at index start and ending at index end
     */
    public static String ArrToString(int[] arr, String delim, int start, int end) {
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[end - 1]);
        return sb.toString();
    }

    /**
     * prints an array using delim to separate the entries
     */
    public static String ArrToString(long[] arr, String delim) {
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[arr.length - 1]);
        return sb.toString();
    }

    /**
     * prints an array using delim to separate the entries, beginning at index start and ending at index end
     */
    public static String ArrToString(long[] arr, String delim, int start, int end) {
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[end - 1]);
        return sb.toString();
    }

    /**
     * prints an array using delim to separate the entries, beginning at index start and ending at index end
     */
    public static String ArrToString(String[] arr, String delim, int start, int end) {
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[end - 1]);
        return sb.toString();
    }

    /**
     * prints an array using delim to separate the entries
     */
    public static String ArrToString(String[] arr, String delim) {
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[arr.length - 1]);
        return sb.toString();
    }

    /**
     * Returns a new array that is the first array with the second concatenated to the end of it
     */
    public static <T> T[] Concat(T[] first, T[] second) {
        int firstLen = first.length;
        int secondLen = second.length;
        T[] ret = (T[]) Array.newInstance(first.getClass().getComponentType(), firstLen + secondLen);
        System.arraycopy(first, 0, ret, 0, firstLen);
        System.arraycopy(second, 0, ret, firstLen, firstLen + secondLen);
        return ret;
    }

    /**
     * Returns a new array that is the first array with the appendMe object appended to the end of it
     */
    public static <T> T[] Append(T[] arr, T appendMe) {
        int firstLen = arr.length;
        T[] ret = (T[]) Array.newInstance(arr.getClass().getComponentType(), firstLen + 1);
        System.arraycopy(arr, 0, ret, 0, firstLen);
        ret[firstLen] = appendMe;
        return ret;
    }

    /**
     * returns an array of indices starting with 0 and ending with nEntries-1
     */
    public static int[] GenIndicesArray(int nEntries) {
        int indices[] = new int[nEntries];
        for (int i = 0; i < nEntries; i++) {
            indices[i] = i;
        }
        return indices;
    }


    /**
     * Returns the coordinates defining the Von Neumann neighborhood centered on (0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [xDim,yDim,xDim,yDim...]
     */
    public static int[] VonNeumannHood(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0, 0, 0, 0, 0,
                    0, 0, 1, 0, -1, 0, 0, 1, 0, -1};
        } else {
            return new int[]{0, 0, 0, 0,
                    1, 0, -1, 0, 0, 1, 0, -1};
        }
    }

    /**
     * Returns the coordinates defining the Moore neighborhood centered on (0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [xDim,yDim,xDim,yDim,...]
     */
    public static int[] MooreHood(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 1, 1, 1, 0, 1, -1, 0, -1, -1, -1, -1, 0, -1, 1, 0, 1};
        } else {
            return new int[]{0, 0, 0, 0, 0, 0, 0, 0,
                    1, 1, 1, 0, 1, -1, 0, -1, -1, -1, -1, 0, -1, 1, 0, 1};
        }
    }

    /**
     * Returns the coordinates defining the Hexagonal neighborhood for even yDim coordinates centered on (0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [xDim,yDim,xDim,yDim,...]
     */
    public static int[] HexHoodEvenY(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0, 0, 0, 0, 0, 0, 0,
                    0, 0, 1, 1, 1, 0, 1, -1, 0, -1, -1, 0, 0, 1};
        } else {
            return new int[]{0, 0, 0, 0, 0, 0,
                    1, 1, 1, 0, 1, -1, 0, -1, -1, 0, 0, 1};
        }
    }

    /**
     * Returns the coordinates defining the Hexagonal neighborhood for odd yDim coordinates centered on (0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [xDim,yDim,xDim,yDim,...]
     */
    public static int[] HexHoodOddY(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0, 0, 0, 0, 0, 0, 0,
                    0, 0, 1, 0, 0, -1, -1, -1, -1, 0, -1, 1, 0, 1};
        } else {
            return new int[]{0, 0, 0, 0, 0, 0,
                    1, 0, 0, -1, -1, -1, -1, 0, -1, 1, 0, 1};
        }
    }

    /**
     * Returns the coordinates defining the Triangular neighborhood for even xDim, even yDim or oddx, odd yDim. centered
     * on (0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [xDim,yDim,xDim,yDim,...]
     */
    public static int[] TriangleHoodSameParity(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0, 0, 0, 0,
                    0, 0, -1, 0, 1, 0, 0, 1};
        } else {
            return new int[]{0, 0, 0,
                    -1, 0, 1, 0, 0, 1};
        }
    }

    /**
     * Returns the coordinates defining the Triangular neighborhood for even xDim, odd yDim or oddx, even yDim. centered
     * on (0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [xDim,yDim,xDim,yDim,...]
     */
    public static int[] TriangleHoodDifParity(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0, 0, 0, 0,
                    0, 0, -1, 0, 1, 0, 0, -1};
        } else {
            return new int[]{0, 0, 0,
                    -1, 0, 1, 0, 0, -1};
        }
    }


    /**
     * Returns the coordinates defining the Von Neumann neighborhood centered on (0,0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0,0)
     * @return coordinates returned as an array of the form [xDim,yDim,z,xDim,yDim,z,...]
     */
    public static int[] VonNeumannHood3D(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1};
        } else {
            return new int[]{0, 0, 0, 0, 0, 0,
                    1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1};
        }
    }
    public static int[] SemiMooreHood3D(boolean includeOrigin){
        if(includeOrigin){
            return new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    0,0,0,
                    1,0,0,
                    -1,0,0,
                    0,1,0,
                    0,-1,0,
                    0,0,1,
                    0,0,-1,
                    1,1,0,
                    1,-1,0,
                    1,0,1,
                    1,0,-1,
                    -1,1,0,
                    -1,-1,0,
                    -1,0,1,
                    -1,0,-1,
                    0,1,1,
                    0,1,-1,
                    0,-1,1,
                    0,-1,-1,
            };
        }else{
            return new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    1,0,0,
                    -1,0,0,
                    0,1,0,
                    0,-1,0,
                    0,0,1,
                    0,0,-1,
                    1,1,0,
                    1,-1,0,
                    1,0,1,
                    1,0,-1,
                    -1,1,0,
                    -1,-1,0,
                    -1,0,1,
                    -1,0,-1,
                    0,1,1,
                    0,1,-1,
                    0,-1,1,
                    0,-1,-1,
            };
        }
    }

    public static int[] MooreHood3D(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0,
                    0, 0, 1,
                    0, 0, -1,
                    0, 1, 0,
                    0, -1, 0,
                    1, 0, 0,
                    -1, 0, 0,
                    1, 0, 1,
                    1, 0, -1,
                    1, 1, 0,
                    0, 1, 1,
                    0, 1, -1,
                    -1, 0, 1,
                    -1, 0, -1,
                    -1, 1, 0,
                    -1, -1, 0,
                    0, -1, 1,
                    0, -1, -1,
                    1, -1, 0,
                    1, 1, 1,
                    1, 1, -1,
                    -1, 1, 1,
                    -1, 1, -1,
                    -1, -1, 1,
                    -1, -1, -1,
                    1, -1, 1,
                    1, -1, -1,
            };
        } else {
            return new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 1,
                    0, 0, -1,
                    0, 1, 0,
                    0, -1, 0,
                    1, 0, 0,
                    -1, 0, 0,
                    1, 0, 1,
                    1, 0, -1,
                    1, 1, 0,
                    0, 1, 1,
                    0, 1, -1,
                    -1, 0, 1,
                    -1, 0, -1,
                    -1, 1, 0,
                    -1, -1, 0,
                    0, -1, 1,
                    0, -1, -1,
                    1, -1, 0,
                    1, 1, 1,
                    1, 1, -1,
                    -1, 1, 1,
                    -1, 1, -1,
                    -1, -1, 1,
                    -1, -1, -1,
                    1, -1, 1,
                    1, -1, -1,
            };
        }
    }

    public static int[]CubicHoneyHood3DevenZ(boolean includeOrigin){
        if(includeOrigin){
            return new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    0,0,0,
                    0,0,2,
                    0,0,-2,
                    1,0,0,
                    -1,0,0,
                    0,1,0,
                    0,-1,0,
                    0,0,1,
                    -1,0,1,
                    -1,-1,1,
                    0,-1,1,
                    0,0,-1,
                    -1,0,-1,
                    -1,-1,-1,
                    0,-1,-1,
            };
        }else{
            return new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    0,0,2,
                    0,0,-2,
                    1,0,0,
                    -1,0,0,
                    0,1,0,
                    0,-1,0,
                    0,0,1,
                    -1,0,1,
                    -1,-1,1,
                    0,-1,1,
                    0,0,-1,
                    -1,0,-1,
                    -1,-1,-1,
                    0,-1,-1,
            };
        }
    }
    public static int[]CubicHoneyHood3DoddZ(boolean includeOrigin){
        if(includeOrigin){
            return new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    0,0,0,
                    0,0,2,
                    0,0,-2,
                    1,0,0,
                    -1,0,0,
                    0,1,0,
                    0,-1,0,
                    0,0,1,
                    1,0,1,
                    1,1,1,
                    0,1,1,
                    0,0,-1,
                    1,0,-1,
                    1,1,-1,
                    0,1,-1,
            };
        }else{
            return new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    0,0,2,
                    0,0,-2,
                    1,0,0,
                    -1,0,0,
                    0,1,0,
                    0,-1,0,
                    0,0,1,
                    1,0,1,
                    1,1,1,
                    0,1,1,
                    0,0,-1,
                    1,0,-1,
                    1,1,-1,
                    0,1,-1,
            };
        }
    }

    /**
     * Returns a coordinates array of all squares along the line between (x1,y1) and (x2,y2)
     */
    public static int[] AlongLineCoords(double x1, double y1, double x2, double y2) {
        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);

        int x = (int) (Math.floor(x1));
        int y = (int) (Math.floor(y1));

        int n = 1;
        int x_inc, y_inc;
        double error;

        if (dx == 0) {
            x_inc = 0;
            error = Double.MAX_VALUE;
        } else if (x2 > x1) {
            x_inc = 1;
            n += (int) (Math.floor(x2)) - x;
            error = (Math.floor(x1) + 1 - x1) * dy;
        } else {
            x_inc = -1;
            n += x - (int) (Math.floor(x2));
            error = (x1 - Math.floor(x1)) * dy;
        }

        if (dy == 0) {
            y_inc = 0;
            error -= Double.MAX_VALUE;
        } else if (y2 > y1) {
            y_inc = 1;
            n += (int) (Math.floor(y2)) - y;
            error -= (Math.floor(y1) + 1 - y1) * dx;
        } else {
            y_inc = -1;
            n += y - (int) (Math.floor(y2));
            error -= (y1 - Math.floor(y1)) * dx;
        }

        int[] writeHere = new int[n ^ 2];
        int Count = 0;
        for (; n > 0; --n) {
            writeHere[Count * 2] = (int) Math.floor(x);
            writeHere[Count * 2 + 1] = (int) Math.floor(y);
            Count++;

            if (error > 0) {
                y += y_inc;
                error -= dx;
            } else {
                x += x_inc;
                error += dy;
            }
        }
        return writeHere;
    }

    /**
     * puts into Coords all squares along the line between (x1,y1) and (x2,y2)
     */
    public static int AlongLineCoords(double x1, double y1, double x2, double y2, int[] returnCoords) {

        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);

        int x = (int) (Math.floor(x1));
        int y = (int) (Math.floor(y1));

        int n = 1;
        int x_inc, y_inc;
        double error;

        if (dx == 0) {
            x_inc = 0;
            error = Double.MAX_VALUE;
        } else if (x2 > x1) {
            x_inc = 1;
            n += (int) (Math.floor(x2)) - x;
            error = (Math.floor(x1) + 1 - x1) * dy;
        } else {
            x_inc = -1;
            n += x - (int) (Math.floor(x2));
            error = (x1 - Math.floor(x1)) * dy;
        }

        if (dy == 0) {
            y_inc = 0;
            error -= Double.MAX_VALUE;
        } else if (y2 > y1) {
            y_inc = 1;
            n += (int) (Math.floor(y2)) - y;
            error -= (Math.floor(y1) + 1 - y1) * dx;
        } else {
            y_inc = -1;
            n += y - (int) (Math.floor(y2));
            error -= (y1 - Math.floor(y1)) * dx;
        }

        int Count = 0;
        for (; n > 0; --n) {
            returnCoords[Count * 2] = (int) Math.floor(x);
            returnCoords[Count * 2 + 1] = (int) Math.floor(y);
            Count++;

            if (error > 0) {
                y += y_inc;
                error -= dx;
            } else {
                x += x_inc;
                error += dy;
            }
        }
        return Count;
    }

    /**
     * executes the provided action function using as argument the coordinates of all squares along the line between
     * (x1,y1) and (x2,y2)
     */
    public static void AlongLineAction(double x1, double y1, double x2, double y2, Coords2DAction Action) {
        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);

        int x = (int) (Math.floor(x1));
        int y = (int) (Math.floor(y1));

        int n = 0;
        int x_inc, y_inc;
        double error;

        if (dx == 0) {
            x_inc = 0;
            error = Double.MAX_VALUE;
        } else if (x2 > x1) {
            x_inc = 1;
            n += (int) (Math.floor(x2)) - x;
            error = (Math.floor(x1) + 1 - x1) * dy;
        } else {
            x_inc = -1;
            n += x - (int) (Math.floor(x2));
            error = (x1 - Math.floor(x1)) * dy;
        }

        if (dy == 0) {
            y_inc = 0;
            error -= Double.MAX_VALUE;
        } else if (y2 > y1) {
            y_inc = 1;
            n += (int) (Math.floor(y2)) - y;
            error -= (Math.floor(y1) + 1 - y1) * dx;
        } else {
            y_inc = -1;
            n += y - (int) (Math.floor(y2));
            error -= (y1 - Math.floor(y1)) * dx;
        }

        int Count = 0;
        for (; n > 0; --n) {
            Action.Action((int) Math.floor(x), (int) Math.floor(y));
            Count++;

            if (error > 0) {
                y += y_inc;
                error -= dx;
            } else {
                x += x_inc;
                error += dy;
            }
        }
    }

    /**
     * generates a neighborhood with the set of all coordinates in the circle with center 0,0 and the given radius.
     */
    static public int[] CircleHood(boolean includeOrigin, double radius) {
        double distSq = radius * radius;
        int min = (int) Math.floor(-radius);
        int max = (int) Math.ceil(radius);
        int[] retLong = new int[((max + 1 - min) * (max + 1 - min)) * 2];
        int ct = 0;
        if (includeOrigin) {
            ct++;
            retLong[0] = 0;
            retLong[1] = 0;
        }
        for (int x = min; x <= max; x++) {
            for (int y = min; y <= max; y++) {
                if (x*x+y*y <= distSq) {
                    if (x == 0 && y == 0) {
                        continue;
                    }
                    retLong[ct * 2] = x;
                    retLong[ct * 2 + 1] = y;
                    ct++;
                }
            }
        }
        int[] ret = new int[ct * 3];
        System.arraycopy(retLong, 0, ret, ct, ct * 2);
        return ret;
    }

    /**
     * generates a spherical neighborhood with the set of all coordinates in the sphere with center 0,0,0 and the given radius
     */
    static public int[]SphereHood(boolean includeOrigin, double radius) {
        double distSq = radius * radius;
        int min = (int) Math.floor(-radius);
        int max = (int) Math.ceil(radius);
        int[] retLong = new int[((max + 1 - min) * (max + 1 - min) * (max + 1 - min)) * 3];
        int ct = 0;
        if (includeOrigin) {
            ct++;
            retLong[0] = 0;
            retLong[1] = 0;
            retLong[2] = 0;
        }
        for (int x = min; x <= max; x++) {
            for (int y = min; y <= max; y++) {
                for (int z = min; z <= max; z++) {
                    if (x * x + y * y + z * z < distSq) {
                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }
                        retLong[ct * 3] = x;
                        retLong[ct * 3 + 1] = y;
                        retLong[ct * 3 + 2] = z;
                        ct++;
                    }
                }
            }
        }
        int[] ret = new int[ct * 4];
        System.arraycopy(retLong, 0, ret, ct, ct * 3);
        return ret;
    }


    /**
     * Returns the coordinates of all squares whose centers lie within a rectangle of the provided radius, centered on
     * (0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0)
     * @param radX          the radius of the rectangle in the xDim direction
     * @param radY          the radius of the rectangle in the yDim direction
     * @return coordinates returned as an array of the form [xDim,yDim,xDim,yDim,...]
     */
    static public int[] RectangleHood(boolean includeOrigin, int radX, int radY) {
        //returns a square with a center location at 0,0
        int[] dataIn;
        int iCoord;
        int nCoords = (radX * 2 + 1) * (radY * 2 + 1);
        if (includeOrigin) {
            iCoord = 1;
        } else {
            nCoords--;
            iCoord = 0;
        }
        dataIn = new int[nCoords * 3];
        for (int x = -radX; x <= radX; x++) {
            for (int y = -radY; y <= radY; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                dataIn[iCoord * 2+nCoords] = x;
                dataIn[iCoord * 2 + 1+nCoords] = y;
                iCoord++;
            }
        }
        return dataIn;
    }

    /**
     * generates a 1D neighborhood from a set of coordinates, with space for the results of mapping
     */
    static public int[] GenHood1D(int[] coords) {
        int isStart = coords.length;
        int[] ret = new int[coords.length + isStart];
        System.arraycopy(coords, 0, ret, isStart, coords.length);
        return ret;
    }

    /**
     * generates a 2D neighborhood from a set of coordinates, with space for the results of mapping
     */
    static public int[] GenHood2D(int[] coords) {
        if (coords.length % 2 != 0) {
            throw new IllegalArgumentException("2D coords list must be divisible by 2");
        }
        int isStart = coords.length / 2;
        int[] ret = new int[coords.length + isStart];
        System.arraycopy(coords, 0, ret, isStart, coords.length);
        return ret;
    }

    /**
     * generates a 3D neighborhood from a set of coordinates, with space for the results of mapping
     */
    static public int[] GenHood3D(int[] coords) {
        if (coords.length % 3 != 0) {
            throw new IllegalArgumentException("3D coords list must be divisible by 3");
        }
        int isStart = coords.length / 3;
        int[] ret = new int[coords.length + isStart];
        System.arraycopy(coords, 0, ret, isStart, coords.length);
        return ret;
    }

    public static long Log2(long n){
        long y,v;
        if (n < 0) {
            throw new IllegalArgumentException("cannot take long of negative input");
        }
        v=n;
        y=-1;
        while(v>0){
            v>>=1;
            y++;
        }
        return y;
    }

    //computes log base 2 using bit shifting
    public static int Log2(int n){
        int y,v;
        if (n < 0) {
            throw new IllegalArgumentException("cannot take long of negative input");
        }
        v=n;
        y=-1;
        while(v>0){
            v>>=1;
            y++;
        }
        return y;
    }

    /**
     * Factorial of a positive integer, uses FactorialSplit
     *
     * @param n 0 or a natural number
     * @return Factorial of toFact. Factorial(0) is 1
     *
     * possibly refactor with lround(exp(lgamma(n+1)))
     */

    public static long Factorial(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial input cannot be negative");
        }
        if(n<2){
            return 1;
        }
        long[]currentN=new long[]{1};//rare break from tradition
        long log2n=Log2(n);
        long p=1,r=1,h=0,shift=0,high=1;
        while(h!=n){
            shift+=h;
            h=n>>log2n--;
            long len=high;
            high=(h-1)|1;
            len=(high-len)/2;
            if(len>0){
                p*=FactInternal(len,currentN);
                r*=p;
            }
        }
        return r<<shift;
    }

    //todo fix or get rid of this, factorial, and BinomialDistPMF
    public static long NchooseK(long n,long k){
        if(n<0||n<k){
            throw new IllegalArgumentException("n and k must be > 0 and k must be <= n");
        }
        if(k==0||n==k){return 1;}
        if((Factorial(k)==0)||(Factorial(n-k))==0||(Factorial(k)*Factorial(n-k))==0){
            System.out.println("here");
        }
        return Factorial(n)/((Factorial(k)*Factorial(n-k)));
    }

    public static double BinomialDistPDF(long n, double p, long k){
        return NchooseK(n,k)*Math.pow(p,k)*Math.pow(1-p,n-k);
    }

    private static long FactInternal(long n, long[] currentN){
        long m=n/2;
        if (m==0){return currentN[0]+=2;}
        if(m==2){return (currentN[0]+=2)*(currentN[0]+=2);}
        return FactInternal(n-m,currentN)*FactInternal(m,currentN);
    }

    /**
     * Computes the probability of a specific average from a poisson distribution
     *
     * @param sampleSize How many times the event happens
     * @param avg        The average number of times the event happens
     * @return the probability of toSamp many events
     */
    public static double PoissonProb(int sampleSize, double avg) {
        return Math.pow(Math.E, -avg) * Math.pow(avg, sampleSize) / Factorial(sampleSize);
    }

    public static double GaussianPDF(double pos){
        return Math.exp(-pos*pos / 2) / Math.sqrt(2 * Math.PI);
    }

    public static double GaussianPDF(double mean, double std, double pos){
        return GaussianPDF((pos-mean)/std)/std;
    }

//    public static double BinomialDistPDF(long n,double p, long pos){
//        Math.l
//    }

    /**
     * transforms val with a sigmoid curve with the given properties
     *
     * @param val         the input value
     * @param stretch     linearly scales the sigmoid curve in the x dimension, the default is 1
     * @param inflectionX the point at which the slope changes sign
     * @param minCap      the minimum return value of the sigmoid function
     * @param maxCap      the maximum return value of the sigmoid function
     */
    public static double Sigmoid(double val, double stretch, double inflectionX, double minCap, double maxCap) {
        return minCap + ((maxCap - minCap)) / (1.0 + Math.exp(((-val) + inflectionX) / stretch));
    }

    /**
     * transforms val with a sigmoid curve with a minCap of 0, a maxCap of 1, and an inflectionX value of 0
     *
     * @param val     the input value
     * @param stretch linearly scales the sigmoid curve in the x dimension, the default is 1 (gets close to y=1 at around x=4)
     */
    public static double Sigmoid(double val, double stretch) {
        return 1 / (1.0 + Math.exp(-val / stretch));
    }



//    /**
//     * returns the number of heads from nTrials coin flips, where successProb is the probability of heads
//     */
//    public static int BinomialNaive(double successProb, int nTrials, Random rn) {
//        int ret = 0;
//        for (int iTrial = 0; iTrial < nTrials; iTrial++) {
//            if (rn.nextDouble() < successProb) {
//                ret++;
//            }
//        }
//        return ret;
//    }


    /**
     * sets the values in the array such that they sum to 1
     */
    public static double SumTo1(double[] vals) {
        double tot = 0;
        for (int i = 0; i < vals.length; i++) {
            tot += vals[i];
        }
        for (int i = 0; i < vals.length; i++) {
            vals[i] = vals[i] / tot;
        }
        return tot;
    }

    /**
     * sets the values in the array such that they sum to 1, using only the numbers between start and end
     */
    public static double SumTo1(double[] vals, int start, int end) {
        double tot = 0;
        for (int i = start; i < end; i++) {
            tot += vals[i];
        }
        for (int i = start; i < end; i++) {
            vals[i] = vals[i] / tot;
        }
        return tot;
    }

    /**
     * creates directories to ensure that the path argument exists
     */
    public static boolean MakeDirs(String path) {
        File dir = new File(path);
        return dir.mkdirs();
    }

    public static boolean IsPath(String path){
        File f=new File(path);
        if(f.isFile()||f.isDirectory()){
            return true;
        }
        return false;
    }

//    public static double Gaussian()

    /**
     * uses the Michaelis Menten equation to compute the reaction rate for a given substrate concentration
     *
     * @param conc         concentration of the reaction limiting substrate
     * @param maxRate      reaction rate given maximum concentration
     * @param halfRateConc substrate concentration at which the reaction rate is 1/2 the maximum
     * @return the reaction rate at the given substrate concentration
     */
    public static double MichaelisMenten(final double conc, final double maxRate, final double halfRateConc) {
        if (conc > 0) {
            return (maxRate * conc) / (halfRateConc + conc);
        }
        return 0;
    }

    public static double HillEqn(final double conc, final double dissociationRate, final double hillCoef) {
        if (conc > 0) {
            double adj_conc = Math.pow(conc, hillCoef);
            return adj_conc / (dissociationRate + adj_conc);
        }
        return 0;
    }

    /**
     * gets the euclidean distance between (x1,y1) and (x2,y2)
     */
    public static double Dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(DistSquared(x1, y1, x2, y2));
    }

    /**
     * gets the euclidean distance between (x1,y1) and (x2,y2), assuming boundaries 0,xDim and 0,yDim, and wraparound as
     * given in the arguments
     */
    public static double Dist(double x1, double y1, double x2, double y2, double xDim, double yDim, boolean wrapX, boolean wrapY) {
        return Math.sqrt(DistSquared(x1, y1, x2, y2, xDim, yDim, wrapX, wrapY));
    }

    /**
     * gets the euclidean distance between (x1,y1,z1) and (x2,y2,z2)
     */
    public static double Dist(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(DistSquared(x1, y1, z1, x2, y2, z2));
    }

    /**
     * gets the euclidean distance between (x1,y1,z1) and (x2,y2,z2), assuming boundaries (0,xDim),(0,yDim), and (0,zDim) and wraparound as
     * given in the arguments
     */
    public static double Dist(double x1, double y1, double z1, double x2, double y2, double z2, int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ) {
        return Math.sqrt(DistSquared(x1, y1, z1, x2, y2, z2, xDim, yDim, zDim, wrapX, wrapY, wrapZ));
    }

    /**
     * similar to dist above, but returns the distance squared
     */
    public static double DistSquared(double x1, double y1, double x2, double y2) {
        double xDist = x2 - x1, yDist = y2 - y1;
        return xDist * xDist + yDist * yDist;
    }

    /**
     * similar to dist above, but returns the distance squared
     */
    public static double DistSquared(double x1, double y1, double x2, double y2, double xDim, double yDim, boolean wrapX, boolean wrapY) {
        double xDist, yDist;
        if (wrapX) {
            xDist = DispWrap(x2, x1, xDim);
        } else {
            xDist = x2 - x1;
        }
        if (wrapY) {
            yDist = DispWrap(y2, y1, yDim);
        } else {
            yDist = y2 - y1;
        }
        return xDist * xDist + yDist * yDist;
    }

    /**
     * similar to dist above, but returns the distance squared
     */
    public static double DistSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        double xDist = x2 - x1, yDist = y2 - y1, zDist = z2 - z1;
        return xDist * xDist + yDist * yDist + zDist * zDist;
    }

    /**
     * similar to dist above, but returns the distance squared
     */
    public static double DistSquared(double x1, double y1, double z1, double x2, double y2, double z2, int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ) {
        double xDist, yDist, zDist;
        if (wrapX) {
            xDist = DispWrap(x2, x1, xDim);
        } else {
            xDist = x2 - x1;
        }
        if (wrapY) {
            yDist = DispWrap(y2, y1, yDim);
        } else {
            yDist = y2 - y1;
        }
        if (wrapZ) {
            zDist = DispWrap(z2, z1, zDim);
        } else {
            zDist = z2 - z1;
        }
        return xDist * xDist + yDist * yDist + zDist * zDist;
    }

    /**
     * returns the distance squared between the two position provided in any number of dimensions
     *
     * @param p1 the coordinates of the first position
     * @param p2 the coordinates of the second position
     * @return the distance squared between the first and second position
     */
    public static double DistSquared(double[] p1, double[] p2) {
        double sum = 0;
        for (int i = 0; i < p1.length; i++) {
            double diff = p1[i] - p2[i];
            sum += diff * diff;
        }
        return sum;
    }

    /**
     * returns the norm or "length" of the vector (v1,v2)
     */
    public static double Norm(double v1, double v2) {
        return Math.sqrt((v1 * v1) + (v2 * v2));
    }

    /**
     * returns the norm or "length" of the vector (v1,v2,v3)
     */
    public static double Norm(double v1, double v2, double v3) {
        return Math.sqrt((v1 * v1) + (v2 * v2) + (v3 * v3));
    }

    /**
     * returns the norm or "length" of the vector (v1,v2,v3,v4)
     */
    public static double Norm(double v1, double v2, double v3, double v4) {
        return Math.sqrt((v1 * v1) + (v2 * v2) + (v3 * v3) + (v4 * v4));
    }

    /**
     * returns the norm or "length" of the vector array
     */
    public static double Norm(double[] vals) {
        double tot = 0;
        for (double val : vals) {
            tot += val * val;
        }
        return Math.sqrt(tot);
    }

    /**
     * normalizes the vector array in place so that its norm is 1
     */
    public static void Normalize(double[] vals) {
        double norm = Norm(vals);
        for (int i = 0; i < vals.length; i++) {
            vals[i] = vals[i] / norm;
        }
    }

    /**
     * returns the norm or "length" of the vector (v1,v2) squared
     */
    public static double NormSquared(double v1, double v2) {
        return (v1 * v1) + (v2 * v2);
    }

    /**
     * returns the norm or "length" of the vector (v1,v2,v3) squared
     */
    public static double NormSquared(double v1, double v2, double v3) {
        return (v1 * v1) + (v2 * v2) + (v3 * v3);
    }

    /**
     * returns the norm or "length" of the vector (v1,v2,v3,v4) squared
     */
    public static double NormSquared(double v1, double v2, double v3, double v4) {
        return (v1 * v1) + (v2 * v2) + (v3 * v3) + (v4 * v4);
    }

    /**
     * returns the norm or "length" of the vector array squared
     */
    public static double NormSquared(double[] vals) {
        double tot = 0;
        for (double val : vals) {
            tot += val * val;
        }
        return tot;
    }

    /**
     * gets the point at which the line from (x1,y1) to (x2,y2) intersects with the line from (x3,y3) and (x4,y4) and
     * puts the coordinates in retCoords. returns whether the lines intersect
     */
    public static boolean InfiniteLinesIntersection2D(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double[] retCoords) {
        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (Math.abs(denom) - DOUBLE_EPSILON <= 0) {
            return false;
        } else {
            retCoords[0] = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / denom;
            retCoords[1] = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / denom;
            return true;
        }
    }

    /**
     * returns the evenly spaced coordinates along the edge of a circle in 2D, centered on (0,0)
     */
    public static float[] GenCirclePoints(final float rad, final int nCorners) {
        float[] ret = new float[nCorners * 2 + 2];
        double step = (Math.PI * 2) / (nCorners);
        double pos = 0;
        for (int i = 0; i < nCorners + 1; i++) {
            ret[i * 2] = (float) Math.cos(pos) * rad;
            ret[i * 2 + 1] = (float) Math.sin(pos) * rad;
            pos += step;
        }
        return ret;
    }

    /**
     * computes the minimum distance between p1 and p2, using wraparound if it is shorter
     *
     * @param p1
     * @param p2
     * @param dim
     * @return
     */
    public static double DispWrap(double p1, double p2, double dim) {
        if (Math.abs(p1 - p2) > dim / 2) {
            if (p1 > p2) {
                p2 = p2 + dim;
            } else {
                p1 = p1 + dim;
            }
        }
        return p2-p1;
    }


    /**
     * returns the original value bounded by min and max inclusive
     */
    public static double Bound(double val, double min, double max) {
        return val < min ? min : (val > max ? max : val);
    }

    /**
     * returns the original value bounded by min and max inclusive
     */
    public static long Bound(long val, long min, long max) {
        return val < min ? min : (val > max ? max : val);
    }

    /**
     * returns the original value bounded by min and max inclusive
     */
    public static int Bound(int val, int min, int max) {
        return val < min ? min : (val > max ? max : val);
    }

    /**
     * returns the original value bounded by min and max inclusive
     */
    public static float Bound(float val, double min, double max) {
        return (float) (val < min ? min : (val > max ? max : val));
    }

    /**
     * interpolates value from 0 to 1 to be between min and max
     */
    public static double ScaleMinToMax(double val, double min, double max) {
        return val * (max - min) + min;
    }

    /**
     * returns where the value is from min to max as a number from 0 to 1
     */
    public static double Scale0to1(double val, double min, double max) {
        return (val - min) / (max - min);
    }

    /**
     * rescales the value from being between oldMin and oldMax to being between newMin and newMax
     */
    public static double Rescale(double val, double oldMin, double oldMax, double newMin, double newMax) {
        return ScaleMinToMax(Scale0to1(val, oldMin, oldMax), newMin, newMax);
    }
    public static double RescaleBounded(double val, double oldMin, double oldMax, double newMin, double newMax) {
        return Bound(ScaleMinToMax(Scale0to1(val, oldMin, oldMax), newMin, newMax),newMin,newMax);
    }

    /**
     * returns value with wraparound between 0 and max
     */
    public static int Wrap(int val, int max) {
        int mod=val%max;
        return mod<0?max+mod:mod;
    }

    /**
     * returns value with wraparound between 0 and max
     */
    public static double Wrap(double val, double max) {
        double mod=val%max;
        return mod<0?max+mod:mod;
    }

    /**
     * converts proton concentration to pH
     */
    public static double ProtonsToPh(double protonConc) {
        return -Math.log10(protonConc) + 3;
    }

    /**
     * converts pH to proton concentration
     */
    public static double PhToProtons(double ph) {
        return Math.pow(10, 3.0 - ph);
    }

    /**
     * adjusts probability that an event will occur in 1 unit of time to the probability that the event will occur at
     * least once over the duration
     *
     * @param prob     probability that an event occurs in 1 unit of time
     * @param duration duration in units of time over which event may occur
     * @return the probability that the event will occur at least once over the duration
     */
    public static double ProbScale(double prob, double duration) {
        return 1.0f - (Math.pow(1.0 - prob, duration));

    }

    /**
     * gets the probability density value for a position in a normal distribution with given mean and standard deviation
     */

    /**
     * returns a timestamp of the form "yyyy_MM_dd_HH_mm_ss" as a string
     */
    static public String TimeStamp() {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

    }

    /**
     * gets the current working directory as a string
     */
    static public String PWD() {
        return Paths.get("").toAbsolutePath().toString() + "/";
        //return System.getProperty("user.dir");
    }

    /**
     * gets information about the memory usage and max memory allocated for the program
     */
    public static String MemoryUsageStr() {
        int mb = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        return "Used Memory: " + (rt.totalMemory() - rt.freeMemory()) / mb + " mb, Free Momory: " + rt.freeMemory() / mb + " mb, Total Memory:" + rt.totalMemory() / mb + " mb, GetMax  Memory: " + rt.maxMemory() / mb + " mb";
    }

    //writes into the passed array [used memory,free memory,total memory,max memory] all in mb.
    public static void MemoryUsageInfo(long[] ret) {
        int mb = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        ret[0] = rt.totalMemory() - rt.freeMemory() / mb;
        ret[1] = rt.freeMemory() / mb;
        ret[2] = rt.totalMemory() / mb;
        ret[3] = rt.maxMemory() / mb;
    }

    /**
     * Runs quicksort on an object that implements Sortable
     *
     * @param sortMe          the object to be sorted
     * @param greatestToLeast if true, sorting will be form greatest to least, otherwise will be least to greatest
     */
    public static <T extends Sortable> void QuickSort(T sortMe, boolean greatestToLeast) {
        SortHelper(sortMe, 0, sortMe.Length() - 1, greatestToLeast);
    }


    /**
     * returns whether the input value is between 0 and the dimension value
     */
    public static boolean InDim(int Val, int Dim) {
        return Val >= 0 && Val < Dim;
    }

    /**
     * returns whether the input value is between 0 and the dimension value
     */
    public static boolean InDim(double Val, double Dim) {
        return Val >= 0 && Val < Dim;
    }

    /**
     * subsets a set of indices such that all indices evaluate to true under the EvalFun
     *
     * @param hood       a neighborhood or set of indices
     * @param lenToCheck the number of indices to subset
     * @param EvalFun    function should return true for all indices to keep
     */
    public static int SubsetIndices(int[] hood, int lenToCheck, IndexBool EvalFun) {
        int validCt = 0;
        for (int i = 0; i < lenToCheck; i++) {
            if (EvalFun.Eval(hood[i])) {
                hood[validCt] = hood[i];
                validCt++;
            }
        }
        return validCt;
    }

    /**
     * finds the area of overlap between 2 circles of equal radii
     */
    public static double CircleOverlapArea(double radii,double centerDist){
        return 2*radii*radii*Math.acos(centerDist/(2*radii))-0.5*Math.sqrt(centerDist*centerDist*(2*radii-centerDist)*(2*radii+centerDist));
    }
    public static double CircleOverlapArea(double r1,double r2,double centerDist){
        double d=centerDist;
        double dsq=d*d;
        double r1sq=r1*r1;
        double r2sq=r2*r2;
        double term1=r1sq*Math.acos((dsq+r1sq-r2sq)/(2*d*r1));
        double term2=r2sq*Math.acos((dsq+r2sq-r1sq)/(2*d*r2));
        double term3=0.5*Math.sqrt((-d+r1+r2)*(d+r1-r2)*(d-r1+r2)*(d+r1+r2));
        return term1+term2-term3;
    }


    /**
     * Creates a thread pool and
     * launches a total of nRun threads, with nThreads running simultaneously at a time. the RunFun that is passed in
     * must be a void function that takes an integer argument. when the function is called, this integer will be the
     * index of that particular run in the lineup. This can be used to assign the result of many runs to a single array,
     * for example, if the array is written to once by each RunFun at its run index. If you want to run many simulations
     * simultaneously, this function is for you.
     */
    public static void MultiThread(int nRuns, int nThreads, ParallelFunction RunFun) {
        ArrayList<SweepRun> runners = new ArrayList<>(nRuns);
        for (int i = 0; i < nRuns; i++) {
            runners.add(new SweepRun(RunFun, i));
        }
        ExecutorService exec = Executors.newFixedThreadPool(nThreads);
        for (SweepRun run : runners) {
            exec.execute(run);
        }
        exec.shutdown();
        while (!exec.isTerminated()) ;
    }

    /**
     * does the same thing as MultiThread above, but generates a number of threads equal to the number of available processors
     * @param nRuns
     * @param RunFun
     */
    public static void MultiThread(int nRuns, ParallelFunction RunFun) {
        MultiThread(nRuns, Runtime.getRuntime().availableProcessors(), RunFun);
    }


    /**
     * Saves a model state to a byte array and returns it. The model must implement the SerializableModel interface
     */
    public static byte[] SaveState(SerializableModel model) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(model);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String AwaitInput(){
        if(inputReader==null){
            inputReader=new Scanner(System.in);
        }
        return inputReader.nextLine();
    }

    /**
     * Saves a model state to a file with the name specified. creates a new file or overwrites one if the file already exists. The model must implement the SerializableModel interface
     */
    public static void SaveState(SerializableModel model, String stateFileName) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(model);
            out.flush();
            bos.writeTo(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(stateFileName, false))));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     *  Loads a model from a file created with SaveState. The model must implement the SerializableModel interface
     */
    public static <T extends SerializableModel> T LoadState(String stateBytesFile) {
        return LoadState(StateFromFile(stateBytesFile));
    }

    /**
     * Loads a model form a byte array created with SaveState. The model must implement the SerializableModel interface
     */
    public static <T extends SerializableModel> T LoadState(byte[] state) {
        ByteArrayInputStream bis = new ByteArrayInputStream(state);
        ObjectInput in = null;
        SerializableModel ret = null;
        try {
            in = new ObjectInputStream(bis);
            ret = (SerializableModel) in.readObject();
            ret.SetupConstructors();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return (T) ret;
    }

    //REFLECTION
    public static boolean IsMethodOverridden(Class derived,Class base,String methodName){
        Method[] meths=derived.getDeclaredMethods();
        Method[] baseMeths=base.getDeclaredMethods();
        for (Method meth : meths) {
            if(meth.getName().equals(methodName)) {
                return true;
            }
        }
        boolean found=false;
        for (Method meth : baseMeths) {
            if(meth.getName().equals(methodName)) {
                found=true;
                break;
            }
        }
        if(!found) {
            throw new IllegalArgumentException("name "+methodName+" not found in base class "+base.getName()+"!");
        }
        return false;
    }

    public static<T,O extends T> boolean IsMethodOverridden(Class<O> derived,String methodName){
        Method[] meths=derived.getDeclaredMethods();
        for (Method meth : meths) {
            if(meth.getName().equals(methodName)) {
                return true;
            }
        }
        return false;
    }
    public static double[] MatVecMul(final double[] mat,final double[] vec,double[]out){
        final int nCols=vec.length;
        final int nRows=mat.length/nCols;
        for (int y = 0; y < nRows; y++) {
            out[y]=mat[y*nCols]*vec[0];
            for (int x = 1; x < nCols; x++) {
                out[y]+=mat[y*nCols+x]*vec[x];
            }
        }
        return out;
    }
    public static void MatrixToCSV(double[][]mat,String filePath){
        FileIO out=new FileIO(filePath,"w");
        for (double[]row : mat) {
            out.Write(ArrToString(row,",")+"\n");
        }
        out.Close();
    }

    public static void MatrixToCSV(long[][]mat,String filePath){
        FileIO out=new FileIO(filePath,"w");
        for (long[]row : mat) {
            out.Write(ArrToString(row,",")+"\n");
        }
        out.Close();
    }
    public static void MatrixToCSV(int[][]mat,String filePath){
        FileIO out=new FileIO(filePath,"w");
        for (int[]row : mat) {
            out.Write(ArrToString(row,",")+"\n");
        }
        out.Close();
    }

    //Py4j byte array functions

    static public byte[] Py4jDoublesOut(double[]doubles){
        ByteBuffer out= ByteBuffer.allocate(Double.BYTES*doubles.length);
        out.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < doubles.length; i++) {
            out.putDouble(doubles[i]);
        }
        return out.array();
    }
    static public byte[] Py4jDoublesOut(double[][]doubles){
        int maxLen=0;
        for(int i=0;i<doubles.length;i++){
            maxLen=Math.max(doubles[i].length,maxLen);
        }
        ByteBuffer out=ByteBuffer.allocate(Double.BYTES*doubles.length*maxLen);
        out.order(ByteOrder.LITTLE_ENDIAN);
        for(int j=0;j<doubles.length;j++){
            double[] row=doubles[j];
            for(int i=0;i<maxLen;i++){
                if(row.length>i){
                    out.putDouble(row[i]);
                }else{
                    out.putDouble(0);//pad missing entries
                }
            }
        }
        return out.array();
    }
//    static public byte[] Py4jDoublesOut(ArrayList<double[]> doubles){
//        int maxLen=0;
//        for (int i = 0; i < doubles.size(); i++) {
//            maxLen=Math.max(doubles.get(i).length,maxLen);
//        }
//        ByteBuffer out= ByteBuffer.allocate(Double.BYTES* doubles.size() *maxLen);
//        out.order(ByteOrder.LITTLE_ENDIAN);
//        for (int j = 0; j < doubles.size(); j++) {
//            double[] row= doubles.get(j);
//            for (int i = 0; i < maxLen; i++) {
//                if(row.length>i) {
//                    out.putDouble(row[i]);
//                }else{
//                    out.putDouble(0);
//                }
//            }
//        }
//        return out.array();
//    }

//    static public ArrayList<double[]> Py4jDoublesInAsArrayList(byte[] in,int nRows) {
//        int length = in.length / Double.BYTES;
//        ArrayList<double[]> out = new ArrayList<>(nRows);
//        ByteBuffer buf = ByteBuffer.wrap(in);
//        buf.order(ByteOrder.LITTLE_ENDIAN);
//        for (int i = 0; i < nRows; i++) {
//            out.add(new double[length/nRows]);
//            double[] row = out.get(out.size()-1);
//            for (int j = 0; j < row.length; j++) {
//                row[j] = buf.getDouble();
//            }
//        }
//        return out;
//    }
    static public double[][] Py4jDoublesIn(byte[] in,int nRows) {
        int length = in.length / Double.BYTES;
        double[][] out = new double[nRows][length / nRows];
        ByteBuffer buf = ByteBuffer.wrap(in);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < out.length; i++) {
            double[] row = out[i];
            for (int j = 0; j < row.length; j++) {
                row[j] = buf.getDouble();
            }
        }
        return out;
    }
    static public double[] Py4jDoublesIn(byte[] in){
        int length=in.length/Double.BYTES;
        double[]out=new double[length];
        ByteBuffer buf=ByteBuffer.wrap(in);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < length; i++) {
            out[i]=buf.getDouble();
        }
        return out;
    }

    static public void DoublesToCSV(String path,double[][]data,String[]headers){
        FileIO out=new FileIO(path,"w");
        out.Write(ArrToString(headers,",")+"\n");
        for(double[] d: data){
            out.Write(ArrToString(d,",")+"\n");
        }
        out.Close();
    }
    static public void DoublesToCSV(String path,double[][]data){
        FileIO out=new FileIO(path,"w");
        for(double[] d: data){
            out.Write(ArrToString(d,",")+"\n");
        }
        out.Close();
    }

    static int InterpComp(double val, int minComp, int maxComp) {
        return (int) ((maxComp - minComp) * val) + minComp;
    }

    static <T extends Sortable> void SortHelper(T sortMe, int lo, int hi, boolean greatestToLeast) {
        if (lo < hi) {
            int p = Partition(sortMe, lo, hi, greatestToLeast);
            SortHelper(sortMe, lo, p - 1, greatestToLeast);
            SortHelper(sortMe, p + 1, hi, greatestToLeast);
        }
    }

    static <T extends Sortable> int Partition(T sortMe, int lo, int hi, boolean greatestToLeast) {
        if (greatestToLeast) {
            for (int j = lo; j < hi; j++) {
                if (sortMe.Compare(hi, j) <= 0) {
                    sortMe.Swap(lo, j);
                    lo++;
                }
            }
            sortMe.Swap(lo, hi);
            return lo;
        } else {
            for (int j = lo; j < hi; j++) {
                if (sortMe.Compare(hi, j) >= 0) {
                    sortMe.Swap(lo, j);
                    lo++;
                }
            }
            sortMe.Swap(lo, hi);
            return lo;
        }
    }

    static public void Sort(IsEntry1Before2 Compare, SwapEntries Swap, int length){
            _SortHelper(Compare, Swap, 0, length - 1);
        }

        static private void _SortHelper(IsEntry1Before2 Compare, SwapEntries Swap, int lo, int hi) {
            if (lo < hi) {
                int p = _Partition(Compare,Swap, lo, hi);
                _SortHelper(Compare, Swap, lo, p - 1);
                _SortHelper(Compare, Swap, p + 1, hi);
            }
        }

        static private int _Partition(IsEntry1Before2 Compare, SwapEntries Swap, int lo, int hi) {
                for (int j = lo; j < hi; j++) {
                    if (!Compare.Compare(hi, j)) {
                        Swap.Swap(lo, j);
                        lo++;
                    }
                }
                Swap.Swap(lo, hi);
                return lo;
        }

    static byte[] StateFromFile(String stateBytesFile) {
        Path path = Paths.get(stateBytesFile);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    static double[][]ReadCSVDoubles(String filename){
        FileIO io=new FileIO(filename,"r");
        ArrayList<double[]> data=io.ReadDoubles(",");
        double[][]out=new double[data.size()][];
        for(int i=0;i<out.length;i++){
            out[i]=data.get(i);
        }
        return out;
    }

    static int[][]ReadCSVInts(String filename){
        FileIO io=new FileIO(filename,"r");
        ArrayList<int[]> data=io.ReadInts(",");
        int[][]out=new int[data.size()][];
        for(int i=0;i<out.length;i++){
            out[i]=data.get(i);
        }
        return out;
    }

    /**
     * draws a colorbar on a UIGrid
     * @param barHere colorbar will be drawn on this UIGrid. Leave 34 pixels of clearance on the right of the bar to ensure that all text will fit.
     * @param xBottomLeft x position of bottom left corner of bar
     * @param yBottomLeft y position of bottom left corner of bar
     * @param barWidth width of bar in pixels
     * @param barHeight height of bar in pixels (bar will actually be barHeight-4 pixels tall, to make room for tick labels)
     * @param min min value to display on bar
     * @param max max value to display on bar
     * @param ticks number of tick labels to display on bar
     * @param Color color function that generates bar colors
     */
    public static void DrawColorBar(UIGrid barHere,int xBottomLeft,int yBottomLeft,int barWidth,int barHeight,double min,double max,int ticks,DoubleToColor Color){
        double colorstep=(max-min)/(barHeight-4);
        for(int y=0;y<barHeight-4;y++){
            int color=Color.GenColor(y*colorstep+min);
            for(int x=0;x<barWidth;x++){
                barHere.SetPix(x+xBottomLeft,y+2+yBottomLeft,color);
            }
        }
        double tickStep=(max-min)/(ticks-1);
        double tickPix=(barHeight-5)*1.0/(ticks-1);
        for(int i=0;i<ticks;i++){
            barHere.SetPix(barWidth+xBottomLeft,(int)(i*tickPix)+2+yBottomLeft,RED);
            barHere.SetPix(barWidth+1+xBottomLeft,(int)(i*tickPix)+2+yBottomLeft,RED);
            barHere.SetString(fmt.format(min+tickStep*i),barWidth+2+xBottomLeft,(int)(i*tickPix)+5+yBottomLeft,WHITE,BLACK);
        }
    }
    public static void ChangeColorBarMinMax(UIGrid barHere,int xBottomLeft,int yBottomLeft,int barWidth,int barHeight,double min,double max,int ticks){
        double tickStep=(max-min)/(ticks-1);
        double tickPix=(barHeight-5)*1.0/(ticks-1);
        for(int i=0;i<ticks;i++){
            barHere.SetPix(barWidth+xBottomLeft,(int)(i*tickPix)+2+yBottomLeft,RED);
            barHere.SetPix(barWidth+1+xBottomLeft,(int)(i*tickPix)+2+yBottomLeft,RED);
            barHere.SetString(String.format("%-8s",fmt.format(min+tickStep*i)),barWidth+2+xBottomLeft,(int)(i*tickPix)+5+yBottomLeft,WHITE,BLACK);
        }

    }

    public static Grid2Dint PNGtoGrid(String filename) throws IOException {
        // set up Grid2dint to store RGB values from PNG:
        BufferedImage image = ImageIO.read(new FileInputStream(filename));
        Grid2Dint RGBgrid = new Grid2Dint(image.getWidth(),image.getHeight());

        // loop through all pixels:
        for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) {
            for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) {
                // subtract from yDim so picture is right-side-up:
                RGBgrid.Set(xPixel,RGBgrid.yDim-yPixel-1,image.getRGB(xPixel, yPixel));
            }
        }

        return RGBgrid;
    }



//    static public int[] RingHood(double innerRadius,double outerRadius) {
//        if(innerRadius<=outerRadius){
//            throw new IllegalArgumentException("inner radius must be less than outer radius");
//        }
//        double distSqInner = innerRadius * innerRadius;
//        double distSqOuter = outerRadius * outerRadius;
//        int min = (int) Math.floor(-outerRadius);
//        int max = (int) Math.ceil(outerRadius);
//        int[] retLong = new int[((max + 1 - min) * (max + 1 - min)) * 2];
//        int ct = 0;
//        for (int x = min; x <= max; x++) {
//            for (int y = min; y <= max; y++) {
//                double distSq= Util.DistSquared(0, 0, x, y);
//                if (distSq >= distSqInner&&distSq<=distSqOuter) {
//                    retLong[ct * 2] = x;
//                    retLong[ct * 2 + 1] = y;
//                    ct++;
//                }
//            }
//        }
//        int[] ret = new int[ct * 2];
//        System.arraycopy(retLong, 0, ret, 0, ret.length);
//        return ret;
//    }
//    static public int RingHood(double innerRadius,double outerRadius, int[] returnCoords) {
//        if(innerRadius<=outerRadius){
//            throw new IllegalArgumentException("inner radius must be less than outer radius");
//        }
//        double distSqInner = innerRadius * innerRadius;
//        double distSqOuter = outerRadius * outerRadius;
//        int min = (int) Math.floor(-outerRadius);
//        int max = (int) Math.ceil(outerRadius);
//        int ct = 0;
//        for (int x = min; x <= max; x++) {
//            for (int y = min; y <= max; y++) {
//                double distSq= Util.DistSquared(0, 0, x, y);
//                if (distSq >= distSqInner&&distSq<=distSqOuter) {
//                    returnCoords[ct * 2] = x;
//                    returnCoords[ct * 2 + 1] = y;
//                    ct++;
//                }
//            }
//        }
//        return ct;
//    }
    //    public static <T extends AgentPT2D,G extends AgentGrid2D<T>> void GetAgentsRadApprox(G searchMe, final ArrayList<T> putHere, final double x, final double y, final double rad, boolean wrapX, boolean wrapY){
//        putHere.clear();
//        int nAgents;
//        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
//            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
//                int retX=xSq; int retY=ySq;
//                boolean inX=Util.InDim(searchMe.xDim,retX);
//                boolean inY=Util.InDim(searchMe.yDim,retY);
//                if((!wrapX&&!inX)||(!wrapY&&!inY)){
//                    continue;
//                }
//                if(wrapX&&!inX){
//                    retX=Util.Wrap(retX,searchMe.xDim);
//                }
//                if(wrapY&&!inY){
//                    retY=Util.Wrap(retY,searchMe.yDim);
//                }
//                searchMe.GetAgents(putHere,searchMe.I(retX,retY));
//            }
//        }
//    }
//
//    public static <T extends AgentPhys2,Q extends AgentPhys2,G extends AgentGrid2D<Q>>double CollisionSum2D(T agent,G searchMe, final ArrayList<Q> putAgentsHere,RadToForceMap ForceFun,double searchRad,boolean wrapX,boolean wrapY){
//        double ret=0;
//        putAgentsHere.clear();
//        GetAgentsRadApprox(searchMe,putAgentsHere,agent.Xpt(),agent.Ypt(),searchRad,wrapX,wrapY);
//        for (Q a : putAgentsHere) {
//            if(a!=agent){
//                double xComp=wrapX?DispWrap(agent.Xpt(), a.Xpt(), searchMe.xDim):a.Xpt()-agent.Xpt();
//                double yComp=wrapY?DispWrap(agent.Ypt(),a.Ypt(),searchMe.yDim):a.Ypt()-agent.Ypt();
//                double dist=Math.sqrt(xComp*xComp+yComp*yComp)-(agent.radius+a.radius);
//                double force=ForceFun.DistToForce(dist);
//                agent.AddForce(xComp,yComp,force);
//                ret+=force;
//            }
//        }
//        return ret;
//    }
}

