package com.linsir.designpattern.factory;

public class TeacherWorkFactory implements IWorkFactory {
    public IWork getWork() {
        return new TeacherWork();
    }
}
