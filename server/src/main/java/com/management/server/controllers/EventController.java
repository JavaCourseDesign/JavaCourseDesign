package com.management.server.controllers;

import com.management.server.models.DailyActivity;
import com.management.server.models.Event;
import com.management.server.models.Student;
import com.management.server.repositories.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//event controller负责实现innovation dailyActivity; lesson course单独实现
@RestController
public class EventController {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @PostMapping("/addDailyActivity")
    public Object addDailyActivity(){
        return null;
    }

    @PostMapping("/getAllEvents")
    public List<Event> getAllDailyActivity(@Valid @RequestBody Map<String,String> data){
        /*Student s=new Student();
        s.setName("tst");
        s.setPersonId(1);
        s.setMajor("software");
        Event e1=new DailyActivity();
        Event e2=new DailyActivity();
        *//*e1.setEventId(3);
        e2.setEventId(4);*//*
        e1.setName("e1");
        e2.setName("e2");
        List<Event> es=List.of(e1,e2);
        s.setEvents(es);
        eventRepository.save(e1); //先存属性再存本体！！！
        eventRepository.save(e2);
        studentRepository.save(s);*/

        System.out.println(Integer.valueOf(data.get("personId")));
        System.out.println(studentRepository.findByPersonId(Integer.valueOf(data.get("personId"))).getEvents());
        return studentRepository.findByPersonId(Integer.valueOf(data.get("personId"))).getEvents();
    }
}
