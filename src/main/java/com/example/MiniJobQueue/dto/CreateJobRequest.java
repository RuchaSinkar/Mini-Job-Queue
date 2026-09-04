package com.example.MiniJobQueue.dto;

import com.example.MiniJobQueue.enums.JobPriority;
import com.example.MiniJobQueue.enums.JobType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateJobRequest {
    @NotNull
    private JobType type;

    private JobPriority priority;

    private LocalDateTime scheduledAt;
}
