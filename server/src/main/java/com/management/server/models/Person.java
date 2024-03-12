package com.management.server.models;

import com.management.server.util.ComDataUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


/*@Entity
@Table(	name = "person",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "num"),   //人员表中的编号 唯一
        })*/
@Data
//@Inheritance(strategy = InheritanceType.JOINED)
@MappedSuperclass

public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer personId;

    //@NotBlank    // 字段非空
    @Size(max = 20)   //字段长度最长为20
    private String num;

    @Size(max = 50)
    private String name;

    @Size(max = 2)
    private String type;

    @Size(max = 50)
    private String dept;

    @Size(max = 20)
    private String card;
    @Size(max = 2)
    private String gender;

    private String birthday;

    @Size(max = 60)
    @Email
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 20)
    private String address;

    @Size(max = 1000)
    private String introduce;


    public Person() {
    }

    public String getGenderName() {
        return  ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender);
    }

    public void setGenderName(String genderName) {
    }

}
