package com.example.MiniJobQueue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QueueMonitoringResponse {
    private String queueName;
    private Long readyMessages;
    private Long unackedMessages;
    private Long totalMessages;
}

