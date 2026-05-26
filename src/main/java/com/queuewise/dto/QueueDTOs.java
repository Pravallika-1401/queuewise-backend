package com.queuewise.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class QueueDTOs {

    @Data
    public static class CreateQueueRequest {
        private String name;
        private String location;
        private int avgServiceTime; // minutes
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueResponse {
        private Long id;
        private String name;
        private String location;
        private int avgServiceTime;
        private boolean isActive;
        private int waitingCount; // live count
        private String currentServing; // current token being served
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        private Long id;
        private String tokenNumber; // "A-101"
        private String status;
        private String queueName;
        private int peopleAhead;
        private int estimatedWaitMinutes;
        private String aiEstimate; // AI generated estimate message
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueStatusResponse {
        private String currentServing;
        private int totalWaiting;
        private int avgServiceTime;
    }
}
