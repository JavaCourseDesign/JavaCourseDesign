package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

//should be the super class of course,activities,etc
@Entity
@Table(name="event",uniqueConstraints = {})
@Data
//@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventId;

    private String name;

    private String beginTime;//2024-03-12 08:31

    private String endTime;//2024-03-12 08:31

    private String introduction;

    private String location;

    //private Integer personId;//to be deleted why????

    /*@ManyToMany(mappedBy = "events")
    private List<Person> persons=new ArrayList<>();*/

    @ManyToMany
    @JoinTable(name = "student_event")
    private List<Student> students;

    @ManyToMany
    @JoinTable(name = "teacher_event")
    private List<Teacher> teachers;



  /*  public void addStudent(Student student){
        students.add(student);
    }

    public void addTeacher(Teacher teacher){
        teachers.add(teacher);
    }*/
}
