package HAL.GridsAndAgents;

import HAL.Interfaces.OverlapForceResponse2D;
import HAL.Rand;
import HAL.Util;

import java.io.Serializable;
import java.util.ArrayList;

import static HAL.Util.*;

/**
 * Spherical cow model of 2D cells
 */
public class SphericalAgent2D<A extends SphericalAgent2D,G extends AgentGrid2D<A>> extends AgentPT2D<G> implements Serializable {
    // the radius property is used during SumForces to determine collisions
    public double radius;
    // the x and y velocity properties are added to by SumForces and applied to the agent's position by calling
    // ForceMove. adding to these properties can cause agents to move in a particular direction
    public double xVel;
    public double yVel;
    private static ThreadLocal<double[]> scratchCoordDefault=new ThreadLocal<>();

    /**
     * a default initialization function that sets the radius based on the argument, and the x and y velocities to 0
     */
    public void DefaultInit(double radius) {
        this.radius = radius;
        this.xVel = 0;
        this.yVel = 0;
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
    public double SumForces(double interactionRad, OverlapForceResponse2D<A> OverlapFun) {
        return SumForces(interactionRad,OverlapFun,null);
    }
    public double SumForces(double interactionRad, OverlapForceResponse2D<A> OverlapFun,Rand resolvePerfectOverlap) {
        ArrayList<A> scratchAgentList = G.GetFreshAgentSearchArr();
        scratchAgentList.clear();
        double sum = 0;
        G.GetAgentsRadApprox(scratchAgentList, Xpt(), Ypt(), interactionRad);
        for (A a : scratchAgentList) {
            if (a != this) {
                double xComp = DispX(a.Xpt());
                double yComp = DispY(a.Ypt());
                if (xComp == 0 && yComp == 0) {
                    if(resolvePerfectOverlap==null) {
                        xComp = Math.random() - 0.5;
                        yComp = Math.random() - 0.5;
                    }
                    else{
                        xComp=resolvePerfectOverlap.Double()-0.5;
                        yComp=resolvePerfectOverlap.Double()-0.5;
                    }
                }
                double dist = Norm(xComp, yComp);
                if (dist < interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force = OverlapFun.CalcForce(touchDist, a);
                    xVel -= (xComp / dist) * force;
                    yVel -= (yComp / dist) * force;
                    if (force > 0) {
                        sum += Math.abs(force);
                    }
                }
            }
        }
        return sum;
    }

    public <T extends SphericalAgent2D> double SumForces(ArrayList<T> neighbors,ArrayList<double[]> displacementInfo, OverlapForceResponse2D<T> OverlapFun) {
        double sum=0;
        for (int i = 0; i < neighbors.size(); i++) {
            T a = neighbors.get(i);
            if(a!=this) {
                double[] info = displacementInfo.get(i);
                double dist = info[0];
                double xComp = info[1];
                double yComp = info[2];
                double touchDist = (radius + a.radius) - dist;
                double force = OverlapFun.CalcForce(touchDist, a);
                xVel += (xComp / dist) * force;
                yVel += (yComp / dist) * force;
                if (force > 0) {
                    sum += Math.abs(force);
                }
            }
        }
        return sum;
    }
    public <T extends SphericalAgent2D> double SumForces(double interactionRad, AgentGrid2D<T> otherGrid, OverlapForceResponse2D<T> OverlapFun) {
        return SumForces(interactionRad, otherGrid, OverlapFun, null);
    }

    /**
     * similar to the SumForces function above, but it can be used with other AgentGrids
     */
    public <T extends SphericalAgent2D> double SumForces(double interactionRad, AgentGrid2D<T> otherGrid, OverlapForceResponse2D<T> OverlapFun, Rand resolvePerfectOverlap) {
        ArrayList<T> scratchAgentList = otherGrid.GetFreshAgentSearchArr();
        scratchAgentList.clear();
        double sum = 0;
        otherGrid.GetAgentsRadApprox(scratchAgentList, Xpt(), Ypt(), interactionRad);
        for (T a : scratchAgentList) {
            if (a != this) {
                double xComp = DispX(a.Xpt());
                double yComp = DispY(a.Ypt());
                if (xComp == 0 && yComp == 0) {
                    if(resolvePerfectOverlap==null) {
                        xComp = Math.random() - 0.5;
                        yComp = Math.random() - 0.5;
                    }
                    else{
                        xComp=resolvePerfectOverlap.Double()-0.5;
                        yComp=resolvePerfectOverlap.Double()-0.5;
                    }
                }
                double dist = Norm(xComp, yComp);
                if (dist < interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force = OverlapFun.CalcForce(touchDist, a);
                    xVel += (xComp / dist) * force;
                    yVel += (yComp / dist) * force;
                    if (force > 0) {
                        sum += Math.abs(force);
                    }
                }
            }
        }
        return sum;
    }

    /**
     * mulitplies xVel and yVel by frictionConst. if frictionConst = 1, then no friction force will be applied, if
     * frictionConst = 0, then the cell won't move.
     */
    public void ApplyFriction(double frictionConst) {
        xVel *= frictionConst;
        yVel *= frictionConst;
    }

    /**
     * caps the xVel and yVel variables such that their norm is not greater than maxVel
     */
    public void CapVelocity(double maxVel) {
        double maxVelSq = maxVel * maxVel;
        double normSq = NormSquared(xVel, yVel);
        if (normSq > maxVelSq) {
            double convFactor = maxVel / Math.sqrt(normSq);
            xVel *= convFactor;
            yVel *= convFactor;
        }
    }

    /**
     * adds xVel and yVel property values to the x,y position of the agent.
     */
    public void ForceMove() {
        MoveSafePT(Xpt() + xVel, Ypt() + yVel);
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
    public A Divide(double divRadius, double[] scratchCoordArr, Rand rn) {
        if (rn != null) {
            rn.RandomPointOnCircleEdge(divRadius, scratchCoordArr);
        }
        double normSq = Util.NormSquared(scratchCoordArr[0], scratchCoordArr[1]);
        if (normSq != divRadius * divRadius) {
            double norm = Math.sqrt(normSq);
            scratchCoordArr[0] = scratchCoordArr[0] * divRadius / norm;
            scratchCoordArr[1] = scratchCoordArr[1] * divRadius / norm;
        }
        A child = G.NewAgentPTSafe(Xpt() + scratchCoordArr[0], Ypt() + scratchCoordArr[1], Xpt(), Ypt());
        MoveSafePT(Xpt() - scratchCoordArr[0], Ypt() - scratchCoordArr[1]);
        return child;
    }
    public A Divide(double divRadius,Rand rn) {
        if(scratchCoordDefault.get()==null){
            scratchCoordDefault.set(new double[2]);
        }
        return Divide(divRadius,scratchCoordDefault.get(),rn);
    }
}
