package com.example.waterlily.persistence;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "responses")
public class ResponseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Lob
    @Column(nullable = false)
    private String answersJson;   // stores the survey answers as JSON text

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getAnswersJson() { return answersJson; }
    public void setAnswersJson(String answersJson) { this.answersJson = answersJson; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
