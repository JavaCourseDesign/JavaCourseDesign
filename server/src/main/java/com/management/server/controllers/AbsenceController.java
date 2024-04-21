package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Absence;
import com.management.server.models.Event;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import com.management.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AbsenceController {
    @Autowired
    private AbsenceRepository absenceRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private EventRepository eventRepository;
    @PostMapping("/getAllAbsences")
    public DataResponse getAllAbsences() {
        return new DataResponse(0,absenceRepository.findAll(),null);
    }
    @PostMapping("/getAbsencesByStudent")
    public DataResponse getAbsencesByStudent(@RequestBody Map m) {
        Student student=studentRepository.findByStudentId(CommonMethod.getUsername());
        return new DataResponse(0,absenceRepository.findAbsencesByPerson(student),null);
    }
    @PostMapping("/addAbsence")
    public DataResponse addAbsence(@RequestBody Map m) {

        Absence absence=new Absence();
        if(m.get("type").equals("学生"))
        {
            absence.setPerson(studentRepository.findByStudentId((String) m.get("id")));
        }
        else if(m.get("type").equals("教师"))
        {
            absence.setPerson(teacherRepository.findByTeacherId((String) m.get("id")));
        }
        //System.out.println((String)m.get("eventId"));
        //System.out.println(eventRepository.findEventByEventId((String) m.get("eventId")));
        Event event=eventRepository.findEventByEventId((String) m.get("eventId"));
        if(absenceRepository.existsByEvent(event))
        {
            return new DataResponse(1,null,"该事件已经添加过");
        }
        absence.setEvent(event);
        absenceRepository.save(absence);
        return new DataResponse(0,null,"添加成功");
    }
    @PostMapping("/uploadAbsence")
    public DataResponse uploadAbsence(@RequestBody Map m)
    {
        Absence absence=new Absence();
        Event event=eventRepository.findEventByEventId((String) m.get("eventId"));
        if(absenceRepository.existsByEvent(event))
        {
            return new DataResponse(1,null,"该事件已经添加过");
        }
        absence.setEvent(event);
        absence.setPerson(studentRepository.findByStudentId((String) m.get("id")));
        absence.setOffReason((String) m.get("offReason"));
        absence.setDestination((String) m.get("destination"));
        absenceRepository.save(absence);
        return new DataResponse(0,null,"添加成功");
    }
    @PostMapping("/updateAbsence")
    public DataResponse updateAbsence(@RequestBody Map m)
    {
        Absence absence=absenceRepository.findAbsenceByAbsenceId((String) m.get("absenceId"));
        absence.setIsApproved((Boolean) m.get("isApproved"));
        absenceRepository.save(absence);
        return new DataResponse(0,null,"修改成功");
    }
}
