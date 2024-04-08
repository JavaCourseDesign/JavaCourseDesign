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
            student.setGender("男");
            student.setMajor("软件工程");
            students.add(student);
            studentRepository.save(student);
        }

        ArrayList<Teacher> teachers=new ArrayList<>();
        for(int i=0;i<15;i++){
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
            for (int j = 0; j < r.nextInt(3); j++) {
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
            for (int j = 0; j < r.nextInt(30); j++) {
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
        String[] firstNames = {"张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴", "郑", "胡", "洪", "崔", "段", "雷", "侯", "龙", "史", "陶"};
        String[] lastNames = {"伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "洋", "艳", "军", "杰", "勇", "良", "明", "超", "刚", "平", "辉", "毅"};

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

