package com.linsir.designpattern.state;

public class CloseState implements ElevatorState{

    @Override
    public void openDoors() {
        System.out.println("Opening doors.");
    }

    @Override
    public void closeDoors() {
        System.out.println("Doors are already closed.");
    }

    @Override
    public void move() {
        System.out.println("Moving.");
    }

    @Override
    public void stop() {
        System.out.println("Stopping.");
    }
}
