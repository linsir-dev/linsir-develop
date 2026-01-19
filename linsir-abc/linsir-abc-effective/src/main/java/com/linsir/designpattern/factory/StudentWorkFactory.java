package com.linsir.designpattern.factory;

public class StudentWorkFactory implements IWorkFactory {
    public IWork getWork() {
        return new StudentWork();
    }
}
