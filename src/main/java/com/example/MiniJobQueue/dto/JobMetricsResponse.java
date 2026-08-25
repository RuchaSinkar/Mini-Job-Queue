package com.example.MiniJobQueue.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobMetricsResponse {
    private Long totalJobs;
    private Long queuedJobs;
    private Long processingJobs;
    private Long completedJobs;
    private Long failedJobs;
}
