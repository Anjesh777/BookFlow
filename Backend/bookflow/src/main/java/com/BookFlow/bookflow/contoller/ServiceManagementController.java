package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.dto.NotificationDTO;
import com.BookFlow.bookflow.dto.ServiceDTO;
import com.BookFlow.bookflow.model.ServiceFilterDTO;
import com.BookFlow.bookflow.model.Services;
import com.BookFlow.bookflow.services.AdminNotificationService;
import com.BookFlow.bookflow.services.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            services.setServiceDescription(serviceDTO.getDescription());

            adminNotificationService.addServicce(services);
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get-services")
    public ResponseEntity<List<Services>> getCmpService(){
        try {
            List<Services> services = adminNotificationService.getAllCompanyService();
            return ResponseEntity.ok(services);
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete-service/{service_id}")
    public ResponseEntity<?> deleteCMPService(@PathVariable String service_id){

        try {
            adminNotificationService.deleteService(service_id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/service-update/{service_id}")
    public ResponseEntity<ServiceDTO> update(@PathVariable String service_id, @RequestBody ServiceDTO request) {
        try {
            request.setService_id(service_id);
            ServiceDTO updatedService = adminNotificationService.updateServices(request);
            System.out.print("Service is " + updatedService.toString());
            return ResponseEntity.ok(updatedService); // Return the updated service
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/get-service-filter")
    public ResponseEntity<List<ServiceDTO>> getServiceFilter(@RequestBody ServiceFilterDTO filter) {
        try {


            List<ServiceDTO> filteredServices = adminNotificationService.getFilteredServices(filter);
            return ResponseEntity.ok(filteredServices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}
