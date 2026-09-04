package com.example.MiniJobQueue.service;

import com.example.MiniJobQueue.config.JobConfig;
import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.repository.JobRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class JobTimeoutService {

    private final JobConfig jobConfig;
    private final JobRepository jobRepository;
    private final JobHistoryService jobHistoryService;

    @Scheduled(fixedRate = 5000)
    public void findTimedOutJobs() {

        LocalDateTime time = LocalDateTime.now();

        LocalDateTime cutoff =
                time.minusSeconds(jobConfig.getTimeout());

        List<Job> timedOutJobs =
                jobRepository.findTimeOutJobs(
                        JobStatus.PROCESSING,
                        cutoff
                );

        for (Job job : timedOutJobs) {

            JobStatus fromStatus = job.getStatus();

            int updated = jobRepository.timeOutUpdate(
                    job.getId(),
                    JobStatus.FAILED,
                    JobStatus.PROCESSING
            );

            if (updated == 1) {
                job.setStatus(JobStatus.FAILED);
                job.setFailureReason("Job processing timed out");
                job.setUpdatedAt(LocalDateTime.now());

                jobRepository.save(job);

                jobHistoryService.recordHistory(
                        job,
                        fromStatus,
                        JobStatus.FAILED,
                        job.getFailureReason()
                );
            }
        }
    }
}