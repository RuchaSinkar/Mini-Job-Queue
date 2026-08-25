package com.example.MiniJobQueue.queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

import java.util.HashMap;
import java.util.Map;
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
    public DirectExchange failedJobExchange(){
        return new DirectExchange("failed.job.exchange");
    }

    @Bean
    public Queue failedJobQueue(){
        return new Queue(
                "failed.job.queue",
                true,
                false,
                false
        );
    }

    @Bean
    public Binding failedJobBinding(Queue failedJobQueue, DirectExchange failedJobExchange){
        return BindingBuilder
                .bind(failedJobQueue)
                .to(failedJobExchange)
                .with("failed.job.routing.key");
    }

    @Bean
    // create binding
    public Binding jobBinding(Queue jobQueue, DirectExchange jobExchange){
        return BindingBuilder
                .bind(jobQueue)
                .to(jobExchange)
                .with("job.routing.key");
    }

    @Bean
    public Queue retryQueue1(){
        Map<String, Object> arguments=new HashMap<>();
        arguments.put("x-message-ttl",1000);
        arguments.put("x-dead-letter-exchange","job.exchange");
        arguments.put("x-dead-letter-routing-key","job.routing.key");
        return new Queue(
                "retry.queue.1",
                true,
                false,
                false,
                arguments
        );
    }

    @Bean
    public Queue retryQueue2(){
        Map<String,Object> argument=new HashMap<>();
        argument.put("x-message-ttl",2000);
        argument.put("x-dead-letter-exchange","job.exchange");
        argument.put("x-dead-letter-routing-key","job.routing.key");
        return new Queue(
                "retry.queue.2",
                true,
                false,
                false,
                argument
        );
    }

    @Bean
    public Queue retryQueue3(){
        Map<String,Object> argument=new HashMap<>();
        argument.put("x-message-ttl",4000);
        argument.put("x-dead-letter-exchange","job.exchange");
        argument.put("x-dead-letter-routing-key","job.routing.key");
        return new Queue(
                "retry.queue.3",
                true,
                false,
                false,
                argument
        );
    }

    @Bean
    public Binding retryQueue1Binding(
            Queue retryQueue1,
            DirectExchange jobExchange
    ){
        return BindingBuilder.bind(retryQueue1).to(jobExchange).with("retry.1");
    }

    @Bean
    public Binding retryQueue2Binding(
            Queue retryQueue2,
            DirectExchange jobExchange
    ){
        return BindingBuilder.bind(retryQueue2).to(jobExchange).with("retry.2");
    }

    @Bean
    public Binding retryQueue3Binding(
            Queue retryQueue3,
            DirectExchange jobExchange
    ){
        return BindingBuilder.bind(retryQueue3).to(jobExchange).with("retry.3");
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory
    ){
        SimpleRabbitListenerContainerFactory factory=new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(5);
        factory.setPrefetchCount(1);
        return factory;
    }
}
