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

    public void sendRetryJob(Long id,int retryCount){
        switch (retryCount){
            case 1:
                rabbitTemplate.convertAndSend(
                        "job.exchange",
                        "retry.1",
                id
                );
                break;

            case 2:
                rabbitTemplate.convertAndSend(
                        "job.exchange",
                        "retry.2",
                        id
                );
                break;

            case 3:
                rabbitTemplate.convertAndSend(
                        "job.exchange",
                        "retry.3",
                        id
                );
                break;
        }
    }
}
