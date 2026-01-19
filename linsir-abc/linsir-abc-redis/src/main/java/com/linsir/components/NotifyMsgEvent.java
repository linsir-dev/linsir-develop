package com.linsir.components;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class NotifyMsgEvent extends ApplicationEvent {

    private String notifyType;

    private Object object;

    public NotifyMsgEvent(Object source,String notifyType,Object object) {
        super(source);
        this.notifyType = notifyType;
        this.object = object;
    }

}
