package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

//should be the super class of course,activities,etc
@Entity
@Table(name="event")
@Data
//@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String eventId;

    private String name;

    private String time;//5,8.30,1.50     周五 8:30 一小时五十分钟  lesson的此属性第一个数值应为-1，开始周与结束周属性在course 中

    private String introduction;

    private String location;

    private boolean checked;//用于判定是否已经通知到学生，如果未通知，应在通知栏显示

    private boolean[] eventWeek;

    @ManyToMany
    @JoinTable(name = "person_event")
    private List<Person> persons;
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
