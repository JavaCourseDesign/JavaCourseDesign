package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Innovation;
import com.management.server.models.Person;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.InnovationRepository;
import com.management.server.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class InnovationController {
    @Autowired
    InnovationRepository innovationRepository;
    @Autowired
    StudentRepository studentRepository;
    @PostMapping("/getAllInnovations")
    public DataResponse getAllInnovations()
    {
        return new DataResponse(200,innovationRepository.findAll(),null);
    }
    @PostMapping("/getInnovationsByStudent")
    public DataResponse getInnovationByStudent(@RequestBody Map m)
    {
        Student s=studentRepository.findByStudentId((String) m.get("studentId"));
        List<Innovation> list=innovationRepository.findByPersons(s);
        return new DataResponse(200,list,null);
    }
    @PostMapping("/addInnovation")
    public DataResponse addInnovation(@RequestBody Map m){
        Student s=studentRepository.findByStudentId((String) m.get("studentId"));
        Set<Person> studentList=new HashSet<>();
        studentList.add(s);
        Innovation innovation=new Innovation();
        innovation.setName((String) m.get("name"));
        innovation.setType((String) m.get("type"));
        innovation.setTime((String) m.get("time"));
        innovation.setLocation((String) m.get("location"));
        innovation.setPerformance((String) m.get("performance"));
        innovation.setPersons(studentList);
        innovationRepository.save(innovation);
        return new DataResponse(0,null,"添加成功");
    }
    @PostMapping("/deleteInnovation")
    public DataResponse deleteInnovation(@RequestBody Map m)
    {
        innovationRepository.deleteByEventId((String)m.get("eventId"));
        return new DataResponse(0,null,"删除成功");
    }
    @PostMapping("/updateInnovation")
    public DataResponse updateInnovation(@RequestBody Map m)
    {
        //save()方法既可以用于保存新的实体，也可以用于更新已存在的实体。
        Innovation innovation=innovationRepository.findByEventId((String) m.get("eventId"));
        List<Map> personsMap = (List<Map>) m.get("persons");
        System.out.println(personsMap);
        Set<Person> persons = new HashSet<>();
        for (Map personMap : personsMap) {
            //改成student突然可以了,目前不知道有没有bug
            Person person = BeanUtil.mapToBean(personMap, Student.class, true, CopyOptions.create());
            persons.add(person);
        }
        m.remove("persons");
        BeanUtil.fillBeanWithMap(m, innovation, true, CopyOptions.create());
        innovation.setPersons(persons);
        innovationRepository.save(innovation);
        return new DataResponse(0,null,"更新成功");
    }
}
