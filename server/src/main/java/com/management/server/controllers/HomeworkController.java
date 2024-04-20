package com.management.server.controllers;

import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.HomeworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeworkController {
    @Autowired
    HomeworkRepository homeworkRepository;
    @PostMapping("/getAllHomework")
    public DataResponse getAllHomework(){
        return new DataResponse(0,homeworkRepository.findAll(),null);
    }

}
