package com.linsir.designpattern.composite;

import java.util.ArrayList;
import java.util.List;

public class Directory implements FileSystemComponent{

    private String name;

    private List<FileSystemComponent> components;

    public Directory(String name) {
        this.name = name;
        components = new ArrayList<FileSystemComponent>();
    }
    public void addComponent(FileSystemComponent component) {
        components.add(component);
    }

    @Override
    public void display() {
        System.out.println("Directory: " + name);
        for (FileSystemComponent component : components) {
            component.display();
        }
    }
}
