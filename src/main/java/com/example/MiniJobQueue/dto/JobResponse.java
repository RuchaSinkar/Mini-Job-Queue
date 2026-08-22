package com.example.MiniJobQueue.dto;

import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.enums.JobType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class JobResponse {
    private Long id;
    private JobType type;
    private JobStatus status;
    private int retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
