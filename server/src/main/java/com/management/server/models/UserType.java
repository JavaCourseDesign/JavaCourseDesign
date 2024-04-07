package com.management.server.models;


import jakarta.persistence.*;
import lombok.Data;

/**
 * UserType用户类型表实体类 三种类型 管理员，学生和教师 对应 枚举类型EUserType
 * Integer id user_type 表 主键 id
 * EUserType nam 角色名称 ROLE_ADMIN, ROLE_STUDENT,ROLE_TEACHER
 */
@Entity
@Data
@Table(name = "user_type")

public class UserType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EUserType name;

    public UserType() {
    }
}