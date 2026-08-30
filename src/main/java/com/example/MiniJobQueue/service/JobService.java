package com.example.MiniJobQueue.service;

import com.example.MiniJobQueue.dto.CreateJobRequest;
import com.example.MiniJobQueue.dto.JobResponse;
import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobPriority;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.exception.InvalidJobStateException;
import com.example.MiniJobQueue.exception.JobNotFoundException;
import com.example.MiniJobQueue.queue.JobProducer;
import com.example.MiniJobQueue.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final JobProducer jobProducer;

    public JobResponse createJob(CreateJobRequest createJobRequest){
        Job job=new Job();
        job.setType(createJobRequest.getType());
        job.setStatus(JobStatus.QUEUED);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job.setRetryCount(0);
        job.setPriority(createJobRequest.getPriority());

        Job saved=jobRepository.save(job);
        jobProducer.sendJob(saved.getId(),saved.getPriority());
        JobResponse jobResponse=new JobResponse();
        jobResponse.setId(saved.getId());
        jobResponse.setType(saved.getType());
        jobResponse.setStatus(saved.getStatus());
        jobResponse.setRetryCount(saved.getRetryCount());
        jobResponse.setCreatedAt(saved.getCreatedAt());
        jobResponse.setPriority(saved.getPriority());
        jobResponse.setUpdatedAt(saved.getUpdatedAt());
        return jobResponse;
    }
    public JobResponse getJob(Long id){
        Job job=jobRepository.findById(id).orElseThrow(()->new JobNotFoundException("Job Not Found"));
        JobResponse jobResponse=new JobResponse();
        jobResponse.setId(job.getId());
        jobResponse.setType(job.getType());
        jobResponse.setStatus(job.getStatus());
        jobResponse.setRetryCount(job.getRetryCount());
        jobResponse.setCreatedAt(job.getCreatedAt());
        jobResponse.setUpdatedAt(job.getUpdatedAt());
        jobResponse.setPriority(job.getPriority());
        return jobResponse;
    }

    public void failedJob(Long id) {
        Job job=jobRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Job not found"));
        if(job.getStatus()==JobStatus.FAILED){
            job.setRetryCount(0);
            job.setStatus(JobStatus.QUEUED);
            job.setUpdatedAt(LocalDateTime.now());
            job.setFailureReason(null);
            job.setPriority(JobPriority.LOW);
            Job saved=jobRepository.save(job);
            jobProducer.sendJob(id,saved.getPriority());
        }
        else{
            throw new InvalidJobStateException("Only Failed jobs can be retried");
        }
    }
    public void cancelJob(Long id){
        Job job=jobRepository.findById(id).orElseThrow(()->new JobNotFoundException("Job not found"));
        if(job.getStatus()==JobStatus.QUEUED){
            job.setStatus(JobStatus.CANCELLED);
        }
        else{
            throw new InvalidJobStateException("Only queued jobs can be cancelled");
        }
    }
}
