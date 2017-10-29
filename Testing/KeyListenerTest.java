package Testing;

import Framework.Gui.GuiGridVis;
import Framework.Gui.GuiWindow;

/**
 * Created by rafael on 7/1/17.
 */
public class KeyListenerTest {
    public static void main(String[] args) {
        GuiWindow gui=new GuiWindow("testing",true);
        GuiGridVis ggv=new GuiGridVis(100,100,10);
        gui.AddCol(1, ggv);
//        ggv.addMouseListener(new MouseListener() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//            }
//
//            @Override
//            public void mousePressed(MouseEvent e) {
//                int x=e.getX()/10;
//                int y=99-e.getY()/10;
//                System.out.println(x+","+y);
//                ggv.SetColor(x,y,1,1,1);
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent e) {
//
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//
//            }
//        });
//        gui.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent e) {
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//                System.out.println(e.getKeyChar());
//            }
//        });
//        ggv.addMouseMotionListener(new MouseMotionListener() {
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                int x=e.getX()/10;
//                int y=99-e.getY()/10;
//                System.out.println(x+","+y);
//                ggv.SetColor(x,y,1,0,0);
//            }
//
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                int x=ggv.ClickXsq(e);
//                int y=ggv.ClickYsq(e);
//                System.out.println(x+","+y);
//                ggv.SetColor(x,y,0,0,1);
//            }
//        });
        gui.RunGui();
    }
}
