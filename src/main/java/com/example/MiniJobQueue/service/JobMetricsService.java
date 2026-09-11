package com.example.MiniJobQueue.service;

import com.example.MiniJobQueue.dto.JobMetricsResponse;
import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.repository.JobRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobMetricsService {

    private final JobRepository jobRepository;
    private final MeterRegistry meterRegistry;

    public JobMetricsResponse getMetrics() {

        JobMetricsResponse jobMetricsResponse = new JobMetricsResponse();

        long totalJobCount = jobRepository.count();

        long totalQueueWaitTime = 0;
        long totalProcessingTime = 0;

        long totalRetries = jobRepository.getTotalRetries();
        long retriedJobs = jobRepository.countByRetryCountGreaterThan(0);

        long failedJobs = jobRepository.countByStatus(JobStatus.FAILED);

        Double retryRate = 0.0;
        Double failureRate = 0.0;

        if (totalJobCount != 0) {

            failureRate =
                    (double) failedJobs / totalJobCount * 100;

            retryRate =
                    (double) retriedJobs / totalJobCount * 100;
        }

        List<Job> completedJobs =
                jobRepository.findByStatusAndStartedAtIsNotNullAndCompletedAtIsNotNull(
                        JobStatus.COMPLETED
                );

        if (!completedJobs.isEmpty()) {

            for (Job completedJob : completedJobs) {

                LocalDateTime createdAt =
                        completedJob.getCreatedAt();

                LocalDateTime startedAt =
                        completedJob.getStartedAt();

                LocalDateTime completedAt =
                        completedJob.getCompletedAt();

                Duration queueWaitTime =
                        Duration.between(createdAt, startedAt);

                Duration queueProcessTime =
                        Duration.between(startedAt, completedAt);

                totalQueueWaitTime += queueWaitTime.toMillis();

                totalProcessingTime += queueProcessTime.toMillis();
            }

            jobMetricsResponse.setAverageProcessingTime(
                    (double) totalProcessingTime / completedJobs.size()
            );

            jobMetricsResponse.setAverageQueueWaitTime(
                    (double) totalQueueWaitTime / completedJobs.size()
            );

        } else {

            jobMetricsResponse.setAverageProcessingTime(0.0);
            jobMetricsResponse.setAverageQueueWaitTime(0.0);
        }

        jobMetricsResponse.setTotalJobs(totalJobCount);
        jobMetricsResponse.setCompletedJobs(
                jobRepository.countByStatus(JobStatus.COMPLETED)
        );
        jobMetricsResponse.setFailedJobs(failedJobs);
        jobMetricsResponse.setQueuedJobs(
                jobRepository.countByStatus(JobStatus.QUEUED)
        );
        jobMetricsResponse.setProcessingJobs(
                jobRepository.countByStatus(JobStatus.PROCESSING)
        );

        jobMetricsResponse.setRetriedJobs(retriedJobs);
        jobMetricsResponse.setTotalRetries(totalRetries);

        return jobMetricsResponse;
    }
    @PostConstruct
    public void registerMetrics() {

        System.out.println("REGISTERING JOB METRICS");

        Gauge.builder(
                        "job_total_jobs",
                        this,
                        service -> service.calculateTotalJobs()
                )
                .description("Total number of jobs")
                .register(meterRegistry);

        Gauge.builder(
                        "job_completed_jobs",
                        this,
                        service -> service.calculateCompletedJobs()
                )
                .description("Total number of completed jobs")
                .register(meterRegistry);

        Gauge.builder(
                        "job_failed_jobs",
                        this,
                        service -> service.calculateFailedJobs()
                )
                .description("Total number of failed jobs")
                .register(meterRegistry);

        Gauge.builder(
                        "job_queued_jobs",
                        this,
                        service -> service.calculateQueuedJobs()
                )
                .description("Current number of queued jobs")
                .register(meterRegistry);

        Gauge.builder(
                        "job_processing_jobs",
                        this,
                        service -> service.calculateProcessingJobs()
                )
                .description("Current number of processing jobs")
                .register(meterRegistry);

        Gauge.builder(
                        "job_retried_jobs",
                        this,
                        service -> service.calculateRetriedJobs()
                )
                .description("Number of jobs that have been retried")
                .register(meterRegistry);

        Gauge.builder(
                        "job_total_retries",
                        this,
                        service -> service.calculateTotalRetries()
                )
                .description("Total number of retry attempts across all jobs")
                .register(meterRegistry);

        Gauge.builder(
                        "job_failure_rate",
                        this,
                        service -> service.calculateFailureRate()
                )
                .description("Percentage of jobs that failed")
                .register(meterRegistry);

        Gauge.builder(
                        "job_retry_rate",
                        this,
                        service -> service.calculateRetryRate()
                )
                .description("Percentage of jobs that were retried")
                .register(meterRegistry);

        Gauge.builder(
                        "job_average_queue_wait_time",
                        this,
                        service -> service.calculateAverageQueueWaitTime()
                )
                .description("Average time jobs wait in the queue before processing")
                .register(meterRegistry);

        System.out.println("JOB METRICS REGISTERED");
    }

    private double calculateFailureRate() {

        long totalJobs = jobRepository.count();

        if (totalJobs == 0) {
            return 0.0;
        }

        long failedJobs =
                jobRepository.countByStatus(JobStatus.FAILED);

        return (double) failedJobs / totalJobs * 100;
    }

    private double calculateAverageQueueWaitTime() {

        List<Job> completedJobs =
                jobRepository.findByStatusAndStartedAtIsNotNullAndCompletedAtIsNotNull(
                        JobStatus.COMPLETED
                );

        if (completedJobs.isEmpty()) {
            return 0.0;
        }

        long totalWaitTime = 0;

        for (Job job : completedJobs) {
            Duration waitTime =
                    Duration.between(job.getCreatedAt(), job.getStartedAt());

            totalWaitTime += waitTime.toMillis();
        }

        return (double) totalWaitTime / completedJobs.size() / 1000.0;
    }
    private double calculateTotalRetries() {
        return jobRepository.getTotalRetries();
    }

    private double calculateTotalJobs() {
        return jobRepository.count();
    }

    private double calculateCompletedJobs() {
        return jobRepository.countByStatus(JobStatus.COMPLETED);
    }

    private double calculateFailedJobs() {
        return jobRepository.countByStatus(JobStatus.FAILED);
    }

    private double calculateQueuedJobs() {
        return jobRepository.countByStatus(JobStatus.QUEUED);
    }

    private double calculateProcessingJobs() {
        return jobRepository.countByStatus(JobStatus.PROCESSING);
    }

    private double calculateRetriedJobs() {
        return jobRepository.countByRetryCountGreaterThan(0);
    }

    private double calculateRetryRate() {

        long totalJobs = jobRepository.count();

        if (totalJobs == 0) {
            return 0.0;
        }

        long retriedJobs =
                jobRepository.countByRetryCountGreaterThan(0);

        return (double) retriedJobs / totalJobs * 100;
    }
}