package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Absence;
import com.management.server.models.Event;
import com.management.server.models.Student;
import com.management.server.models.Teacher;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import com.management.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
    @PostMapping("/getAllStudentAbsences")
    public DataResponse getAllAbsences() {
        List<Absence> list=absenceRepository.findAll();
        List<Absence> studentAbsenceList=new ArrayList<>();
        for(Absence a:list)
        {
            if(a.getPerson() instanceof Student)
            {
                studentAbsenceList.add(a);
            }
        }
        return new DataResponse(0,studentAbsenceList,null);
    }
    @PostMapping("/getAllTeacherAbsences")
    public DataResponse getAllTeacherAbsences() {
        List<Absence> list=absenceRepository.findAll();
        List<Absence> teacherAbsenceList=new ArrayList<>();
        for(Absence a:list)
        {
            if(a.getPerson() instanceof Teacher)
            {
                teacherAbsenceList.add(a);
            }
        }
        return new DataResponse(0,teacherAbsenceList,null);
    }
    @PostMapping("/getAbsencesByStudent")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getAbsencesByStudent(@RequestBody Map m) {

        Student student=studentRepository.findByStudentId(CommonMethod.getUsername());
        return new DataResponse(0,absenceRepository.findAbsencesByPerson(student),null);
    }
    @PostMapping("/addStudentAbsence")
    public DataResponse addAbsence(@RequestBody Map m) {
        ArrayList<Map> studentList=(ArrayList<Map>) m.get("studentList");
        Event event=eventRepository.findEventByEventId((String) m.get("eventId"));
        for(Map studentMap:studentList)
        {
            Student student=studentRepository.findByStudentId((String) studentMap.get("studentId"));
            if(absenceRepository.existsByEventAndPerson(event,student))
            {
                return new DataResponse(1,null,"重复添加请假事件！");
            }
        }
        for (Map studentMap:studentList)
        {
            Absence absence=new Absence();
            Student student=studentRepository.findByStudentId((String) studentMap.get("studentId"));
            absence.setPerson(student);
            absence.setEvent(event);
            absenceRepository.save(absence);
        }
        return new DataResponse(0,null,"添加成功");
    }
    @PostMapping("/uploadAbsence")
    public DataResponse uploadAbsence(@RequestBody Map m)
    {
        Absence absence=new Absence();
        Event event=eventRepository.findEventByEventId((String) m.get("eventId"));
       /* if(absenceRepository.existsByEvent(event))
        {
            return new DataResponse(1,null,"该事件已经添加过");
        }*/
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
