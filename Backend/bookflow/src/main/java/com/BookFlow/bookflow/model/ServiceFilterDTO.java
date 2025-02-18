package com.BookFlow.bookflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceFilterDTO {
    private String serchService;
    private Boolean filter;
}
