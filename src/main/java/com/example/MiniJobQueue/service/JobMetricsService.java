package com.example.MiniJobQueue.service;

import com.example.MiniJobQueue.dto.JobMetricsResponse;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobMetricsService {
    private final JobRepository jobRepository;
    public JobMetricsResponse getMetrics(){
        JobMetricsResponse jobMetricsResponse=new JobMetricsResponse();
        jobMetricsResponse.setTotalJobs(jobRepository.count());
        jobMetricsResponse.setCompletedJobs(jobRepository.countByStatus(JobStatus.COMPLETED));
        jobMetricsResponse.setFailedJobs(jobRepository.countByStatus(JobStatus.FAILED));
        jobMetricsResponse.setQueuedJobs(jobRepository.countByStatus(JobStatus.QUEUED));
        jobMetricsResponse.setProcessingJobs(jobRepository.countByStatus(JobStatus.PROCESSING));
        return jobMetricsResponse;
    }
}
