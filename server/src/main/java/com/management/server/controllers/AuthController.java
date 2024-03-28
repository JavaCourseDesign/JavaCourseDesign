package com.management.server.controllers;

import cn.hutool.jwt.JWT;
import com.management.server.models.User;
import com.management.server.payload.response.DataResponse;
import com.management.server.payload.response.JwtResponse;
import com.management.server.repositories.UserRepository;
import com.management.server.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static org.springframework.security.config.Elements.JWT;


@RestController
public class AuthController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    /*@Autowired
    AuthenticationManager authenticationManager;*/
    @PostMapping("/login")
    public String login( @RequestBody Map<String,String> req){
      /*  UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(req.get("username"), req.get("password"));
        authenticationManager.authenticate(authenticationToken);*/
        System.out.println("check");
        if(passwordEncoder.matches(req.get("password"),userRepository.findByUsername(req.get("username")).getPassword()))
        {
            String token = JwtUtil.generateToken(req.get("username"));
            return token;
        }
        return null;
    }


    @PostMapping("/register")
    public DataResponse registerUser(@RequestBody Map<String,String> map) {
        String encodedPassword =passwordEncoder.encode(map.get("password"));
        User user=new User();
        user.setUsername(map.get("username"));
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return new DataResponse(1,null,"注册成功");
    }

}
