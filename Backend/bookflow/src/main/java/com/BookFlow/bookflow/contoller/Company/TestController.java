package com.BookFlow.bookflow.contoller.Company;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping("api")
    public String testapi(){
        return "This is test validation sucess";
    }
}
