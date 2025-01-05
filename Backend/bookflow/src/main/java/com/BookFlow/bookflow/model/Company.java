package com.BookFlow.bookflow.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;



@Entity

@Getter
@Setter

public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID company_id;
    private String company_name;
    private String registration_number;
    private String company_email;
    private String company_phone;
    private String company_address;
    private String company_status;
    private Date company_createdAt;
    private Date company_updated_at;
    private String company_password;
    private String super_admin;

}
