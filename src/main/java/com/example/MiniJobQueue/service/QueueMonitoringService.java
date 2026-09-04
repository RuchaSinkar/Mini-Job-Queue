package com.example.MiniJobQueue.service;

import com.example.MiniJobQueue.dto.QueueMonitoringResponse;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QueueMonitoringService {

    private final RabbitAdmin rabbitAdmin;

    public QueueMonitoringResponse getMessagesCount() {

        QueueInformation queueInformation =
                rabbitAdmin.getQueueInfo("job.queue");

        long readyMessages = queueInformation.getMessageCount();

        // RabbitAdmin does not expose unacked messages directly.
        // For now, calculate monitoring for ready messages.
        long unackedMessages = 0;

        long totalMessages = readyMessages + unackedMessages;

        return new QueueMonitoringResponse(
                "job.queue",
                readyMessages,
                unackedMessages,
                totalMessages
        );
    }
}