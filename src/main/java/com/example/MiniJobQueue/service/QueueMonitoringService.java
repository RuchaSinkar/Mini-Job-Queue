package com.example.MiniJobQueue.service;

import com.example.MiniJobQueue.dto.QueueMonitoringResponse;
import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.exception.JobNotFoundException;
import com.example.MiniJobQueue.queue.JobProducer;
import com.example.MiniJobQueue.repository.JobRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
@Service
public class QueueMonitoringService {

    private final RabbitAdmin rabbitAdmin;
    private final MeterRegistry meterRegistry;

    private final AtomicLong queueSize = new AtomicLong(0);

    public QueueMonitoringService(
            RabbitAdmin rabbitAdmin,
            MeterRegistry meterRegistry
    ) {
        this.rabbitAdmin = rabbitAdmin;
        this.meterRegistry = meterRegistry;

        Gauge.builder("job_queue_messages", queueSize, AtomicLong::get)
                .description("Number of messages waiting in the job queue")
                .register(meterRegistry);
    }

    @Scheduled(fixedRate = 5000)
    public void updateQueueSize() {

        QueueInformation queueInformation =
                rabbitAdmin.getQueueInfo("job.queue");

        if (queueInformation != null) {
            queueSize.set(queueInformation.getMessageCount());
        }
    }

    public QueueMonitoringResponse getMessagesCount() {

        QueueInformation queueInformation =
                rabbitAdmin.getQueueInfo("job.queue");

        long readyMessages = queueInformation.getMessageCount();

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