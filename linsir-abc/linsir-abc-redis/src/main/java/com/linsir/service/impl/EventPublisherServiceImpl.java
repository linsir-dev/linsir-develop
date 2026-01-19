package com.linsir.service.impl;

import com.linsir.components.NotifyMsgEvent;
import com.linsir.components.PersonSaveEvent;
import com.linsir.service.EventPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherServiceImpl implements EventPublisherService {


    private ApplicationEventPublisher  eventPublisher;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    @Override
    public void personSaveEventPublish(PersonSaveEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void notifyMsgEventPublish(NotifyMsgEvent event) {
        eventPublisher.publishEvent(event);
    }
}
