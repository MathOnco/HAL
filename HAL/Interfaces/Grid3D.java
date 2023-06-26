package HAL.Interfaces;

import HAL.Rand;
import HAL.Util;

import java.util.ArrayList;
import java.util.Arrays;

import static HAL.Util.InDim;
import static HAL.Util.NormSquared;

/**
 * holds functions that all 3D Grids share
 */
public interface Grid3D{

    /**
     * gets the index of the square at the specified coordinates with wrap around
     */
    default public int WrapI(int x,int y,int z){
        //wraps Coords to proper index
        if(In(x,y,z)){
            return I(x,y,z);
        }
        if(IsWrapX()){
            x=Util.Wrap(x,Xdim());
        }
        if(IsWrapY()){
            y=Util.Wrap(y,Xdim());
        }
        if(IsWrapZ()){
            z=Util.Wrap(z,Zdim());
        }
        if(In(x,y,z)){
            return I(x,y,z);
        }
        throw new IllegalArgumentException("cannot map to index in bounds!");
    }


    /**
     * gets the index of the voxel at the specified coordinates
     */
    default public int I(int x,int y,int z){
        //gets typeGrid index from location
        return x*Ydim()*Zdim()+y*Zdim()+z;
    }
    /**
     * get the value of the given x component with wraparound
     */
    default public double WrapX(double x){
        return Util.Wrap(x,Xdim());
    }
    /**
     * get the value of the given y component with wraparound
     */
    default public double WrapY(double y){
        return Util.Wrap(y,Ydim());
    }
    /**
     * get the value of the given z component with wraparound
     */
    default public double WrapZ(double z){
        return Util.Wrap(z,Zdim());
    }
    /**
     * get the value of the given x component with wraparound
     */
    default public int WrapX(int x){
        return Util.Wrap(x,Xdim());
    }
    /**
     * get the value of the given y component with wraparound
     */
    default public int WrapY(int y){
        return Util.Wrap(y,Ydim());
    }
    /**
     * get the value of the given z component with wraparound
     */
    default public int WrapZ(int z){
        return Util.Wrap(z,Zdim());
    }

    /**
     * gets the xDim component of the voxel at the specified index
     */
    default public int ItoX(int i){
        return i/(Ydim()*Zdim());
    }

    /**
     * gets the yDim component of the voxel at the specified index
     */
    default public int ItoY(int i){
        return (i/Zdim())%Ydim();
    }

    /**
     * gets the z component of the voxel at the specified index
     */
    default public int ItoZ(int i){
        return i%Zdim();
    }

    /**
     * gets the index of the square that contains the specified coordinates
     */
    default public int I(double x,double y,double z){
        //gets typeGrid index from location
        return (int)Math.floor(x)*Ydim()*Zdim()+(int)Math.floor(y)*Zdim()+(int)Math.floor(z);
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    default public boolean In(int x,int y,int z){
        if(x >= 0&&x<Xdim()&&y >= 0&&y<Ydim()&&z >= 0&&z<Zdim()){
            return true;
        }
        return false;
    }

    /**
     * returns whether the specified coordinates are inside the typeGrid bounds
     */
    default public boolean In(double x,double y,double z){
        int xInt=(int)Math.floor(x);
        int yInt=(int)Math.floor(y);
        int zInt=(int)Math.floor(z);
        return In(xInt,yInt,zInt);
    }

    /**
     * returns whether the specified coordinates are inside the Grid bounds with wraparound
     */
    default public boolean InWrap(int x,int y,int z){
        if(IsWrapX()||InDim(x,Xdim())&&(IsWrapY()||InDim(y,Ydim()))&&(IsWrapZ()||InDim(z,Zdim()))){
            return true;
        }
        return false;
    }

    /**
     * returns whether the specified coordinates are inside the Grid bounds with wraparound
     */
    default public boolean InWrap(double x,double y,double z){
        if(IsWrapX()||InDim(x,Xdim())&&(IsWrapY()||InDim(y,Ydim()))&&(IsWrapZ()||InDim(z,Zdim()))){
            return true;
        }
        return false;
    }

    /**
     * This function is very similar to the previous definition of MapHood, only it additionally takes as argument an
     * EvaluationFunctoin. this function should take as argument (i,x,y) of a location and return a boolean that decides
     * whether that location should be included as a valid one.
     */
    default public int MapHood(int[] hood,int centerX,int centerY,int centerZ){
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt=0;
        int iStart=hood.length/4;
        for(int i=iStart;i<hood.length;i+=3){
            int x=hood[i]+centerX;
            int y=hood[i+1]+centerY;
            int z=hood[i+2]+centerZ;
            if(!Util.InDim(x,Xdim())){
                if(IsWrapX()){
                    x=Util.Wrap(x,Xdim());
                }else{
                    continue;
                }
            }
            if(!Util.InDim(y,Ydim())){
                if(IsWrapY()){
                    y=Util.Wrap(y,Ydim());
                }else{
                    continue;
                }
            }
            if(!Util.InDim(z,Zdim())){
                if(IsWrapZ()){
                    z=Util.Wrap(z,Zdim());
                }else{
                    continue;
                }
            }
            hood[ptCt]=I(x,y,z);
            ptCt++;
        }
        return ptCt;
    }

    /**
     * This function is very similar to the previous definition of MapHood, only it additionally takes as argument an
     * EvaluationFunctoin. this function should take as argument (i,x,y) of a location and return a boolean that decides
     * whether that location should be included as a valid one.
     */
    default public int MapHood(int[] hood,int centerX,int centerY,int centerZ,IndexCoords3DBool Eval){
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt=0;
        int iStart=hood.length/4;
        for(int i=iStart;i<hood.length;i+=3){
            int x=hood[i]+centerX;
            int y=hood[i+1]+centerY;
            int z=hood[i+2]+centerZ;
            if(!Util.InDim(x,Xdim())){
                if(IsWrapX()){
                    x=Util.Wrap(x,Xdim());
                }else{
                    continue;
                }
            }
            if(!Util.InDim(y,Ydim())){
                if(IsWrapY()){
                    y=Util.Wrap(y,Ydim());
                }else{
                    continue;
                }
            }
            if(!Util.InDim(z,Zdim())){
                if(IsWrapZ()){
                    z=Util.Wrap(z,Zdim());
                }else{
                    continue;
                }
            }
            int j=I(x,y,z);
            if(Eval.Eval(j,x,y,z)){
                hood[ptCt]=j;
                ptCt++;
            }
        }
        return ptCt;
    }

    /**
     * This function is very similar to the previous definition of MapHood, only it additionally takes as argument an
     * EvaluationFunctoin. this function should take as argument (i,x,y) of a location and return a boolean that decides
     * whether that location should be included as a valid one.
     */
    default public int MapHood(int[] hood,int centerI){
        return MapHood(hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI));
    }

    /**
     * This function takes a neighborhood centered around the origin, translates the set of coordinates to be centered
     * around a particular central location, and computes which indices the translated coordinates map to. The function
     * returns the number of valid locations it set. this function differs from HoodToIs and CoordsToIs in that it takes
     * no ret[], MapHood instead puts the result of the mapping back into the hood array.
     */
    default public int MapHood(int[] hood,int centerI,IndexCoords3DBool Eval){
        return MapHood(hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI),Eval);
    }

    /**
     * returns a list of indices, where each index maps to one square on the boundary of the grid
     */
    default public int[] BoundaryIs(){
        int[] ret=new int[(Xdim()*Ydim()+Xdim()*Zdim()+Ydim()*Zdim())*2];
        int side1=Xdim()*Ydim();
        int side2=Xdim()*Zdim();
        int side3=Ydim()*Zdim();
        for(int x=0;x<Xdim();x++){
            for(int y=0;y<Ydim();y++){
                ret[x*Ydim()+y]=I(x,y,0);
                ret[x*Ydim()+y+side1]=I(x,y,Zdim()-1);
            }
        }
        for(int x=0;x<Xdim();x++){
            for(int z=0;z<Zdim();z++){
                ret[x*Zdim()+z+side1*2]=I(x,0,z);
                ret[x*Zdim()+z+side1*2+side2]=I(x,Ydim()-1,z);
            }
        }
        for(int y=0;y<Ydim();y++){
            for(int z=0;z<Zdim();z++){
                ret[y*Zdim()+z+(side1+side2)*2]=I(0,y,z);
                ret[y*Zdim()+z+(side1+side2)*2+side3]=I(Xdim()-1,y,z);
            }
        }
        return ret;
    }


    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    default public int ConvXsq(int x,Grid3D other){
        return (int)(((x+0.5)*other.Xdim())/Xdim());
    }

    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    default public int ConvYsq(int y,Grid3D other){
        return (int)(((y+0.5)*other.Ydim())/Ydim());
    }

    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    default public int ConvZsq(int z,Grid3D other){
        return (int)(((z+0.5)*other.Zdim())/Zdim());
    }

    /**
     * returns the index of the center of the square in otherGrid that the coordinate maps to.
     */
    default public int ConvI(int i,Grid3D other){
        int x=ItoX(i);
        int y=ItoY(i);
        int z=ItoZ(i);
        return other.I(ConvXsq(x,other),ConvYsq(y,other),ConvZsq(z,other));
    }

    /**
     * returns the position that x rescales to in the other grid
     */
    default public double ConvXpt(double x,Grid3D other){
        return x*other.Xdim()/Xdim();
    }

    /**
     * returns the position that y rescales to in the other grid
     */
    default public double ConvYpt(double y,Grid3D other){
        return y*other.Ydim()/Ydim();
    }

    /**
     * returns the position that z rescales to in the other grid
     */
    default public double ConvZpt(double z,Grid3D other){
        return z*other.Zdim()/Zdim();
    }

    /**
     * applies the action function to all positions in the rectangle, will use wraparound if appropriate
     */
    default public void ApplyRectangle(int startX,int startY,int startZ,int width,int height,int depth,Coords3DAction Action){
        for(int x=startX;x<startX+width;x++){
            for(int y=startY;y<startY+height;y++){
                for(int z=startZ;z<startY+depth;z++){
                    int xFinal=x;
                    int yFinal=y;
                    int zFinal=z;
                    if(IsWrapX()){
                        xFinal=Util.Wrap(x,Xdim());
                    }
                    if(IsWrapY()){
                        yFinal=Util.Wrap(y,Ydim());
                    }
                    if(IsWrapZ()){
                        zFinal=Util.Wrap(z,Zdim());
                    }
                    Action.Action(xFinal,yFinal,zFinal);
                }
            }
        }
    }

    /**
     * applies the action function to all positions in the neighborhood
     */
    default int ApplyHood(int[] hood,int centerI,Coords3DAction Action){
        return ApplyHood(hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI),Action);
    }

    /**
     * applies the action function to all positions in the neighborhood
     */
    default int ApplyHood(int[] hood,int centerX,int centerY,int centerZ,Coords3DAction Action){
        int ptCt=0;
        int iStart=hood.length/4;
        for(int i=iStart;i<hood.length;i+=3){
            int x=hood[i]+centerX;
            int y=hood[i+1]+centerY;
            int z=hood[i+2]+centerZ;
            if(!Util.InDim(x,Xdim())){
                if(IsWrapX()){
                    x=Util.Wrap(x,Xdim());
                }else{
                    continue;
                }
            }
            if(!Util.InDim(y,Ydim())){
                if(IsWrapY()){
                    y=Util.Wrap(y,Ydim());
                }else{
                    continue;
                }
            }
            if(!Util.InDim(z,Zdim())){
                if(IsWrapZ()){
                    z=Util.Wrap(z,Zdim());
                }else{
                    continue;
                }
            }
            Action.Action(x,y,z);
        }
        return ptCt;
    }

    /**
     * applies the action function to all positions in the neighborhood, includes the index in the neighborhood
     */
    default int ApplyHoodWithIndex(int[] hood,int centerX,int centerY,int centerZ,ICoords3DAction Action){
        int ptCt=0;
        int iStart=hood.length/4;
        for(int i=iStart;i<hood.length;i+=3){
            int x=hood[i]+centerX;
            int y=hood[i+1]+centerY;
            int z=hood[i+2]+centerZ;
            if(!Util.InDim(x,Xdim())){
                if(IsWrapX()){
                    x=Util.Wrap(x,Xdim());
                }else{
                    continue;
                }
            }
            if(!Util.InDim(y,Ydim())){
                if(IsWrapY()){
                    y=Util.Wrap(y,Ydim());
                }else{
                    continue;
                }
            }
            if(!Util.InDim(z,Zdim())){
                if(IsWrapZ()){
                    z=Util.Wrap(z,Zdim());
                }else{
                    continue;
                }
            }
            Action.Action((i-iStart)/3,x,y,z);
        }
        return ptCt;
    }

    /**
     * applies the action function to all positions in the neighborhood, includes the index in the neighborhood
     */
    default int ApplyHoodWithIndex(int[] hood,int centerI,ICoords3DAction Action){
        return ApplyHoodWithIndex(hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI),Action);
    }

    /**
     * applies the action function to all positions in the neighborhood up to validCount, assumes the neighborhood is
     * already mapped
     */
    default void ApplyHoodMapped(int[] hood,int validCount,IndexAction Action){
        for(int i=0;i<validCount;i++){
            Action.Action(hood[i]);
        }
    }

    /**
     * returns whether a valid index exists in the neighborhood
     */
    default public boolean ContainsValidI(int[] hood,int centerI,Coords3DBool IsValid){
        return ContainsValidI(hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI),IsValid);
    }

    /**
     * returns whether a valid index exists in the neighborhood
     */
    default public boolean ContainsValidI(int[] hood,int centerX,int centerY,int centerZ,Coords3DBool IsValid){
        int iStart=hood.length/4;
        for(int i=iStart;i<hood.length;i+=3){
            int x=hood[i]+centerX;
            int y=hood[i+1]+centerY;
            int z=hood[i+2]+centerZ;
            if(!Util.InDim(x,Xdim())){
                if(IsWrapX()){
                    x=Util.Wrap(x,Xdim());
                }else{
                    continue;
                }
            }
            if(!Util.InDim(y,Ydim())){
                if(IsWrapY()){
                    y=Util.Wrap(y,Ydim());
                }else{
                    continue;
                }
            }
            if(!Util.InDim(z,Zdim())){
                if(IsWrapZ()){
                    z=Util.Wrap(z,Zdim());
                }else{
                    continue;
                }
            }
            if(IsValid.Eval(x,y,z)){
                return true;
            }
        }
        return false;
    }

    /**
     * gets a random index from the full neighborhood, if the index does not map, returns -1
     */
    default public int RandomHoodI(int[] hood,int centerX,int centerY,int centerZ,Rand rng){
        int i=rng.Int(hood.length/4);
        return GetHoodI(hood,centerX,centerY,centerZ,i);
    }


    /**
     * gets one mapped index from the neighborhood
     */
    default public int GetHoodI(int[] hood,int centerX,int centerY,int centerZ,int entryIndex){
        int i=entryIndex*3+hood.length/4;
        int x=hood[i]+centerX;
        int y=hood[i+1]+centerY;
        int z=hood[i+2]+centerZ;
        if(!Util.InDim(x,Xdim())){
            if(IsWrapX()){
                x=Util.Wrap(x,Xdim());
            }else{
                return -1;
            }
        }
        if(!Util.InDim(y,Ydim())){
            if(IsWrapY()){
                y=Util.Wrap(y,Ydim());
            }else{
                return -1;
            }
        }
        if(!Util.InDim(z,Zdim())){
            if(IsWrapZ()){
                y=Util.Wrap(z,Zdim());
            }else{
                return -1;
            }
        }
        return I(x,y,z);
    }

    /**
     * gets the displacement from the first coorinate to the second. using wraparound if allowed over the given axis to
     * find the shortest displacement.
     */
    default public double DispX(double x1,double x2){
        if(IsWrapX()){
            return Util.DispWrap(x2,x1,Xdim());
        }else{
            return x2-x1;
        }
    }

    /**
     * gets the displacement from the first coorinate to the second. using wraparound if allowed over the given axis to
     * find the shortest displacement.
     */
    default public double DispY(double y1,double y2){
        if(IsWrapY()){
            return Util.DispWrap(y2,y1,Ydim());
        }else{
            return y2-y1;
        }
    }

    /**
     * gets the displacement from the first coorinate to the second. using wraparound if allowed over the given axis to
     * find the shortest displacement.
     */
    default public double DispZ(double z1,double z2){
        if(IsWrapY()){
            return Util.DispWrap(z2,z1,Zdim());
        }else{
            return z2-z1;
        }
    }

    /**
     * gets the distance between two positions with or without grid wrap around (if wraparound is enabled, the shortest
     * distance taking this into account will be returned)
     */
    default public double Dist(double x1,double y1,double z1,double x2,double y2,double z2){
        return Math.sqrt(DistSquared(x1,y1,z1,x2,y2,z2));
    }
    default public double Dist(int i1, int i2) {
        int x1=ItoX(i1);
        int y1=ItoY(i1);
        int z1=ItoZ(i1);
        int x2=ItoX(i2);
        int y2=ItoY(i2);
        int z2=ItoZ(i2);
        return Dist(x1,y1,z1,x2,y2,z2);
    }

    default public double Dist(int i1, int x2,int y2,int z2) {
        int x1=ItoX(i1);
        int y1=ItoY(i1);
        int z1=ItoZ(i1);
        return Dist(x1,y1,z1,x2,y2,z2);
    }
    /**
     * gets the distance squared between two positions with or without grid wrap around (if wraparound is enabled, the
     * shortest distance taking this into account will be returned) more efficient than the Dist function above as it
     * skips a square-root calculation.
     */
    default public double DistSquared(double x1,double y1,double z1,double x2,double y2,double z2){
        double xDisp=DispX(x1,x2);
        double yDisp=DispY(y1,y2);
        double zDisp=DispZ(z1,z2);
        return NormSquared(xDisp,yDisp,zDisp);

    }

    default public void BucketFillMask(boolean[]out,int[]nearbyHood,boolean[]scratch,ArrayList<Integer>scratch2,int iStart,IndexCoords3DBool IsMask){
        Arrays.fill(scratch,false);
        Arrays.fill(out,false);
        scratch2.clear();
        scratch2.add(iStart);
        scratch[iStart]=true;
        while(scratch2.size()>0){
            int i=scratch2.remove(scratch2.size()-1);
            int x=ItoX(i);int y=ItoY(i);int z=ItoZ(i);
            int ct=MapHood(nearbyHood,x,y,z,(i2,x2,y2,z2)->!scratch[i2]);
            for(int j=0;j<ct;j++){
                int i3=nearbyHood[j];
                int x3=ItoX(i3);
                int y3=ItoY(i3);
                int z3=ItoZ(i3);
                scratch[i3]=true;
                if(IsMask.Eval(i3,x3,y3,z3)){
                    out[i3]=true;
                    scratch2.add(i3);
                }
            }
        }
    }

    default public void BorderFindMask(boolean[]out,int[]nearbyHood,boolean[]scratch,ArrayList<Integer>scratch2,int iStart,IndexCoords3DBool IsMask){
        Arrays.fill(scratch,false);
        Arrays.fill(out,false);
        scratch2.clear();
        scratch2.add(iStart);
        scratch[iStart]=true;
        while(scratch2.size()>0){
            int i=scratch2.remove(scratch2.size()-1);
            int x=ItoX(i);int y=ItoY(i);int z=ItoZ(i);
            if(IsMask.Eval(i,x,y,z)){
                out[i]=true;
            }else{
                int ct=MapHood(nearbyHood,x,y,z,(i2,x2,y2,z2)->!scratch[i2]);
                for(int j=0;j<ct;j++){
                    int i3=nearbyHood[j];
                    scratch[i3]=true;
                    scratch2.add(i3);
                }
            }
        }
    }

    default public void ForEdge(IndexCoords3DAction Action){
        int xDim=Xdim();
        int yDim=Ydim();
        int zDim=Zdim();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int z=0;
                Action.Action(I(x,y,z),x,y,z);
                z=zDim-1;
                Action.Action(I(x,y,z),x,y,z);
            }
        }
        for (int x = 0; x < xDim; x++) {
            for (int z = 1; z < zDim-1; z++) {
                int y=0;
                Action.Action(I(x,y,z),x,y,z);
                y=yDim-1;
                Action.Action(I(x,y,z),x,y,z);
            }
        }
        for (int y = 1; y < yDim-1; y++) {
            for (int z = 1; z < zDim-1; z++) {
                int x=0;
                Action.Action(I(x,y,z),x,y,z);
                x=xDim-1;
                Action.Action(I(x,y,z),x,y,z);
            }
        }
    }


    public int Xdim();

    public int Ydim();

    public int Zdim();

    public int Length();

    public boolean IsWrapX();

    public boolean IsWrapY();

    public boolean IsWrapZ();
}

