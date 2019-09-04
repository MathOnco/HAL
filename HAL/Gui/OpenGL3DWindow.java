package HAL.Gui;

import HAL.Interfaces.ColorIntGenerator;
import HAL.Interfaces.Grid3D;
import HAL.Util;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static HAL.Util.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by rafael on 5/28/17.
 */
public class OpenGL3DWindow implements Grid3D {
    final boolean active;
    public final int xDim;
    public final int yDim;
    public final int zDim;
    public final int length;
    final int maxDim;
    final float scaleDims;
    final float trans;
    final float[] circlPtsDefault = Util.GenCirclePoints(1, 20);
    //final float transZ;
    public final int xPix;
    public final int yPix;
    long lastFrameTime = -1;
    TickTimer tt = new TickTimer();
    Camera camera;

    /**
     *
     * creates a new OpenGL2DWindow
     * @param title the title that will appear at the top of the window (default "")
     * @param xPix the length of the window in screen pixels
     * @param yPix the height of the window in screen pixels
     * @param xDim the length that the window will represent for drawing, should match the xDim of the model
     * @param yDim the height that the window will represent for drawing, should match the yDim of the model
     * @param zDim the depth that the window will represent for drawing, should match the zDim of the model
     * @param active if set to false, the OpenGL2DWindow will not actually render and its methods will be skipped (default true)
     */
    public OpenGL3DWindow(String title, int xPix, int yPix, int xDim, int yDim, int zDim, boolean active) {
        this.active = active;
        int maxDim = Math.max(xDim, yDim);
        this.maxDim = Math.max(maxDim, zDim);
        this.xDim = xDim;
        this.yDim = yDim;
        this.zDim = zDim;
        this.length=xDim*yDim*zDim;
        this.xPix = xPix;
        this.yPix = yPix;
        scaleDims = (float) (2.0 / this.maxDim);
        trans = (float) (-this.maxDim / 2.0);
        //transZ = (float) (-zDim * 0.6);

        if (active) {
            camera = new Camera();
            try {
                Display.setDisplayMode(new DisplayMode(xPix, yPix));
                Display.setTitle(title);
                Display.create();
            } catch (LWJGLException e) {
                e.printStackTrace();
                System.err.println("unable to create Vis3D display");
            }
            glEnable(GL_DEPTH_TEST);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glFrustum(-1, 1, -1, 1, 1, 1000);
            //glFrustum(0,maxDim,0,maxDim,maxDim,maxDim+zDim);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            glScalef(scaleDims, scaleDims, scaleDims);
            //glTranslatef(transXY, transXY, transZ);
        }
    }

    /**
     * the below constructors are variants of the above constructor with default values for some of the arguments
     */
    public OpenGL3DWindow(int xPix, int yPix, int xDim, int yDim, int zDim) {
        this("", xPix, yPix, xDim, yDim, zDim, true);
    }
    public OpenGL3DWindow(int xPix, int yPix, int xDim, int yDim, int zDim, boolean active) {
        this("", xPix, yPix, xDim, yDim, zDim, active);
    }
    public OpenGL3DWindow(String title, int xPix, int yPix, int xDim, int yDim, int zDim) {
        this(title, xPix, yPix, xDim, yDim, zDim, true);
    }


    int GetDelta() {
        long time = System.currentTimeMillis();
        int delta = (int) (time - lastFrameTime);
        if (lastFrameTime == -1) {
            delta = 0;
        }
        lastFrameTime = time;

        return delta;
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
    public void Clear(int color) {
        if (active) {
            glClearColor((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        }
    }

    /**
     * usually called before any other draw commands, sets the screen to a color. and draws a box around the grid
     * domain
     */
    public void ClearBox(int clearColor, int lineColor) {
        glClearColor((float) GetRed(clearColor), (float) GetGreen(clearColor), (float) GetBlue(clearColor), 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        Line(0, 0, 0, xDim, 0, 0, lineColor);
        Line(0, 0, 0, 0, yDim, 0, lineColor);
        Line(0, 0, 0, 0, 0, zDim, lineColor);
        Line(xDim, yDim, zDim, 0, yDim, zDim, lineColor);
        Line(xDim, yDim, zDim, xDim, 0, zDim, lineColor);
        Line(xDim, yDim, zDim, xDim, yDim, 0, lineColor);
        Line(xDim, 0, 0, xDim, yDim, 0, lineColor);
        Line(xDim, 0, 0, xDim, 0, zDim, lineColor);
        Line(0, yDim, 0, 0, yDim, zDim, lineColor);
        Line(0, yDim, 0, xDim, yDim, 0, lineColor);
        Line(0, 0, zDim, xDim, 0, zDim, lineColor);
        Line(0, 0, zDim, 0, yDim, zDim, lineColor);
    }

    /**
     * renders all draw commands to the window
     */
    public void Update() {
        if (active) {
            camera.acceptInputRotate(1);
            camera.acceptInputGrab();
            camera.acceptInputMove(GetDelta() / 10.0f);
            camera.apply();
            Display.update();
        }
    }

    /**
     * returns true if the close button has been clicked in the Gui
     */
    public boolean IsClosed() {
        if (active) {
            return Display.isCloseRequested();
        }
        return true;
    }

    /**
     * closes the gui
     */
    public void Close() {
        if (active) {
            Display.destroy();
        }
    }

    /**
     * returns whether the Gui is active (whether it exists)
     */
    public boolean IsActive() {
        return active;
    }

    /**
     * draws a circle with a white dot and background black border to mark the outline of a cell
     */
    public void CelSphere(double x, double y, double z, double rad, int color) {
        if (active) {
            float xf = (float) x, yf = (float) y, zf = (float) z, radf = (float) rad, rf = (float) GetRed(color), gf = (float) GetGreen(color), bf = (float) GetBlue(color);
            //draw outline
            FanShape(xf, yf, zf, radf, circlPtsDefault, RGB((float) 0, (float) 0, (float) 0), (float) 0, (float) 0, -0.1f);
            //draw circle
            FanShape(xf, yf, zf, radf * 0.9f, circlPtsDefault, RGB(rf, gf, bf));
            //add cool specular lighting dot
            FanShape(xf, yf, zf, radf * 0.2f, circlPtsDefault, RGB((float) 1, (float) 1, (float) 1), 1.4f, 1.4f, 0.1f);
        }
    }

    /**
     * draws a circle centered around x,y, subsequent circles will be drawn over top
     */
    public void Circle(double x, double y, double z, double rad, int color) {
        FanShape((float) x, (float) y, (float) z, (float) rad, circlPtsDefault, color);
    }

    /**
     * draws a circle centered around x,y, subsequent circles will be drawn over top, the ColorGen function is used to
     * generate the color of the circle and will not be called if the Gui is not active
     */
    public void Circle(double x, double y, double z, double rad, ColorIntGenerator ColorGen) {
        FanShape((float) x, (float) y, (float) z, (float) rad, circlPtsDefault, ColorGen.GenColorInt());
    }

    /**
     * draws a fan shape around the center x and y, using the array of points to define the edges of the fan. used as
     * part of the circle function
     */
    public void FanShape(float centerX, float centerY, float centerZ, float scale, float[] points, int color) {
        if (active) {
            glPushMatrix();
            glTranslatef(centerX + trans, centerY + trans, -centerZ + trans);
            //glRotatef((float)Math.PI,0,1,0);
            //glTranslatef(0,0,-trans);
            glScalef(scale, scale, scale);
            glRotatef(camera.rotation[2], 0, 0, -1);
            glRotatef(camera.rotation[1], 0, -1, 0);
            glRotatef(camera.rotation[0], -1, 0, 0);
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_TRIANGLE_FAN);
            glVertex3f(0, 0, 0);
            for (int i = 0; i < points.length / 2; i++) {
                float x = (points[i * 2]);
                float y = (points[i * 2 + 1]);
                float z = 0;
                glVertex3f(x, y, z);
            }
            glEnd();
            glPopMatrix();
        }
    }
    public void SetPixXY(int x,int y,int color){
        RectangleXY(x,y,x+1,y+1,zDim,color);
    }

    public void SetPixXZ(int x,int z,int color){
        RectangleXZ(x,z,x+1,z+1,0,color);
    }

    public void SetPixYZ(int y,int z,int color){
        RectangleYZ(y,z,y+1,z+1,0,color);
    }
    /**
     * draws a rectangle between (x1,y1,z) and (x2,y2,z)
     */
    public void RectangleXY(double x1, double y1, double x2, double y2, double z, int color) {
        if (active) {
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_QUADS);
            glVertex3f((float) x1+trans, (float) y1+trans,(float)-z+trans);
            glVertex3f((float) x2+trans, (float) y1+trans,(float)-z+trans);
            glVertex3f((float) x2+trans, (float) y2+trans,(float)-z+trans);
            glVertex3f((float) x1+trans, (float) y2+trans,(float)-z+trans);
            glEnd();
        }
    }
    public void RectangleXZ(double x1, double z1, double x2, double z2, double y, int color) {
        if (active) {
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_QUADS);
            glVertex3f((float) x1+trans, (float) y+trans,(float)-z1+trans);
            glVertex3f((float) x2+trans, (float) y+trans,(float)-z1+trans);
            glVertex3f((float) x2+trans, (float) y+trans,(float)-z2+trans);
            glVertex3f((float) x1+trans, (float) y+trans,(float)-z2+trans);
            glEnd();
        }
    }
    public void RectangleYZ(double y1, double z1, double y2, double z2, double x, int color) {
        if (active) {
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_QUADS);
            glVertex3f((float) x+trans, (float) y1+trans,(float)-z1+trans);
            glVertex3f((float) x+trans, (float) y1+trans,(float)-z2+trans);
            glVertex3f((float) x+trans, (float) y2+trans,(float)-z2+trans);
            glVertex3f((float) x+trans, (float) y2+trans,(float)-z1+trans);
            glEnd();
        }
    }

    /**
     * draws a fan shape around the center x and y, using the array of points to define the edges of the fan. used as
     * part of the circle function
     */
    public void FanShape(float centerX, float centerY, float centerZ, float scale, float[] points, int color, float xdisp, float ydisp, float zdisp) {
        if (active) {
            float r = (float) GetRed(color);
            float g = (float) GetGreen(color);
            float b = (float) GetBlue(color);
            glPushMatrix();
            glTranslatef(centerX + trans, centerY + trans, -centerZ + trans);
            //glRotatef((float)Math.PI,0,1,0);
            //glTranslatef(0,0,-trans);
            glScalef(scale, scale, scale);
            glRotatef(camera.rotation[2], 0, 0, -1);
            glRotatef(camera.rotation[1], 0, -1, 0);
            glRotatef(camera.rotation[0], -1, 0, 0);
            glTranslatef(xdisp, ydisp, zdisp);
            glColor3f(r, g, b);
            glBegin(GL_TRIANGLE_FAN);
            glVertex3f(0, 0, 0);
            for (int i = 0; i < points.length / 2; i++) {
                float x = (points[i * 2]);
                float y = (points[i * 2 + 1]);
                float z = 0;
                glVertex3f(x, y, z);
            }
            glEnd();
            glPopMatrix();
        }
    }

    /**
     * draws a line between (x1,y1,z1) and (x2,y2,z2)
     */
    public void Line(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        if (active) {
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_LINES);
            glVertex3f((float) x1 + trans, (float) y1 + trans, (float) -z1 + trans);
            glVertex3f((float) x2 + trans, (float) y2 + trans, (float) -z2 + trans);
            glEnd();
        }
    }

    /**
     * draws a series of lines between all x,y,z triplets
     */
    public void LineStrip(double[] xs, double[] ys, double[] zs, int color) {
        if (active) {
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_LINE_STRIP);
            for (int i = 0; i < xs.length; i++) {
                glVertex3f((float) xs[i] + trans, (float) ys[i] + trans, (float) -zs[i] + trans);
            }
            glEnd();
        }
    }

    /**
     * draws a series of lines between all x,y,z triplets, coords should store triplets as x,y,z,x,y,z...
     */
    public void LineStrip(double[] coords, int color) {
        if (active) {
            glColor4f((float) GetRed(color), (float) GetGreen(color), (float) GetBlue(color), (float) GetAlpha(color));
            glBegin(GL_LINE_STRIP);
            for (int i = 0; i < coords.length; i += 3) {
                glVertex3f((float) coords[i] + trans, (float) coords[i + 1] + trans, (float) coords[i + 2] + trans);
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

    void SaveImg(String path, String mode) {
        if (active) {
            File out = new File(path);
            glReadBuffer(GL_FRONT);
            int width = Display.getDisplayMode().getWidth();
            int height = Display.getDisplayMode().getHeight();
            int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
            glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int i = (x + (width * y)) * bpp;
                    int r = buffer.get(i) & 0xFF;
                    int g = buffer.get(i + 1) & 0xFF;
                    int b = buffer.get(i + 2) & 0xFF;
                    image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
                }
            }
            try {
                ImageIO.write(image, mode, out);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public int Xdim() {
        return xDim;
    }

    @Override
    public int Ydim() {
        return yDim;
    }

    @Override
    public int Zdim() {
        return zDim;
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

    @Override
    public boolean IsWrapZ() {
        return false;
    }
}


class Camera {
    public static float moveSpeed = 0.5f;

    private static float maxLook = 85;

    private static float mouseSensitivity = 0.05f;

    static float[] pos = new float[3];
    static float[] rotation = new float[3];

    public static void apply() {
        if (rotation[1] / 360 > 1) {
            rotation[1] -= 360;
        } else if (rotation[1] / 360 < -1) {
            rotation[1] += 360;
        }
        glLoadIdentity();
        glRotatef(rotation[0], 1, 0, 0);
        glRotatef(rotation[1], 0, 1, 0);
        glRotatef(rotation[2], 0, 0, 1);
        glTranslatef(-pos[0], -pos[1], -pos[2]);
    }

    public static void acceptInput(float delta) {
        acceptInputRotate(delta);
        acceptInputGrab();
        acceptInputMove(delta);
    }

    public static void acceptInputRotate(float delta) {
        if (Mouse.isGrabbed()) {
            float mouseDX = Mouse.getDX();
            float mouseDY = -Mouse.getDY();
            rotation[1] += mouseDX * mouseSensitivity * delta;
            rotation[0] += mouseDY * mouseSensitivity * delta;
            rotation[0] = Math.max(-maxLook, Math.min(maxLook, rotation[0]));
        }
    }

    public static void acceptInputGrab() {
        if (Mouse.isInsideWindow() && Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(true);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            Mouse.setGrabbed(false);
        }
    }

    public static void acceptInputMove(float delta) {
        boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_W);
        boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_S);
        boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_D);
        boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
        boolean keyFast = Keyboard.isKeyDown(Keyboard.KEY_Q);
        boolean keySlow = Keyboard.isKeyDown(Keyboard.KEY_E);
        boolean keyFlyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        boolean keyFlyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        boolean keyReset = Keyboard.isKeyDown(Keyboard.KEY_R);


        float speed;

        if (keyReset) {
            pos[0] = 0;
            pos[1] = 0;
            pos[2] = 0;
            rotation[0] = 0;
            rotation[1] = 0;
            rotation[2] = 0;
        }
        if (keyFast) {
            speed = moveSpeed * 5;
        } else if (keySlow) {
            speed = moveSpeed / 2;
        } else {
            speed = moveSpeed;
        }

        speed *= delta;

        if (keyFlyUp) {
            pos[1] += speed;
        }
        if (keyFlyDown) {
            pos[1] -= speed;
        }

        if (keyDown) {
            pos[0] -= Math.sin(Math.toRadians(rotation[1])) * speed;
            pos[2] += Math.cos(Math.toRadians(rotation[1])) * speed;
        }
        if (keyUp) {
            pos[0] += Math.sin(Math.toRadians(rotation[1])) * speed;
            pos[2] -= Math.cos(Math.toRadians(rotation[1])) * speed;
        }
        if (keyLeft) {
            pos[0] += Math.sin(Math.toRadians(rotation[1] - 90)) * speed;
            pos[2] -= Math.cos(Math.toRadians(rotation[1] - 90)) * speed;
        }
        if (keyRight) {
            pos[0] += Math.sin(Math.toRadians(rotation[1] + 90)) * speed;
            pos[2] -= Math.cos(Math.toRadians(rotation[1] + 90)) * speed;
        }
    }
}
