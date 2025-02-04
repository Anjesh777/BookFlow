package com.BookFlow.bookflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateRequest {
    private String company_name;
    private String registration_number;
    private String company_email;
    private String company_phone;
    private String company_address;

    private boolean enabled;
    private boolean verified;



}
