package com.BookFlow.bookflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerDTO {
    private String entryId;
    private LocalDate date;
    private String particulars;
    private BigDecimal amount;
    private String type;
    private String referenceNumber;
    private String note;
    private String user_id;
    private BigDecimal balance;

    public LedgerDTO(String entryId, LocalDate date, String particulars, BigDecimal amount,
                     String type, String referenceNumber, String note, String user_id) {
        this.entryId = entryId;
        this.date = date;
        this.particulars = particulars;
        this.amount = amount;
        this.type = type;
        this.referenceNumber = referenceNumber;
        this.note = note;
        this.user_id = user_id;
    }
}