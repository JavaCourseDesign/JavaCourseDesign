package com.management.server.controllers;

import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import com.management.server.util.CommonMethod;
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
        if(passwordEncoder.matches(req.get("password"),userRepository.findByUsername(req.get("username")).getPassword()))
        {
            return JwtUtil.generateToken(req.get("username"));
        }
        return null;
    }
    @PostMapping("/register")
    public DataResponse registerUser(@RequestBody Map<String,String> map) {
        String username = map.get("username");//把学号/工号作为用户名
        String name = map.get("name");//姓名用于验证是否匹配
        String password = passwordEncoder.encode(map.get("password"));
        Student foundStudent = studentRepository.findByStudentId(username);
        if (isUserValid(foundStudent, name, username)) {
            return registerUser(foundStudent,username, password, EUserType.ROLE_STUDENT);
        }

        Teacher foundTeacher = teacherRepository.findByTeacherId(username);
        if (isUserValid(foundTeacher, name, username)) {
            return registerUser(foundTeacher, username, password, EUserType.ROLE_TEACHER);
        }
        return new DataResponse(-1,null,"注册失败");
    }

    private boolean isUserValid(Person person, String name, String username) {
        return person != null && person.getName().equals(name) && userRepository.findByUsername(username)==null;
    }

    private DataResponse registerUser(Person person, String username, String password, EUserType userType) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        UserType type = new UserType();
        type.setName(userType);
        userTypeRepository.save(type);

        user.setUserType(type);
        user.setPerson(person);
        userRepository.save(user);
        return new DataResponse(0, null, userType == EUserType.ROLE_STUDENT ? "学生注册成功" : "教师注册成功");
    }
    @PostMapping("/findPersonIdByUsername")
    public DataResponse findPersonIdByUsername(@RequestBody Map<String,String> map) {
        User user = userRepository.findByUsername(map.get("username"));
        if (user == null||user.getPerson()==null) {
            return new DataResponse(-1, null, null);
        }
        return new DataResponse(0, user.getPerson().getPersonId(), null);
    }

    @PostMapping("/getRole")
    public DataResponse getRole() {
        return new DataResponse(0, null, CommonMethod.getUserType());
    }
    @PostMapping("/modifyPassword")
    public DataResponse modifyPassword(@RequestBody Map<String,String> map) {
        User user = userRepository.findByUsername(CommonMethod.getUsername());
        if (user == null) {
            return new DataResponse(-1, null, "用户不存在");
        }
        if(passwordEncoder.matches(map.get("oldPassword"),user.getPassword()))
        {
            user.setPassword(passwordEncoder.encode(map.get("newPassword")));
            userRepository.save(user);
            return new DataResponse(0, null, "修改成功");
        }else
        {
            return new DataResponse(-1, null, "原密码错误");
        }
    }
}
