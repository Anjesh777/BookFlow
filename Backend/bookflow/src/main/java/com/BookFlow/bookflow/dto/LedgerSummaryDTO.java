package com.BookFlow.bookflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerSummaryDTO {
    private BigDecimal totalCredits;
    private BigDecimal totalDebits;
    private BigDecimal balance;
}