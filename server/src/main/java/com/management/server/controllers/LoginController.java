package com.management.server.controllers;
import com.management.server.models.User;
import com.management.server.repositories.UserRepository;
import com.management.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        /*User newuser = new User();
        newuser.setUsername("admin1");
        newuser.setPassword("admin1");
        registerUser(newuser);*/
        User foundUser = userRepository.findByUsername(user.getUsername());
        if (foundUser != null && passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            String token = JwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok().body(token); // 发送Token给前端
        } else {
            System.out.println("Login failed, expect" + foundUser.getPassword() + " got " + user.getPassword());
            return ResponseEntity.badRequest().body("Login failed, expect" + foundUser.getPassword() + " got " + user.getPassword());
        }
    }

        /*User newuser = new User();
        newuser.setUsername("admin1");
        newuser.setPassword("admin1");
        registerUser(newuser);*/






    // 假设这是你处理注册的一个方法
    public void registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        System.out.println("Encoded password on register: " + encodedPassword);
    }

}
