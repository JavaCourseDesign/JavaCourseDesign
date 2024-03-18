package com.management.server.controllers;

// Spring Boot Application
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoController {
    public static void main(String[] args) {
        SpringApplication.run(DemoController.class, args);
    }
}

@RestController
class HelloWorldController {
    @GetMapping("/helloworld")
    public String helloWorld() {
        return "Hello, World!";
    }
}
