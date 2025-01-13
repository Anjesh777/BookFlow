package com.BookFlow.bookflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CompanyDTO {

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("company_email")
    private String companyEmail;

    @JsonProperty("company_phone")
    private String companyPhone;

    @JsonProperty("company_address")
    private String companyAddress;

    @JsonProperty("company_password")
    private String companyPassword;

    @JsonProperty("super_admin")
    private String super_admin;


}
