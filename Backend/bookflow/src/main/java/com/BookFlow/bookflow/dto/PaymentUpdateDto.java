package com.BookFlow.bookflow.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentUpdateDto {

    private BigDecimal amount;
    private String paymentMethod;
    private boolean paymentStatus;
    private LocalDateTime paymentDate;
}
