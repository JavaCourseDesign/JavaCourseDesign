package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Honor;
import com.management.server.models.Person;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.DailyActivityRepository;
import com.management.server.repositories.HonorRepository;
import com.management.server.repositories.StudentRepository;
import com.management.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class HonorController {
    @Autowired
    private HonorRepository honorRepository;
    @Autowired
    private StudentRepository studentRepository;
    @PostMapping("/getAllHonors")
    public DataResponse getAllHonors(){
        return new DataResponse(0,honorRepository.findAll(),null);
    }
    @PostMapping("/addHonor")
    public DataResponse addHonor(@RequestBody Map m){
        Honor honor= BeanUtil.mapToBean(m, Honor.class, true, CopyOptions.create().ignoreError());
        List<Person> studentList=new ArrayList<>();
        for(Map studentMap:(ArrayList<Map>)(m.get("studentList")))
        {
            Student student=studentRepository.findByStudentId((String) studentMap.get("studentId"));
            studentList.add(student);
        }
        honor.setPersons(studentList);
        honorRepository.save(honor);
        return new DataResponse(0,null,"添加成功");
    }
    @PostMapping("/deleteHonors")
    public DataResponse deleteHonors(@RequestBody ArrayList<Map> honorList){
        for(Map honorMap:honorList)
        {
            honorRepository.deleteByHonorId((String) honorMap.get("honorId"));
        }
        return new DataResponse(0,null,"删除成功");
    }
    @PostMapping("/updateHonor")
    public DataResponse updateHonor(@RequestBody Map m){
        Honor honor= BeanUtil.mapToBean(m, Honor.class, true, CopyOptions.create().ignoreError());
        List<Person> studentList=new ArrayList<>();
        for(Map studentMap:(ArrayList<Map>)(m.get("studentList")))
        {
            Student student=studentRepository.findByStudentId((String) studentMap.get("studentId"));
            studentList.add(student);
        }
        honor.setPersons(studentList);
        honorRepository.save(honor);
        return new DataResponse(0,null,"更新成功");
    }
    @PostMapping("/getHonorsByStudent")
    public DataResponse getHonorsByStudent(){
        Student s=studentRepository.findByStudentId(CommonMethod.getUsername());
        return new DataResponse(0,s.getHonors(),null);
    }
}
