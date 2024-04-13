package com.management.server.controllers;

import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.management.server.models.EUserType.ROLE_ADMIN;

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
    private AdministrativeClassRepository administrativeClassRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
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
        user.setPassword(passwordEncoder.encode("admin"));
        user.setUserType(userType);
        userTypeRepository.save(userType);
        userRepository.save(user);

        ArrayList<Student> students=new ArrayList<>();
        for(int i=0;i<100;i++){
            Student student=new Student();
            student.setStudentId("201921"+String.format("%03d",i));
            student.setName(generateRandomChineseName());
            if(i==0) student.setName("tst");
            if(i==1) student.setName("wzk");
            if(i==2) student.setName("why");
            if(i==3) student.setName("hzx");
            student.setGender("男");
            student.setMajor("软件工程");
            student.setDept("软件学院");
            student.setSocial("群众");
            student.setHighSchool("南京市第一中学");
            student.setFamilyMember("父亲");
            student.setFamilyMemberPhone("1234567890");
            student.setAddress("山东大学宿舍");
            student.setHomeTown("江苏南京");
            students.add(student);
            studentRepository.save(student);
        }

        ArrayList<Teacher> teachers=new ArrayList<>();
        for(int i=0;i<50;i++){
            Teacher teacher=new Teacher();
            teacher.setTeacherId("10000"+i);
            teacher.setName(generateRandomChineseName());
            teacher.setGender("男");
            teacher.setTitle("讲师");
            teachers.add(teacher);
            teacherRepository.save(teacher);
        }

        ArrayList<Course> courses=new ArrayList<>();
        for(int i=0;i<10;i++){
            Course course=new Course();
            course.setName(generateRandomCourseName());
            course.setCapacity(1.0*r.nextInt(100));
            course.setCredit(1.0*r.nextInt(6));
            List<Person> persons=new ArrayList<>();
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

        ArrayList<AdministrativeClass> administrativeClasses=new ArrayList<>();
        List<Student> assignedStudents = new ArrayList<>();
        for(int i=0;i<10;i++){
            AdministrativeClass administrativeClass=new AdministrativeClass();
            List<Student> studentsInClass=new ArrayList<>();
            while (studentsInClass.size()<30){
                Student student=students.get(r.nextInt(students.size()));
                if(!studentsInClass.contains(student) && !assignedStudents.contains(student)) {
                    studentsInClass.add(student);
                    assignedStudents.add(student);
                }
            }
            administrativeClass.setStudents(studentsInClass);
            administrativeClasses.add(administrativeClass);
            administrativeClassRepository.save(administrativeClass);
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
}

