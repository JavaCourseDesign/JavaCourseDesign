package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Clazz;
import com.management.server.models.Score;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.ClazzRepository;
import com.management.server.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ClazzController {
    @Autowired
    private ClazzRepository clazzRepository;
    @Autowired
    private StudentRepository studentRepository;
    @PostMapping("/getAllClazz")
    public DataResponse getAllClazz(){
        List<Clazz> clazzes = clazzRepository.findAll();
        List<Map> clazzesMap = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.##");
        for (Clazz clazz : clazzes) {
            Map<String, Object> clazzMap = BeanUtil.beanToMap(clazz);
            clazzMap.put("studentCount", clazz.getStudents().size());
            clazzMap.put("maleCount", clazz.getStudents().stream().filter(student -> student.getGender().equals("男")).count());
            clazzMap.put("femaleCount", clazz.getStudents().stream().filter(student -> student.getGender().equals("女")).count());
            clazzMap.put("CPCCount", clazz.getStudents().stream().filter(student -> student.getSocial().equals("共产党员")).count());
            clazzMap.put("CYLCount", clazz.getStudents().stream().filter(student -> student.getSocial().equals("共青团员")).count());
            clazzMap.put("under18Count", clazz.getStudents().stream().filter(student -> student.getAge()!=-1&&student.getAge() < 18).count());
            clazzMap.put("over18Count", clazz.getStudents().stream().filter(student -> student.getAge() >= 18).count());
            int innoCount = 0;
            for (Student student : clazz.getStudents()) {
                innoCount+=student.getInnovations().size();
            }
            clazzMap.put("innoCount", innoCount);
            int honorCount = 0;
            for (Student student : clazz.getStudents()) {
                honorCount+=student.getHonors().size();
            }
            clazzMap.put("honorCount", honorCount);
            double avgScore = 0;
            for (Student student : clazz.getStudents()) {
                double pslAvgScore = 0;
                List<Score> scores = student.getScores();
                for (Score score : scores) {
                    pslAvgScore+=score.getMark();
                }
                avgScore+=pslAvgScore;
            }
            clazzMap.put("avgScore", Double.parseDouble(df.format(avgScore/clazz.getStudents().size())));
            clazzesMap.add(clazzMap);
        }

        return new DataResponse(0, clazzesMap,null);
    }

    @PostMapping("/addClazz")
    public DataResponse addClazz(@RequestBody Map m){
        String clazzId = (String) m.get("clazzId");
        if(clazzRepository.existsByClazzId(clazzId)) {
            return new DataResponse(-1,null,"行政班已存在，无法添加");
        }
        Clazz clazz = BeanUtil.mapToBean(m, Clazz.class, true, CopyOptions.create());//要求map键值与对象一致
        if(m.get("studentIds") != null){
            List<Student> students = new ArrayList<>();
            for (int i = 0; i < ((ArrayList)m.get("studentIds")).size(); i++) {
                students.add(studentRepository.findByStudentId((((Map)((ArrayList)m.get("studentIds")).get(i)).get("studentId")).toString()));
            }
            clazz.setStudents(students);
        }
        clazz.setName(""+m.get("major")+m.get("grade")+"级"+m.get("clazzNumber")+"班");
        clazzRepository.save(clazz);
        return new DataResponse(0,null,"添加成功");
    }

    @PostMapping("/deleteClazz")
    public DataResponse deleteClazz(@RequestBody Map m){
        clazzRepository.deleteAllByClazzId(""+m.get("clazzId"));
        return new DataResponse(0,null,"删除成功");
    }

    @PostMapping("/updateClazz")
    public DataResponse updateClazz(@RequestBody Map m) {
        String clazzId = (String) m.get("clazzId");
        Clazz clazz = clazzRepository.findByClazzId(clazzId);
        if (clazz == null) {
            return new DataResponse(-1,null,"行政班不存在");
        }
        BeanUtil.fillBeanWithMap(m, clazz, true, true);
        clazz.setName(""+m.get("major")+m.get("grade")+"级"+m.get("clazzNumber")+"班");
        if(m.get("studentIds") != null){
            List<Student> students = new ArrayList<>();
            for (int i = 0; i < ((ArrayList)m.get("studentIds")).size(); i++) {
                students.add(studentRepository.findByStudentId((((Map)((ArrayList)m.get("studentIds")).get(i)).get("studentId")).toString()));
            }
            clazz.setStudents(students);
        }
        clazzRepository.save(clazz);
        return new DataResponse(0,null,"更新成功");
    }
}
