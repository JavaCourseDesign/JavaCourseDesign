package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Honor;
import com.management.server.models.Person;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.HonorRepository;
import com.management.server.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HonorController {
    @Autowired
    HonorRepository honorRepository;
    @Autowired
    StudentRepository studentRepository;
    @PostMapping("/getAllHonors")
    public DataResponse getAllHonors()
    {
        return new DataResponse(200,honorRepository.findAll(),null);
    }
    @PostMapping("/getHonorsByStudent")
    public DataResponse getHonorByStudent(@RequestBody Map m)
    {

        Student s=studentRepository.findByStudentId((String) m.get("studentId"));
        List<Honor> list=honorRepository.findByPerson(s);
        return new DataResponse(200,list,null);
    }
    @PostMapping("/addHonor")
    public DataResponse addhonor(@RequestBody Map m){
        Student s=studentRepository.findByStudentId((String) m.get("studentId"));
        Set<Person> studentList=new HashSet<>();
        studentList.add(s);
        Honor honor=new Honor();
        honor.setName((String) m.get("name"));
        honor.setTime((String) m.get("time"));
        honor.setMessage((String) m.get("message"));
        honor.setDepartment((String) m.get("department"));
        honorRepository.save(honor);
        return new DataResponse(0,null,"添加成功");
    }
    @PostMapping("/deleteHonor")
    public DataResponse deleteHonor(@RequestBody Map m)
    {
        honorRepository.deleteByHonorId((String)m.get("eventId"));
        return new DataResponse(0,null,"删除成功");
    }
    @PostMapping("/updateHonor")
    public DataResponse updateHonor(@RequestBody Map m)
    {
        //save()方法既可以用于保存新的实体，也可以用于更新已存在的实体。
        Honor honor=honorRepository.findByHonorId((String) m.get("eventId"));
        List<Map> personsMap = (List<Map>) m.get("persons");
        System.out.println(personsMap);
        Set<Person> persons = new HashSet<>();
        for (Map personMap : personsMap) {
            //改成student突然可以了,目前不知道有没有bug
            Person person = BeanUtil.mapToBean(personMap, Student.class, true, CopyOptions.create());
            persons.add(person);
        }
        m.remove("persons");
        BeanUtil.fillBeanWithMap(m, honor, true, CopyOptions.create());

        honorRepository.save(honor);
        return new DataResponse(0,null,"更新成功");
    }
}
