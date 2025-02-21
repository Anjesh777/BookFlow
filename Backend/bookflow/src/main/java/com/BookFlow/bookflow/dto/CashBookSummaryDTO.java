package com.BookFlow.bookflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashBookSummaryDTO {

    private BigDecimal currentBalance = BigDecimal.ZERO;
    private BigDecimal totalReceiptsToday = BigDecimal.ZERO;
    private BigDecimal totalPaymentsToday = BigDecimal.ZERO;
    private BigDecimal pendingReimbursements = BigDecimal.ZERO;

}

