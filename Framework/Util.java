package Framework;

import Framework.Extensions.PDEGrid2DCoarse;
import Framework.GridsAndAgents.Agent2DBase;
import Framework.GridsAndAgents.Agent3DBase;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Interfaces.*;
import Framework.Tools.SerializableModel;
import Framework.Tools.SweepRun;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * A collection of helpful static utility functions
 * recommended import: import static Util.*
 * Created by rafael on 10/11/16.
 */
public final class Util {

    public static double DOUBLE_EPSILON = 2.22E-16;

    //COLOR MAKERS
    public static int RGB(double r, double g, double b) {
        int ri = Bound((int) (r * 256), 0, 255);
        int gi = Bound((int) (g * 256), 0, 255);
        int bi = Bound((int) (b * 256), 0, 255);
        return (0xff000000) | (ri << 16) | (gi << 8) | (bi);
    }

    public static int RGBA(double r, double g, double b, double a) {
        int ri = Bound((int) (r * 256), 0, 255);
        int gi = Bound((int) (g * 256), 0, 255);
        int bi = Bound((int) (b * 256), 0, 255);
        int ai = Bound((int) (a * 256), 0, 255);
        return (ai << 24) | (ri << 16) | (gi << 8) | (bi);
    }

    public static int RGB256(int r, int g, int b) {
        r = Bound(r, 0, 255);
        g = Bound(g, 0, 255);
        b = Bound(b, 0, 255);
        return (0xff000000) | (r << 16) | (g << 8) | (b);
    }

    public static int RGBA256(int r, int g, int b, int a) {
        r = Bound(r, 0, 255);
        g = Bound(g, 0, 255);
        b = Bound(b, 0, 255);
        a = Bound(a, 0, 255);
        return (a << 24) | (r << 16) | (g << 8) | (b);
    }

    //COMPONENT GETTERS
    public static int GetRed256(int color) {
        return (color & 0x00ff0000) >> 16;
    }

    public static int GetGreen256(int color) {
        return (color & 0x0000ff00) >> 8;
    }

    public static int GetBlue256(int color) {
        return color & 0x000000ff;
    }

    public static int GetAlpha256(int color) {
        return color >>> 24;
    }

    public static double GetRed(int color) {
        return ((color & 0x00ff0000) >> 16) / 255.0;
    }

    public static double GetGreen(int color) {
        return ((color & 0x0000ff00) >> 8) / 255.0;
    }

    public static double GetBlue(int color) {
        return (color & 0x000000ff) / 255.0;
    }

    public static double GetAlpha(int color) {
        return (color >>> 24) / 255.0;
    }

    //COMPONENT SETTERS
    public static int SetRed(int color, double r) {
        int ri = Bound((int) (r * 256), 0, 255);
        return color & 0xff00ffff | (ri << 16);
    }

    public static int SetGreen(int color, double g) {
        int gi = Bound((int) (g * 256), 0, 255);
        return color & 0xffff00ff | (gi << 8);
    }

    public static int SetBlue(int color, double b) {
        int bi = Bound((int) (b * 256), 0, 255);
        return color & 0xffffff00 | (bi);
    }

    public static int SetAlpha(int color, double a) {
        a = Math.max(a, 0);
        int ai = Bound((int) (a * 256), 0, 255);
        return color & 0x00ffffff | (ai << 24);
    }

    public static int SetRed256(int color, int r) {
        r = Bound(r, 0, 255);
        return color & 0xff00ffff | (r << 16);
    }

    public static int SetGreen256(int color, int g) {
        g = Bound(g, 0, 255);
        return color & 0xffff00ff | (g << 8);
    }

    public static int SetBlue256(int color, int b) {
        b = Bound(b, 0, 255);
        return color & 0xffffff00 | (b);
    }

    public static int SetAlpha256(int color, int a) {
        a = Bound(a, 0, 255);
        return color & 0x00ffffff | (a << 24);
    }
    final public static int RED = RGB(1,0,0), GREEN= RGB(0,1,0), BLUE=RGB(0,0,1), BLACK=RGB(0,0,0), WHITE=RGB(1,1,1), YELLOW=RGB(1,1,0), CYAN=RGB(0,1,1), MAGENTA=RGB(1,0,1);

    //OTHER COLOR GENERATORS
    final private static int CC0 = RGB256(56, 116, 177), CC1 = RGB256(198, 56, 44), CC2 = RGB256(79, 159, 57), CC3 = RGB256(189, 190, 58), CC4 = RGB256(142, 102, 186), CC5 = RGB256(240, 134, 39), CC6 = RGB256(83, 187, 206), CC7 = RGB256(214, 123, 191), CC8 = RGB256(133, 88, 76), CC9 = RGB256(178, 197, 230), CC10 = RGB256(243, 156, 151), CC11 = RGB256(166, 222, 144), CC12 = RGB256(220, 220, 147), CC13 = RGB256(194, 174, 211), CC14 = RGB256(246, 191, 126), CC15 = RGB256(169, 216, 228), CC16 = RGB256(238, 184, 209), CC17 = RGB256(190, 157, 146), CC18 = RGB256(199, 199, 199), CC19 = RGB256(127, 127, 127);

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

    //add backend for inactivation?
    public static int HeatMapRGB(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c1, c2, c3);
    }

    public static int HeatMapRBG(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c1, c3, c2);
    }

    public static int HeatMapGRB(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c2, c1, c3);
    }

    public static int HeatMapGBR(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c3, c1, c2);
    }

    public static int HeatMapBRG(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c2, c3, c1);
    }

    public static int HeatMapBGR(double val) {
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c3, c2, c1);
    }

    public static int HeatMapRGB(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c1, c2, c3);
    }

    public static int HeatMapRBG(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c1, c3, c2);
    }

    public static int HeatMapGRB(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c2, c1, c3);
    }

    public static int HeatMapGBR(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c3, c1, c2);
    }

    public static int HeatMapBRG(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c2, c3, c1);
    }

    public static int HeatMapBGR(double val, double min, double max) {
        val = Scale0to1(val, min, max);
        double c1 = val * 4;
        double c2 = (val - 0.25) * 2;
        double c3 = (val - 0.75) * 4;
        return RGB(c2, c3, c1);
    }
    public static String ColorString(int color){
        return "r: "+GetRed256(color)+", g: "+GetGreen256(color)+", b: "+GetBlue256(color)+", a: "+GetAlpha256(color);
    }


    //TODO check alpha value of return
    public static int HSBColor(double hue, double saturation, double brightness) {
        return Color.HSBtoRGB((float) hue, (float) saturation, (float) brightness);
    }
    public static void ColorToHSB(int color,float[]ret) {
        Color.RGBtoHSB(GetRed256(color),GetGreen256(color),GetBlue256(color),ret);
    }

    public static int YCbCrColor(double y, double cb, double cr) {
        double yd = (double) (Bound(y * 256, 0, 255));
        double cbd = (double) (Bound(cb * 256, 0, 255));
        double crd = (double) (Bound(cr * 256, 0, 255));
        int r = (int) (yd + 1.402 * (crd - 128));
        int g = (int) (yd - 0.344136 * (cbd - 128) - 0.714136 * (crd - 128));
        int b = (int) (yd + 1.772 * (cbd - 128));
        return RGB256(r, g, b);
    }

    public static int CbCrPlaneColor(double x, double y) {
        double ycbcrY = 0.5;
        return YCbCrColor(ycbcrY, x, y);
    }

    public static double ArrayMax(double[]arr){
        double max=Double.MIN_VALUE;
        for (double val : arr) {
            max=max<val?val:max;
        }
        return max;
    }
    public static int ArrayMax(int[]arr){
        int max=Integer.MIN_VALUE;
        for (int val : arr) {
            max=max<val?val:max;
        }
        return max;
    }
    public static double ArrayMin(double[]arr){
        double min=Double.MIN_VALUE;
        for (double val : arr) {
            min=min>val?min:val;
        }
        return min;
    }
    public static int ArrayMin(int[]arr){
        int max=Integer.MIN_VALUE;
        for (int val : arr) {
            max=max<val?max:val;
        }
        return max;
    }
    public static double ArraySum(double[] arr) {
        double sum = 0;
        for (double val : arr) {
            sum += val;
        }
        return sum;
    }

    public static int ArraySum(int[] arr) {
        int sum = 0;
        for (int val : arr) {
            sum += val;
        }
        return sum;
    }
    public static long ArraySum(long[] arr) {
        long sum = 0;
        for (long val : arr) {
            sum += val;
        }
        return sum;
    }

    /**
     * prints an array to a string
     *
     * @param arr   array to be printed
     * @param delim the delimiter used to separate entries
     * @param <T>   the type of the buf entries in the array
     */
    public static <T> String ArrToString(T[] arr, String delim) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[arr.length - 1]);
        return sb.toString();
    }

    public static <T> String ArrToString(T[] arr, String delim, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end - 1; i++) {
            sb.append(arr[i] + delim);
        }
        sb.append(arr[end - 1]);
        return sb.toString();
    }
    public static double Interpolate2D(double bottomLeft, double bottomRight, double topLeft, double topRight, double x, double y){
        if(x<0||x>1||y<0||y>1){
            throw new IllegalArgumentException("x and y for interpolation must be between 0 and 1 x: "+x+" y: "+y);
        }
        double bottom =(bottomRight-bottomLeft)*x+bottomLeft;
        double top =(topRight-topLeft)*x+topLeft;
        return (top-bottom)*y+bottom;
    }

    public static int GetBit(int v, int i) {
        return v >> i % 1;
    }

    public static long GetBit(long v, int i) {
        return v >> i % 1;
    }

    public static int FlipBit(int v, int i) {
        return v ^ (1 << i);
    }

    public static long FlipBit(long v, int i) {
        return v ^ (1 << i);
    }

    public static int SetBit(int v, int i, boolean val) {
        return val ? v | (1 << i) : v & ~(1 << i);
    }

    /**
     * prints an array
     *
     * @param arr   array to be printed
     * @param delim the delimiter used to separate entries
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
     * prints an array
     *
     * @param arr   array to be printed
     * @param delim the delimiter used to separate entries
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
     *
     * @param <T> the type of the input and output arrays
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
     *
     * @param <T> the type of the inputs and output array
     */
    public static <T> T[] Append(T[] arr, T appendMe) {
        int firstLen = arr.length;
        T[] ret = (T[]) Array.newInstance(arr.getClass().getComponentType(), firstLen + 1);
        System.arraycopy(arr, 0, ret, 0, firstLen);
        ret[firstLen] = appendMe;
        return ret;
    }

    public static int[] GenIndicesArray(int nEntries) {
        int indices[] = new int[nEntries];
        for (int i = 0; i < nEntries; i++) {
            indices[i] = i;
        }
        return indices;
    }


    /**
     * returns the mean value of the provided array
     */
    static public double Mean(double[] a) {
        double tot = 0;
        for (int i = 0; i < a.length; i++) {
            tot += a[i];
        }
        return tot / a.length;
    }


    /**
     * Returns the coordinates defining the Von Neumann neighborhood centered on (0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [xDim,yDim,xDim,yDim...]
     */
    public static int[] VonNeumannHood(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0,0,0,0,0,
                    0, 0, 1, 0, -1, 0, 0, 1, 0, -1};
        } else {
            return new int[]{0,0,0,0,
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
            return new int[]{0,0,0,0,0,0,0,0,0,
                    0, 0, 1, 1, 1, 0, 1, -1, 0, -1, -1, -1, -1, 0, -1, 1, 0, 1};
        } else {
            return new int[]{0,0,0,0,0,0,0,0,
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
            return new int[]{0,0,0,0,0,0,0,
                    0, 0, 1, 1, 1, 0, 1, -1, 0, -1, -1, 0, 0, 1};
        } else {
            return new int[]{0,0,0,0,0,0,
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
            return new int[]{0,0,0,0,0,0,0,
                    0, 0, 1, 0, 0, -1, -1, -1, -1, 0, -1, 1, 0, 1};
        } else {
            return new int[]{0,0,0,0,0,0,
                    1, 0, 0, -1, -1, -1, -1, 0, -1, 1, 0, 1};
        }
    }

    /**
     * Returns the coordinates defining the Triangular neighborhood for even xDim, even yDim or oddx, odd yDim. centered on (0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [xDim,yDim,xDim,yDim,...]
     */
    public static int[] TriangleHoodSameParity(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0,0,0,0,
                    0, 0, -1, 0, 1, 0, 0, 1};
        } else {
            return new int[]{0,0,0,
                    -1, 0, 1, 0, 0, 1};
        }
    }

    /**
     * Returns the coordinates defining the Triangular neighborhood for even xDim, odd yDim or oddx, even yDim. centered on (0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [xDim,yDim,xDim,yDim,...]
     */
    public static int[] TriangleHoodDifParity(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0,0,0,0,
                    0, 0, -1, 0, 1, 0, 0, -1};
        } else {
            return new int[]{0,0,0,
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
            return new int[]{0,0,0,0,0,0,0,
                    0, 0, 0, 1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1};
        } else {
            return new int[]{0,0,0,0,0,0,
                    1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1};
        }
    }

    public int[] MooreHood3D(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    0, 0, 0,
                    0, 0, 1,
                    0, 0, -1,
                    1, 0, 0,
                    1, 0, 1,
                    1, 0, -1,
                    1, 1, 0,
                    1, 1, 1,
                    1, 1, -1,
                    0, 1, 0,
                    0, 1, 1,
                    0, 1, -1,
                    -1, 0, 0,
                    -1, 0, 1,
                    -1, 0, -1,
                    -1, 1, 0,
                    -1, 1, 1,
                    -1, 1, -1,
                    -1, -1, 0,
                    -1, -1, 1,
                    -1, -1, -1,
                    0, -1, 0,
                    0, -1, 1,
                    0, -1, -1,
                    1, -1, 0,
                    1, -1, 1,
                    1, -1, -1,
            };
        } else {
            return new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    0, 0, 1,
                    0, 0, -1,
                    1, 0, 0,
                    1, 0, 1,
                    1, 0, -1,
                    1, 1, 0,
                    1, 1, 1,
                    1, 1, -1,
                    0, 1, 0,
                    0, 1, 1,
                    0, 1, -1,
                    -1, 0, 0,
                    -1, 0, 1,
                    -1, 0, -1,
                    -1, 1, 0,
                    -1, 1, 1,
                    -1, 1, -1,
                    -1, -1, 0,
                    -1, -1, 1,
                    -1, -1, -1,
                    0, -1, 0,
                    0, -1, 1,
                    0, -1, -1,
                    1, -1, 0,
                    1, -1, 1,
                    1, -1, -1,
            };
        }
    }

    /**
     * Returns an array of all squares touching a line between the positions provided
     *
     * @param x1 the xDim coordinate of the starting position
     * @param y1 the yDim coordinate of the starting position
     * @param x2 the xDim coordinate of the ending position
     * @param y2 the yDim coordinate of the ending position
     * @return coordinates return as an array of the form [xDim,yDim,xDim,yDim,...]
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
            writeHere[Count * 2+1] = (int) Math.floor(y);
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

//    /**
//     * Returns an array of all squares touching a line between the positions provided
//     *
//     * @param x1 the xDim coordinate of the starting position
//     * @param y1 the yDim coordinate of the starting position
//     * @param x2 the xDim coordinate of the ending position
//     * @param y2 the yDim coordinate of the ending position
//     * @return coordinates return as an array of the form [xDim,yDim,xDim,yDim,...]
//     */
//    public static int AlongLineCoords(double x1, double y1, double x2, double y2, int[] returnCoords) {
//        double dx = Math.abs(x2 - x1);
//        double dy = Math.abs(y2 - y1);
//
//        int x = (int) (Math.floor(x1));
//        int y = (int) (Math.floor(y1));
//
//        int n = 1;
//        int x_inc, y_inc;
//        double error;
//
//        if (dx == 0) {
//            x_inc = 0;
//            error = Double.MAX_VALUE;
//        } else if (x2 > x1) {
//            x_inc = 1;
//            n += (int) (Math.floor(x2)) - x;
//            error = (Math.floor(x1) + 1 - x1) * dy;
//        } else {
//            x_inc = -1;
//            n += x - (int) (Math.floor(x2));
//            error = (x1 - Math.floor(x1)) * dy;
//        }
//
//        if (dy == 0) {
//            y_inc = 0;
//            error -= Double.MAX_VALUE;
//        } else if (y2 > y1) {
//            y_inc = 1;
//            n += (int) (Math.floor(y2)) - y;
//            error -= (Math.floor(y1) + 1 - y1) * dx;
//        } else {
//            y_inc = -1;
//            n += y - (int) (Math.floor(y2));
//            error -= (y1 - Math.floor(y1)) * dx;
//        }
//
//        int Count = 0;
//        for (; n > 0; --n) {
//            returnCoords[Count * 2] = (int) Math.floor(x);
//            returnCoords[Count * 2] = (int) Math.floor(y);
//            Count++;
//
//            if (error > 0) {
//                y += y_inc;
//                error -= dx;
//            } else {
//                x += x_inc;
//                error += dy;
//            }
//        }
//        return Count;
//    }
    public static void AlongLineAction(double x1, double y1, double x2, double y2, CoordsAction Action) {
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
            Action.Action((int) Math.floor(x),(int) Math.floor(y));
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
     * Returns the coordinates of all squares whose centers lie within a circle of the provided radius, centered on (0,0)
     *
     * @param includeOrigin defines whether to include the origin (0,0)
     * @param radius        the radius of the circle
     * @return coordinates returned as an array of the form [xDim,yDim,xDim,yDim,...]
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
                if (Util.DistSquared(0, 0, x, y) <= distSq) {
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
        System.arraycopy(retLong, 0, ret, ct, ct*2);
        return ret;
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

    /**
     * Returns the coordinates of all squares whose centers lie within a rectangle of the provided radius, centered on (0,0)
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
        int nCoords=(radX * 2 + 1) * (radY * 2 + 1);
        if (includeOrigin) {
            dataIn = new int[nCoords * 3];
            iCoord = 1;
        } else {
            dataIn = new int[nCoords * 3 - 3];
            iCoord = 0;
        }
        for (int x = -radX; x <= radX; x++) {
            for (int y = -radY; y <= radY; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                dataIn[iCoord * 2] = x;
                dataIn[iCoord * 2 + 1] = y;
                iCoord++;
            }
        }
        return dataIn;
    }
    static public int[]GenHood1D(int[]coords){
        int isStart=coords.length;
        int[]ret=new int[coords.length+isStart];
        System.arraycopy(coords,0,ret,isStart,coords.length);
        return ret;
    }
    static public int[]GenHood2D(int[]coords){
        if(coords.length%2!=0){
            throw new IllegalArgumentException("2D coords list must be divisible by 2");
        }
        int isStart=coords.length/2;
        int[]ret=new int[coords.length+isStart];
        System.arraycopy(coords,0,ret,isStart,coords.length);
        return ret;
    }
    static public int[]GenHood3D(int[]coords){
        if(coords.length%3!=0){
            throw new IllegalArgumentException("3D coords list must be divisible by 3");
        }
        int isStart=coords.length/3;
        int[]ret=new int[coords.length+isStart];
        System.arraycopy(coords,0,ret,isStart,coords.length);
        return ret;
    }

    static public int RectangleHood(boolean includeOrigin, int radX, int radY, int[] returnCoords) {
        //returns a square with a center location at 0,0
        int nCoord;
        if (includeOrigin) {
            returnCoords[0] = 0;
            returnCoords[1] = 0;
            nCoord = 1;
        } else {
            returnCoords = new int[(radX * 2 + 1) * (radY * 2 + 1) * 2 - 1];
            nCoord = 0;
        }
        for (int x = -radX; x <= radX; x++) {
            for (int y = -radY; y <= radY; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                returnCoords[nCoord * 2] = x;
                returnCoords[nCoord * 2 + 1] = y;
                nCoord++;
            }
        }
        return nCoord;
    }

    /**
     * Factorial of a positive integer
     * @param toFact 0 or a natural number
     * @return Factorial of toFact. Factorial(0) is 1
     */
    public static int Factorial(int toFact) {
        if (toFact < 0) {
            throw new IllegalArgumentException("Factorial input cannot be negative");
        }
        int ret = 1;
        for (int i = 1; i <= toFact; i++) {
            ret *= i;
        }
        return ret;
    }

    /**
     * Samples a Poisson distribution, giving the probability of toSamp many
     * events assuming a Poisson distribution
     * @param sampleSize How many times the event happens
     * @param avg The average number of times the event happens
     * @return the probability of toSamp many events
     */
    public static double PoissonProb(int sampleSize, double avg) {
        return Math.pow(Math.E, -avg) * Math.pow(avg, sampleSize) / Factorial(sampleSize);
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
     *
     * @param vals an array of values
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

    public static boolean MakeDirs(String path) {
        File dir = new File(path);
        return dir.mkdirs();
    }

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

    public static double Dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(DistSquared(x1, y1, x2, y2));
    }
    public static double DistSqaured(Agent2DBase a1,Agent2DBase a2){
        return DistSquared(a1.Xpt(), a1.Ypt(), a2.Xpt(), a2.Ypt());
    }
    public static double Dist(Agent2DBase a1,Agent2DBase a2){
        return Dist(a1.Xpt(), a1.Ypt(), a2.Xpt(), a2.Ypt());
    }
    public static double DistSquared(Agent3DBase a1, Agent3DBase a2){
        return DistSquared(a1.Xpt(), a1.Ypt(),a1.Zpt(), a2.Xpt(), a2.Ypt(),a2.Zpt());
    }
    public static double Dist(Agent3DBase a1, Agent3DBase a2){
        return Dist(a1.Xpt(), a1.Ypt(),a1.Zpt(), a2.Xpt(), a2.Ypt(),a2.Zpt());
    }

    public static double Dist(double x1, double y1, double x2, double y2, double xDim, double yDim, boolean wrapX, boolean wrapY) {
        return Math.sqrt(DistSquared(x1, y1, x2, y2, xDim, yDim, wrapX, wrapY));
    }

    public static double Dist(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(DistSquared(x1, y1, z1, x2, y2, z2));
    }

    public static double Dist(double x1, double y1, double z1, double x2, double y2, double z2, int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ) {
        return Math.sqrt(DistSquared(x1, y1, z1, x2, y2, z2, xDim, yDim, zDim, wrapX, wrapY, wrapZ));
    }

    /**
     * returns the distance squared between the two position provided in 2D
     *
     * @param x1 the xDim coordinate of the first position
     * @param y1 the yDim coordinate of the first position
     * @param x2 the xDim coordinate of the second position
     * @param y2 the yDim coordinate of the second position
     * @return the distance squared between the first and second position
     */
    public static double DistSquared(double x1, double y1, double x2, double y2) {
        double xDist = x2 - x1, yDist = y2 - y1;
        return xDist * xDist + yDist * yDist;
    }

    public static double DistSquared(double x1, double y1, double x2, double y2, double xDim, double yDim, boolean wrapX, boolean wrapY) {
        double xDist, yDist;
        if (wrapX) {
            xDist = DistWrap(x1, x2, xDim);
        } else {
            xDist = x2 - x1;
        }
        if (wrapY) {
            yDist = DistWrap(y1, y2, yDim);
        } else {
            yDist = y2 - y1;
        }
        return xDist * xDist + yDist * yDist;
    }

    /**
     * @param x1 the xDim coordinate of the first position
     * @param y1 the yDim coordinate of the first position
     * @param x2 the xDim coordinate of the second position
     * @param y2 the yDim coordinate of the second position
     * @return the distance squared between the first and second position
     */
    public static double DistSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        double xDist = x2 - x1, yDist = y2 - y1, zDist = z2 - z1;
        return xDist * xDist + yDist * yDist + zDist * zDist;
    }

    public static double DistSquared(double x1, double y1, double z1, double x2, double y2, double z2, int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ) {
        double xDist, yDist, zDist;
        if (wrapX) {
            xDist = DistWrap(x1, x2, xDim);
        } else {
            xDist = x2 - x1;
        }
        if (wrapY) {
            yDist = DistWrap(y1, y2, yDim);
        } else {
            yDist = y2 - y1;
        }
        if (wrapZ) {
            zDist = DistWrap(z1, z2, zDim);
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


    public static double Norm(double v1, double v2) {
        return Math.sqrt((v1 * v1) + (v2 * v2));
    }

    public static double Norm(double v1, double v2, double v3) {
        return Math.sqrt((v1 * v1) + (v2 * v2) + (v3 * v3));
    }

    public static double Norm(double v1, double v2, double v3, double v4) {
        return Math.sqrt((v1 * v1) + (v2 * v2) + (v3 * v3) + (v4 * v4));
    }

    public static double Norm(double[] vals) {
        double tot = 0;
        for (double val : vals) {
            tot += val * val;
        }
        return Math.sqrt(tot);
    }
    public static void Normalize(double[]vals){
        double norm=Norm(vals);
        for (int i = 0; i < vals.length; i++) {
            vals[i]=vals[i]/norm;
        }
    }

    public static double NormSq(double v1, double v2) {
        return (v1 * v1) + (v2 * v2);
    }

    public static double NormSq(double v1, double v2, double v3) {
        return (v1 * v1) + (v2 * v2) + (v3 * v3);
    }

    public static double NormSq(double v1, double v2, double v3, double v4) {
        return (v1 * v1) + (v2 * v2) + (v3 * v3) + (v4 * v4);
    }
    public static double NormSq(double [] vals) {
        double tot = 0;
        for (double val : vals) {
            tot += val * val;
        }
        return tot;
    }

    /**
     * x1,y1 and x2,y2 are from the first line, x3,y3 and x4,y4 are from the second line
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
//                    retX=Util.ModWrap(retX,searchMe.xDim);
//                }
//                if(wrapY&&!inY){
//                    retY=Util.ModWrap(retY,searchMe.yDim);
//                }
//                searchMe.GetAgents(putHere,searchMe.I(retX,retY));
//            }
//        }
//    }
//
    public static double DistWrap(double p1, double p2, double dim) {
        if (Math.abs(p2 - p1) > dim / 2) {
            if (p1 > p2) {
                p2 = p2 + dim;
            } else {
                p2 = p2 - dim;
            }
        }
        return p2 - p1;
    }

//    public static <T extends AgentPhys2,Q extends AgentPhys2,G extends AgentGrid2D<Q>>double CollisionSum2D(T agent,G searchMe, final ArrayList<Q> putAgentsHere,RadToForceMap ForceFun,double searchRad,boolean wrapX,boolean wrapY){
//        double ret=0;
//        putAgentsHere.clear();
//        GetAgentsRadApprox(searchMe,putAgentsHere,agent.Xpt(),agent.Ypt(),searchRad,wrapX,wrapY);
//        for (Q a : putAgentsHere) {
//            if(a!=agent){
//                double xComp=wrapX?DistWrap(agent.Xpt(), a.Xpt(), searchMe.xDim):a.Xpt()-agent.Xpt();
//                double yComp=wrapY?DistWrap(agent.Ypt(),a.Ypt(),searchMe.yDim):a.Ypt()-agent.Ypt();
//                double dist=Math.sqrt(xComp*xComp+yComp*yComp)-(agent.radius+a.radius);
//                double force=ForceFun.DistToForce(dist);
//                agent.AddForce(xComp,yComp,force);
//                ret+=force;
//            }
//        }
//        return ret;
//    }

    /**
     * returns the original value bounded by min and max inclusive
     */
    public static double Bound(double val, double min, double max) {
        return val < min ? min : (val > max ? max : val);
    }
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

    public static double ScaleMinToMax(double val, double min, double max) {
        return val*(max-min)+min;
    }
    /**
     * returns where the value is from min to max as a number from 0 to 1
     */
    public static double Scale0to1(double val, double min, double max) {
        return (val - min) / (max - min);
    }
    public static double Rescale(double val, double oldMin, double oldMax, double newMin, double newMax) {
        return ScaleMinToMax(Scale0to1(val,oldMin,oldMax),newMin,newMax);
    }

    /**
     * returns value with wraparound between 0 and max
     */
    public static int ModWrap(int val, int max) {
        return val < 0 ? max + val % max : val % max;
    }

    /**
     * returns value with wraparound between 0 and max
     */
    public static double ModWrap(double val, double max) {
        return val < 0 ? max + val % max : val % max;
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
     * adjusts probability that an event will occur in 1 unit of time to the probability that the event will occur in timeFraction duration
     *
     * @param prob     probability that an event occurs in 1 unit of time
     * @param duration duration in units of time over which event may occur
     * @return the probability that the event will occur in timeFraction
     */
    public static double ProbScale(double prob, double duration) {
        return 1.0f - (Math.pow(1.0 - prob, duration));

    }

    //LIST FUNCTIONS
    public static double LogDist(double min, double max, Rand rn) {
        if (min <= 0 || max <= 0) {
            System.err.println("Error, LogDist contains range value < 0!");
        }
        double logMin = Math.log(min);
        double logMax = Math.log(max);
        double inVal = rn.Double() * (logMax - logMin) + logMin;
        return Math.exp(inVal);
    }


    /**
     * runs a fully connected neural network layer
     *
     * @param neurons      array of all neurons in the network
     * @param weights      array of weights in the fully connected layer
     * @param iFromStart   index of the start of the input to the fully connected layer
     * @param iFromEnd     index of the end of the input to the fully connected layer
     * @param iToStart     index of the start of the output of the fully connected layer
     * @param iToEnd       index of the end of the output of the fully connected layer
     * @param iWeightStart index of the start of the weights for this layer out of the weights array
     */
    public static void NNfullyConnectedLayer(double[] neurons, double[] weights, int iFromStart, int iFromEnd, int iToStart, int iToEnd, int iWeightStart) {
        int iWeight = iWeightStart;
        for (int iFrom = iFromStart; iFrom < iFromEnd; iFrom++) {
            for (int iTo = iToStart; iTo < iToEnd; iTo++) {
                neurons[iTo] += neurons[iFrom] * weights[iWeight];
                iWeight++;
            }
        }
    }
    public static void NNfullyConnectedLayer(double[] neurons, double[] weights, int iFromStart, int iFromEnd, int iToStart, int iToEnd, int iWeightStart, DoubleToDouble ActivationFunction) {
        int iWeight = iWeightStart;
        for (int iFrom = iFromStart; iFrom < iFromEnd; iFrom++) {
            for (int iTo = iToStart; iTo < iToEnd; iTo++) {
                neurons[iTo] += neurons[iFrom] * weights[iWeight];
                iWeight++;
            }
        }
        for (int iTo = iToStart; iTo < iToEnd; iTo++) {
            neurons[iTo]=ActivationFunction.DoubleToDouble(neurons[iTo]);
        }
    }

    /**
     * set all neurons between iStart and iEnd with the given value
     */
    public static void NNset(double[] neurons, int iStart, int iEnd, double val) {
        Arrays.fill(neurons, iStart, iEnd, val);
    }

    public static double Sigmoid(double val, double stretch, double inflectionValue, double minCap, double maxCap) {
        return minCap + ((maxCap - minCap)) / (1.0 + Math.exp(((-val) + inflectionValue) / stretch));
    }
    public static double Sigmoid(double val, double stretch) {
        return 1 / (1.0 + Math.exp(-val/stretch));
    }


    //UTILITIES

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
        ret[0]=rt.totalMemory()-rt.freeMemory()/mb;
        ret[1]=rt.freeMemory()/mb;
        ret[2]=rt.totalMemory()/mb;
        ret[3]=rt.maxMemory()/mb;
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

    /**
     * returns whether the input value is between 0 and the dimension value
     */
    public static boolean InDim(int Dim, int Val) {
        return Val >= 0 && Val < Dim;
    }

    /**
     * returns whether the input value is between 0 and the dimension value
     */
    public static boolean InDim(double Dim, double Val) {
        return Val >= 0 && Val < Dim;
    }

    public static double GradientX2D(double[] vals, int xDim, int yDim, int centerX, int centerY, boolean boundaryCond, double boundaryValue, boolean wrapX) {
        double xP1, xM1;
        if (InDim(xDim, centerX + 1)) {
            xP1 = vals[(centerX + 1) * yDim + centerY];
        } else if (boundaryCond) {
            xP1 = boundaryValue;
        } else if (wrapX) {
            xP1 = vals[(0) * yDim + centerY];
        } else {
            xP1 = vals[centerX * yDim + centerY];
        }
        if (InDim(xDim, centerX - 1)) {
            xM1 = vals[(centerX - 1) * yDim + centerY];
        } else if (boundaryCond) {
            xM1 = boundaryValue;
        } else if (wrapX) {
            xM1 = vals[(xDim - 1) * yDim + centerY];
        } else {
            xM1 = vals[centerX * yDim + centerY];
        }
        return xP1 - xM1;
    }

    public static double GradientY2D(double[] vals, int xDim, int yDim, int centerX, int centerY, boolean boundaryCond, double boundaryValue, boolean wrapY) {
        double yP1, yM1;
        if (InDim(yDim, centerY + 1)) {
            yP1 = vals[centerX * yDim + (centerY + 1)];
        } else if (boundaryCond) {
            yP1 = boundaryValue;
        } else if (wrapY) {
            yP1 = vals[centerX * yDim + (0)];
        } else {
            yP1 = vals[centerX * yDim + centerY];
        }
        if (InDim(yDim, centerY - 1)) {
            yM1 = vals[centerX * yDim + (centerY - 1)];
        } else if (boundaryCond) {
            yM1 = boundaryValue;
        } else if (wrapY) {
            yM1 = vals[centerX * yDim + (yDim - 1)];
        } else {
            yM1 = vals[centerX * yDim + centerY];
        }
        return yP1 - yM1;
    }

    //REFLECTION
    public static<T,O extends T> boolean IsMethodOverridden(Class<O> derived,Class<T> base,String methodName){
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

    //MULTITHREADING
    public static void MultiThread(int nRuns,ParallelFunction RunFun){
        MultiThread(nRuns,Runtime.getRuntime().availableProcessors(),RunFun);
    }

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


    //SAVING AND LOADING
    public static byte[] SaveState(SerializableModel model){
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutput out;
        try{
            out= new ObjectOutputStream(bos);
            out.writeObject(model);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try{
                bos.close();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }
    public static void SaveState(SerializableModel model,String stateFileName) {
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

    static byte[] StateFromFile(String stateBytesFile){
        Path path= Paths.get(stateBytesFile);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends SerializableModel> T LoadState(String stateBytesFile){
        return LoadState(StateFromFile(stateBytesFile));
    }
    public static <T extends SerializableModel> T LoadState(byte[] state){
        ByteArrayInputStream bis=new ByteArrayInputStream(state);
        ObjectInput in=null;
        SerializableModel ret=null;
        try{
            in=new ObjectInputStream(bis);
            ret= (SerializableModel) in.readObject();
            ret.SetupConstructors();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally{
            try{
                if(in!=null){
                    in.close();
                }
            }
            catch (IOException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return (T)ret;
    }


    //used to interpolate values over a PDEGrid2DCoarse, when normal lattice positions are 1/3 the length of PDEGridCoarse lattice positions
    //at some point it would be good to make a generic version of this function somehow
    private final static double interpInSide =2.0/3.0, interpOutSide =1.0/3.0, interpInCorner =4.0/9.0, interpOutCorner =5.0/18.0;
    static int InFallback(int val,int fallback,int dim){
        return(Util.InDim(dim,val))?val:fallback;
    }
    public static double GetInterp3x3(int xCell,int yCell,PDEGrid2DCoarse diff){
        PDEGrid2D g=diff.grid;
        final int xDiff=xCell/3;
        final int yDiff=yCell/3;
        final int xMod=xCell%3;
        final int yMod=yCell%3;
        switch (xMod) {
            case 0:
                switch (yMod) {
                    case 0://left bottom
                        return g.Get(xDiff, yDiff) * interpInCorner +
                                g.Get(InFallback(xDiff - 1, xDiff, g.xDim), yDiff) * interpOutCorner +
                                g.Get(xDiff, InFallback(yDiff - 1, yDiff, g.yDim)) * interpOutCorner;
                    case 1://left middle
                        return g.Get(xDiff, yDiff) * interpInSide + g.Get(InFallback(xDiff - 1, xDiff, g.xDim), yDiff) * interpOutSide;
                    case 2://left top
                        return g.Get(xDiff, yDiff) * interpInCorner +
                                g.Get(InFallback(xDiff - 1, xDiff, g.xDim), yDiff) * interpOutCorner +
                                g.Get(xDiff, InFallback(yDiff + 1, yDiff, g.yDim)) * interpOutCorner;
                    default: throw new IllegalStateException("mod calculation did not work!");
                }
            case 1:
                switch (yMod){
                    case 0://middle bottom
                        return g.Get(xDiff, yDiff) * interpInSide + g.Get(xDiff, InFallback(yDiff-1,yDiff,g.yDim)) * interpOutSide;
                    case 1://middle
                        return g.Get(xDiff,yDiff);
                    case 2://middle top
                        return g.Get(xDiff, yDiff) * interpInSide + g.Get(xDiff, InFallback(yDiff+1,yDiff,g.yDim)) * interpOutSide;
                    default: throw new IllegalStateException("mod calculation did not work!");
                }
            case 2:
                switch (yMod) {
                    case 0://right bottom
                        return g.Get(xDiff, yDiff) * interpInCorner +
                                g.Get(InFallback(xDiff + 1, xDiff, g.xDim), yDiff) * interpOutCorner +
                                g.Get(xDiff, InFallback(yDiff - 1, yDiff, g.yDim)) * interpOutCorner;
                    case 1://right middle
                        return g.Get(xDiff, yDiff) * interpInSide + g.Get(InFallback(xDiff + 1, xDiff, g.xDim), yDiff) * interpOutSide;
                    case 2://right top
                        return g.Get(xDiff, yDiff) * interpInCorner +
                                g.Get(InFallback(xDiff + 1, xDiff, g.xDim), yDiff) * interpOutCorner +
                                g.Get(xDiff, InFallback(yDiff + 1, yDiff, g.yDim)) * interpOutCorner;
                    default: throw new IllegalStateException("mod calculation did not work!");
                }

            default: throw new IllegalStateException("mod calculation did not work!");
        }
    }

}








