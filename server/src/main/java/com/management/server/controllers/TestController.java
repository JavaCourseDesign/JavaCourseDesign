package com.management.server.controllers;

import cn.hutool.core.util.IdcardUtil;
import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.management.server.models.EUserType.ROLE_ADMIN;
import static com.management.server.util.NativePlaceUtil.getNativePlace;

@RestController
public class TestController {//专门用于添加测试数据
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ClazzRepository clazzRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FamilyRepository familyRepository;
    @PostMapping("/test/addTestData")
    public DataResponse addTestData(){
        if(studentRepository.count()!=0||teacherRepository.count()!=0||courseRepository.count()!=0){
            return new DataResponse(1,null,"测试数据已存在");
        }

        Random r=new Random();

        User user=new User();
        UserType userType=new UserType();
        userType.setName(ROLE_ADMIN);
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUserType(userType);
        userTypeRepository.save(userType);
        userRepository.save(user);

        Family f=new Family();
        f.setPhone("12345678901");
        f.setRelationship("父亲");
        f.setName("谭斌");
        f.setGender("男");
        familyRepository.save(f);

        ArrayList<Student> students=new ArrayList<>();
        for(int i=0;i<200;i++){
            Student student=new Student();
            student.setStudentId("202300300"+String.format("%03d",i));
            student.setName(generateRandomChineseName());
            if(i==0) {student.setName("谭绍庭");student.setFamilies(Collections.singletonList(f));}
            if(i==1)  student.setName("王志凯");
            if(i==2)  student.setName("张小三");
            if(i==3)  student.setName("李小四");
            student.setGender("男");
            student.setMajor("软件工程");
            student.setDept("软件学院");
            student.setSocial("群众");
            student.setHighSchool("南京市第一中学");
            student.setAddress("山东大学宿舍");
            student.setIdCardNum(generateIdCardNum());
            completeStudentById(student);
            students.add(student);
            studentRepository.save(student);
        }

        ArrayList<Teacher> teachers=new ArrayList<>();
        for(int i=0;i<50;i++){
            Teacher teacher=new Teacher();
            teacher.setTeacherId("199900100"+String.format("%03d",i));
            teacher.setName(generateRandomChineseName());
            if(i==0) teacher.setName("向辉");
            if(i==1) teacher.setName("李学庆");
            teacher.setGender("男");
            teacher.setTitle("教授");
            teachers.add(teacher);
            teacherRepository.save(teacher);
        }

        ArrayList<Course> courses=new ArrayList<>();
        for(int i=0;i<10;i++){
            Course course=new Course();
            course.setName(generateRandomCourseName());
            course.setCapacity(1.0*r.nextInt(100));
            course.setCredit(1.0*r.nextInt(6));
            Set<Person> persons=new HashSet<>();
            if(i==0) {
                persons.add(students.get(0));
                persons.add(teachers.get(0));
            }
            for (int j = 0; j < r.nextInt(30); j++) {
                persons.add(students.get(r.nextInt(students.size())));
            }
            for (int j = 0; j < r.nextInt(3)+1; j++) {
                persons.add(teachers.get(r.nextInt(teachers.size())));
            }
            course.setPersons(persons);
            courses.add(course);
            courseRepository.save(course);
        }

        ArrayList<Course> coursesWithoutStudents=new ArrayList<>();
        for(int i=0;i<3;i++){
            Course course=new Course();
            course.setName(generateRandomCourseName());
            course.setCapacity(1.0*r.nextInt(5)+1);
            course.setCredit(1.0*r.nextInt(6));
            Set<Person> persons=new HashSet<>();
            for (int j = 0; j < r.nextInt(3)+1; j++) {
                persons.add(teachers.get(r.nextInt(teachers.size())));
            }
            course.setPersons(persons);
            coursesWithoutStudents.add(course);
            courseRepository.save(course);
        }

        ArrayList<Clazz> clazzes =new ArrayList<>();
        List<Student> assignedStudents = new ArrayList<>();
        for(int i=0;i<5;i++){
            Clazz clazz =new Clazz();
            clazz.setMajor("软件工程");
            clazz.setGrade("2019");
            clazz.setClazzNumber(i+1+"");
            clazz.setName(clazz.getMajor()+clazz.getGrade()+"级"+clazz.getClazzNumber()+"班");
            List<Student> studentsInClass=new ArrayList<>();
            while (studentsInClass.size()<30){
                Student student=students.get(r.nextInt(students.size()));
                if(!studentsInClass.contains(student) && !assignedStudents.contains(student)) {
                    studentsInClass.add(student);
                    student.setClazz(clazz);
                }
            }
            //clazz.setStudents(studentsInClass);
            clazzes.add(clazz);
            clazzRepository.save(clazz);
        }

        return new DataResponse(0,null,"测试数据添加成功");
    }

    public String generateRandomChineseName() {
        String[] firstNames = {"张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴", "郑", "胡", "洪", "崔", "段", "雷", "侯", "龙", "史", "陶", "孙", "马", "朱", "秦", "何", "吕", "施", "孔", "曹", "严", "华", "金", "魏", "卫", "蒋", "沈", "韩", "杨", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄", "和", "穆", "萧", "尹"};
        String[] lastNames = {"伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "洋", "艳", "军", "杰", "勇", "良", "明", "超", "刚", "平", "辉", "毅", "明明", "强强", "芳芳", "伟伟", "天天", "小明", "小芳", "立志", "志强", "国强", "小刚", "小华", "立华", "小琴", "美琳", "丽丽", "婷婷", "晓晓", "佳佳", "欢欢", "宝宝", "悦悦", "琳琳", "瑶瑶", "莉莉", "天翔", "宇航", "梦琪", "雨婷", "嘉欣", "欣怡", "子涵", "梓萱", "雅静", "怡然", "晨曦", "雨泽", "星辰", "浩宇", "嘉慧", "瑾瑜", "宇轩", "思远", "浩然", "子轩", "雅涵", "佳怡", "思淼", "梓睿", "欣妍", "泽洋", "梦洁", "倩倩", "智宇", "欣然", "诗涵", "婧琪", "茹雪", "俊杰", "雨辰", "宇辰", "雅萱", "思琪", "欣宜", "瑾萱", "佳宁", "若瑶", "雨珍", "思宇", "嘉艺", "梓涵", "天佑", "宇鹏", "俊逸", "雨嘉", "梦瑶", "韵寒", "凯瑞", "宇杰", "思颖", "雅婷", "梦涵", "雪怡", "佳美", "宇航", "梦茹", "雨婷", "婷婷", "蕊蕊", "璐璐", "欣欣", "莹莹", "妍妍", "萱萱", "琪琪", "佩佩", "思思", "淼淼", "瑶瑶", "雅雅", "菲菲", "凡凡", "楠楠", "悠悠"};

        Random random = new Random();

        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];

        return firstName + lastName;
    }

    public String generateRandomCourseName() {
        String[] firstNames = {"软件工程", "计算机网络", "数据库", "数据结构", "操作系统", "编译原理", "计算机组成原理", "计算机图形学", "人工智能", "机器学习"};
        String[] lastNames = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

        Random random = new Random();

        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];

        return firstName + lastName;
    }

    public String generateIdCardNum() {
        StringBuilder generater = new StringBuilder();
        generater.append("110101");
        generater.append(generateBirthday());
        generater.append(this.randomCode());
        generater.append(this.calcTrailingNumber(generater.toString().toCharArray()));
        return generater.toString();
    }
    public String generateBirthday() {
        long minDay = LocalDate.of(2002, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2008, 12, 31).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return randomBirthDate.format(formatter);
    }
    public char calcTrailingNumber(char[] chars) {
        if (chars.length < 17) {
            return ' ';
        }
        int[] c = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
        char[] r = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
        int[] n = new int[17];
        int result = 0;
        for (int i = 0; i < n.length; i++) {
            n[i] = Integer.parseInt(chars[i] + "");
        }
        for (int i = 0; i < n.length; i++) {
            result += c[i] * n[i];
        }
        return r[result % 11];
    }
    public String randomCode() {
        int code = (int) (Math.random() * 1000);
        if (code < 10) {
            return "00" + code;
        } else if (code < 100) {
            return "0" + code;
        } else {
            return "" + code;
        }
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
}

