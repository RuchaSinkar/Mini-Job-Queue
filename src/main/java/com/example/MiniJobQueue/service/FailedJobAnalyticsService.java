package com.example.MiniJobQueue.service;

import com.example.MiniJobQueue.dto.FailedJobAnalyticsResponse;
import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.repository.JobRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FailedJobAnalyticsService {
    private final JobRepository jobRepository;
    public long getFailedJobCount(){
        return jobRepository.countByStatus(JobStatus.FAILED);
    }
    public List<Job> getLatestFailedJobs(){
        return jobRepository.findTop5ByStatusOrderByUpdatedAtDesc(JobStatus.FAILED);
    }
    public long getRetryEntriesExhaustedJobs(){
        return jobRepository.countByStatusAndFailureReasonContaining(JobStatus.FAILED,"retry");
    }
    public long getTimeoutJobs(){
        return jobRepository.countByStatusAndFailureReasonContaining(JobStatus.FAILED,"timed out");

    }
    public FailedJobAnalyticsResponse getFailureAnalytics(){
        FailedJobAnalyticsResponse jobAnalyticsResponse=new FailedJobAnalyticsResponse();
        jobAnalyticsResponse.setTotalFailedJobs(getFailedJobCount());
        jobAnalyticsResponse.setRetryExhaustedFailures(getRetryEntriesExhaustedJobs());
        jobAnalyticsResponse.setTimeoutFailures(getTimeoutJobs());
        jobAnalyticsResponse.setOtherFailures(getFailedJobCount()-getTimeoutJobs()-getRetryEntriesExhaustedJobs());
        return jobAnalyticsResponse;
    }
}
