package com.andrewexe.Engine.Physics;

public class RigidBody implements PhysicsInteractable {

    private double mass;
    private double velocity;
    private double acceleration;

    public RigidBody(double mass) {
        this.mass = mass;
        this.velocity = 0;
        this.acceleration = 0;
    }

    public double getMass() {
        return mass;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }


    @Override
    public void applyForce(double forceX, double forceY) {

    }

    @Override
    public void applyImpulse(double impulseX, double impulseY) {

    }
}
