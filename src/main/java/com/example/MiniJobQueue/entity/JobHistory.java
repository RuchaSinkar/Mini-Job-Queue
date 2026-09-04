package com.example.MiniJobQueue.entity;

import com.example.MiniJobQueue.enums.JobStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "job_history")
public class JobHistory {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    private Job job;

    @Enumerated(EnumType.STRING)
    private JobStatus fromStatus;

    @Enumerated(EnumType.STRING)
    private JobStatus toStatus;

    private LocalDateTime changedAt;

    private String reason;
}
