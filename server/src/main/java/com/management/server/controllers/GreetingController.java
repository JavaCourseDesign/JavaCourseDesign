package com.management.server.controllers;

import com.management.server.models.Greeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    //@GetMapping("/greeting")
    @PostMapping("/greeting")
    public Greeting greeting() {
        return new Greeting("Hello, World!");
    }
}
