package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import com.management.server.util.CommonMethod;
import com.management.server.util.FileUtil;
import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    private ClazzRepository clazzRepository;
    @Autowired
    private InnovationRepository innovationRepository;
    @Autowired
    private FamilyRepository familyRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private DailyActivityRepository dailyActivityRepository;
    @Autowired
    private HonorRepository honorRepository;
    @PostMapping("/getStudent")
    public DataResponse getStudent()
    {
        String username = CommonMethod.getUsername();
        Student s = studentRepository.findByStudentId(username);

        Map student = BeanUtil.beanToMap(s) ;
        student.put("families",s.getFamilies());
        //student.put("courses",courseRepository.findCoursesByPersonId(map.get("personId")));
        student.put("clazzName", clazzRepository.findClazzByStudent(s)+"班");
        return new DataResponse(0,student,null);
    }
    @PostMapping("/getAllStudentsByTeacherCourses")
    public DataResponse getAllStudentsByTeacherCourses()
    {
        String t=teacherRepository.findByTeacherId(CommonMethod.getUsername()).getPersonId();
        List<Course> courseArrayList=courseRepository.findCoursesByPersonId(t);
        List<Student> studentList=new ArrayList<>();
        for(Course c:courseArrayList)
        {
            for(Person p:c.getPersons())
            {
                if(p instanceof Student)
                {
                    studentList.add((Student) p);
                }
            }
        }
        return new DataResponse(0,studentList,null);
    }
    @PostMapping("/getAllStudents")
    public DataResponse getAllStudents()
    {
        return new DataResponse(0,studentRepository.findAll(),null);
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
        Student student = studentRepository.findByPersonId(personId);
        if(student != null) {
            //System.out.println(m);
            BeanUtil.fillBeanWithMap(m, student, true, CopyOptions.create());
            studentRepository.save(student);
            return new DataResponse(0, null, "更新成功");
        } else {
            return new DataResponse(-1, null, "学号不存在，无法更新");
        }
    }
    @PostMapping("/saveStudentPersonalInfo")
    public DataResponse saveStudentPersonalInfo(@RequestBody Map m)
    {
        String studentId = CommonMethod.getUsername();
        Student student = studentRepository.findByStudentId(studentId);

        student.getFamilies().clear();

        List<Map> familiesList= (List<Map>) m.get("families");
        m.remove("families");

        BeanUtil.fillBeanWithMap(m,student,true, CopyOptions.create().ignoreError());

        List<Family> families = new ArrayList<>();
        for(Map familyMap:familiesList)
        {
            familyMap.remove("personId");
            Family family = BeanUtil.fillBeanWithMap(familyMap, new Family(), true, CopyOptions.create());
            families.add(family);
        }
        familyRepository.saveAll(families);
        student.getFamilies().addAll(families);

        studentRepository.save(student);
        return new DataResponse(0,null,"保存成功");
    }
    @PostMapping("/getStudentsByDailyActivity")
    public DataResponse getStudentsByDailyActivity(@RequestBody Map m)
    {
        DailyActivity dailyActivity = dailyActivityRepository.findByEventId((String) m.get("eventId"));
        List<Student> studentList = new ArrayList<>();
        for(Person p:dailyActivity.getPersons())
        {
            if(p instanceof Student)
            {
                studentList.add((Student) p);
            }
        }
        return new DataResponse(0,studentList,null);
    }
    @PostMapping("/getStudentsByInnovation")
    public DataResponse getStudentsByInnovation(@RequestBody Map m)
    {
        Innovation innovation = innovationRepository.findByEventId((String) m.get("eventId"));
        List<Student> studentList = new ArrayList<>();
        for(Person p:innovation.getPersons())
        {
            if(p instanceof Student)
            {
                studentList.add((Student) p);
            }
        }
        return new DataResponse(0,studentList,null);
    }
    @PostMapping("/getStudentsByHonor")
    public DataResponse getStudentsByHonor(@RequestBody Map m)
    {
        Honor honor = honorRepository.findByHonorId((String) m.get("honorId"));
        List<Student> studentList = new ArrayList<>();
        for(Person p:honor.getPersons())
        {
            if(p instanceof Student)
            {
                studentList.add((Student) p);
            }
        }
        return new DataResponse(0,studentList,null);
    }
    @PostMapping("/getStudentIntroduce")
    public ResponseEntity<byte[]> getStudentIntroduce()
    {
        String studentId=CommonMethod.getUsername();
        Map info=getMapFromStudentForIntroduce(studentId);
        String content = (String)info.get("introduce");
        content = addHeadInfo(content,"<style> html { font-family: \"SourceHanSansSC\", \"Open Sans\";}  </style> <meta charset='UTF-8' />  <title>Insert title here</title>");
        content = replaceNameValue(content,info);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .body(getPdfDataFromHtml(content));
    }
    public byte[] getPdfDataFromHtml(String htmlContent) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, "classpath:/static/resume.html");
            builder.useFastMode();
            //builder.withWmMargins(0, 0, 0, 0).useDefaultPageSize(PageOrientation.PORTRAIT, PageSize.A4);
            builder.useCacheStore(PdfRendererBuilder.CacheStore.PDF_FONT_METRICS, fSDefaultCacheStore);
            Resource resource = resourceLoader.getResource("classpath:/font/SourceHanSansSC-Regular.ttf");
            InputStream fontInput = resource.getInputStream();
            builder.useFont(new FSSupplier<InputStream>() {
                @Override
                public InputStream supply() {
                    return fontInput;
                }
            }, "SourceHanSansSC");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        }
        catch (Exception e) {
            return null;
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
        map.put("className", clazzRepository.findClazzByStudent(student)+"班");
        List<Innovation> list=innovationRepository.findByPersons(student);
        String filePath = "server/src/main/resources/static/resume.html";
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String imgStr= FileUtil.getPhotoImageStr(student.getPhoto());
        map.put("introduce",content);
        map.put("photo",imgStr);
        int cnt1=0;// 学科竞赛
        int cnt2=0;//科研成果
        int cnt3=0;//社会实践
        for(Innovation innovation:list){
            if(innovation.getType().equals("学科竞赛"))
            {
                cnt1++;
                map.put("competitionTime"+cnt1,innovation.getStartDate().toString());
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
                map.put("practiceTime"+cnt3,innovation.getStartDate().toString());
                map.put("practiceName"+cnt3,innovation.getName());
                map.put("practicePerformance"+cnt3,innovation.getPerformance());
            }
        }
        return map;
    }

}
