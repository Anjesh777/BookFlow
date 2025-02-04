package com.BookFlow.bookflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CompanyFilterDTO {

    private String search;
    private Boolean verified;
    private Boolean status;
    private DateRangeDTO dateRange;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRangeDTO {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date fromDate;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date toDate;
    }
}
