package com.BookFlow.bookflow.contoller.Company;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class TestController {

    @GetMapping("/user")
    public String testapi(){
        return "This is test validation sucess";
    }
    @GetMapping("/superadmin")
    public ResponseEntity<String> testapisuper() {
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body("\"This is test validation success\"");
    }



}
