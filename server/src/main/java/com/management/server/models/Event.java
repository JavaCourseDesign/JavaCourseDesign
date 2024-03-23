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
    private Integer eventId;

    private String name;

    private String beginTime;//2024-03-12 08:31

    private String endTime;//2024-03-12 08:31

    private String introduction;

    private String location;

    //private Integer personId;//to be deleted why????

    /*@ManyToMany(mappedBy = "events")
    private List<Person> persons=new ArrayList<>();*/

    /*@ManyToMany
    @JoinTable(name = "student_event")
    private List<Student> students;

    @ManyToMany
    @JoinTable(name = "teacher_event")
    private List<Teacher> teachers;*/

    @ManyToMany
    @JoinTable(name = "person_event")
    private List<Person> persons;



  /*  public void addStudent(Student student){
        students.add(student);
    }

    public void addTeacher(Teacher teacher){
        teachers.add(teacher);
    }*/
}
