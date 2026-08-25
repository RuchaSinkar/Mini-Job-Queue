package com.example.MiniJobQueue.controller;

import com.example.MiniJobQueue.dto.JobMetricsResponse;
import com.example.MiniJobQueue.service.JobMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JobMetricsController {
    private JobMetricsService jobMetricsService;

    @GetMapping("/jobs/metrics")
    public JobMetricsResponse getJobMetrics(){
        return jobMetricsService.getMetrics();
    }
}
