package com.queuewise.service;

import com.queuewise.dto.QueueDTOs.*;
import com.queuewise.entity.*;
import com.queuewise.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final AIService aiService;

    // Admin — new queue create cheyyi
    public QueueResponse createQueue(CreateQueueRequest request, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Queue queue = Queue.builder()
                .name(request.getName())
                .location(request.getLocation())
                .avgServiceTime(request.getAvgServiceTime())
                .createdBy(admin)
                .isActive(true)
                .build();

        Queue saved = queueRepository.save(queue);
        return toQueueResponse(saved);
    }

    // All active queues fetch cheyyi — home page ki
    public List<QueueResponse> getAllActiveQueues() {
        return queueRepository.findByIsActiveTrue()
                .stream()
                .map(this::toQueueResponse)
                .collect(Collectors.toList());
    }

    // User token join cheyyi — core feature
    @Transactional
    public TokenResponse joinQueue(Long queueId, String userEmail) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Already waiting token undo check
        tokenRepository.findByUserIdAndQueueIdAndStatus(user.getId(), queueId, "WAITING")
                .ifPresent(t -> { throw new RuntimeException("You already have token: " + t.getTokenNumber()); });

        // Next token number generate cheyyi
        Integer maxNum = tokenRepository.findMaxTokenNumber(queueId);
        int nextNum = (maxNum == null) ? 100 : maxNum + 1;
        String tokenNumber = "A-" + nextNum;

        Token token = Token.builder()
                .tokenNumber(tokenNumber)
                .status("WAITING")
                .queue(queue)
                .user(user)
                .build();

        Token saved = tokenRepository.save(token);

        // People ahead calculate cheyyi
        int peopleAhead = tokenRepository.countPeopleAhead(queueId, saved.getId());
        int simpleEst = peopleAhead * queue.getAvgServiceTime();

        // AI estimate get cheyyi
        int currentHour = LocalTime.now().getHour();
        String aiEstimate = aiService.getSmartWaitEstimate(peopleAhead, queue.getAvgServiceTime(), currentHour);

        return TokenResponse.builder()
                .id(saved.getId())
                .tokenNumber(tokenNumber)
                .status("WAITING")
                .queueName(queue.getName())
                .peopleAhead(peopleAhead)
                .estimatedWaitMinutes(simpleEst)
                .aiEstimate(aiEstimate)
                .build();
    }

    // My token status check cheyyi — live refresh
    public TokenResponse getMyToken(Long tokenId, String userEmail) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        Queue queue = token.getQueue();
        int peopleAhead = tokenRepository.countPeopleAhead(queue.getId(), tokenId);
        int simpleEst = peopleAhead * queue.getAvgServiceTime();

        int currentHour = LocalTime.now().getHour();
        String aiEstimate = aiService.getSmartWaitEstimate(peopleAhead, queue.getAvgServiceTime(), currentHour);

        return TokenResponse.builder()
                .id(token.getId())
                .tokenNumber(token.getTokenNumber())
                .status(token.getStatus())
                .queueName(queue.getName())
                .peopleAhead(peopleAhead)
                .estimatedWaitMinutes(simpleEst)
                .aiEstimate(aiEstimate)
                .build();
    }

    // Admin — next token call cheyyi
    @Transactional
    public String callNextToken(Long queueId) {
        // Current SERVING token ni COMPLETED mark cheyyi
        tokenRepository.findByQueueIdAndStatus(queueId, "SERVING")
                .ifPresent(t -> {
                    t.setStatus("COMPLETED");
                    tokenRepository.save(t);
                });

        // Next WAITING token fetch cheyyi
        List<Token> waiting = tokenRepository
                .findByQueueIdAndStatusOrderByCreatedAtAsc(queueId, "WAITING");

        if (waiting.isEmpty()) {
            return "Queue is empty. No more tokens.";
        }

        Token next = waiting.get(0);
        next.setStatus("SERVING");
        tokenRepository.save(next);

        return "Now serving: " + next.getTokenNumber();
    }

    // Admin — token skip cheyyi
    @Transactional
    public String skipToken(Long tokenId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        token.setStatus("SKIPPED");
        tokenRepository.save(token);
        return "Token " + token.getTokenNumber() + " skipped.";
    }

    // Queue status — public page ki
    public QueueStatusResponse getQueueStatus(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue not found"));

        String currentServing = tokenRepository
                .findByQueueIdAndStatus(queueId, "SERVING")
                .map(Token::getTokenNumber)
                .orElse("None");

        int waitingCount = tokenRepository
                .findByQueueIdAndStatusOrderByCreatedAtAsc(queueId, "WAITING")
                .size();

        return QueueStatusResponse.builder()
                .currentServing(currentServing)
                .totalWaiting(waitingCount)
                .avgServiceTime(queue.getAvgServiceTime())
                .build();
    }

    // Admin — all tokens of a queue
    public List<TokenResponse> getQueueTokens(Long queueId) {
        return tokenRepository.findByQueueIdOrderByCreatedAtAsc(queueId)
                .stream()
                .map(t -> TokenResponse.builder()
                        .id(t.getId())
                        .tokenNumber(t.getTokenNumber())
                        .status(t.getStatus())
                        .queueName(t.getQueue().getName())
                        .build())
                .collect(Collectors.toList());
    }

    private QueueResponse toQueueResponse(Queue queue) {
        int waitingCount = tokenRepository
                .findByQueueIdAndStatusOrderByCreatedAtAsc(queue.getId(), "WAITING")
                .size();

        String currentServing = tokenRepository
                .findByQueueIdAndStatus(queue.getId(), "SERVING")
                .map(Token::getTokenNumber)
                .orElse("None");

        return QueueResponse.builder()
                .id(queue.getId())
                .name(queue.getName())
                .location(queue.getLocation())
                .avgServiceTime(queue.getAvgServiceTime())
                .isActive(queue.isActive())
                .waitingCount(waitingCount)
                .currentServing(currentServing)
                .build();
    }
}
