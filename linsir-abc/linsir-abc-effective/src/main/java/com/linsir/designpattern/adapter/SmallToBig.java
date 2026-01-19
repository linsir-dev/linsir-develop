package com.linsir.designpattern.adapter;

public class SmallToBig implements BigPort{
    private  SmallPort smallPort;

    public SmallToBig(SmallPort smallPort)
    {
        this.smallPort=smallPort;
    }

    public void userBigPort() {
        this.smallPort.userSmallPort();
    }
}
