package com.linsir.designpattern.observer;

// 主题接口
public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}
