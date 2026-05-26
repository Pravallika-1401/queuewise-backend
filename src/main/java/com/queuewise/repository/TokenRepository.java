package com.queuewise.repository;

import com.queuewise.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    // Queue lo anni WAITING tokens fetch cheyyi
    List<Token> findByQueueIdAndStatusOrderByCreatedAtAsc(Long queueId, String status);

    // User token find — "My Token" page ki
    Optional<Token> findByUserIdAndQueueIdAndStatus(Long userId, Long queueId, String status);

    // Currently SERVING token find cheyyi
    Optional<Token> findByQueueIdAndStatus(Long queueId, String status);

    // User ki muundi unna people count — wait time calculation ki
    @Query("SELECT COUNT(t) FROM Token t WHERE t.queue.id = :queueId " +
           "AND t.status = 'WAITING' AND t.createdAt < " +
           "(SELECT t2.createdAt FROM Token t2 WHERE t2.id = :tokenId)")
    int countPeopleAhead(@Param("queueId") Long queueId, @Param("tokenId") Long tokenId);

    // Latest token number fetch — next token generate ki
    @Query("SELECT MAX(CAST(SUBSTRING(t.tokenNumber, 3) AS int)) FROM Token t WHERE t.queue.id = :queueId")
    Integer findMaxTokenNumber(@Param("queueId") Long queueId);

    // Admin dashboard — all tokens of a queue
    List<Token> findByQueueIdOrderByCreatedAtAsc(Long queueId);

    // User history
    List<Token> findByUserIdOrderByCreatedAtDesc(Long userId);
}
