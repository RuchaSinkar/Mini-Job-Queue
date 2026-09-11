package com.example.MiniJobQueue.queue;

import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobPriority;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.exception.JobNotFoundException;
import com.example.MiniJobQueue.repository.JobHistoryRepository;
import com.example.MiniJobQueue.repository.JobRepository;
import com.example.MiniJobQueue.service.JobHistoryService;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class JobConsumer {

    private final JobHistoryService jobHistoryService;
    private final JobRepository jobRepository;
    private static final Integer MAX_RETRIES = 3;
    private final JobProducer jobProducer;
    private final Counter jobsCompleted;
    private final Counter jobsFailed;
    private final Counter jobsRetried;
    private final Timer jobProcessingTime;

    public JobConsumer(
            JobHistoryService jobHistoryService,
            JobRepository jobRepository,
            JobProducer jobProducer,
            MeterRegistry meterRegistry
    ) {
        this.jobHistoryService = jobHistoryService;
        this.jobRepository = jobRepository;
        this.jobProducer = jobProducer;

        this.jobsCompleted = Counter.builder("jobs_completed_total")
                .description("Total number of completed jobs")
                .register(meterRegistry);

        this.jobsFailed = Counter.builder("jobs_failed_total")
                .description("Total number of failed jobs")
                .register(meterRegistry);

        this.jobsRetried = Counter.builder("jobs_retried_total")
                .description("Total number of job retry attempts")
                .register(meterRegistry);

        this.jobProcessingTime = Timer.builder("job_processing_duration")
                .description("Time taken to process jobs")
                .register(meterRegistry);
    }

    // Runs once at startup, after dependency injection is complete.
    // Seeds the in-memory counters with historical DB counts so a
    // restart doesn't reset jobs_completed_total / jobs_failed_total / jobs_retried_total back to 0.
    @PostConstruct
    public void initializeCountersFromDatabase() {

        long completedFromDb = jobRepository.countByStatus(JobStatus.COMPLETED);
        long failedFromDb = jobRepository.countByStatus(JobStatus.FAILED);
        long retriedFromDb = jobRepository.getTotalRetries();

        if (completedFromDb > 0) {
            jobsCompleted.increment(completedFromDb);
        }
        if (failedFromDb > 0) {
            jobsFailed.increment(failedFromDb);
        }
        if (retriedFromDb > 0) {
            jobsRetried.increment(retriedFromDb);
        }

        System.out.println("SEEDED COUNTERS FROM DB -> completed: " + completedFromDb
                + ", failed: " + failedFromDb
                + ", retried: " + retriedFromDb);
    }

    @RabbitListener(queues = "job.queue", containerFactory = "rabbitListenerContainerFactory")
    public void consumeJob(Long id) {
        System.out.println("CONSUMER RECEIVED JOB: " + id);
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobNotFoundException("Job not found"));
        if (job.getStatus() == JobStatus.CANCELLED || job.getStatus() == JobStatus.COMPLETED || job.getStatus() == JobStatus.FAILED) return;

        Timer.Sample timer = Timer.start();

        try {
            int updated = jobRepository.claimJob(id, JobStatus.PROCESSING, JobStatus.QUEUED);
            if (updated == 0) return;
            JobStatus fromStatus = JobStatus.QUEUED;
            job.setStatus(JobStatus.PROCESSING);
            jobHistoryService.recordHistory(job, fromStatus, JobStatus.PROCESSING, null);
            job.setStartedAt(LocalDateTime.now());

            System.out.println("PROCESSING JOB: " + job.getId());
            if (job.getId().equals(22L)) {
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
            fromStatus = job.getStatus();
            job.setStatus(JobStatus.COMPLETED);
            jobHistoryService.recordHistory(job, fromStatus, JobStatus.COMPLETED, null);
            job.setUpdatedAt(LocalDateTime.now());
            job.setCompletedAt(LocalDateTime.now());
            Duration.between(job.getStartedAt(), job.getCompletedAt());
            jobRepository.save(job);
            System.out.println("JOB " + id + " COMPLETED");
            jobsCompleted.increment();

        } catch (IOException e) {
            int retries = job.getRetryCount();
            retries++;
            System.out.println("RETRYING JOB " + id + " - ATTEMPT " + retries);
            if (retries <= MAX_RETRIES) {
                job.setRetryCount(retries);
                JobStatus fromStatus = job.getStatus();
                job.setStatus(JobStatus.QUEUED);
                jobHistoryService.recordHistory(job, fromStatus, JobStatus.QUEUED, "Retry attempt " + retries);
                job.setUpdatedAt(LocalDateTime.now());
                jobRepository.save(job);

                jobsRetried.increment();

                jobProducer.sendRetryJob(id, retries, job.getPriority());
            } else {
                System.out.println("JOB " + id + " FAILED AFTER MAX RETRIES");
                JobStatus fromStatus = job.getStatus();
                job.setStatus(JobStatus.FAILED);
                job.setUpdatedAt(LocalDateTime.now());
                job.setFailureReason(e.getMessage());
                jobHistoryService.recordHistory(job, fromStatus, JobStatus.FAILED, job.getFailureReason());
                jobRepository.save(job);

                jobsFailed.increment();

                jobProducer.sendFailedJob(id, job.getPriority());
            }
        } finally {
            timer.stop(jobProcessingTime);
        }
    }
}