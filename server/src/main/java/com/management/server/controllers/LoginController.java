package com.management.server.controllers;
import com.management.server.models.User;
import com.management.server.payload.response.JwtResponse;
import com.management.server.repositories.UserRepository;
import com.management.server.repositories.UserTypeRepository;
import com.management.server.security.jwt.JwtUtils;
import com.management.server.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> user) {
        User newuser = new User();
        newuser.setUsername("admin1");
        newuser.setPassword("admin1");
        registerUser(newuser);
        User foundUser = userRepository.findByUsername(user.get("username"));
        if (foundUser != null && passwordEncoder.matches(user.get("password"), foundUser.getPassword())) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(foundUser.getUsername(), foundUser.getPassword()));
            String token = jwtUtils.generateJwtToken(authentication);

            System.out.println("Encoded password on login: " + foundUser.getPassword());

            //return ResponseEntity.ok().body(token); // 发送Token给前端
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new JwtResponse(token,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    roles.get(0))); // 发送Token给前端
        } else {
            System.out.println("Login failed, expect" + foundUser.getPassword() + " got " + user.get("password"));
            return ResponseEntity.badRequest().body("Login failed, expect" + foundUser.getPassword() + " got " + user.get("password"));
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
