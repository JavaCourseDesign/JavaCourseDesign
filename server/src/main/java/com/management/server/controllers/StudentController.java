package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Innovation;
import com.management.server.models.Person;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.AdministrativeClassRepository;
import com.management.server.repositories.CourseRepository;
import com.management.server.repositories.InnovationRepository;
import com.management.server.repositories.StudentRepository;
import com.management.server.util.CommonMethod;
import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

@RestController
public class StudentController {
    @Autowired
    private ResourceLoader resourceLoader;  //资源装在服务对象自动注入
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private AdministrativeClassRepository administrativeClassRepository;
    @Autowired
    private InnovationRepository innovationRepository;
    @PostMapping("/getStudent")
    public DataResponse getStudent()
    {
        String username = CommonMethod.getUsername();
        Student s = studentRepository.findByStudentId(username);

        Map student = BeanUtil.beanToMap(s) ;
        //student.put("courses",courseRepository.findCoursesByPersonId(map.get("personId")));
        student.put("className",administrativeClassRepository.findAdministrativeClassByStudent(s)+"班");
        return new DataResponse(0,student,null);
    }
    @PostMapping("/getAllStudents")
    public DataResponse getAllStudents()
    {

        return new DataResponse(200,studentRepository.findAll(),null);
    }
    @PostMapping("/addStudent")
    public DataResponse addStudent(@RequestBody Map m)
    {
        String studentId = (String) m.get("studentId");
        if(studentRepository.existsByStudentId(studentId)) {
            return new DataResponse(-1,null,"学号已存在，无法添加");
        }
        Student student = BeanUtil.mapToBean(m, Student.class, true, CopyOptions.create());//要求map键值与对象一致
        studentRepository.save(student);
        return new DataResponse(0,null,"添加成功");
    }

    @PostMapping("/deleteStudent")
    public DataResponse deleteStudent(@RequestBody Map m)
    {
        studentRepository.deleteAllByStudentId(""+m.get("studentId"));
        return new DataResponse(0,null,"删除成功");
    }

    @PostMapping("/updateStudent")
    public DataResponse updateStudent(@RequestBody Map m) {
        String personId = (String) m.get("personId");
        Optional<Student> optionalStudent = Optional.ofNullable(studentRepository.findByPersonId(personId));
        if(optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            System.out.println(m);
            BeanUtil.fillBeanWithMap(m, student, true, CopyOptions.create());
            studentRepository.save(student);
            return new DataResponse(0, null, "更新成功");
        } else {
            return new DataResponse(-1, null, "学号不存在，无法更新");
        }
    }
    @PostMapping("/saveStudentPersonalInfo")
    public DataResponse saveStudentPersonalInfo(@RequestBody Map<String,String> m)
    {
        String studentId = CommonMethod.getUsername();
        Student student = studentRepository.findByStudentId(studentId);
        student.setPhone(m.get("phone"));
        student.setAddress(m.get("address"));
        student.setHomeTown(m.get("homeTown"));
        student.setFamilyMember(m.get("familyMember"));
        student.setFamilyMemberPhone(m.get("familyMemberPhone"));
        student.setEmail(m.get("email"));
        studentRepository.save(student);
        return new DataResponse(0,null,"保存成功");
    }
    @PostMapping("/getStudentIntroduce")
    public ResponseEntity<StreamingResponseBody> getStudentIntroduce()
    {
        String studentId=CommonMethod.getUsername();
        Map info=getMapFromStudentForIntroduce(studentId);
        String content = (String)info.get("introduce");
        content = addHeadInfo(content,"<style> html { font-family: \"SourceHanSansSC\", \"Open Sans\";}  </style> <meta charset='UTF-8' />  <title>Insert title here</title>");
        content = replaceNameValue(content,info);
        return getPdfDataFromHtml(content);
    }
    public ResponseEntity<StreamingResponseBody> getPdfDataFromHtml(String htmlContent) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, "classpath:/static/resume.html");
            builder.useFastMode();
            //builder.withWmMargins(0, 0, 0, 0).useDefaultPageSize(PageOrientation.PORTRAIT, PageSize.A4);
            builder.useCacheStore(PdfRendererBuilder.CacheStore.PDF_FONT_METRICS, fSDefaultCacheStore);
            Resource resource = resourceLoader.getResource("classpath:font/SourceHanSansSC-Regular.ttf");
           InputStream fontInput = resource.getInputStream();
            builder.useFont(new FSSupplier<InputStream>() {
                @Override
                public InputStream supply() {
                    return fontInput;
                }
            }, "SourceHanSansSC");
            StreamingResponseBody stream = outputStream -> {
                builder.toStream(outputStream);
                builder.run();
            };
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(stream);
        }
        catch (Exception e) {
            return  ResponseEntity.internalServerError().build();
        }
    }
    public static String replaceNameValue(String html, Map<String,String>m) {
        if(html== null || html.length() == 0)
            return html;
        StringBuffer buf = new StringBuffer();
        StringTokenizer sz = new StringTokenizer(html,"$");
        if(sz.countTokens()<=1)
            return html;
        String str,key,value;
        Integer index;
        while(sz.hasMoreTokens()) {
            str = sz.nextToken();
            if(str.charAt(0)== '{') {
                index = str.indexOf("}",1);
                key = str.substring(1,index);
                value = m.get(key);
                if(value== null){
                    value = "";
                }
                buf.append(value+str.substring(index+1,str.length()));
            }else {
                buf.append(str);
            }
        }
        return buf.toString();
    }

    public static String addHeadInfo(String html,String head) {
        int index0 = html.indexOf("<head>");
        int index1 = html.indexOf("</head>");
        return html.substring(0,index0+6)+head + html.substring(index1,html.length());
    }

    public Map getMapFromStudentForIntroduce(String studentId) {
        Student student = studentRepository.findByStudentId(studentId);
        Map map = BeanUtil.beanToMap(student);
        map.put("className",administrativeClassRepository.findAdministrativeClassByStudent(student)+"班");
        List<Innovation> list=innovationRepository.findByPersons(student);
        String filePath = "server/src/main/resources/static/resume.html";
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        map.put("introduce",content);
        map.put("photo","../Photo/" + CommonMethod.getUsername() + ".jpg");
        int cnt1=0;// 学科竞赛
        int cnt2=0;//科研成果
        int cnt3=0;//社会实践
        for(Innovation innovation:list){
            if(innovation.getType().equals("学科竞赛"))
            {
                cnt1++;
                map.put("competitionTime"+cnt1,innovation.getTime());
                map.put("competitionName"+cnt1,innovation.getName());
                map.put("competitionLevel"+cnt1,innovation.getPerformance());
            }
            else if(innovation.getType().equals("科研成果"))
            {
                cnt2++;
                map.put("paperName"+cnt2,innovation.getName());
                map.put("paperLocation"+cnt2,innovation.getLocation());
                map.put("paperPerformance"+cnt2,innovation.getPerformance());
            }
            else if(innovation.getType().equals("社会实践"))
            {
                cnt3++;
                map.put("practiceTime"+cnt3,innovation.getTime());
                map.put("practiceName"+cnt3,innovation.getName());
                map.put("practicePerformance"+cnt3,innovation.getPerformance());
            }
        }
        return map;
    }

}
