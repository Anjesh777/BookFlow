package com.BookFlow.bookflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id")
    private UUID company_id;

    @Column(name = "company_name", nullable = false)
    private String company_name;

    @Column(name = "company_registration", nullable = false, unique = true)
    private String registration_number;

    @Column(name = "company_email", nullable = false, unique = true)
    private String company_email;

    @Column(name = "company_phone",unique = true)
    private String company_phone;

    @Column(name = "company_address")
    private String company_address;

    @CurrentTimestamp
    @Column(name = "company_created_at", updatable = false)
    private LocalDate company_createdAt;

    @Column(name = "company_updateAt")
    private LocalDate company_updatedAt;

    @Column(name = "is_enable")
    private boolean enabled = true;

    @Column(name = "is_verified")
    private boolean verified = false;

}
