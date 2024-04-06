package com.management.server.controllers;

import com.management.server.models.DailyActivity;
import com.management.server.models.Event;
import com.management.server.models.Person;
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


}

//getAllEvents测试代码
/*Student s=new Student();
        s.setName("tst");
        s.setPersonId(1);
        s.setMajor("software");
        Event e1=new DailyActivity();
        Event e2=new DailyActivity();
        e1.setName("e1");
        e2.setName("e2");
        List<Event> es=List.of(e1,e2);
        s.setEvents(es);
        eventRepository.save(e1); //先存属性再存本体！！！
        eventRepository.save(e2);
        studentRepository.save(s);*/
