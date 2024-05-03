package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.DailyActivityRepository;
import com.management.server.repositories.StudentRepository;
import com.management.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class DailyActivityController {
    @Autowired
    DailyActivityRepository dailyActivityRepository;
    @Autowired
    StudentRepository studentRepository;
    @PostMapping("/getAllDailyActivities")
    public DataResponse getAllDailyActivities()
    {
        ArrayList<DailyActivity> list=(ArrayList<DailyActivity>) dailyActivityRepository.findAll();
        return new DataResponse(0,dailyActivityRepository.findAll(),null);
    }
    @PostMapping("/getDailyActivitiesByStudent")
    public DataResponse getDailyActivitiesByStudent()
    {
        Student s=studentRepository.findByStudentId(CommonMethod.getUsername());
        List<DailyActivity> list=new ArrayList<>();
        Set<Event> eventList=s.getEvents();
        for(Event e:eventList)
        {
            if(e instanceof DailyActivity)
            {
                list.add((DailyActivity) e);
            }
        }
        return new DataResponse(0,list,null);
    }
    @PostMapping("/addDailyActivity")
    public DataResponse addDailyActivity(@RequestBody Map m){
        List<Map> studentList=(ArrayList<Map>)(m.get("studentList"));
        Set<Person> studentSet=new HashSet<>();
        for(Map studentMap:studentList)
        {
            Student student=studentRepository.findByStudentId((String) studentMap.get("studentId"));
            studentSet.add(student);
        }
        DailyActivity dailyActivity=BeanUtil.mapToBean(m, DailyActivity.class, true, CopyOptions.create().ignoreError());
        dailyActivity.setPersons(studentSet);
        dailyActivityRepository.save(dailyActivity);
        return new DataResponse(0,null,"添加成功");
    }
    @PostMapping("/deleteDailyActivities")
    public DataResponse deleteDailyActivities(@RequestBody List<Map> list)
    {
        for(Map m:list)
        {
           dailyActivityRepository.deleteByEventId((String)m.get("eventId"));
        }
        return new DataResponse(0,null,"删除成功");
    }
    @PostMapping("/updateDailyActivity")
    public DataResponse updateDailyActivity(@RequestBody Map m)
    {
        //save()方法既可以用于保存新的实体，也可以用于更新已存在的实体。
       DailyActivity dailyActivity=dailyActivityRepository.findByEventId((String) m.get("eventId"));
        List<Map> personsMap = (List<Map>) m.get("studentList");
        Set<Person> persons = new HashSet<>();
        for (Map personMap : personsMap) {
            Person person = BeanUtil.mapToBean(personMap, Student.class, true, CopyOptions.create());
            persons.add(person);
        }
        m.remove("persons");
        BeanUtil.fillBeanWithMap(m, dailyActivity, true, CopyOptions.create());
        dailyActivity.setPersons(persons);
        dailyActivityRepository.save(dailyActivity);
        return new DataResponse(0,null,"更新成功");
    }
    @PostMapping("/getStudentsInfoByDailyActivity")
    public DataResponse getStudentsByDailyActivity(@RequestBody Map m)
    {
        DailyActivity dailyActivity = dailyActivityRepository.findByEventId((String) m.get("eventId"));
        List<Map> studentList = new ArrayList<>();
        for(Person p:dailyActivity.getPersons())
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
