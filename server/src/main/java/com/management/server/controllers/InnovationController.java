package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.*;
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
        List<Map> result=new ArrayList<>();
        for(Event e:eventList) {
            if (e instanceof Innovation) {
                Map m = BeanUtil.beanToMap(e);
                s.getHonors().forEach(h -> {
                    if (h.getEvent().getEventId().equals(e.getEventId())) {
                        m.put("performance", h.getName());
                    }
                });
                m.remove("persons");
                result.add(m);
            }
        }
        return new DataResponse(0,result,null);
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
        //System.out.println(personsMap);
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
    @PostMapping("/getStudentsInfoByInnovation")
    public DataResponse getStudentsByInnovation(@RequestBody Map m)
    {
        Innovation innovation=innovationRepository.findByEventId((String) m.get("eventId"));
        List<Map> studentList = new ArrayList<>();
        for(Person p:innovation.getPersons())
        {
            if(p instanceof Student)
            {
                Map student=BeanUtil.beanToMap(p);
                for(Honor h:p.getHonors())
                {
                    if(h.getEvent().getEventId().equals((String) m.get("eventId")))
                    {
                        student.put("performance",h.getName());
                    }
                }
                studentList.add(student);
            }
        }
        return new DataResponse(0,studentList,null);
    }
}
