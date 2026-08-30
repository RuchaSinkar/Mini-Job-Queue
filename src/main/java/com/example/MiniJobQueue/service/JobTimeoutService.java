package com.example.MiniJobQueue.service;

import com.example.MiniJobQueue.dto.JobResponse;
import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobTimeoutService {
    private final long timeoutSeconds;
    private final JobRepository jobRepository;
    public JobTimeoutService(
            @Value("${job.processing.timeout}")
    long timeoutSeconds, JobRepository jobRepository, JobResponse jobResponse
    )    {
        this.timeoutSeconds = timeoutSeconds;
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void findTimedOutJobs(){
        LocalDateTime time= LocalDateTime.now();
        LocalDateTime cutoff=time.minusSeconds(timeoutSeconds);
        List<Job> timedOutJobs=jobRepository.findTimeOutJobs(JobStatus.PROCESSING,cutoff);
        for (Job job:timedOutJobs){
            job.setStatus(JobStatus.FAILED);
            job.setFailureReason("Job processing timed out");
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);
        }
    }
}