package com.moviebooking.reservation_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RESERVATION_EXCHANGE = "reservation.events";
    public static final String TICKET_EXCHANGE = "ticket.events";
    public static final String PAYMENT_EXCHANGE = "payment.events";

    public static final String RESERVATION_UPDATE_QUEUE = "reservation.update.queue";

    @Bean public TopicExchange reservationExchange() { return new TopicExchange(RESERVATION_EXCHANGE); }
    @Bean public TopicExchange ticketExchange() { return new TopicExchange(TICKET_EXCHANGE); }
    @Bean public TopicExchange paymentExchange() { return new TopicExchange(PAYMENT_EXCHANGE); }

    @Bean
    public Queue reservationUpdateQueue() {
        return QueueBuilder.durable(RESERVATION_UPDATE_QUEUE).build();
    }

    // Optional: lets us fill in the real totalPrice once Show Service confirms it.
    // Reservation stays PENDING here — this event alone doesn't change status.
    @Bean
    public Binding ticketReservedBinding() {
        return BindingBuilder.bind(reservationUpdateQueue()).to(ticketExchange()).with("ticket.reserved");
    }

    // Tickets weren't available — cancel immediately, payment never happens.
    @Bean
    public Binding ticketFailedBinding() {
        return BindingBuilder.bind(reservationUpdateQueue()).to(ticketExchange()).with("ticket.reservation.failed");
    }

    // The two outcomes from Payment Service — this is what actually resolves PENDING.
    @Bean
    public Binding paymentSucceededBinding() {
        return BindingBuilder.bind(reservationUpdateQueue()).to(paymentExchange()).with("payment.succeeded");
    }

    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder.bind(reservationUpdateQueue()).to(paymentExchange()).with("payment.failed");
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