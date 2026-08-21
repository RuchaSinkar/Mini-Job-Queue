package com.example.MiniJobQueue.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendJob(Long id){
        rabbitTemplate.convertAndSend(
                "job.exchange",
                "job.routing.key",
                id
        );
    }
}
