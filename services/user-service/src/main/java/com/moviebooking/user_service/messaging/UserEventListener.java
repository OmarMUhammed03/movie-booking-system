package com.moviebooking.user_service.messaging;

import com.moviebooking.shared.event.UserRegisteredEvent;
import com.moviebooking.user_service.config.RabbitMQConfig;
import com.moviebooking.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final UserService userService;

    @RabbitListener(queues = RabbitMQConfig.USER_REGISTRATION_QUEUE)
    public void handleUserRegistration(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent for authUserId: {}", event.getAuthUserId());
        try {
            userService.createUserFromEvent(event);
        } catch (Exception e) {
            log.error("Error processing UserRegisteredEvent: {}", e.getMessage());
            // TODO: handle error
        }
    }
}
