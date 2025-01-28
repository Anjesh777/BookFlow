package com.BookFlow.bookflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Entity
@Data
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id")
    private UUID company_id;

    @Column(name = "company_name", nullable = false)
    private String company_name;

    @Column(name = "company_registration", nullable = false)
    private String registration_number;

    @Column(name = "company_email", nullable = false, unique = true)
    private String company_email;

    @Column(name = "company_phone",unique = true)
    private String company_phone;

    @Column(name = "company_address")
    private String company_address;

    @CurrentTimestamp
    @Column(name = "company_createdAt", updatable = false)
    private Date company_createdAt;

    @Column(name = "company_updateAt")
    private Date company_updatedAt;

    @Transient
    private boolean is_enabled = false;

    @Transient
    @Column(name = "is_verified", nullable = false)
    private boolean is_verified = false;

}
