package com.example.MiniJobQueue.service;

import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.queue.JobProducer;
import com.example.MiniJobQueue.repository.JobRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class JobSchedulerService {
    private final JobRepository jobRepository;
    private final JobProducer jobProducer;
    private final JobHistoryService jobHistoryService;
    @Scheduled(fixedRate = 1000)// check every sec
    public void scheduleJobs(){
        List<Job> jobs=jobRepository.findByStatusAndScheduledAtLessThanEqual(JobStatus.SCHEDULED, LocalDateTime.now());
        JobStatus fromStatus;
        for(Job job:jobs){
            fromStatus=job.getStatus();
            int output=jobRepository.claimScheduledJob(job.getId(),JobStatus.QUEUED,JobStatus.SCHEDULED);
            if(output==1){
                job.setStatus(JobStatus.QUEUED);
                jobHistoryService.recordHistory(job,fromStatus,job.getStatus(),null);
                jobProducer.sendJob(job.getId(),job.getPriority());
            }
        }
    }
}
