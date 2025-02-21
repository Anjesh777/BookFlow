package com.BookFlow.bookflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashBookDTO {

    private Long id;
    private LocalDate date;
    private String voucherNumber;
    private String description;
    private String category;
    private BigDecimal receiptAmount;
    private BigDecimal paymentAmount;
    private BigDecimal balance;
    private boolean isReimbursementPending;

}


