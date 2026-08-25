package com.example.MiniJobQueue.queue;

import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.exception.JobNotFoundException;
import com.example.MiniJobQueue.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FailedJobConsumer {
    private final JobRepository jobRepository;

    @RabbitListener(queues = "failed.job.queue")
    public void consumeFailedJob(Long id){
        Job job=jobRepository.findById(id).orElseThrow(()->new JobNotFoundException("Job Not Found"));
        log.error("Job {} permanently failed after {} retries due to {}",
                job.getId(),
                job.getRetryCount(),
                job.getFailureReason());
    }
}
