package com.moviebooking.show_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- Exchanges (same names as reservation-service; declarations are idempotent) ---
    public static final String RESERVATION_EXCHANGE = "reservation.events";
    public static final String TICKET_EXCHANGE = "ticket.events";
    public static final String PAYMENT_EXCHANGE = "payment.events";

    public static final String TICKET_UPDATE_QUEUE = "ticket.update.queue";

    public static final String ROUTING_KEY_RESERVATION_CREATED = "reservation.created";
    public static final String ROUTING_KEY_TICKET_RESERVED = "ticket.reserved";
    public static final String ROUTING_KEY_TICKET_FAILED = "ticket.reservation.failed";
    public static final String ROUTING_KEY_PAYMENT_SUCCEEDED = "payment.succeeded";
    public static final String ROUTING_KEY_PAYMENT_FAILED = "payment.failed";

    // --- Exchange Beans ---
    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(RESERVATION_EXCHANGE);
    }

    @Bean
    public TopicExchange ticketExchange() {
        return new TopicExchange(TICKET_EXCHANGE);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue ticketUpdateQueue() {
        return QueueBuilder.durable(TICKET_UPDATE_QUEUE).build();
    }

    @Bean
    public Binding reservationCreatedBinding() {
        return BindingBuilder.bind(ticketUpdateQueue()).to(reservationExchange()).with(ROUTING_KEY_RESERVATION_CREATED);
    }

    @Bean
    public Binding paymentSucceededBinding() {
        return BindingBuilder.bind(ticketUpdateQueue()).to(paymentExchange()).with(ROUTING_KEY_PAYMENT_SUCCEEDED);
    }

    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder.bind(ticketUpdateQueue()).to(paymentExchange()).with(ROUTING_KEY_PAYMENT_FAILED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
