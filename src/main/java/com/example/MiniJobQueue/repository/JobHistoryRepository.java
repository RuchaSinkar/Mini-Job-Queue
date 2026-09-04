package com.example.MiniJobQueue.repository;

import com.example.MiniJobQueue.entity.JobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobHistoryRepository extends JpaRepository<JobHistory, Long> {

    List<JobHistory> findByJobIdOrderByChangedAtAsc(Long id);
}