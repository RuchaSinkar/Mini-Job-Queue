package com.example.MiniJobQueue.queue;

import com.example.MiniJobQueue.enums.JobPriority;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobProducer {
    private final RabbitTemplate rabbitTemplate;
    public int getPriorityValue(JobPriority priority){
        int priorityValue = -1;
        switch (priority){
            case LOW ->
                    priorityValue=1;
            case HIGH ->
                    priorityValue=10;
            case MEDIUM ->
                    priorityValue=5;
        }
        return priorityValue;
    }
    public void sendJob(Long id, JobPriority priority){
        final int finalPriority=getPriorityValue(priority);
        rabbitTemplate.convertAndSend(
                "job.exchange",
                "job.routing.key",
                id,
                message -> {
                    message.getMessageProperties().setPriority(finalPriority);
                    return message;
                }
        );
    }

    public void sendRetryJob(Long id,int retryCount,JobPriority priority){
        final int finalPriority=getPriorityValue(priority);
        switch (retryCount){
            case 1:
                rabbitTemplate.convertAndSend(
                        "job.exchange",
                        "retry.1",
                        id,
                        message -> {
                            message.getMessageProperties().setPriority(finalPriority);
                            return message;
                        }
                );
                break;

            case 2:
                rabbitTemplate.convertAndSend(
                        "job.exchange",
                        "retry.2",
                        id,
                        message -> {
                            message.getMessageProperties().setPriority(finalPriority);
                            return message;
                        }
                );
                break;

            case 3:
                rabbitTemplate.convertAndSend(
                        "job.exchange",
                        "retry.3",
                        id,
                        message -> {
                            message.getMessageProperties().setPriority(finalPriority);
                            return message;
                        }
                );
                break;

        }
    }
    public void sendFailedJob(Long id,JobPriority priority){
        final int finalPriority=getPriorityValue(priority);
        rabbitTemplate.convertAndSend(
                "failed.job.exchange",
                "failed.job.routing.key",
                id,
                message -> {
                    message.getMessageProperties().setPriority(finalPriority);
                    return message;
                }
        );
    }
}
