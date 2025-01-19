package com.BookFlow.bookflow.contoller.Company;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api")
public class TestController {

    @GetMapping("v1/user")
    public String testapi(){
        return "This is test validation sucess";
    }
    @GetMapping("v1/superadmin")
    public String testapisuper(){
        return "This is test validation sucess";
    }
}
