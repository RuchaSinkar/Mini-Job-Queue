package com.example.MiniJobQueue.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FailedJobAnalyticsResponse {
    private Long totalFailedJobs;
    private Long timeoutFailures;
    private Long retryExhaustedFailures;
    private Long otherFailures;
}
