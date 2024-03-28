package com.management.server.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 这里简化了用户信息的加载，实际应用中应该从数据库中加载用户信息
        // 返回一个硬编码的用户。实际开发时，应该查询数据库获取用户信息和权限
        return User.builder()
                .username("admin")
                .password("password") // 注意：密码应该是加密过的
                .roles("USER") // 这里指定用户的角色，根据实际情况调整
                .build();
    }
}

