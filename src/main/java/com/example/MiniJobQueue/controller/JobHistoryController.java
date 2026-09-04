package com.example.MiniJobQueue.controller;

import com.example.MiniJobQueue.entity.JobHistory;
import com.example.MiniJobQueue.service.JobHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/jobs")
public class JobHistoryController {
    private final JobHistoryService jobHistoryService;

    @GetMapping("/{id}/history")
    public List<JobHistory> getJobHistory(@PathVariable Long id){
        return jobHistoryService.getJobHistory(id);
    }
}
