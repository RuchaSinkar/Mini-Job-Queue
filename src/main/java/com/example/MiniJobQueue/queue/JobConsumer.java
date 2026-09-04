package com.example.MiniJobQueue.queue;

import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobPriority;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.exception.JobNotFoundException;
import com.example.MiniJobQueue.repository.JobHistoryRepository;
import com.example.MiniJobQueue.repository.JobRepository;
import com.example.MiniJobQueue.service.JobHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JobConsumer {

    private final JobHistoryService jobHistoryService;
    private final JobRepository jobRepository;
    private static final Integer MAX_RETRIES=3;
    private final JobProducer jobProducer;

    @RabbitListener(queues = "job.queue", containerFactory = "rabbitListenerContainerFactory")
    public void consumeJob(Long id)  {
        Job job=jobRepository.findById(id).orElseThrow(()->new JobNotFoundException("Job not found"));
        if(job.getStatus()==JobStatus.CANCELLED || job.getStatus()==JobStatus.COMPLETED || job.getStatus()==JobStatus.FAILED) return;
        try {
            int updated= jobRepository.claimJob(id,JobStatus.PROCESSING,JobStatus.QUEUED);
            if(updated==0) return;
            JobStatus fromStatus=JobStatus.QUEUED;
            job.setStatus(JobStatus.PROCESSING);
            jobHistoryService.recordHistory(job,fromStatus,JobStatus.PROCESSING,null);
            job.setStartedAt(LocalDateTime.now());
            //worker does work
            // worker does work
            if (job.getId() == 13) {
                throw new IOException("Test failure");
            }

            String filename = "report_" + job.getId() + ".txt";
            File directory = new File("reports");
            if (!directory.exists()) directory.mkdirs();

            File file = new File(directory, filename);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("JOB REPORT\n");
                writer.write("--------------");
                writer.write("Job ID: " + job.getId() + "\n");
                writer.write("Job Type: " + job.getType() + "\n");
                writer.write("Generated at: " + LocalDateTime.now() + "\n");
                writer.write("Status: COMPLETED\n");
            }
            job.setResult(file.getAbsolutePath());
            fromStatus=job.getStatus();
            job.setStatus(JobStatus.COMPLETED);
            jobHistoryService.recordHistory(job,fromStatus,JobStatus.COMPLETED,null);
            job.setUpdatedAt(LocalDateTime.now());
            job.setCompletedAt(LocalDateTime.now());
            Duration.between(job.getStartedAt(), job.getCompletedAt());
            jobRepository.save(job);
        }catch (IOException e){
            int retries=job.getRetryCount();
            retries++;
            if(retries<=MAX_RETRIES) {
                job.setRetryCount(retries);
                JobStatus fromStatus=job.getStatus();
                job.setStatus(JobStatus.QUEUED);
                jobHistoryService.recordHistory(job,fromStatus,JobStatus.QUEUED,"Retry attempt "+retries);
                job.setUpdatedAt(LocalDateTime.now());
                jobRepository.save(job);
                jobProducer.sendRetryJob(id,retries,job.getPriority());
            }
            else {
                JobStatus fromStatus=job.getStatus();
                job.setStatus(JobStatus.FAILED);
                job.setUpdatedAt(LocalDateTime.now());
                job.setFailureReason(e.getMessage());
                jobHistoryService.recordHistory(job,fromStatus,JobStatus.FAILED,job.getFailureReason());
                jobRepository.save(job);
                jobProducer.sendFailedJob(id,job.getPriority());
            }
        }
    }
}
