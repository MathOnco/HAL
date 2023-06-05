package HAL.Gui;


import HAL.Interfaces.Grid2D;

public class UIGridView extends UIGrid {
    UIGrid parent;
    int xStart;
    int yStart;
    public UIGridView(UIGrid parent, int xDim, int yDim, int xStart, int yStart){
        super(xDim,yDim,1,false);
        this.parent=parent;
        this.xStart=xStart;
        this.yStart=yStart;
    }

    public void SetPix(int x,int y,int color) {
        if(In(x,y)) {
            parent.SetPix(x + xStart,y+yStart,color);
        }
        else{
            throw new IllegalArgumentException(+x+","+y+" is outside the GridView dimensions: "+xDim+","+yDim );
        }
    }

    public void SetPix(int i,int color){
        SetPix(ItoX(i),ItoY(i),color);
    }

//    @Override
//    public int Xdim() {
//        return xDim;
//    }
//
//    @Override
//    public int Ydim() {
//        return yDim;
//    }
//
//    @Override
//    public int Length() {
//        return length;
//    }
//
//    @Override
//    public boolean IsWrapX() {
//        return false;
//    }
//
//    @Override
//    public boolean IsWrapY() {
//        return false;
//    }
}
