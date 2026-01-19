package com.linsir.designpattern.builder;

public class FullModeBuilder extends PlayerBuilder {
    public void buildMainWindow() {
    this.player.setMainWindow("完整的窗口");
    }

    public void buildController() {
        this.player.setController("完整的控制");
    }

    @Override
    public void buildKeepList() {
        this.player.setKeepList("完整模式收藏列表");
    }

    @Override
    public void buildMenu() {
        this.player.setMenu("完整模式菜单");
    }

    @Override
    public void buildPlayList() {
        this.player.setPlayList("完整模式播放列表");
    }

}
