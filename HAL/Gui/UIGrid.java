package HAL.Gui;

import HAL.GridsAndAgents.*;
import HAL.Interfaces.*;
import HAL.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * a gui item that is used to efficiently visualize a grid in 2 dimensions
 * uses an array of pixels whose color values are individually set
 */
public class UIGrid implements Grid2D,GuiComp {
    final public int xDim;
    final public int yDim;
    final public int length;
    boolean active;
    final PaintPanel panel;
    public final int scale;
    public final int compX;
    public final int compY;
    final BufferedImage buff;
    protected BufferedImage scaledBuff;
    protected Graphics2D scaledG;
    final int[] data;
    TickTimer tickTimer = new TickTimer();


    /**
     * @param gridW       width of the UIGrid in pixels
     * @param gridH       height of the UIGrid in pixels
     * @param scaleFactor the width and height in screen pixels of each UIGrid pixel
     * @param compX       width on the gui GridBagLayout
     * @param compY       height on the gui GridBagLayout
     * @param active
     */
    public UIGrid(int gridW, int gridH, int scaleFactor, int compX, int compY, boolean active) {
        this.xDim=gridW;
        this.yDim=gridH;
        this.length=xDim*yDim;
        this.active = active;
        this.compX = compX;
        this.compY = compY;
        scale = scaleFactor;
        if (this.active) {
            buff = new BufferedImage(xDim, yDim, BufferedImage.TYPE_INT_ARGB);
            panel = new PaintPanel(buff, xDim, yDim, scaleFactor, scaleFactor);
            data = ((DataBufferInt) buff.getRaster().getDataBuffer()).getData();
            for (int i = 0; i < length; i++) {
                SetPix(i, Util.BLACK);
            }
        } else {
            panel = null;
            buff = null;
            data = null;
        }
    }

    public UIGrid(int gridW, int gridH, int scaleFactor, int compX, int compY) {
        this(gridW, gridH, scaleFactor, compX, compY, true);
    }

    /**
     * @param gridW       width of the UIGrid in pixels
     * @param gridH       height of the UIGrid in pixels
     * @param scaleFactor the width and height in screen pixels of each UIGrid pixel
     */
    public UIGrid(int gridW, int gridH, int scaleFactor, boolean active) {
        this(gridW, gridH, scaleFactor, 1, 1, active);
    }

    public UIGrid(int gridW, int gridH, int scaleFactor) {
        this(gridW, gridH, scaleFactor, 1, 1, true);
    }

    /**
     * call this once per step of your model, and the function will ensure that your model runs at the rate provided in
     * milliseconds. the function will take the amount time between calls into account to ensure a consistent tick
     * rate.
     */
    public void TickPause(int millis) {
        if (active) {
            tickTimer.TickPause(millis);
        }
    }
    /**
     * sets an individual pixel on the GridWindow. in the visualization the pixel will take up scaleFactor*scaleFactor
     * screen pixels.
     */
    public void SetPix(int x, int y, int color) {
        if (active) {
            data[(yDim - y - 1) * xDim + x] = color;
        }
    }

    /**
     * sets an individual pixel on the GridWindow. in the visualization the pixel will take up scaleFactor*scaleFactor
     * screen pixels.
     */
    public void SetPix(int i, int color) {
        if (active) {
            SetPix(i / yDim, i % yDim, color);
        }
    }

    /**
     * same functionality as SetPix with a color argument, but instead takes a ColorIntGenerator function (a function
     * that takes no arguments and returns an int). the reason to use this method is that when the gui is inactivated
     * the ColorIntGenerator function will not be called, which saves the computation time of generating the color.
     */
    public void SetPix(int x, int y, ColorIntGenerator ColorFunction) {
        if (active) {
            SetPix(x, y, ColorFunction.GenColorInt());
        }
    }

    /**
     * same functionality as SetPix with a color argument, but instead takes a ColorIntGenerator function (a function
     * that takes no arguments and returns an int). the reason to use this method is that when the gui is inactivated
     * the ColorIntGenerator function will not be called, which saves the computation time of generating the color.
     */
    public void SetPix(int i, ColorIntGenerator ColorFunction) {
        if (active) {
            SetPix(i, ColorFunction.GenColorInt());
        }
    }

    /**
     * returns the pixel color at that location as a colorInt
     */
    public int GetPix(int x, int y) {
        return data[(yDim - y - 1) * xDim + x];

    }

    /**
     * returns the pixel color at that location as a colorInt
     */
    public int GetPix(int i) {
        return GetPix(i / yDim, i % yDim);
    }

    /**
     * sets all pixels to a single color.
     */
    public void Clear(int color) {
        if (active) {
            Arrays.fill(data, color);
        }
    }

    /**
     * sets a rectangular section of the UIGrid using the ColorFn argument
     */
    public void SetRect(int xStart, int yStart, int xLen, int yLen, Coords2DColor ColorFn) {
        for (int x = 0; x < xLen; x++) {
            for (int y = 0; y < yLen; y++) {
                SetPix(x + xStart, y + yStart, ColorFn.SetPix(x, y));
            }
        }
    }

    /**
     * adds another UIGrid as an overlay to compose with the main UIGrid. alpha blending will be used to combine them.
     */
    public void AddAlphaGrid(UIGrid overlay) {
        this.panel.alphaGrids.add(overlay);
    }

    /**
     * plots a line segment, connecting all pixels between the points defined by (x1,y1) and (x2,y2) with the provided
     * color.
     */
    public void PlotSegment(double x1, double y1, double x2, double y2, int color) {
        Util.AlongLineAction(x1, y1, x2, y2, (int x, int y) -> {
            this.SetPix(x, y, color);
        });
    }

    /**
     * plots a line segment, connecting all pixels between the points defined by (x1,y1) and (x2,y2) with the provided
     * color. the coordinates will be multiplied by the scale parameter
     */
    public void PlotSegment(double x1, double y1, double x2, double y2, int color, double scale) {
        if (scale <= 0) {
            throw new IllegalArgumentException("scale must be >0! scale: " + scale);
        }
        Util.AlongLineAction(x1 * scale, y1 * scale, x2 * scale, y2 * scale, (int x, int y) -> {
            this.SetPix(x, y, color);
        });
    }

    /**
     * plots a line segment, connecting all pixels between the points defined by (x1,y1) and (x2,y2) with the provided
     * color. the coordinates will be multiplied by the scale parameters
     */
    public void PlotSegment(double x1, double y1, double x2, double y2, int color, double scaleX, double scaleY) {
        if (scaleX < 0 || scaleY < 0) {
            throw new IllegalArgumentException("scaleX and scaleY must be >=0! scaleX: " + scaleX + " scaleY: " + scaleY);
        }
        Util.AlongLineAction(x1 * scaleX, y1 * scaleY, x2 * scaleX, y2 * scaleY, (int x, int y) -> {
            this.SetPix(x, y, color);
        });
    }

    /**
     * plots a line, connecting all pixels between the points defined by the list of x,y,x,y... pairs with the provided
     * color
     */
    public void PlotLine(double[] xys, int color) {
        PlotLine(xys, color, 0, xys.length);
    }

    /**
     * plots a line, connecting all pixels between the points defined by the list of x,y,x,y... pairs with the provided
     * color. the coordinates will be multiplied by the scale parameter
     */
    public void PlotLine(double[] xys, int color, double scale) {
        PlotLine(xys, color, 0, xys.length, scale, scale);
    }

    /**
     * plots a line, connecting all pixels between the points defined by the list of x,y,x,y... beginning with pair
     * startPoint and ending with pair endPoint pairs with the provided color.
     */
    public void PlotLine(double[] xys, int color, int startPoint, int endPoint) {
        if (xys.length < 4) {
            throw new IllegalArgumentException("xys array too short, must define at least 2 points! length: " + xys.length);
        }
        if (xys.length % 2 != 0) {
            throw new IllegalArgumentException("xys array should have even length! length: " + xys.length);
        }
        if (startPoint < 0 || startPoint > xys.length / 2 || endPoint < 0 || endPoint > xys.length / 2 || startPoint >= endPoint) {
            throw new IllegalArgumentException("invalid startPoint or endPoint for plotting " + xys.length / 2 + " points! startPoint: " + startPoint + " endPoint: " + endPoint);
        }
        for (int i = 0; i < xys.length / 2 - 1; i++) {
            Util.AlongLineAction(xys[i * 2], xys[i * 2 + 1], xys[(i + 1) * 2], xys[(i + 1) * 2 + 1], (int x, int y) -> {
                this.SetPix(x, y, color);
            });
        }
    }

    /**
     * plots a line, connecting all pixels between the points defined by the list of x,y,x,y... beginning with pair
     * startPoint and ending with pair endPoint pairs with the provided color. the coordinates will be multiplied by the
     * scale parameter
     */
    public void PlotLine(double[] xys, int color, int startPoint, int endPoint, double scale) {
        PlotLine(xys, color, startPoint, endPoint, scale, scale);
    }

    /**
     * plots a line, connecting all pixels between the points defined by the list of x,y,x,y... beginning with pair
     * startPoint and ending with pair endPoint pairs with the provided color. the coordinates will be multiplied by the
     * scale parameters
     */
    public void PlotLine(double[] xys, int color, int startPoint, int endPoint, double scaleX, double scaleY) {
        if (scaleX < 0 || scaleY < 0) {
            throw new IllegalArgumentException("scaleX and scaleY must be >=0! scaleX: " + scaleX + " scaleY: " + scaleY);
        }
        if (xys.length < 4) {
            throw new IllegalArgumentException("xys array too short, must define at least 2 points! length: " + xys.length);
        }
        if (xys.length % 2 != 0) {
            throw new IllegalArgumentException("xys array should have even length! length: " + xys.length);
        }
        if (startPoint < 0 || startPoint > xys.length / 2 || endPoint < 0 || endPoint > xys.length / 2 || startPoint >= endPoint) {
            throw new IllegalArgumentException("invalid startPoint or endPoint for plotting " + xys.length / 2 + " points! startPoint: " + startPoint + " endPoint: " + endPoint);
        }
        for (int i = 0; i < xys.length / 2 - 1; i++) {
            Util.AlongLineAction(xys[i * 2] * scaleX, xys[i * 2 + 1] * scaleY, xys[(i + 1) * 2] * scaleX, xys[(i + 1) * 2 + 1] * scaleY, (int x, int y) -> {
                this.SetPix(x, y, color);
            });
        }
    }

    /**
     * plots a line, connecting all pixels between the points defined by the x,y pairs from the xs and ys array with the
     * provided color
     */
    public void PlotLine(double[] xs, double[] ys, int color) {
        PlotLine(xs, ys, color, 0, xs.length);
    }

    /**
     * plots a line, connecting all pixels between the points defined by the x,y pairs from the xs and ys array with the
     * provided color.
     */
    public void PlotLine(double[] xs, double[] ys, int color, double scale) {
        PlotLine(xs, ys, color, 0, xs.length, scale, scale);
    }

    /**
     * plots a line, connecting all pixels between the points defined by the x,y pairs with the provided color. the coordinates will be multiplied by the
     * scale parameter beginning with pair startPoint and ending with pair endPoint
     */
    public void PlotLine(double[] xs, double[] ys, int color, int startPoint, int endPoint, double scale) {
        PlotLine(xs, ys, color, startPoint, endPoint, scale, scale);
    }

    /**
     * plots a line, connecting all pixels between the points defined by the x,y pairs with the provided color.
     * beginning with pair startPoint and ending with pair endPoint
     */
    public void PlotLine(double[] xs, double[] ys, int color, int startPoint, int endPoint) {
        if (xs.length != ys.length) {
            throw new IllegalArgumentException("xs and ys must have the same length! xs.length: " + xs.length + " ys.length: " + ys.length);
        }
        if (xs.length < 2) {
            throw new IllegalArgumentException("arrays too short, must define at least 2 points! length: " + xs.length);
        }
        if (startPoint < 0 || startPoint > xs.length || endPoint < 0 || endPoint > xs.length || startPoint >= endPoint - 1) {
            throw new IllegalArgumentException("invalid startPoint or endPoint for plotting " + xs.length + " points! startPoint: " + startPoint + " endPoint: " + endPoint);
        }
        for (int i = startPoint; i < endPoint - 1; i++) {
            Util.AlongLineAction(xs[i], ys[i], xs[i + 1], ys[i + 1], (int x, int y) -> {
                this.SetPix(x, y, color);
            });
        }
    }

    /**
     * plots a line, connecting all pixels between the points defined by the x,y pairs with the provided color. the coordinates will be multiplied by the
     * scale parametesr beginning with pair startPoint and ending with pair endPoint
     */
    public void PlotLine(double[] xs, double[] ys, int color, int startPoint, int endPoint, double scaleX, double scaleY) {
        if (scaleX < 0 || scaleY < 0) {
            throw new IllegalArgumentException("scaleX and scaleY must be >=0! scaleX: " + scaleX + " scaleY: " + scaleY);
        }
        if (xs.length != ys.length) {
            throw new IllegalArgumentException("xs and ys must have the same length! xs.length: " + xs.length + " ys.length: " + ys.length);
        }
        if (xs.length < 2) {
            throw new IllegalArgumentException("arrays too short, must define at least 2 points! length: " + xs.length);
        }
        if (startPoint < 0 || startPoint > xs.length || endPoint < 0 || endPoint > xs.length || startPoint > endPoint - 1) {
            throw new IllegalArgumentException("invalid startPoint or endPoint for plotting " + xs.length + " points! startPoint: " + startPoint + " endPoint: " + endPoint);
        }
        for (int i = startPoint; i < endPoint; i++) {
            Util.AlongLineAction(xs[i] * scaleX, ys[i] * scaleY, xs[i + 1] * scaleX, ys[i + 1] * scaleY, (int x, int y) -> {
                this.SetPix(x, y, color);
            });
        }
    }

    /**
     * gets the xDim component of the vis window
     */
    @Override
    public int compX() {
        return compX;
    }

    /**
     * gets the yDim component of the vis window
     */
    @Override
    public int compY() {
        return compY;
    }

    /**
     * returns whether the UIGrid is active (whether it exists)
     */
    @Override
    public boolean IsActive() {
        return active;
    }

    /**
     * can be used to inactivate the component, preventing drawing and other overhead
     */
    @Override
    public void SetActive(boolean isActive) {
        this.active = isActive;
    }

    /**
     * gets the X coordinate of the mouse event in terms of the UIGrid coordinate space, useful with AddMouseListeners
     */
    public double ClickXpt(MouseEvent e) {
        return e.getX() * 1.0 / scale;
    }


    /**
     * gets the X square of the mouse event in terms of the UIGrid coordinate space, useful with AddMouseListeners
     */
    public int ClickXsq(MouseEvent e) {
        return e.getX() / scale;
    }

    /**
     * gets the Y coordinate of the mouse event in terms of the UIGrid coordinate space, useful with AddMouseListeners
     */
    public double ClickYpt(MouseEvent e) {
        return (yDim - 1) - e.getY() * 1.0 / scale;
    }

    /**
     * gets the Y square of the mouse event in terms of the UIGrid coordinate space, useful with AddMouseListeners
     */
    public int ClickYsq(MouseEvent e) {
        return (yDim - 1) - e.getY() / scale;
    }

    /**
     * adds a mouse listener function to the UIGrid that will be called whenever the user interacts with the UIGrid
     */
    public void AddMouseListeners(MouseAdapter mouseListener) {
        if (mouseListener != null) {
            this.panel.addMouseListener(mouseListener);
        }
    }

    /**
     * sets every pixel in the UIGrid using the DrawPix function
     */
    public void SetAll(Coords2DColor DrawPix) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                SetPix(x, y, DrawPix.SetPix(x, y));
            }
        }
    }

    /**
     * sets every pixel in the UIGrid by reading values from the PDEGrid2D
     */
    public void DrawPDEGrid(PDEGrid2D drawMe, DoubleToInt ColorFn) {
        if (active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    SetPix(x, y, ColorFn.DoubleToInt(drawMe.Get(x, y)));
                }
            }
        }
    }

    /**
     * sets every pixel in the UIGrid by reading values from the PDEGrid3D, values are summed over the Z dimension
     */
    public void DrawPDEGridXY(PDEGrid3D drawMe, DoubleToInt ColorFn) {
        if (active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    double sum = 0;
                    for (int z = 0; z < drawMe.zDim; z++) {
                        sum += drawMe.Get(x, y, z);
                    }
                    SetPix(x, y, ColorFn.DoubleToInt(sum / drawMe.zDim));
                }
            }
        }
    }

    /**
     * sets every pixel in the UIGrid by reading values from the PDEGrid3D, values are summed over the X dimension
     */
    public void DrawPDEGridYZ(PDEGrid3D drawMe, DoubleToInt ColorFn) {
        if (active) {
            for (int y = 0; y < drawMe.yDim; y++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    double sum = 0;
                    for (int x = 0; x < drawMe.xDim; x++) {
                        sum += drawMe.Get(x, y, z);
                    }
                    SetPix(y, z, ColorFn.DoubleToInt(sum / drawMe.xDim));
                }
            }
        }
    }

    /**
     * sets every pixel in the UIGrid by reading values from the PDEGrid3D, values are summed over the Y dimension
     */
    public void DrawPDEGridXZ(PDEGrid3D drawMe, DoubleToInt ColorFn) {
        if (active) {
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

    /**
     * sets every pixel in the UIGrid by iterating over the AgentGrid2D and drawing the first agent found at each position. the background color will be used when no agent exists
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid2D<Q>> void DrawAgents(T drawMe, AgentToColorInt<Q> ColorFn, Coords2DInt backgroundColor) {
        if (active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    Q a = drawMe.GetAgent(x, y);
                    if (a != null) {
                        SetPix(x, y, ColorFn.AgentToColor(a));
                    } else {
                        SetPix(x, y, backgroundColor.GenInt(x,y));
                    }
                }
            }
        }
    }

    /**
     * sets every pixel in the UIGrid by iterating over the AgentGrid2D and drawing the first agent found at each position. positions with no agent will be skipped
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid2D<Q>> void DrawAgents(T drawMe, AgentToColorInt<Q> ColorFn) {
        if (active) {
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

    /**
     * sets every pixel in the UIGrid by drawing the closest agent (agent with lowest Z value) at each X,Y pair, the background color will be used when no agent is found
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawClosestAgentsXY(T drawMe, AgentToColorInt<Q> ColorFn, Coords2DInt backgroundColor) {
        if (active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    for (int z = 0; z <= drawMe.zDim; z++) {
                        if (z == drawMe.zDim) {
                            SetPix(x, y, backgroundColor.GenInt(x,y));
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

    /**
     * sets every pixel in the UIGrid by drawing the closest agent (agent with lowest Y value) at each X,Z pair, the background color will be used when no agent is found
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawClosestAgentsXZ(T drawMe, AgentToColorInt<Q> ColorFn, Coords2DInt backgroundColor) {
        if (active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int y = 0; y <= drawMe.yDim; y++) {
                        if (y == drawMe.yDim) {
                            SetPix(x, z, backgroundColor.GenInt(x,z));
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

    /**
     * sets every pixel in the UIGrid by drawing the closest agent (agent with lowest X value) at each Y,Z pair, the background color will be used when no agent is found
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawClosestAgentsYZ(T drawMe, AgentToColorInt<Q> ColorFn, Coords2DInt backgroundColor) {
        if (active) {
            for (int y = 0; y < drawMe.yDim; y++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int x = 0; x <= drawMe.xDim; x++) {
                        if (x == drawMe.yDim) {
                            SetPix(y, z, backgroundColor.GenInt(y,z));
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

    /**
     * sets every pixel in the UIGrid by drawing the furthest agent (agent with highest Z value) at each X,Y pair, the background color will be used when no agent is found
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawFurthestAgentsXY(T drawMe, AgentToColorInt<Q> ColorFn, Coords2DInt backgroundColor) {
        if (active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    for (int z = drawMe.zDim - 1; z >= -1; z++) {
                        if (z == -1) {
                            SetPix(x, y, backgroundColor.GenInt(x,y));
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

    /**
     * sets every pixel in the UIGrid by drawing the furthest agent (agent with highest Y value) at each X,Z pair, the background color will be used when no agent is found
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawFurthestAgentsXZ(T drawMe, AgentToColorInt<Q> ColorFn, Coords2DInt backgroundColor) {
        if (active) {
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int y = drawMe.yDim - 1; y >= -1; y++) {
                        if (y == -1) {
                            SetPix(x, z, backgroundColor.GenInt(x,z));
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

    /**
     * sets every pixel in the UIGrid by drawing the furthest agent (agent with highest X value) at each Y,Z pair, the background color will be used when no agent is found
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawFurthestAgentsYZ(T drawMe, AgentToColorInt<Q> ColorFn, Coords2DInt backgroundColor) {
        if (active) {
            for (int y = 0; y < drawMe.yDim; y++) {
                for (int z = 0; z < drawMe.zDim; z++) {
                    for (int x = drawMe.xDim - 1; x >= 0; x++) {
                        if (x == -1) {
                            SetPix(y, z, backgroundColor.GenInt(y,z));
                        }else {
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

    /**
     * sets every pixel in the UIGrid by drawing the closest agent (agent with lowest Z value) at each X,Y pair, positions with no agent will be skipped
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawClosestAgentsXY(T drawMe, AgentToColorInt<Q> ColorFn) {
        if (active) {
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

    /**
     * sets every pixel in the UIGrid by drawing the closest agent (agent with lowest Y value) at each X,Z pair, positions with no agent will be skipped
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawClosestAgentsXZ(T drawMe, AgentToColorInt<Q> ColorFn) {
        if (active) {
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

    /**
     * sets every pixel in the UIGrid by drawing the closest agent (agent with lowest X value) at each Y,Z pair, positions with no agent will be skipped
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawClosestAgentsYZ(T drawMe, AgentToColorInt<Q> ColorFn) {
        if (active) {
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

    /**
     * sets every pixel in the UIGrid by drawing the furthest agent (agent with highest Z value) at each X,Y pair, positions with no agent will be skipped
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawFurthestAgentsXY(T drawMe, AgentToColorInt<Q> ColorFn) {
        if (active) {
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

    /**
     * sets every pixel in the UIGrid by drawing the furthest agent (agent with highest Y value) at each X,Z pair, positions with no agent will be skipped
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawFurthestAgentsXZ(T drawMe, AgentToColorInt<Q> ColorFn) {
        if (active) {
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

    /**
     * sets every pixel in the UIGrid by drawing the furthest agent (agent with highest X value) at each Y,Z pair, positions with no agent will be skipped
     */
    public <Q extends AgentBaseSpatial, T extends AgentGrid3D<Q>> void DrawFurthestAgentsYZ(T drawMe, AgentToColorInt<Q> ColorFn) {
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

    void SetupScaledBuff() {
        if (scaledBuff == null || scaledBuff.getHeight() != panel.scaleX * yDim || scaledBuff.getWidth() != panel.scaleX * xDim) {
            scaledBuff = new BufferedImage(panel.scaleX * xDim, panel.scaleX * yDim, BufferedImage.TYPE_INT_ARGB);
            scaledG = scaledBuff.createGraphics();
        }
        scaledG.drawImage((panel.buff.getScaledInstance(panel.scaleX * xDim, -panel.scaleX * yDim, Image.SCALE_FAST)), 0, 0, null);
    }

    /**
     * saves the current state image to a PNG
     */
    public void ToPNG(String path) {
        if (active) {
            SetupScaledBuff();
            File out = new File(path);
            try {
                ImageIO.write(scaledBuff, "png", out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * saves the current state image to a JPG
     */
    public void ToJPG(String path) {
        if (active) {
            SetupScaledBuff();
            File out = new File(path);
            try {
                ImageIO.write(scaledBuff, "jpg", out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * saves the current state image to a GIF
     */
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

    /**
     * draws a string to the UIGrid on a single line. will crash if the UIGrid is too short
     */
    public void SetString(String s, int xLeft, int yTop, int color, int bkColor) {
        //on top line and first char in line, don't draw bk, else draw bk to left & above
        if (s.length() > 0) {
            SetChar(s.charAt(0), xLeft, yTop - 1, color, bkColor);
            for (int i = 1; i < s.length(); i++) {
                DrawVertCharBar(xLeft + i * 4 - 1, yTop - 1, bkColor);
                SetChar(s.charAt(i), xLeft + i * 4, yTop - 1, color, bkColor);
            }
        }
    }

    public void SetString(String s, int xLeft, int yTop, int color, int bkColor, int charsPerLine,int maxLines) {
        for (int i = 0;; i++) {
            int start=i*charsPerLine;
            if(start>s.length()||i==maxLines){ break; }
            int end=i*charsPerLine+charsPerLine;
            if(end>s.length()){ end=s.length(); }
            SetString(s.substring(start,end),xLeft,yTop-i*6,color,bkColor);
        }
    }

    public void SetString(String s, int xLeft, int yTop, int color, int bkColor, int charsPerLine) {
        SetString(s,xLeft,yTop,color,bkColor,charsPerLine,-1);
    }

    public void SetPixRect(int color,int xLeft,int xRight,int yBottom,int yTop){
        for (int x = xLeft; x < xRight; x++) {
            for (int y = yBottom; y < yTop; y++) {
                SetPix(x,y,color);
            }
        }
    }

    public void Legend(String[] labels,int[] colors,int labelsColor,int bkColor,int xLeft,int yTop){
        int y=yTop;
        for (int i = 0; i < labels.length; i++) {
            SetPixRect(colors[i],xLeft,xLeft+5,y-5,y);
            SetString(labels[i],xLeft+5,y,labelsColor,bkColor);
            y-=6;
        }

    }

    /**
     * draws a single character to the UIGrid
     */
    public void SetChar(char c, int xLeft, int yTop, int color, int bkColor) {
        if (c > alphabet.length + 30) {
            c = 0;
        }
        short s = (c <= 30 && c < alphabet.length + 30) ? alphabet[0] : alphabet[c - 30];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 5; y++) {
                if (((s >> (x * 5 + y)) & 1) == 0) {
                    SetPix(x + xLeft, y + yTop - 4, bkColor);
                } else {
                    SetPix(x + xLeft, y + yTop - 4, color);
                }
            }
        }

    }


    /**
     * called by the UIWindow class to place the vis window
     */
    @Override
    public void _GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere) {
        putHere.add(panel);
        coordsHere.add(0);
        coordsHere.add(0);
        compSizesHere.add(compX);
        compSizesHere.add(compY);
    }
    private void DrawVertCharBar(int x, int y, int color) {
        for (int dy = y - 4; dy <= y; dy++) {
            SetPix(x, dy, color);
        }
    }

    private static final short[] alphabet = new short[]{
            32319//box
            , 32767//full
            , 0//space
            , 928//!
            , 24600//"
            , 32095//#
            , 21482//$
            , 9362//%
            , 8126//&
            , 768//'
            , 558//(
            , 14880//)
            , 20756//*
            , 4548//+
            , 65//,
            , 4228//-
            , 32//.
            , 24707///
            , 31279//0
            , 1000//1
            , 9907//2
            , 10929//3
            , 31900//4
            , 19133//5
            , 24239//6
            , 25235//7
            , 32447//8
            , 31421//9
            , 320//:
            , 321//;
            , 17732//<
            , 10570//=
            , 4433//>
            , 25264//?
            , 13998//@
            , 16015//A
            , 10943//B
            , 17966//C
            , 14911//D
            , 22207//E
            , 21151//F
            , 24238//G
            , 31903//H
            , 18417//I
            , 30754//J
            , 27807//K
            , 1087//L
            , 32159//M
            , 32223//N
            , 14894//O
            , 8863//P
            , 15982//Q
            , 14031//R
            , 19113//S
            , 17392//T
            , 31806//U
            , 28796//V
            , 31967//W
            , 27803//X
            , 24824//Y
            , 26291//Z
            , 17983//[
            , 2184//\
            , 32305//]
            , 8712//^
            , 1057//_
            , 272//`
            , 7595//a
            , 6463//b
            , 9510//c
            , 32038//d
            , 13670//e
            , 20964//f
            , 14757//g
            , 7455//h
            , 736//i
            , 22562//j
            , 9439//k
            , 2033//l
            , 15823//m
            , 7439//n
            , 6438//o
            , 4423//p
            , 7492//q
            , 8431//r
            , 10725//s
            , 10216//t
            , 15406//u
            , 14446//v
            , 15599//w
            , 9417//x
            , 14505//y
            , 13803//z
            , 18276//{
            , 864//|
            , 4977//}
            , 17160//~
            , 32767//full

    };

    @Override
    public int Xdim() {
        return xDim;
    }

    @Override
    public int Ydim() {
        return yDim;
    }

    @Override
    public int Length() {
        return length;
    }

    @Override
    public boolean IsWrapX() {
        return false;
    }

    @Override
    public boolean IsWrapY() {
        return false;
    }
}
class PaintPanel extends JPanel {

    final BufferedImage buff;
    final int xDim;
    final int yDim;
    public final int scaleX;
    public final int scaleY;
    Graphics2D g;
    final ArrayList<UIGrid> alphaGrids;
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
            UIGrid alphaGrid=alphaGrids.get(i);
            ((Graphics2D)g).drawImage(alphaGrid.buff.getScaledInstance(alphaGrid.scale *alphaGrid.xDim,-alphaGrid.scale *alphaGrid.yDim,Image.SCALE_FAST),null,null);
        }
        repaint();
    }

}
