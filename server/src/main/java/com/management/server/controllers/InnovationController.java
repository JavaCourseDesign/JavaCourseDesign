package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Event;
import com.management.server.models.Innovation;
import com.management.server.models.Person;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.InnovationRepository;
import com.management.server.repositories.StudentRepository;
import com.management.server.util.CommonMethod;
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
        ArrayList<Innovation> list=(ArrayList<Innovation>) innovationRepository.findAll();
        return new DataResponse(0,innovationRepository.findAll(),null);
    }
    @PostMapping("/getInnovationsByStudent")
    public DataResponse getInnovationByStudent()
    {
        Student s=studentRepository.findByStudentId(CommonMethod.getUsername());
        Set<Event> eventList=s.getEvents();
        List<Innovation> list=new ArrayList<>();
        for(Event e:eventList)
        {
            if(e instanceof Innovation)
            {
                list.add((Innovation) e);
            }
        }
        return new DataResponse(0,list,null);
    }
    @PostMapping("/addInnovation")
    public DataResponse addInnovation(@RequestBody Map m){
        List<Map> studentList=(ArrayList<Map>)(m.get("studentList"));
        Set<Person> studentSet=new HashSet<>();
        for(Map studentMap:studentList)
        {
            Student student=studentRepository.findByStudentId((String) studentMap.get("studentId"));
            studentSet.add(student);
        }
        Innovation innovation=BeanUtil.mapToBean(m, Innovation.class, true, CopyOptions.create().ignoreError());
        innovation.setPersons(studentSet);
        innovationRepository.save(innovation);
        return new DataResponse(0,null,"添加成功");
    }
    @PostMapping("/deleteInnovations")
    public DataResponse deleteInnovation(@RequestBody List<Map> list)
    {
        for(Map m:list)
        {
            innovationRepository.deleteByEventId((String)m.get("eventId"));
        }
        return new DataResponse(0,null,"删除成功");
    }
    @PostMapping("/updateInnovation")
    public DataResponse updateInnovation(@RequestBody Map m)
    {
        //save()方法既可以用于保存新的实体，也可以用于更新已存在的实体。
        Innovation innovation=innovationRepository.findByEventId((String) m.get("eventId"));
        List<Map> personsMap = (List<Map>) m.get("studentList");
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
