package com.linsir.spring.framework.ioc.dependencies;

public class AsyncCommand implements Command{

    private  Object commandState;
    @Override
    public void setState(Object object) {
        this.commandState = object;
    }

    @Override
    public String execute() {
        return "成功执行命令"+commandState.toString();
    }
}
