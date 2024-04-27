package com.management.server.controllers;

import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.DailyActivityRepository;
import com.management.server.repositories.HonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HonorController {
    @Autowired
    private HonorRepository honorRepository;
    @PostMapping("/getAllHonors")
    public DataResponse getAllHonors(){
        return new DataResponse(0,honorRepository.findAll(),null);
    }

}
