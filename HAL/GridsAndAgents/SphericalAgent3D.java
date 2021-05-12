package HAL.GridsAndAgents;

import HAL.Interfaces.OverlapForceResponse3D;
import HAL.Rand;
import HAL.Util;

import java.io.Serializable;
import java.util.ArrayList;

import static HAL.Util.*;

/**
 * Spherical cow model of 3D cells
 */
public class SphericalAgent3D<A extends SphericalAgent3D,G extends AgentGrid3D<A>> extends AgentPT3D<G> implements Serializable{
    // the radius property is used during SumForces to determine collisions
    public double radius;
    // the x and y velocity properties are added to by SumForces and applied to the agent's position by calling
    // ForceMove. adding to these properties can cause agents to move in a particular direction
    public double xVel;
    public double yVel;
    public double zVel;
    private static ThreadLocal<double[]>scratchCoordDefault=new ThreadLocal<>();
    public void DefaultInit(double radius){
        this.radius=radius;
        this.xVel=0;
        this.yVel=0;
        this.zVel=0;
    }

    /**
     * The interactionRad argument is a double that specifies how far apart to check for other agent centers to interact
     * with, and should be set to the maximum distance apart that two interacting agent centers can be. the
     * OverlapForceResponse argument must be a function that takes in an overlap and an agent, and returns a force
     * response. aka. (double,Agent) -> double. the double argument is the extent of the overlap. if this value is
     * positive, then the two agents are “overlapping” by the distance specified. if the value is negative, then the two
     * agents are separated by the distance specified. the OverlapForceResponse should return a double which indicates
     * the force to apply to the agent as a result of the overlap. if the force is positive, it will repel the agent
     * away from the overlap direction, if it is negative it will pull it towards that direction. SumForces alters the
     * xVel and yVel properites of the agent by calling OverlapForceResponse using every other agent within the
     * interactionRad.
     */
    public double SumForces(double interactionRad, OverlapForceResponse3D<A> OverlapFun){
        ArrayList<A> scratchAgentList= G.GetFreshAgentSearchArr();
        scratchAgentList.clear();
        double sum=0;
        G.GetAgentsRadApprox(scratchAgentList,Xpt(),Ypt(),Zpt(),interactionRad);
        for (A a : scratchAgentList) {
            if(a!=this){
                double xComp= DispX(a.Xpt());
                double yComp= DispY(a.Ypt());
                double zComp= DispZ(a.Zpt());
                double dist=Norm(xComp,yComp,zComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force=OverlapFun.CalcForce(touchDist,a);
                    xVel-=(xComp/dist)*force;
                    yVel-=(yComp/dist)*force;
                    zVel-=(zComp/dist)*force;
                    if(force>0){
                        sum+=Math.abs(force);
                    }
                }
            }
        }
        return sum;
    }

    /**
     * similar to the SumForces function above, but it can be used with other AgentGrids
     */
    public <T extends SphericalAgent3D> double SumForces(double interactionRad,AgentGrid3D<T> otherGrid, OverlapForceResponse3D<T> OverlapFun){
        ArrayList<T> scratchAgentList=otherGrid.GetFreshAgentSearchArr();
        scratchAgentList.clear();
        double sum=0;
        otherGrid.GetAgentsRadApprox(scratchAgentList,Xpt(),Ypt(),Zpt(),interactionRad);
        for (T a : scratchAgentList) {
            if(a!=this){
                double xComp= DispX(a.Xpt());
                double yComp= DispY(a.Ypt());
                double zComp= DispZ(a.Zpt());
                double dist=Norm(xComp,yComp,zComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force=OverlapFun.CalcForce(touchDist,a);
                    xVel-=(xComp/dist)*force;
                    yVel-=(yComp/dist)*force;
                    zVel-=(zComp/dist)*force;
                    if(force>0){
                        sum+=Math.abs(force);
                    }
                }
            }
        }
        return sum;
    }
    public <T extends SphericalAgent3D> double SumForces(ArrayList<T> neighbors,ArrayList<double[]> displacementInfo, OverlapForceResponse3D<T> OverlapFun) {
        double sum=0;
        for (int i = 0; i < neighbors.size(); i++) {
            T a = neighbors.get(i);
            double[]info=displacementInfo.get(i);
            double dist = info[0];
            double xComp = info[1];
            double yComp = info[2];
            double zComp = info[3];
            double touchDist = (radius + a.radius) - dist;
            double force = OverlapFun.CalcForce(touchDist, a);
            xVel-=(xComp/dist)*force;
            yVel-=(yComp/dist)*force;
            zVel-=(zComp/dist)*force;
            if (force > 0) {
                sum += Math.abs(force);
            }
        }
        return sum;
    }

    /**
     * mulitplies xVel and yVel by frictionConst. if frictionConst = 1, then no friction force will be applied, if
     * frictionConst = 0, then the cell won't move.
     */
    public void ApplyFriction(double frictionConst){
        xVel*=frictionConst;
        yVel*=frictionConst;
        zVel*=frictionConst;
    }

    /**
     * caps the xVel and yVel variables such that their norm is not greater than maxVel
     */
    public void CapVelocity(double maxVel){
        double maxVelSq=maxVel*maxVel;
        double normSq= NormSquared(xVel,yVel,zVel);
        if(normSq>maxVelSq){
            double convFactor=maxVel/Math.sqrt(normSq);
            xVel*=convFactor;
            yVel*=convFactor;
            zVel*=convFactor;
        }
    }

    /**
     * adds xVel and yVel property values to the x,y position of the agent.
     */
    public void ForceMove(){
        MoveSafePT(Xpt()+xVel, Ypt()+yVel, Zpt()+zVel);
    }

    /**
     * Facilitiates modeling cell division. The divRadius specifies how far apart from the center of the parent agent
     * the daughters should be separated. the scratchCoordArr will store the randomly calculated axis of division. The
     * axis is calculated using the Rand argument (HAL's random number generator object) if no Rand argument is
     * provided, the values currently in the scratchCoordArr will be used to determine the axis of division. The first
     * entry of scratchCoordArr is the x component of the axis, the second entry is the y component. division is
     * achieved by placing the newly generated daughter cell divRadius away from the parent cell using divCoordArr for
     * the x and y components of the direction, and placing the parent divRadius away in the negative direction. Divide
     * returns the newly created daughter cell.
     */
    public A Divide(double divRadius, double[] scratchCoordArr, Rand rn){
        if(rn!=null){
            rn.RandomPointOnSphereEdge(divRadius, scratchCoordArr);
        }
        else {
            double normSq = Util.NormSquared(scratchCoordArr[0], scratchCoordArr[1], scratchCoordArr[2]);
            if (normSq != divRadius * divRadius) {
                double norm = Math.sqrt(normSq);
                scratchCoordArr[0] = scratchCoordArr[0] * divRadius / norm;
                scratchCoordArr[1] = scratchCoordArr[1] * divRadius / norm;
                scratchCoordArr[2] = scratchCoordArr[2] * divRadius / norm;
            }
        }
        A child=(G.NewAgentPTSafe(Xpt()+scratchCoordArr[0],Ypt()+scratchCoordArr[1],Zpt()+scratchCoordArr[2],Xpt(),Ypt(),Zpt()));
        MoveSafePT(Xpt()-scratchCoordArr[0], Ypt()-scratchCoordArr[1], Zpt()-scratchCoordArr[2]);
        return child;
    }

    public A Divide(double divRadius,Rand rn){
        if(scratchCoordDefault.get()==null){
            scratchCoordDefault.set(new double[3]);
        }
        return Divide(divRadius,scratchCoordDefault.get(),rn);
    }
}
