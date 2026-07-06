package com.moviebooking.auth_service.service.impl;

import com.moviebooking.auth_service.configuration.RabbitMQConfig;
import com.moviebooking.auth_service.service.MessagingService;
import com.moviebooking.shared.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessagingServiceImpl implements MessagingService {

    private final RabbitTemplate rabbitTemplate;

    public MessagingServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendCreatUserEvent(UserRegisteredEvent user) {
        log.info("Sending User Registration Event To User Service");
        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EVENTS_EXCHANGE,RabbitMQConfig.USER_REGISTERED_ROUTING_KEY, user);
    }
}
