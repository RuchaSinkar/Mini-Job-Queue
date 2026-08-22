package com.example.MiniJobQueue.entity;

import com.example.MiniJobQueue.enums.JobStatus;
import com.example.MiniJobQueue.enums.JobType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private String result;

    private int retryCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
