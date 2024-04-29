package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Family;
import com.management.server.models.Student;
import com.management.server.models.Teacher;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.TeacherRepository;
import com.management.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class TeacherController {
    @Autowired
    TeacherRepository teacherRepository;

    @PostMapping("/getTeacher")
    public DataResponse getTeacher()
    {
        String username = CommonMethod.getUsername();
        Teacher t = teacherRepository.findByTeacherId(username);
        return new DataResponse(0,t,null);
    }

    @PostMapping("/getAllTeachers")
    public DataResponse getAllTeachers()
    {
        return new DataResponse(200,teacherRepository.findAll(),null);
    }

    @PostMapping("/addTeacher")
    public DataResponse addTeacher(@RequestBody Map<String,String> m)
    {
        String teacherId = m.get("teacherId");
        if(teacherRepository.existsByTeacherId(teacherId)) {
            return new DataResponse(-1,null,"教师编号已存在，无法添加");
        }
        //要求map键值与对象一致
        Teacher teacher = BeanUtil.toBean(m, Teacher.class, CopyOptions.create());
        teacherRepository.save(teacher);
        return new DataResponse(0,null,"添加成功");
    }

    @PostMapping("/deleteTeacher")
    public DataResponse deleteTeacher(@RequestBody Map m)
    {
        teacherRepository.deleteAllByTeacherId(""+m.get("teacherId"));
        return new DataResponse(0,null," ");
    }

    @PostMapping("/updateTeacher")
    public DataResponse updateTeacher(@RequestBody Map m) {
        String personId = (String) m.get("personId");
        Optional<Teacher> optionalTeacher = Optional.ofNullable(teacherRepository.findByPersonId(personId));
        if(optionalTeacher.isPresent()) {
            Teacher teacher = optionalTeacher.get();
            BeanUtil.fillBeanWithMap(m, teacher, CopyOptions.create());
            teacherRepository.save(teacher);
            return new DataResponse(0, null, "更新成功");
        } else {
            return new DataResponse(-1, null, "工号不存在，无法更新");
        }
    }

    @PostMapping("/saveTeacherPersonalInfo")
    public DataResponse saveTeacherPersonalInfo(@RequestBody Map m)
    {
        String teacherId = CommonMethod.getUsername();
        Teacher teacher = teacherRepository.findByTeacherId(teacherId);

        BeanUtil.fillBeanWithMap(m, teacher, CopyOptions.create().ignoreError());

        teacherRepository.save(teacher);
        return new DataResponse(0, null, "保存成功");
    }


}