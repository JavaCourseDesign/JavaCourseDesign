package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdcardUtil;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.management.server.util.NativePlaceUtil.getNativePlace;


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
    @Autowired
    private FeeRepository feeRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @PostMapping("/getStudent")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse getStudent()
    {
        String username = CommonMethod.getUsername();
        Student s = studentRepository.findByStudentId(username);

        Map student = BeanUtil.beanToMap(s) ;
        student.put("families",s.getFamilies());
        //student.put("courses",courseRepository.findCoursesByPersonId(map.get("personId")));
        student.put("clazzName", s.getClazz()==null?"未分配":s.getClazz().getName());
        return new DataResponse(0,student,null);
    }

    @PostMapping("/getStudentByPersonId")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentByPersonId(@RequestBody Map m)
    {
        String personId = (String) m.get("personId");
        Student s = studentRepository.findByPersonId(personId);
        Map student = BeanUtil.beanToMap(s) ;
        List<Family> families = s.getFamilies();
        if(families!=null)
        {
            student.put("families",families);
        }
        student.put("clazzName", s.getClazz().getName());
        student.put("honor",s.getHonors());
        //文件不存在时可能出现问题，可以把方法的返回字符串调成一个固定图片
        if(s.getPhoto()!=null&&!Objects.equals(s.getPhoto(), "请先上传文件！"))
        {
            student.put("photo", FileUtil.getPhotoImageStr(s.getPhoto()));
            System.out.println("文件合法"+s.getPhoto());
        }
        List<Score> scoreList=s.getScores();
        List<Map> scoreMapList = new ArrayList<>();
        for(Score score:scoreList)
        {
            Map scoreMap = BeanUtil.beanToMap(score);
            scoreMap.put("courseId",score.getCourse().getCourseId());
            scoreMap.put("courseName",score.getCourse().getName());
            scoreMapList.add(scoreMap);
        }
        student.put("scores",scoreMapList);
        double max=0;
        double min=100;
        double avg=0;

        //System.out.println(scoreList);
        int cnt = scoreList.size();
        double creditCnt = 0;
        double gpa = 0;
        for(Score score:scoreList)
        {
            double credit=score.getCourse().getCredit();
            if(score.getMark()!=null)
            {
                if(score.getMark()>max)
                {
                    max=score.getMark();
                }
                if(score.getMark()<min)
                {
                    min=score.getMark();
                }
                avg+=score.getMark();
                gpa+=(score.getMark()<60?0:1+(score.getMark()-60)/10)*credit;
            }
            creditCnt+=credit;
        }
        if(cnt==0||max<min)
        {
            max=0;
            min=0;
            avg=0;
            gpa=0;
        }
        else {
            avg /= cnt;
            gpa /= creditCnt;
            DecimalFormat df = new DecimalFormat("#.##");
            gpa= Double.parseDouble(df.format(gpa));
        }

        student.put("maxMark",max);
        student.put("minMark",min);
        student.put("avgMark",avg);
        student.put("gpa",gpa);
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
    @PostMapping("/getAllStudentsByCourse")
    public DataResponse getAllStudentsByCourse(@RequestBody Map m)
    {
        Course course=courseRepository.findByCourseId((String) m.get("courseId"));
        List<Student> studentList=new ArrayList<>();
        for(Person p:course.getPersons())
        {
            if(p instanceof Student)
            {
                studentList.add((Student) p);
            }
        }
        return new DataResponse(0,studentList,null);
    }
    @PostMapping("/getAllStudents")
    public DataResponse getAllStudents()
    {
        System.out.println("\nmarkStart\n");
        ArrayList<Map> studentList = new ArrayList<>();
        List<Student> students = studentRepository.findAll();
        System.out.println("\nmarkMid\n");
        for(Student s:students)
        {
            Map student = new HashMap();
            student.put("personId",s.getPersonId());
            student.put("name",s.getName());
            student.put("idCardNum",s.getIdCardNum());
            student.put("gender",s.getGender());
            student.put("birthday",s.getBirthday());
            student.put("age",s.getAge());
            student.put("studentId",s.getStudentId());
            student.put("dept",s.getDept());
            student.put("homeTown",s.getHomeTown());
            student.put("social",s.getSocial());
            student.put("major",s.getMajor());
            student.put("highSchool",s.getHighSchool());
            student.put("address",s.getAddress());
            student.put("familyMemberCount",s.getFamilies().size());
            int innovationCount=0;
            for( Event e:s.getEvents())
            {
                if(e instanceof Innovation)
                {
                    innovationCount++;
                }
            }
            student.put("innovationCount",innovationCount);
            student.put("honorCount",s.getHonors().size());
            student.put("courseCount",s.getCourses().size());
            double max=0;
            double min=100;
            double avg=0;
            List<Score> scoreList=s.getScores();
            //System.out.println(scoreList);
            int cnt = scoreList.size();
            double creditCnt = 0;
            double gpa = 0;
            for(Score score:scoreList)
            {
                double credit=score.getCourse().getCredit();
                if(score.getMark()!=null)
                {
                    if(score.getMark()>max)
                    {
                        max=score.getMark();
                    }
                    if(score.getMark()<min)
                    {
                        min=score.getMark();
                    }
                    avg+=score.getMark();
                    gpa+=(score.getMark()<60?0:1+(score.getMark()-60)/10)*credit;
                }
                creditCnt+=credit;
            }
            if(cnt==0||max<min)
            {
                max=0;
                min=0;
                avg=0;
                gpa=0;
            }
            else {
                avg /= cnt;
                gpa /= creditCnt;
                DecimalFormat df = new DecimalFormat("#.##");
                gpa= Double.parseDouble(df.format(gpa));
            }

            student.put("maxMark",max);
            student.put("minMark",min);
            student.put("avgMark",avg);
            student.put("gpa",gpa);
            studentList.add(student);
        }
        //System.out.println(studentList);
        System.out.println("\nmarkEnd\n");
        return new DataResponse(0,studentList,null);
    }

    @PostMapping("/addStudent")
    public DataResponse addStudent(@RequestBody Map m)
    {
        String studentId = (String) m.get("studentId");
        if(studentRepository.existsByStudentId(studentId)) {
            return new DataResponse(-1,null,"学号已存在，无法添加");
        }
        Student student = BeanUtil.toBean(m, Student.class, CopyOptions.create());//要求map键值与对象一致
        boolean flag = completeStudentById(student);
        student.setClazz(clazzRepository.findByName(""+m.get("clazzName")));
        if(!flag) return new DataResponse(-1,null,"身份证号不合法，无法添加");
        //应已包含 姓名、身份证号、学号、部门、政治面貌、专业、毕业高中
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
            BeanUtil.fillBeanWithMap(m, student, CopyOptions.create().ignoreError());
            if(student.getStudentId().length()!=12)
            {
                return new DataResponse(-1,null,"学号不合法，无法更新");
            }
            boolean flag = completeStudentById(student);
            student.setClazz(clazzRepository.findByName(""+m.get("clazzName")));
            if(!flag) return new DataResponse(-1,null,"身份证号不合法，无法更新");
            studentRepository.save(student);
            return new DataResponse(0, null, "更新成功");
        } else {
            return addStudent(m);
            //return new DataResponse(-1, null, "学生不存在，无法更新");
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
    @PostMapping("/getStudentsByEvent")
    public DataResponse getStudentsByEvent(@RequestBody Map m)
    {
        Event event=eventRepository.findEventByEventId((String) m.get("eventId"));
        List<Student> studentList = new ArrayList<>();
        for(Person p:event.getPersons())
        {
            if(p instanceof Student)
            {
                studentList.add((Student) p);
            }
        }
        return new DataResponse(0,studentList,null);
    }
    @PostMapping("/getStudentPortraitData")
    public DataResponse getStudentPortraitData()
    {
        Map m=getMapFromStudentForIntroduce(CommonMethod.getUsername());
        m.remove("photo");
        m.remove("introduce");
        List<Fee> feeList=feeRepository.findByPerson(studentRepository.findByStudentId(CommonMethod.getUsername()));
        m.put("feeList",feeList);
        List<Score> scoreList=scoreRepository.findByStudentStudentId(CommonMethod.getUsername());
        List<Map> scoreMapList=new ArrayList<>();
        for(Score s:scoreList)
        {
            Map scoreMap=new HashMap();
            scoreMap.put("courseName",s.getCourse().getName());
            scoreMap.put("mark",s.getMark());
            scoreMapList.add(scoreMap);
        }
        m.put("scoreList",scoreMapList);
        m.put("markList",getStudentMarkList(scoreList));
        m.put("gpa",gpa(CommonMethod.getUsername()));
        return new DataResponse(0,m,null);
    }
    public String gpa(String num) {
        double credit = 0;
        double mark = 0;
        double gpa = 0;
        List<Score> scoreList = scoreRepository.findByStudentStudentId(num);
        for (Score item : scoreList) {
            Double c = (Double) item.getCourse().getCredit();
            Double m = item.getMark();
            if(m!=null)
            {
                if ((m*1.0/ 10 - 5) > 0) {
                    mark += (m*1.0 / 10 - 5) * c;
                }
                credit += c;
            }
        }
        gpa = mark / credit;
        return String.valueOf(gpa);
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
        map.put("className", student.getClazz().getName());
        List<Innovation> list=innovationRepository.findByPersons(student);
        List<Honor> honorList=student.getHonors();
        String filePath = "src/main/resources/static/resume.html";
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String imgStr= "data:image/jpeg;base64,"+FileUtil.getPhotoImageStr(student.getPhoto());
        map.put("introduce",content);
        map.put("photo",imgStr);
        int cnt1=0;// 学科竞赛
        int cnt2=0;//科研成果
        int cnt3=0;//社会实践
        int cnt4=0;//创新项目
        for(Honor h:honorList){
            if(h.getEvent() instanceof Innovation)
            {
                Innovation innovation=(Innovation) h.getEvent();
                if(innovation.getType().equals("学科竞赛"))
                {
                    cnt1++;
                    map.put("competitionTime"+cnt1,innovation.getStartDate().toString());
                    map.put("competitionName"+cnt1,innovation.getName());
                    map.put("competitionPerformance"+cnt1,h.getName());
                }
                else if(innovation.getType().equals("科研成果"))
                {
                    cnt2++;
                    map.put("paperName"+cnt2,innovation.getName());
                    map.put("paperLocation"+cnt2,innovation.getLocation());
                    map.put("paperPerformance"+cnt2,h.getName());
                }
                else if(innovation.getType().equals("社会实践"))
                {
                    cnt3++;
                    map.put("practiceTime"+cnt3,innovation.getStartDate().toString());
                    map.put("practiceName"+cnt3,innovation.getName());
                    map.put("practicePerformance"+cnt3,h.getName());
                }
                else if(innovation.getType().equals("创新项目"))
                {
                    cnt4++;
                    map.put("projectTime"+cnt4,innovation.getStartDate().toString());
                    map.put("projectName"+cnt4,innovation.getName());
                    map.put("projectPerformance"+cnt4,h.getName());
                }

            }

        }
        return map;
    }
    private boolean completeStudentById(Student s)
    {
        String id = s.getIdCardNum();
        if(!IdcardUtil.isValidCard(id)) return false;
        s.setHomeTown(getNativePlace(Integer.parseInt(IdcardUtil.getDistrictCodeByIdCard(id))));
        s.setGender(IdcardUtil.getGenderByIdCard(id) == 1 ? "男" : "女");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        s.setBirthday(IdcardUtil.getBirthDate(id).toLocalDateTime().format(formatter));
        return true;
    }
    public List getStudentMarkList(List<Score> sList){
        String title[]={"优","良","中","及格","不及格"};
        int count[]= new int[5];
        List list = new ArrayList();
        if(sList == null || sList.size() == 0)
            return list;
        Map m;
        Course c;
        for(Score s:sList){
            c = s.getCourse();
            if(s.getMark() >= 90)
                count[0]++;
            if(s.getMark() >= 80)
                count[1]++;
            if(s.getMark() >= 70)
                count[2]++;
            if(s.getMark() >= 60)
                count[3]++;
            else
                count[4]++;
        }
        for(int i = 0; i < 5;i++) {
            m = new HashMap();
            m.put("title", title[i]);
            m.put("value", count[i]);
            list.add(m);
        }
        return list;
    }

}
