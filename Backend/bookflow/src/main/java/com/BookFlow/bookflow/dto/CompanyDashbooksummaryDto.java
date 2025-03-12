package com.BookFlow.bookflow.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompanyDashbooksummaryDto {

    private BigDecimal TotalCashbook = BigDecimal.ZERO;
    private BigDecimal TotalLedger = BigDecimal.ZERO;
    private BigDecimal ServiceBooked = BigDecimal.ZERO;
    private BigDecimal TotalUsers = BigDecimal.ZERO;
}
