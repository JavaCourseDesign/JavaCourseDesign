package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.AdministrativeClass;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.AdministrativeClassRepository;
import com.management.server.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class AdministrativeClassController {
    @Autowired
    private AdministrativeClassRepository administrativeClassRepository;
    @Autowired
    private StudentRepository studentRepository;
    @PostMapping("/getAllAdministrativeClasses")
    public DataResponse getAllAdministrativeClasses(){
        return new DataResponse(0,administrativeClassRepository.findAll(),null);
    }

    @PostMapping("/addAdministrativeClass")
    public DataResponse addAdministrativeClass(@RequestBody Map m){
        String administrativeClassId = (String) m.get("administrativeClassId");
        if(administrativeClassRepository.existsByAdministrativeClassId(administrativeClassId)) {
            return new DataResponse(-1,null,"行政班已存在，无法添加");
        }
        AdministrativeClass administrativeClass = BeanUtil.mapToBean(m, AdministrativeClass.class, true, CopyOptions.create());//要求map键值与对象一致
        if(m.get("studentIds") != null){
            List<Student> students = new ArrayList<>();
            for (int i = 0; i < ((ArrayList)m.get("studentIds")).size(); i++) {
                students.add(studentRepository.findByStudentId((((Map)((ArrayList)m.get("studentIds")).get(i)).get("studentId")).toString()));
            }
            administrativeClass.setStudents(students);
        }
        administrativeClassRepository.save(administrativeClass);
        return new DataResponse(0,null,"添加成功");
    }

    @PostMapping("/deleteAdministrativeClass")
    public DataResponse deleteAdministrativeClass(@RequestBody Map m){
        administrativeClassRepository.deleteAllByAdministrativeClassId(""+m.get("administrativeClassId"));
        return new DataResponse(0,null,"删除成功");
    }

    @PostMapping("/updateAdministrativeClass")
    public DataResponse updateAdministrativeClass(@RequestBody Map m) {
        String administrativeClassId = (String) m.get("administrativeClassId");
        AdministrativeClass administrativeClass = administrativeClassRepository.findByAdministrativeClassId(administrativeClassId);
        if (administrativeClass == null) {
            return new DataResponse(-1,null,"行政班不存在");
        }
        if(m.get("studentIds") != null){
            List<Student> students = new ArrayList<>();
            for (int i = 0; i < ((ArrayList)m.get("studentIds")).size(); i++) {
                students.add(studentRepository.findByStudentId((((Map)((ArrayList)m.get("studentIds")).get(i)).get("studentId")).toString()));
            }
            administrativeClass.setStudents(students);
        }
        administrativeClassRepository.save(administrativeClass);
        return new DataResponse(0,null,"更新成功");
    }
}
