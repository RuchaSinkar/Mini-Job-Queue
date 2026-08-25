package com.example.MiniJobQueue.repository;

import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Modifying
    @Transactional
    @Query("""
    UPDATE Job j
    SET j.status=:processing,
    j.updatedAt=CURRENT_TIMESTAMP
    where j.id=:id
    AND j.status=:queued
""")
    int claimJob(
            @Param("id") Long id,
            @Param("processing") JobStatus processing,
            @Param("queued") JobStatus queued
    );

    long countByStatus(JobStatus status);
}