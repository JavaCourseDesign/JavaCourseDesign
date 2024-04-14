package com.management.server.controllers;

import com.management.server.models.Absence;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.AbsenceRepository;
import com.management.server.repositories.PersonRepository;
import com.management.server.repositories.StudentRepository;
import com.management.server.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/getAllAbsences")
    public DataResponse getAllAbsences() {
        return new DataResponse(0,absenceRepository.findAll(),null);
    }
    @PostMapping("/addAbsence")
    public DataResponse addAbsence(@RequestBody Map<String,String> m) {
        Absence absence=new Absence();
        if(m.get("type").equals("学生"))
        {
            absence.setPerson(studentRepository.findByStudentId(m.get("id")));
        }
        else if(m.get("type").equals("教师"))
        {
            absence.setPerson(teacherRepository.findByTeacherId(m.get("id")));
        }
        absence.setTime(m.get("time"));
        absenceRepository.save(absence);
        return new DataResponse(0,null,"添加成功");
    }
}
