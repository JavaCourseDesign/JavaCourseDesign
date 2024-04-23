package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.Set;

//should be the super class of course,activities,etc
@Entity
@Table(name="event")
@Data
//@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
/*@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "eventId")//在递归中第二次出现时用name属性替代本对象避免无限递归
@JsonIgnoreProperties(value = {"persons"})*/
/*@NamedEntityGraph(name = "Event.persons",
        attributeNodes = @NamedAttributeNode("persons"))*/
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String eventId;

    private String name;

    //private String time;//12,5,8.30,1.50     第十二周 周五 8:30 一小时五十分钟
    private String week;//12,13,14,15
    private String day;//1,2,3,4,5,6,7
    private String time;//8.30,10.20,14.00,15.50
    private String duration;//1.50

    private String introduction;

    private String location;

    private boolean checked;//用于判定是否已经通知到学生，如果未通知，应在通知栏显示

    //private boolean[] eventWeek; 不能这样设计，必须一节对一个对象，否则单节的请假情况和作业情况难以对应

    @ManyToMany
    @JoinTable(name = "person_event")
    @JsonIgnore
    private Set<Person> persons;
    //private Integer personId;//to be deleted why????

    /*@ManyToMany(mappedBy = "events")
    private List<Person> persons=new ArrayList<>();*/

    /*@ManyToMany
    @JoinTable(name = "student_event")
    private List<Student> students;

    @ManyToMany
    @JoinTable(name = "teacher_event")
    private List<Teacher> teachers;*/

    /*  public void addStudent(Student student){
        students.add(student);
    }

    public void addTeacher(Teacher teacher){
        teachers.add(teacher);
    }*/
}
