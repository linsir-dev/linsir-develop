package com.linsir.designpattern.state;

public class Elevator {

    private ElevatorState state;

    public Elevator() {
        state = new CloseState(); // 初始状态为关门状态
    }

    public void setState(ElevatorState state) {
        this.state = state;
    }

    public void openDoors() {
        state.openDoors();
    }

    public void closeDoors() {
        state.closeDoors();
    }

    public void move() {
        state.move();
    }

    public void stop() {
        state.stop();
    }
}
