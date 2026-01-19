package com.linsir.designpattern.state;

public class OpenState implements ElevatorState{

    @Override
    public void openDoors() {
        System.out.println("Doors are already open.");
    }

    @Override
    public void closeDoors() {
        System.out.println("Closing doors.");
    }

    @Override
    public void move() {
        System.out.println("Cannot move while doors are open.");
    }

    @Override
    public void stop() {
        System.out.println("Stopping while doors are open.");
    }
}
