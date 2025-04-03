package com.BookFlow.bookflow.repository;

import com.BookFlow.bookflow.dto.ServiceDTO;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.Services;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepo extends JpaRepository<Services,String> {

    @Query("SELECT s FROM Services s WHERE s.company_id = :company")
    List<Services> findAllCmpService(@Param("company") Company company);

    @Modifying
    @Transactional
    @Query(value = "UPDATE services SET " +
            "service_name = CASE WHEN :serviceName IS NULL THEN service_name ELSE :serviceName END, " +
            "service_description = CASE WHEN :description IS NULL THEN service_description ELSE :description END, " +
            "category = CASE WHEN :category IS NULL THEN category ELSE :category END, " +
            "price = CASE WHEN :price IS NULL THEN price ELSE :price END, " +
            "duration = CASE WHEN :duration IS NULL THEN duration ELSE :duration END, " +
            "status = CASE WHEN :status IS NULL THEN status ELSE :status END " +
            "WHERE service_id = :serviceId",
            nativeQuery = true)
    int updateService(
            @Param("serviceId") String serviceId,
            @Param("serviceName") String serviceName,
            @Param("description") String description,
            @Param("category") String category,
            @Param("price") BigDecimal price,
            @Param("duration") String duration,
            @Param("status") Boolean status);


    @Query("SELECT new com.BookFlow.bookflow.dto.ServiceDTO(" +
            "s.service_id, s.serviceName, s.category, s.price, s.duration, s.status, s.serviceDescription) " +
            "FROM Services s " +
            "WHERE LOWER(s.serviceName) LIKE LOWER(CONCAT('%', :serchService, '%'))")
    List<ServiceDTO> findByServiceNameContaining(@Param("serchService") String serchService);

    @Query("SELECT new com.BookFlow.bookflow.dto.ServiceDTO(" +
            "s.service_id, s.serviceName, s.category, s.price, s.duration, s.status, s.serviceDescription) " +
            "FROM Services s " +
            "WHERE s.status = :filter")
    List<ServiceDTO> findByStatus(@Param("filter") Boolean filter);

    @Query("SELECT new com.BookFlow.bookflow.dto.ServiceDTO(" +
            "s.service_id, s.serviceName, s.category, s.price, s.duration, s.status, s.serviceDescription) " +
            "FROM Services s " +
            "WHERE LOWER(s.serviceName) LIKE LOWER(CONCAT('%', :serchService, '%')) " +
            "AND s.status = :filter")
    List<ServiceDTO> findByServiceNameAndStatus(@Param("serchService") String serchService, @Param("filter") Boolean filter);

    @Query("SELECT new com.BookFlow.bookflow.dto.ServiceDTO(" +
            "s.service_id, s.serviceName, s.category, s.price, s.duration, s.status, s.serviceDescription) " +
            "FROM Services s")
    List<ServiceDTO> findAllServices();
////***
//    @Query("SELECT s FROM Services s WHERE s.company_id = :company AND s.status = true")
//    List<Services> findAllActiveServicesByCompany(@Param("company") Company company);
//



}
