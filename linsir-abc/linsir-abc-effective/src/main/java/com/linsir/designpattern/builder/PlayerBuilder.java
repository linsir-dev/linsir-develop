package com.linsir.designpattern.builder;

public abstract class PlayerBuilder {

    //方便子类进行访问
    protected Player player = new Player();

    //产品的基础的构件
    public abstract void buildMainWindow();

    public abstract void buildController();

    public void buildMenu() {
        this.player.setMenu(null);
    }


    public void buildPlayList() {
        this.player.setPlayList(null);
    }


    public void buildKeepList() {
        this.player.setKeepList(null);
    }

    public Player build() {
     this.buildMainWindow();
     this.buildController();
     this.buildMenu();
     this.buildPlayList();
     this.buildKeepList();
     return  this.player;
    }
}