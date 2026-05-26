package com.queuewise.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_number", nullable = false)
    private String tokenNumber; // e.g. "A-101", "A-102"

    @Column(nullable = false)
    private String status; // WAITING, SERVING, COMPLETED, SKIPPED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "served_at")
    private LocalDateTime servedAt;

    // This token belongs to which queue
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_id", nullable = false)
    private Queue queue;

    // This token belongs to which user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = "WAITING";
    }
}
