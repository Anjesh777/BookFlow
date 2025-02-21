package com.BookFlow.bookflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cash_book")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class CashBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id",nullable = false)
    private Company company_id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "voucher_number", nullable = false, unique = true)
    private String voucherNumber;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal receiptAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal paymentAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(name = "is_reimbursement_pending")
    private boolean isReimbursementPending = false;

}
