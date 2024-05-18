package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdcardUtil;
import com.management.server.models.Teacher;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.TeacherRepository;
import com.management.server.util.CommonMethod;
import com.management.server.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import static com.management.server.util.NativePlaceUtil.getNativePlace;

@RestController
public class TeacherController {
    @Autowired
    TeacherRepository teacherRepository;

    @PostMapping("/getTeacher")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse getTeacher()
    {
        String username = CommonMethod.getUsername();
        Teacher t = teacherRepository.findByTeacherId(username);
        return new DataResponse(0,t,null);
    }

    @PostMapping("/getTeacherByPersonId")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherByPersonId(@RequestBody Map m)
    {
        String personId = (String) m.get("personId");
        Teacher t = teacherRepository.findByPersonId(personId);
        Map teacher = BeanUtil.beanToMap(t) ;
        teacher.put("honor",t.getHonors());
        //文件不存在时可能出现问题，可以把方法的返回字符串调成一个固定图片
        if(t.getPhoto()!=null&&!Objects.equals(t.getPhoto(), "请先上传文件！"))
        {
            teacher.put("photo", FileUtil.getPhotoImageStr(t.getPhoto()));
            System.out.println("文件合法"+t.getPhoto());
        }
        return new DataResponse(0,teacher,null);
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
        Teacher teacher = teacherRepository.findByPersonId(personId);
        if(teacher != null) {
            BeanUtil.fillBeanWithMap(m, teacher, CopyOptions.create());
            boolean flag = completeTeacherById(teacher);
            if(!flag) return new DataResponse(-1,null,"身份证号不合法，无法更新");
            teacherRepository.save(teacher);
            return new DataResponse(0, null, "更新成功");
        } else {
            return addTeacher(m);
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

    private boolean completeTeacherById(Teacher s)
    {
        String id = s.getIdCardNum();
        if(!IdcardUtil.isValidCard(id)) return false;
        s.setHomeTown(getNativePlace(Integer.parseInt(IdcardUtil.getDistrictCodeByIdCard(id))));
        s.setGender(IdcardUtil.getGenderByIdCard(id) == 1 ? "男" : "女");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        s.setBirthday(IdcardUtil.getBirthDate(id).toLocalDateTime().format(formatter));
        return true;
    }


}