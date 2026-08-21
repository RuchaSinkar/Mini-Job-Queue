package com.example.MiniJobQueue.queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
// AMQP - Advanced Message Queueing Protocol- communication protocol that defines how applications can send message through message brokers

@Configuration

public class RabbitMQConfig {

    @Bean
    // create queue
    public Queue jobQueue(){
        return new Queue("job.queue");
    }


    @Bean
    // create exchange
    public DirectExchange jobExchange(){
        return new DirectExchange("job.exchange");
    }

    @Bean
    // create binding
    public Binding jobBinding(Queue jobQueue, DirectExchange jobExchange){
        return BindingBuilder
                .bind(jobQueue)
                .to(jobExchange)
                .with("job.routing.key");
    }
}
