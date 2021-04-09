package com.steshkovladyslav.transportexchangebackend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steshkovladyslav.transportexchangebackend.dto.EventType;
import com.steshkovladyslav.transportexchangebackend.dto.ObjectType;
import com.steshkovladyslav.transportexchangebackend.dto.WsEventDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
public class WsSender {
    private final SimpMessagingTemplate template;
    private final ObjectMapper mapper;

    public WsSender(SimpMessagingTemplate template, ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    public <T> BiConsumer<EventType, T> getSender(ObjectType objectType) {
        ObjectMapper writer = mapper.setConfig(mapper.getSerializationConfig());

        return (EventType eventType, T payload) -> {
            String value;

            try {
                value = writer.writeValueAsString(payload);
            } catch (RuntimeException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            template.convertAndSend("/topic/activity", new WsEventDto(objectType, eventType, value));
        };
    }
}
