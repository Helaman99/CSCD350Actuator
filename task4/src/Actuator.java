/*

    Alan Rose
    CSCD 350
    Professor Tappan

    This class defines an object that moves from point 'a' to point 'b' at different speeds.

*/

package cs350.s22.task4;

import java.util.*;

public class Actuator implements I_Callbackable {

    // Instance variables
    private String id; // The identifier for the object
    private double positionMin; // The minimum position
    private double positionMax; // The maximum position
    private double positionCurrent; // The current position
    private double positionTarget; // The position we are trying to reach
    private double acceleration; // The rate of acceleration. This remains constant.
    private double currentVelocity; // The current velocity
    private double velocityLimit; // The velocity limit
    private List<I_Callbackable> callbacks; // The list of callbackable onbjects that recieve a message
                                            // when the actuator reaches its destination.

    // EVC
    public Actuator(String id, double positionMin, 
                    double positionMax, double positionInitial, 
                    double acceleration, double velocityLimit, 
                    List<I_Callbackable> callbacks) {

        this.id = id;
        this.positionMin = positionMin;
        this.positionMax = positionMax;
        // Make sure the initial position is within bounds.
        if (positionInitial >= this.positionMin && positionInitial <= this.positionMax)
            this.positionCurrent = positionInitial;
        this.positionTarget = positionCurrent;
        this.acceleration = acceleration;
        this.currentVelocity = 0;
        this.velocityLimit = velocityLimit;
        this.callbacks = callbacks;

    }

    // Getters ----------------------------------------------------------------

    public String getID() { return this.id; }
    public double getPositionMin() { return this.positionMin; }
    public double getPositionMax() { return this.positionMax; }
    public double getPosition() { return this.positionCurrent; }
    public double getPositionTarget() { return this.positionTarget; }
    public double getAcceleration() { return this.acceleration; }
    public double getVelocityLimit() { return this.velocityLimit; }
    public double getVelocity() { return this.currentVelocity; }
    public List<I_Callbackable> getCallbacks() { return this.callbacks; }

    // Checks to see if the current position is with 0.1 of the target position.
    public boolean isDone() {
        return (this.positionCurrent <= (this.positionTarget + 0.1) 
                && this.positionCurrent >= (this.positionTarget - 0.1)) ? true : false;
    }

    // Other methods -----------------------------------------------------------

    // Prints to standard output a message sent by another object.
    @Override
    public void receiveCallback_(String id, String message) {
        System.out.println(id + " says " + message);
    }

    // Sets the target position. Will throw an error if it is out of bounds.
    public void setPositionTarget(double position) {

        // Ensures that the passed in position is within the range of this acutator.
        if (position >= this.positionMin || position <= this.positionMax)
            this.positionTarget = position;
        else
            throw new RuntimeException("The specified position is out of bounds!");

    }

    // Will update the current velocity based on the specified acceleration, then updates the
    // current position based off of the newly updated velocity.
    public void update() {

        // If we need to travel backwards...
        if (this.positionTarget < this.positionCurrent) {

            // If we do not meet the velocity limit, then we subtract acceleration from velocity
            if (Math.abs(this.currentVelocity - this.acceleration) < this.velocityLimit)
                this.currentVelocity -= this.acceleration;
            else
                this.currentVelocity = -this.velocityLimit;

            // Update our position
            this.positionCurrent += this.currentVelocity;

            // If we hit the target (or go past) we set our current position to the target.
            // If we don't hit the target, then we will stop if we hit the minimum position.
            if (this.positionCurrent <= this.positionTarget) {
                this.positionCurrent = this.positionTarget;
                this.currentVelocity = 0;
            }
            else if (this.positionCurrent < this.positionMin){
                this.positionCurrent = this.positionMin;
                this.currentVelocity = 0;
            }

        }
        // If we need to travel forwards...
        else if (this.positionTarget > this.positionCurrent) {

            // If we do not meet the velocity limit, then we add acceleration to velocity
            if ((this.currentVelocity + this.acceleration) < this.velocityLimit)
                this.currentVelocity += this.acceleration;
            else
                this.currentVelocity = this.velocityLimit;

            // Update our position
            this.positionCurrent += this.currentVelocity;

            // If we hit the target (or go past) we set our current position to the target.
            // If we don't hit the target, then we will stop if we hit the maximum position.
            if (this.positionCurrent >= this.positionTarget) {
                this.positionCurrent = this.positionTarget;
                this.currentVelocity = 0;
            }
            else if (this.positionCurrent > this.positionMax){
                this.positionCurrent = this.positionMax;
                this.currentVelocity = 0;
            }

        }

        // If we reach the target, we notify all of the objects within the 'callbacks' list, and we
        // also set our current velocity to be 0.
        // The current position can be within 0.1 of the target.
        if (this.positionCurrent <= (this.positionTarget + 0.1) && this.positionCurrent >= (this.positionTarget - 0.1)) {
            this.currentVelocity = 0;
            for (int i = 0; i < callbacks.size(); i++)
                callbacks.get(i).receiveCallback_(this.id, "it reached it's target");
        }

    }

} // End class 'actuator'