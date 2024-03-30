package com.management.server.controllers;

import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import com.management.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;


@RestController
public class AuthController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    UserTypeRepository userTypeRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;

    /*@Autowired
    AuthenticationManager authenticationManager;
     */
    @PostMapping("/login")
    public String login( @RequestBody Map<String,String> req){
       /* UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(req.get("username"), req.get("password"));
        authenticationManager.authenticate(authenticationToken);

        */
        System.out.println("check");
        if(passwordEncoder.matches(req.get("password"),userRepository.findByUsername(req.get("username")).getPassword()))
        {
            return JwtUtil.generateToken(req.get("username"));
        }
        return null;
    }

    @PostMapping("/register")
    public DataResponse registerUser(@RequestBody Map<String,String> map) {

        Student foundStudent=studentRepository.findByStudentId(map.get("id"));//studentId或teacherId！不要与personId混淆！
        if(foundStudent!=null&&foundStudent.getName().equals(map.get("username"))&&userRepository.findByUsername(foundStudent.getName())==null)
        {
            System.out.println("found user by foundStudent"+userRepository.findByUsername(foundStudent.getName()));

            String encodedPassword =passwordEncoder.encode(map.get("password"));
            User user=new User();
            user.setUsername(map.get("username"));
            user.setPassword(encodedPassword);
            UserType userType=new UserType();

            userType.setName(EUserType.ROLE_STUDENT);
            userTypeRepository.save(userType);
            user.setUserType(userType);
            userRepository.save(user);

            user.setPerson(foundStudent);

            return new DataResponse(0,null,"学生注册成功");
        }

        Teacher foundTeacher=teacherRepository.findByTeacherId(map.get("id"));
        if(foundTeacher!=null&&foundTeacher.getName().equals(map.get("username"))&&userRepository.findByUsername(foundTeacher.getName())==null)
        {
            String encodedPassword =passwordEncoder.encode(map.get("password"));
            User user=new User();
            user.setUsername(map.get("username"));
            user.setPassword(encodedPassword);
            UserType userType=new UserType();

            userType.setName(EUserType.ROLE_TEACHER);
            userTypeRepository.save(userType);
            user.setUserType(userType);
            userRepository.save(user);

            user.setPerson(foundTeacher);

            return new DataResponse(0,null,"教师注册成功");
        }

        return new DataResponse(-1,null,"注册失败");
    }

    @PostMapping("/test/addTestData")
    public DataResponse addTestData(){
        Student s1=new Student();
        s1.setStudentId("2019210000");
        s1.setName("tst");
        s1.setMajor("软件工程");
        s1.setClassName("软工1班");
        studentRepository.save(s1);

        Student s2=new Student();
        s2.setStudentId("2019210001");
        s2.setName("wzk");
        s2.setMajor("软件工程");
        s2.setClassName("软工2班");
        studentRepository.save(s2);

        Teacher t1=new Teacher();
        t1.setTeacherId("100000");
        t1.setName("why");
        teacherRepository.save(t1);
        return new DataResponse(0,null,"测试数据添加成功");
    }

   /* @PostMapping("/register")
    public DataResponse registerUser(@RequestBody Map<String,String> map) {
        String encodedPassword =passwordEncoder.encode(map.get("password"));
        User user=new User();
        user.setUsername(map.get("username"));
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return new DataResponse(1,null,"注册成功");
    }*/
    /*@PostMapping("/register/admin")
    public DataResponse registerUser(@RequestBody Map<String,String> map) {
        String encodedPassword =passwordEncoder.encode(map.get("password"));
        User user=new User();
        user.setUsername(map.get("username"));
        user.setPassword(encodedPassword);
        UserType userType=new UserType();
        userType.setName(EUserType.ROLE_ADMIN);
        userTypeRepository.save(userType);
        user.setUserType(userType);
        userRepository.save(user);
        return new DataResponse(1,null,"注册成功");
    }*/
  /* @PostMapping("/register/student")
   public DataResponse registerUser(@RequestBody Map<String,String> map) {
       String encodedPassword =passwordEncoder.encode(map.get("password"));
       User user=new User();
       user.setUsername(map.get("username"));
       user.setPassword(encodedPassword);
       UserType userType=new UserType();
       userType.setName(EUserType.ROLE_STUDENT);
       userTypeRepository.save(userType);
       user.setUserType(userType);
       userRepository.save(user);
       return new DataResponse(1,null,"注册成功");
   }*/
   /*@PostMapping("/register/teacher")
   public DataResponse registerUser(@RequestBody Map<String,String> map) {
       String encodedPassword =passwordEncoder.encode(map.get("password"));
       User user=new User();
       user.setUsername(map.get("username"));
       user.setPassword(encodedPassword);
       UserType userType=new UserType();
       userType.setName(EUserType.ROLE_TEACHER);
       userTypeRepository.save(userType);
       user.setUserType(userType);
       userRepository.save(user);
       return new DataResponse(1,null,"注册成功");
   }*/

}
