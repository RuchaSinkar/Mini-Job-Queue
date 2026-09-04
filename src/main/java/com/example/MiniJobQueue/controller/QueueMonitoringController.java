package com.example.MiniJobQueue.controller;

import com.example.MiniJobQueue.dto.QueueMonitoringResponse;
import com.example.MiniJobQueue.service.QueueMonitoringService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/monitoring")
public class QueueMonitoringController {
    private final QueueMonitoringService queueMonitoringService;

    @GetMapping("/queue")
    public QueueMonitoringResponse getMonitoringQueue(){
        return queueMonitoringService.getMessagesCount();
    }
}
