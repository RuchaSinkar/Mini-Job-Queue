package com.example.MiniJobQueue.dto;

import com.example.MiniJobQueue.enums.JobType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateJobRequest {
    @NotNull
    private JobType type;
}
