package com.moviebooking.user_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_REGISTRATION_QUEUE = "user.registration.queue";
    public static final String USER_EVENTS_EXCHANGE = "user.events";
    public static final String USER_REGISTERED_ROUTING_KEY = "user.registered";

    @Bean
    public Queue userRegistrationQueue() {
        return new Queue(USER_REGISTRATION_QUEUE, true);
    }

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(USER_EVENTS_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue userRegistrationQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(userRegistrationQueue).to(userEventsExchange).with(USER_REGISTERED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
