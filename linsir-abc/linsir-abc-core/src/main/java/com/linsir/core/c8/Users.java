package com.linsir.core.c8;

import java.io.Serializable;

public class Users implements Serializable {
    private static final long serialVersionUID = -4848235426063853010L;
    private String name;
    private String pwd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Users(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }

    public Users() {
    }
}
