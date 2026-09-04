package com.example.MiniJobQueue.service;

import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.entity.JobHistory;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.repository.JobHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class JobHistoryService {
    private final JobHistoryRepository jobHistoryRepository;

    public void recordHistory(Job job, JobStatus fromStatus, JobStatus toStatus, String reason){
        JobHistory jobHistory=new JobHistory();
        jobHistory.setJob(job);
        jobHistory.setFromStatus(fromStatus);
        jobHistory.setToStatus(toStatus);
        jobHistory.setChangedAt(LocalDateTime.now());
        jobHistory.setReason(reason);
        jobHistoryRepository.save(jobHistory);
    }

    public List<JobHistory> getJobHistory(Long id){
        return jobHistoryRepository.findByJobIdOrderByChangedAtAsc(id);
    }
}
