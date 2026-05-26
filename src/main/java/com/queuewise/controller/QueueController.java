package com.queuewise.controller;

import com.queuewise.dto.QueueDTOs.*;
import com.queuewise.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    // GET /api/queues — all active queues (authenticated users)
    @GetMapping("/queues")
    public ResponseEntity<List<QueueResponse>> getAllQueues() {
        return ResponseEntity.ok(queueService.getAllActiveQueues());
    }

    // GET /api/queues/public/status/{id} — live queue status (no auth needed)
    @GetMapping("/queues/public/status/{queueId}")
    public ResponseEntity<QueueStatusResponse> getQueueStatus(@PathVariable Long queueId) {
        return ResponseEntity.ok(queueService.getQueueStatus(queueId));
    }

    // POST /api/queues/join/{id} — user joins queue
    @PostMapping("/queues/join/{queueId}")
    public ResponseEntity<TokenResponse> joinQueue(@PathVariable Long queueId,
                                                    Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(queueService.joinQueue(queueId, email));
    }

    // GET /api/tokens/{id}/status — my token live status
    @GetMapping("/tokens/{tokenId}/status")
    public ResponseEntity<TokenResponse> getTokenStatus(@PathVariable Long tokenId,
                                                         Authentication authentication) {
        return ResponseEntity.ok(queueService.getMyToken(tokenId, authentication.getName()));
    }

    // ===================== ADMIN ENDPOINTS =====================

    // POST /api/admin/queues — create new queue
    @PostMapping("/admin/queues")
    public ResponseEntity<QueueResponse> createQueue(@RequestBody CreateQueueRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(queueService.createQueue(request, authentication.getName()));
    }

    // GET /api/admin/queues/{id}/tokens — all tokens of a queue
    @GetMapping("/admin/queues/{queueId}/tokens")
    public ResponseEntity<List<TokenResponse>> getQueueTokens(@PathVariable Long queueId) {
        return ResponseEntity.ok(queueService.getQueueTokens(queueId));
    }

    // PUT /api/admin/queues/{id}/next — call next token
    @PutMapping("/admin/queues/{queueId}/next")
    public ResponseEntity<String> callNext(@PathVariable Long queueId) {
        return ResponseEntity.ok(queueService.callNextToken(queueId));
    }

    // PUT /api/admin/tokens/{id}/skip — skip this token
    @PutMapping("/admin/tokens/{tokenId}/skip")
    public ResponseEntity<String> skipToken(@PathVariable Long tokenId) {
        return ResponseEntity.ok(queueService.skipToken(tokenId));
    }
}
