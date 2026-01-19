package com.linsir.components;

import org.springframework.context.ApplicationEvent;

public class PersonSaveEvent extends ApplicationEvent {
    private int id;
    private String name;

    public PersonSaveEvent(Object source, int id, String name) {
        super(source);
        this.id = id;
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
