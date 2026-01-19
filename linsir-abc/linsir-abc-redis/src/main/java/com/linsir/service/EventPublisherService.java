package com.linsir.service;

import com.linsir.components.NotifyMsgEvent;
import com.linsir.components.PersonSaveEvent;

public interface EventPublisherService {

    public void personSaveEventPublish(PersonSaveEvent event);

    public void notifyMsgEventPublish(NotifyMsgEvent event);
}
