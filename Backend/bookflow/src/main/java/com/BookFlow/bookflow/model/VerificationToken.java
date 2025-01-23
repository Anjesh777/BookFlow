package com.BookFlow.bookflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "token")
    private String token;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Transient
    private LocalDateTime created_at;

    @Transient
    private LocalDateTime expireAt;

    private boolean isUsed;

    public VerificationToken(String token, Company company) {
        this.token = token;
        this.company = company;
        this.created_at = LocalDateTime.now();
        this.expireAt = LocalDateTime.now().plusMinutes(60);
        this.isUsed = false;
    }


    @Transient
    public boolean isExpired() {
        return expireAt != null && LocalDateTime.now().isAfter(expireAt);
    }

    // Getter with null check
    public LocalDateTime getExpireAt() {
        if (expireAt == null) {
            expireAt = created_at != null ?
                    created_at.plusMinutes(60) :
                    LocalDateTime.now().plusMinutes(60);
        }
        return expireAt;
    }
}