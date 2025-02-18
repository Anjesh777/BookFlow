package com.BookFlow.bookflow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "services")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Services {
    @Id
    @Column(name = "service_id")
    private String service_id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company_id;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "service_description",nullable = true)
    private String serviceDescription;

    @Column(name = "category")
    private String category;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration")
    private String duration;

    @Column(name = "status")
    private boolean status;

    @PrePersist
    public void ensureId() {
        if (this.service_id == null || this.service_id.isEmpty()) {
            this.service_id = "SERVICE_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
    }
}
