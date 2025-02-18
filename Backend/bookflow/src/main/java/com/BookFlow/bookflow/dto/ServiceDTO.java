package com.BookFlow.bookflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ServiceDTO {
    private String service_id;
    private String serviceName;
    private String category;
    private BigDecimal price;
    private String duration;
    private boolean status;
    private String description;
}
