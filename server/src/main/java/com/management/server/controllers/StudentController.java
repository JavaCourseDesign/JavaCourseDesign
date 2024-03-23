package com.management.server.controllers;

import com.management.server.models.Person;
import com.management.server.models.Student;
/*import com.management.server.repository.PersonRepository;*/
import com.management.server.payload.request.DataRequest;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RestController

public class StudentController {
    /*@Autowired
    private PersonRepository personRepository;*/
    @Autowired
    private StudentRepository studentRepository;
    public String demoStudent(){
        Student s=new Student();
        s.setName("wzk");
        s.setMajor("software");
        s.setGender("男");
        s.setStudentId("2023");
        studentRepository.save(s);
        return "HelloStudent";
    }

    @GetMapping("/checkEvents")
    public String checkEvents(){
        Student s=studentRepository.findById(1).get();
        return s.getEvents().toString();
    }

    /*@PostMapping("/addStudent")
    public DataResponse addStudent(@Valid @RequestBody Map<String,String> data){
        Student student=new Student();
        student.setName(data.get("name"));
        student.setGender(data.get("gender"));
        student.setMajor(data.get("studentId"));
        student.setClassName(data.get("major"));
        studentRepository.save(student);
        DataResponse r=new DataResponse();
        r.setMsg("添加成功"+student.getName()+data.get("name"));
        return r;
    }*/
    @PostMapping("/getStudent")
    public DataResponse getStudent()
    {
        ArrayList<Map> studentMapList=new ArrayList<>();
        List<Student> list=studentRepository.findAll();
        for(int i=0;i<list.size();i++)
        {
            Map m=new HashMap();
            m.put("studentId",list.get(i).getStudentId());
            m.put("name",list.get(i).getName());
            m.put("gender",list.get(i).getGender());
            m.put("major",list.get(i).getMajor());
            studentMapList.add(m);
        }
        DataResponse r=new DataResponse(200,studentMapList,null);
        return r;
    }
    @PostMapping("/addStudent")
    public DataResponse addStudent(@RequestBody Map<String,String> m)
    {
        String studentId=m.get("num");
        if(studentRepository.existsByStudentId(studentId))
        {
            return new DataResponse(0,null,"学号已存在，无法添加");
        }
        else
        {
            Student s=new Student();
            s.setStudentId(m.get("num"));
            s.setMajor(m.get("major"));
            s.setName(m.get("name"));
            s.setGender(m.get("gender"));
            studentRepository.save(s);
            return new DataResponse(1,null,"添加成功");
        }
    }
    @PostMapping("/deleteStudent")
    public DataResponse deleteStudent(@RequestBody Map<String,String> m)
    {
        studentRepository.deleteAllByStudentId(m.get("studentId"));
       return new DataResponse(1,null," ");
    }
}
