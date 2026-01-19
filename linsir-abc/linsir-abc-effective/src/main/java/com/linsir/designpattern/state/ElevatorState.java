package com.linsir.designpattern.state;

// 状态接口

public interface ElevatorState {

    void openDoors();
    void closeDoors();
    void move();
    void stop();
}
