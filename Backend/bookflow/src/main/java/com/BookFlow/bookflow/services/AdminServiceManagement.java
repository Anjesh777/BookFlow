
package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.ServiceDTO;
import com.BookFlow.bookflow.model.*;
import com.BookFlow.bookflow.repository.ServiceRepo;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.utils.classes.UserContextUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class AdminServiceManagement {

    @Autowired
    private ServiceRepo serviceRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserContextUtil util;



    @Transactional
    public void addService(Services services) {
        try {
            Company userCompany = util.getCurrentUserCompany();
            services.setCompany_id(userCompany);
            serviceRepo.save(services);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw e;
        }
    }

    public ServiceDTO updateService(ServiceDTO service){
        try {
            serviceRepo.updateService(
                    service.getService_id(),
                    service.getServiceName(),
                    service.getDescription(),
                    service.getCategory(),
                    service.getPrice(),
                    service.getDuration(),
                    service.isStatus()
            );
        }
        catch (Exception e){
            log.error(String.valueOf(e));
        }
        return service;
    }

    public List<ServiceDTO> getFilteredServices(ServiceFilterDTO filter) {
        Company company = util.getCurrentUserCompany();

        String searchTerm = filter.getSerchService();
        Boolean status = filter.getFilter();

        if ((searchTerm == null || searchTerm.trim().isEmpty()) && status == null) {
            return serviceRepo.findAllServicesByCompany(company);
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty() && status == null) {
            return serviceRepo.findByCompanyAndServiceNameContaining(company, searchTerm.trim());
        }

        if ((searchTerm == null || searchTerm.trim().isEmpty()) && status != null) {
            return serviceRepo.findByCompanyAndStatus(company, status);
        }

        return serviceRepo.findByCompanyAndServiceNameAndStatus(company, searchTerm.trim(), status);
    }

    public List<Services> getAllCompanyServices(){
        return serviceRepo.findAllCmpService(util.getCurrentUserCompany());
    }

    public void deleteService(String serviceId){
        serviceRepo.deleteById(serviceId);
    }
}