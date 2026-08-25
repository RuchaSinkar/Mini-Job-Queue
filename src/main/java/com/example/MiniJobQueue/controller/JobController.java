package com.example.MiniJobQueue.controller;

import com.example.MiniJobQueue.dto.CreateJobRequest;
import com.example.MiniJobQueue.dto.JobResponse;
import com.example.MiniJobQueue.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping
    public JobResponse createJob(@Valid @RequestBody CreateJobRequest jobRequest){
        return jobService.createJob(jobRequest);
    }

    @GetMapping("/{id}")
    public JobResponse getJob(@PathVariable Long id){
        return jobService.getJob(id);
    }

    @PostMapping("/{id}/retry")
    public void retryFailedJob(@PathVariable Long id){
        jobService.failedJob(id);
    }
}
