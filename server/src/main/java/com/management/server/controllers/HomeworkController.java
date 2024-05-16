package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import com.management.server.util.CommonMethod;
import com.management.server.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class HomeworkController {
    @Autowired
    HomeworkRepository homeworkRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @PostMapping("/getAllHomework")
    public DataResponse getAllHomework(){
        return new DataResponse(0,homeworkRepository.findAll(),null);
    }
    @PostMapping("/addHomework")
    public DataResponse addHomework(@RequestBody Map m){
       // System.out.println("\nmap:"+m);
        List<Person> personList= personRepository.findPersonsByCourseId((String) m.get("courseId"));
        //System.out.println("\npersonList:"+personList);
        for(Person p:personList){
            Student s=studentRepository.findByPersonId(p.getPersonId());
            //System.out.println("\nstudent:"+s);
            if(s!=null){
                Homework h=BeanUtil.mapToBean(m,Homework.class,true);
                //System.out.println("\nhomework:"+h);
                h.setCourse(courseRepository.findByCourseId((String) m.get("courseId")));
                h.setStudent(s);
                homeworkRepository.save(h);
            }
        }
        return new DataResponse(0,null,null);
    }
    @PostMapping("/markHomework")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse markHomework(@RequestBody Map m){
        Homework h=homeworkRepository.findHomeworkByHomeworkId((String) m.get("homeworkId"));
        h.setGrade((String) m.get("grade"));
        homeworkRepository.save(h);
        return new DataResponse(0,null,null);
    }
    @PostMapping("/getStudentHomework")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getStudentHomework(){
        Student s=studentRepository.findByStudentId(CommonMethod.getUsername());
        return new DataResponse(0,homeworkRepository.findHomeworkByStudent(s),null);
    }
    @PostMapping("/getTeacherHomework")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse getTeacherHomework(@RequestBody Map m){
        Teacher t=teacherRepository.findByTeacherId(CommonMethod.getUsername());
        Course c=courseRepository.findByCourseId((String) m.get("courseId"));
        //if(m.get("courseId")==null) 返回该老师的所有作业
        if(!c.getPersons().contains(t)) return new DataResponse(1,null,"您不是该课程的教师");
        return new DataResponse(0,homeworkRepository.findHomeworkByCourse(c),null);
    }

    /*@PostMapping("/getCourseHomework")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse getCourseHomework(@RequestBody Map m){
        Course c=courseRepository.findByCourseId((String) m.get("courseId"));
        return new DataResponse(0,homeworkRepository.findHomeworkByCourse(c),null);
    }*/

    @PostMapping("/uploadHomework")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse uploadHomeWork(@RequestBody byte[] barr,
                                       @RequestParam(name = "fileName") String fileName,
    @RequestParam(name="paras") String homeworkId ){
        Homework h=homeworkRepository.findHomeworkByHomeworkId(homeworkId);
        if(h.getHomeworkFile()!=null)
        {
            FileUtil.deleteFile("homework",h.getHomeworkFile());
        }
        h.setHomeworkFile(fileName);
        h.setSubmitTime(LocalDate.now());
        homeworkRepository.save(h);
        DataResponse r= FileUtil.uploadFile(barr, "homework",fileName);
        return r;
    }

    @PostMapping("/getHomeworkFile")
    public DataResponse getHomeworkFile(@RequestBody Map m)
    {
        Homework h=homeworkRepository.findHomeworkByHomeworkId((String) m.get("homeworkId"));
        DataResponse r=FileUtil.downloadFile("homework",h.getHomeworkFile());
        return r;
    }
    @PostMapping("/deleteHomework")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse deleteHomework(@RequestBody List<Map> list)
    {
        for(Map m:list)
        {
            Homework h=homeworkRepository.findHomeworkByHomeworkId((String) m.get("homeworkId"));
            if(h.getHomeworkFile()!=null)
            {
                FileUtil.deleteFile("homework",h.getHomeworkFile());
            }
            homeworkRepository.delete(h);
        }
        return new DataResponse(0,null,null);
    }

}
