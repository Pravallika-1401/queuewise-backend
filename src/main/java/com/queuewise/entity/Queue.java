package com.queuewise.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "queues")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. "Clinic Queue", "Salon Queue"

    @Column
    private String location;

    @Column(name = "avg_service_time", nullable = false)
    private int avgServiceTime; // in minutes — used for wait time calculation

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Which admin created this queue
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    // One queue has many tokens
    @OneToMany(mappedBy = "queue", cascade = CascadeType.ALL)
    private List<Token> tokens;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
