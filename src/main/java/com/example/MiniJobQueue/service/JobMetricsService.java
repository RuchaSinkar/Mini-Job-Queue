package com.example.MiniJobQueue.service;

import com.example.MiniJobQueue.dto.JobMetricsResponse;
import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobMetricsService {
    private final JobRepository jobRepository;
    public JobMetricsResponse getMetrics(){
        JobMetricsResponse jobMetricsResponse=new JobMetricsResponse();
        long totalJobCount=jobRepository.count();
        long totalQueueWaitTime=0;
        long totalProcessingTime=0;
        long totalRetries=jobRepository.getTotalRetries();
        long retriedJobs=jobRepository.countByRetryCountGreaterThan(0);
        jobMetricsResponse.setRetriedJobs(retriedJobs);
        jobMetricsResponse.setTotalRetries(totalRetries);
        long failedJobs=jobRepository.countByStatus(JobStatus.FAILED);
        Double retryRate= (double) 0;
        Double failureRate= (double) 0;
        if(totalJobCount!=0) {
            failureRate = (double) failedJobs / totalJobCount * 100;
            retryRate= (double) retriedJobs/totalJobCount * 100;
        }
        List<Job> completedJobs=jobRepository.findByStatusAndStartedAtIsNotNullAndCompletedAtIsNotNull(JobStatus.COMPLETED);
        if(!completedJobs.isEmpty()) {
            for (Job completedJob : completedJobs) {
                LocalDateTime createdAt = completedJob.getCreatedAt();
                LocalDateTime startedAt = completedJob.getStartedAt();
                LocalDateTime completedAt = completedJob.getCompletedAt();
                Duration queueWaitTime = Duration.between(createdAt, startedAt);
                Duration queueProcessTime = Duration.between(startedAt, completedAt);
                totalQueueWaitTime += queueWaitTime.toMillis();
                totalProcessingTime += queueProcessTime.toMillis();
            }
            jobMetricsResponse.setAverageProcessingTime((double) totalProcessingTime/ completedJobs.size());
            jobMetricsResponse.setAverageQueueWaitTime((double) totalQueueWaitTime/ completedJobs.size());
        }
        else {
            jobMetricsResponse.setAverageProcessingTime(0.0);
            jobMetricsResponse.setAverageQueueWaitTime(0.0);
        }
        jobMetricsResponse.setTotalJobs(totalJobCount);
        jobMetricsResponse.setCompletedJobs(jobRepository.countByStatus(JobStatus.COMPLETED));
        jobMetricsResponse.setFailedJobs(failedJobs);
        jobMetricsResponse.setQueuedJobs(jobRepository.countByStatus(JobStatus.QUEUED));
        jobMetricsResponse.setProcessingJobs(jobRepository.countByStatus(JobStatus.PROCESSING));
        return jobMetricsResponse;
    }
}
