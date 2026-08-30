package com.example.MiniJobQueue.controller;

import com.example.MiniJobQueue.dto.FailedJobAnalyticsResponse;
import com.example.MiniJobQueue.service.FailedJobAnalyticsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@AllArgsConstructor
@RestController
@RequestMapping("/analytics")
public class FailedJobAnalyticsController {
    private final FailedJobAnalyticsService failedJobAnalyticsService;

    @GetMapping("/failures")
    public FailedJobAnalyticsResponse getAnalyticsReport(){
        return failedJobAnalyticsService.getFailureAnalytics();
    }
}
