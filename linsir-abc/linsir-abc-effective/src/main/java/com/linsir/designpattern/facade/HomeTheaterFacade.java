package com.linsir.designpattern.facade;

// 外观类：家庭影院外观
public class HomeTheaterFacade {

    private StereoSystem stereoSystem;
    private Projector projector;
    private LightsControl lightsControl;

    public HomeTheaterFacade() {
        stereoSystem = new StereoSystem();
        projector = new Projector();
        lightsControl = new LightsControl();
    }
    public void watchMovie() {
        System.out.println("Watching movie");
        stereoSystem.turnOn();
        projector.turnOn();
        lightsControl.turnOn();
    }

    public void releaseMovie() {
        System.out.println("Releasing movie");
        stereoSystem.turnOff();
        projector.turnOff();
        lightsControl.turnOff();
    }
}
