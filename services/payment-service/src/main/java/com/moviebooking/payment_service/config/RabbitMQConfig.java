package com.moviebooking.payment_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TICKET_EXCHANGE = "ticket.events";
    public static final String PAYMENT_EXCHANGE = "payment.events";

    public static final String PAYMENT_PROCESS_QUEUE = "payment.process.queue";

    public static final String ROUTING_KEY_TICKET_RESERVED = "ticket.reserved";
    public static final String ROUTING_KEY_PAYMENT_SUCCEEDED = "payment.succeeded";
    public static final String ROUTING_KEY_PAYMENT_FAILED = "payment.failed";

    @Bean
    public TopicExchange ticketExchange() {
        return new TopicExchange(TICKET_EXCHANGE);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue paymentProcessQueue() {
        return QueueBuilder.durable(PAYMENT_PROCESS_QUEUE).build();
    }

    @Bean
    public Binding ticketReservedToPaymentProcessBinding() {
        return BindingBuilder.bind(paymentProcessQueue())
                .to(ticketExchange())
                .with(ROUTING_KEY_TICKET_RESERVED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
