package com.queuewise.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // BCrypt encrypted - never plain text

    @Column(nullable = false)
    private String role; // "ADMIN" or "USER"

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // One user can have many tokens
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Token> tokens;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
