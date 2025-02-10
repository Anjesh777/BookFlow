package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.model.ResourceUsage;
import com.BookFlow.bookflow.services.ResourceMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/bookflow")
public class ResourceController {

    @Autowired
    private ResourceMonitorService resourceMonitorService;

    @GetMapping("/resource")
    public ResponseEntity<ResourceUsage> getResourceUsage() {
        return ResponseEntity.ok(resourceMonitorService.getSystemResources());
    }


}
