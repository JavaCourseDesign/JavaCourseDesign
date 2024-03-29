package com.management.server.payload.response;

/**
 * JwtResponse JWT数据返回对象 包含客户登录的信息
 * String token token字符串
 * String type JWT 类型
 * Integer id 用户的ID user_id
 * String username 用户的登录名
 * String role 用户角色 ROLE_ADMIN, ROLE_STUDENT, ROLE_TEACHER
 */

public class JwtResponse {
    private String token;



    public JwtResponse(String accessToken) {
        this.token = accessToken;
    }
}