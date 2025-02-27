package com.BookFlow.bookflow.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ledger")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ledger {

    @Id
    private String entryID;
    @ManyToOne
    @JoinColumn(name = "company_id",nullable = false)
    private Company companyID;
    @Column(nullable = false)
    private LocalDate date;
    private String particulars;
    private BigDecimal amount;
    private String type;
    @Nullable
    private String refrenceNumber;
    @Nullable
    private String note;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userID;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;




}
