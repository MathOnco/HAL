package HAL.Gui;
import HAL.Interfaces.ColorIntGenerator;
import HAL.Interfaces.Grid2D;
import HAL.Util;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
//import org.lwjgl.LWJGLException;
//import org.lwjgl.opengl.Display;
//import org.lwjgl.opengl.DisplayMode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.opengl.GL11.*;
import static HAL.Util.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by rafael on 5/28/17.
 */

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

public class OpenGL2DWindow implements Grid2D {
    final boolean active;
    private long window;
    final public int xPix;
    final public int yPix;
    final public int xDim;
    final public int yDim;
    final public int length;
    private int widthPix;
    private int heightPix;
    public boolean wrapX=false;
    public boolean wrapY=false;
    TickTimer tt = new TickTimer();
    final float[] circlPtsDefault = Util.GenCirclePoints(1, 20);

    /**
     * creates a new OpenGL2DWindow
     * @param title the title that will appear at the top of the window (default "")
     * @param xPix the length of the window in screen pixels
     * @param yPix the height of the window in screen pixels
     * @param xDim the length that the window will represent for drawing, should match the xDim of the model
     * @param yDim the height that the window will represent for drawing, should match the yDim of the model
     * @param active if set to false, the OpenGL2DWindow will not actually render and its methods will be skipped (default true)
     */
    public OpenGL2DWindow(String title, int xPix, int yPix, int xDim, int yDim, boolean active) {
        this.active = active;
        this.xPix = xPix;
        this.yPix = yPix;
        this.xDim = xDim;
        this.yDim = yDim;
        this.length=xDim*yDim;
        if (active) {
            // Setup an error callback. The default implementation
            // will print the error message in System.err.
            GLFWErrorCallback.createPrint(System.err).set();

            // Initialize GLFW. Most GLFW functions will not work before doing this.
            if (!glfwInit()) {
                throw new IllegalStateException("Unable to initialize GLFW");
            }

            // Configure GLFW
            glfwDefaultWindowHints(); // optional, the current window hints are already the default
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

            // Create the window
            window = glfwCreateWindow(xPix, yPix, title, NULL, NULL);
            if (window == NULL) {
                throw new RuntimeException("Failed to create the GLFW window");
            }
            // Setup a key callback. It will be called every time a key is pressed, repeated or released.
//            glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
//                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
//                    glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
//                }
//            });

            // Get the thread stack and push a new frame
            try ( MemoryStack stack = stackPush() ) {
                IntBuffer pWidth = stack.mallocInt(1); // int*
                IntBuffer pHeight = stack.mallocInt(1); // int*

                // Get the window size passed to glfwCreateWindow
                glfwGetWindowSize(window, pWidth, pHeight);

                // Get the resolution of the primary monitor
                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
                widthPix =pWidth.get(0);
                heightPix =pHeight.get(0);

                // Center the window
                glfwSetWindowPos(
                        window,
                        (vidmode.width() - widthPix) / 2,
                        (vidmode.height() - heightPix) / 2
                );
            } // the stack frame is popped automatically

            // Make the OpenGL context current
            glfwMakeContextCurrent(window);
            // Enable v-sync
            glfwSwapInterval(1);

            // Make the window visible
            glfwShowWindow(window);

            // This line is critical for LWJGL's interoperation with GLFW's
            // OpenGL context, or any context that is managed externally.
            // LWJGL detects the context that is current in the current thread,
            // creates the GLCapabilities instance and makes the OpenGL
            // bindings available for use.
            GL.createCapabilities();

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, xDim, 0, yDim, -1, 1);
            glMatrixMode(GL_MODELVIEW);
        }
    }

    /**
     * the below constructors are variants of the above constructor with default values for some of the arguments
     */
    public OpenGL2DWindow(int xPix, int yPix, int xDim, int yDim) {
        this("", xPix, yPix, xDim, yDim, true);
    }
    public OpenGL2DWindow(int xPix, int yPix, int xDim, int yDim, boolean active) {
        this("", xPix, yPix, xDim, yDim, active);
    }
    public OpenGL2DWindow(String title,int xPix, int yPix, int xDim, int yDim) {
        this(title, xPix, yPix, xDim, yDim, true);
    }

    /**
     * renders all draw commands to the window
     */
    public void Update() {
        if (active) {
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    /**
     * returns true if the close button has been clicked in the Gui
     */
    public boolean IsClosed() {
        if (active) {
//            return false;
            return glfwWindowShouldClose(window);
        } else {
            return true;
        }
    }

    /**
     * closes the gui
     */
    public void Close() {
        if (active) {
            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            // Terminate GLFW and free the error callback
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    void SaveImg(String path, String mode) {
        if (active) {
            File out = new File(path);
            glReadBuffer(GL_FRONT);
            int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
            ByteBuffer buffer = BufferUtils.createByteBuffer(widthPix * heightPix * bpp);
            glReadPixels(0, 0, widthPix, heightPix, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            BufferedImage image = new BufferedImage(widthPix, heightPix, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < widthPix; x++) {
                for (int y = 0; y < heightPix; y++) {
                    int i = (x + (widthPix * y)) * bpp;
                    int r = buffer.get(i) & 0xFF;
                    int g = buffer.get(i + 1) & 0xFF;
                    int b = buffer.get(i + 2) & 0xFF;
                    image.setRGB(x, heightPix - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
                }
            }
            try {
                ImageIO.write(image, mode, out);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * call this once per step of your model, and the function will ensure that your model runs at the rate provided in
     * millis. the function will take the amount time between calls into account to ensure a consistent tick rate.
     */
    public void TickPause(int millis) {
        if (active) {
            tt.TickPause(millis);
        }
    }

    /**
     * usually called before any other draw commands, sets the screen to a color.
     */
    public void Clear(int clearColor) {
        if (active) {
            glClearColor((float) GetRed(clearColor), (float) GetGreen(clearColor), (float) GetBlue(clearColor), (float) GetAlpha(clearColor));
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        }
    }



    /**
     * returns whether the Gui is active (whether it exists)
     */
    public boolean IsActive() {
        return active;
    }

    /**
     * duplicates UIGrid functionality, draws a rectangle large enough to fill one lattice position
     */
    public void SetPix(int i,int color){
        int x=ItoX(i);
        int y=ItoY(i);
        SetPix(x,y,color);
    }

    /**
     * duplicates UIGrid functionality, draws a rectangle large enough to fill one lattice position
     */
    public void SetPix(int x,int y,int color){
        Rectangle(x,y,x+1,y+1,color);
    }

    /**
     * draws a rectangle between (x1,y1) and (x2,y2) coded by Jill Gallaher
     */
    public void Rectangle(double x1, double y1, double x2, double y2, int color) {
        if (active) {
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_QUADS);
            glVertex2f((float) x1, (float) y1);
            glVertex2f((float) x2, (float) y1);
            glVertex2f((float) x2, (float) y2);
            glVertex2f((float) x1, (float) y2);
            glEnd();
        }
    }
    /**
     * draws a rectangle centered at position x,y
     */
    public void RectangleAtPoint(double x, double y, double width, double height, int color) {
        double wRad=width/2.0;
        double hRad=height/2.0;
         Rectangle(x-wRad,y-hRad,x+wRad,y+hRad,color);
    }
    public void Square(double x, double y, double rad, int color) {
        Rectangle(x-rad,y-rad,x+rad,y+rad,color);
    }

    /**
     * draws a circle centered around x,y, subsequent circles will be drawn over top
     */
    public void Circle(double x, double y, double rad, int color) {
        FanShape((float) x, (float) y, (float) rad, circlPtsDefault, color);
    }

    /**
     * draws a circle centered around x,y, subsequent circles will be drawn over top, the ColorGen function is used to
     * generate the color of the circle and will not be called if the Gui is not active
     */
    public void Circle(double x, double y, double rad, ColorIntGenerator ColorGen) {
        if (active) {
            FanShape((float) x, (float) y, (float) rad, circlPtsDefault, ColorGen.GenColorInt());
        }
    }

    /**
     * draws a fan shape around the center x and y, using the array of points to define the edges of the fan. used as
     * part of the circle function
     */
    public void FanShape(float centerX, float centerY, float scale, float[] points, ColorIntGenerator ColorGen) {
        if (active) {
            FanShape(centerX, centerY, scale, points, ColorGen.GenColorInt());
        }
    }

    /**
     * draws a fan shape around the center x and y, using the array of points to define the edges of the fan. used as
     * part of the circle function
     */
    public void FanShape(float centerX, float centerY, float scale, float[] points, int color) {
        if (active) {
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_TRIANGLE_FAN);
            glVertex2f(centerX, centerY);
            for (int i = 0; i < points.length / 2; i++) {
                glVertex2f((points[i * 2] * scale + centerX), (points[i * 2 + 1] * scale + centerY));
            }
            glEnd();
        }
    }

    /**
     * draws a line between (x1,y1) and (x2,y2)
     */
    public void Line(double x1, double y1, double x2, double y2, int color) {
        if (active) {
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_LINES);
            glVertex2f((float) x1, (float) y1);
            glVertex2f((float) x2, (float) y2);
            glEnd();
        }
    }

    /**
     * draws a series of lines between all x,y pairs
     */
    public void LineStrip(double[] xs, double[] ys, int color) {
        if (active) {
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_LINE_STRIP);
            for (int i = 0; i < xs.length; i++) {
                glVertex2f((float) xs[i], (float) ys[i]);
            }
            glEnd();
        }
    }

    /**
     * draws a series of lines between all x,y pairs, coords should store pairs as x,y,x,y...
     */
    public void LineStrip(double[] coords, int color) {
        if (active) {
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_LINE_STRIP);
            for (int i = 0; i < coords.length; i += 2) {
                glVertex2f((float) coords[i], (float) coords[i + 1]);
            }
            glEnd();
        }
    }

    /**
     * saves the current state image to a PNG, call Update first
     */
    public void ToPNG(String path) {
        SaveImg(path, "png");
    }

    /**
     * saves the current state image to a JPG, call Update first
     */
    public void ToJPG(String path) {
        SaveImg(path, "jpg");
    }

    /**
     * saves the current state image to a GIF image, call Update first
     */
    public void ToGIF(String path) {
        SaveImg(path, "gif");
    }

    //int width = Display.getDisplayMode().getWidth();
    //int height = Display.getDisplayMode().getHeight();

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
        return wrapX;
    }

    @Override
    public boolean IsWrapY() {
        return wrapY;
    }

    /**
     * Reboot utility for JVM. to run with -XstartOnFirstThread jvm argument
     *
     * @author kappa
     */
    public static boolean MakeMacCompatible(String[] args) {
        return Util.MakeOpenGLMacCompatible(args);
    }
}
