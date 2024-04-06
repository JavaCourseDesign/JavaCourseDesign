package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Teacher;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.TeacherRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TeacherController {
    @Autowired
    TeacherRepository teacherRepository;

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
        Teacher teacher = BeanUtil.mapToBean(m, Teacher.class, true, CopyOptions.create());//要求map键值与对象一致
        teacherRepository.save(teacher);
        return new DataResponse(0,null,"添加成功");
    }

    @PostMapping("/deleteTeacher")
    public DataResponse deleteTeacher(@RequestBody Map m)
    {
        teacherRepository.deleteAllByTeacherId(""+m.get("teacherId"));
        return new DataResponse(0,null," ");
    }

   /* @PostMapping("/updateTeacher")
    public DataResponse updateTeacher(@RequestBody Map m) {
        teacherRepository.deleteAllByPersonId(Integer.parseInt((""+ m.get("personId")).split("\\.")[0]));
        addTeacher(m);
        return new DataResponse(0, null, " ");
    }*/
}