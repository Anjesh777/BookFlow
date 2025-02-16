package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.dto.NotificationDTO;
import com.BookFlow.bookflow.dto.ServiceDTO;
import com.BookFlow.bookflow.model.Services;
import com.BookFlow.bookflow.services.AdminNotificationService;
import com.BookFlow.bookflow.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/admin")
public class ServiceManagementController {


    private final AdminNotificationService adminNotificationService;

    public ServiceManagementController(AdminNotificationService adminNotificationService) {
        this.adminNotificationService = adminNotificationService;
    }


    @PostMapping("/add-service")
    public ResponseEntity<Services> addService(@RequestBody ServiceDTO serviceDTO){
        System.out.println("Received DTO: " + serviceDTO);

        try {

            Services services = new Services();

            services.setServiceName(serviceDTO.getServiceName());
            services.setCategory(serviceDTO.getCategory());
            services.setPrice(serviceDTO.getPrice());
            services.setDuration(serviceDTO.getDuration());
            services.setStatus(serviceDTO.isStatus());

            adminNotificationService.addServicce(services);
            return ResponseEntity.ok().build();

        }
        catch (Exception e){

            return ResponseEntity.internalServerError().build();


        }
    }

   // @GetMapping("/get-services")




}
