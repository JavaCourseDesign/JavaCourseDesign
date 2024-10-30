package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import com.management.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//event controller负责实现innovation dailyActivity; lesson course单独实现
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventController {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @PostMapping("/getAllEvents")
    public DataResponse getAllEvents() {
        return new DataResponse(0, eventRepository.findAll(), null);
    }

    @PostMapping("/getAllEventsExceptLessons")
    public DataResponse getAllEventsExceptLessons() {
        List<Event> list = eventRepository.findAll();
        List<Event> events = new ArrayList<>();
        for (Event e : list) {
            if (!(e instanceof Lesson)) {
                events.add(e);
            }
        }
        return new DataResponse(0, events, null);
    }

    @PostMapping("/getStudentEvents")
    public DataResponse getStudentEvents() {
        Student student = studentRepository.findByStudentId(CommonMethod.getUsername());
        if(student == null)
            return new DataResponse(-1, null, "学生不存在");
        return new DataResponse(0, student.getEvents(), null);
    }

    @PostMapping("/getTeacherEvents")
    public DataResponse getTeacherEvents() {
        Teacher teacher = teacherRepository.findByTeacherId(CommonMethod.getUsername());
        if(teacher == null)
            return new DataResponse(-1, null, "教师不存在");
        return new DataResponse(0, teacher.getEvents(), null);
    }

    @PostMapping("/getEvent")
    public DataResponse getEvent(@RequestBody Map m) {
        return new DataResponse(0, eventRepository.findById((String) m.get("eventId")), null);
    }

    @PostMapping("/saveEvent")
    public DataResponse saveEvent(@RequestBody Map m) {
        Event event;
        if(eventRepository.existsById((String) m.get("eventId")))
            event = eventRepository.findById((String) m.get("eventId")).get();
        else event = new Event();
        BeanUtil.fillBeanWithMap(m, event, true);
        eventRepository.save(event);
        return new DataResponse(0, null, "保存成功");
    }

}
