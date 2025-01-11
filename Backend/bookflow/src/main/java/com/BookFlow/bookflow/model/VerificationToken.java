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
    private LocalDateTime created_at;
    @Column(name = "expiry_date")
    private LocalDateTime expireAt;

    private  boolean isUsed;

   public VerificationToken(String token, Company company) {
        this.token = token;
        this.company = company;
        this.created_at = LocalDateTime.now();
        this.expireAt = LocalDateTime.now().plusMinutes(10); // token expire in 10 min
        this.isUsed = false;
    }
}
