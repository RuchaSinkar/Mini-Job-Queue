package com.example.MiniJobQueue.repository;

import com.example.MiniJobQueue.entity.Job;
import com.example.MiniJobQueue.enums.JobStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


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

    List<Job> findByStatusAndStartedAtIsNotNullAndCompletedAtIsNotNull(JobStatus jobStatus);

    @Query("SELECT COALESCE(SUM(j.retry_count),0) FROM Job j")
    Long getTotalRetries();

    long countByRetryCountGreaterThan(Integer retryCount);

    @Query("""
SELECT j FROM Job j WHERE j.status=:status AND j.startedAt<:cutoff
""")
    List<Job> findTimeOutJobs(@Param( "status") JobStatus status,
                              @Param("cutoff")LocalDateTime cutoff);

    @Query("""
    UPDATE Job j SET j.status=:failed, j.updatedAt=CURRENT_TIMESTAMP WHERE j.id = :id AND j.status = :processing
""")
    @Modifying
    @Transactional
    int timeOutUpdate(@Param("id") Long id,
                      @Param("failed") JobStatus failed,
                      @Param("processing") JobStatus processing);



    long countByStatusAndFailureReasonContaining(JobStatus jobStatus, String jobTimedOut);

    List<Job> findTop5ByStatusOrderByUpdatedAtDesc(JobStatus jobStatus);
}

//
//@Repository
//public interface JobRepository extends JpaRepository<Job, Long> {
//
//    @Modifying
//    @Transactional
//    @Query("""
//        UPDATE Job j
//        SET j.status = :processing,
//            j.updatedAt = CURRENT_TIMESTAMP
//        WHERE j.id = :id
//        AND j.status = :queued
//    """)
//    int claimJob(
//            @Param("id") Long id,
//            @Param("processing") JobStatus processing,
//            @Param("queued") JobStatus queued
//    );
//
//    long countByStatus(JobStatus status);
//
//    List<Job> findByStatusAndStartedAtIsNotNullAndCompletedAtIsNotNull(JobStatus jobStatus);
//
//    // FIXED: Use Java field name retryCount instead of database column retry_count
//    @Query("SELECT COALESCE(SUM(j.retryCount), 0) FROM Job j")
//    Long getTotalRetries();
//
//    long countByRetryCountGreaterThan(Integer retryCount);
//}