package com.management.server.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

//should be the super class of course,activities,etc
@Entity
@Table(name="event")
@Data
//@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(exclude = {"persons","course"})
/*@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "eventId")//在递归中第二次出现时用name属性替代本对象避免无限递归
@JsonIgnoreProperties(value = {"persons"})*/
/*@NamedEntityGraph(name = "Event.persons",
        attributeNodes = @NamedAttributeNode("persons"))*/
public class Event{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String eventId;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String introduction;

    private String location;

    //private boolean checked;//用于判定是否已经通知到学生，如果未通知，应在通知栏显示 单独弄notice类？

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    protected Course course;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "person_event")
    @JsonIgnore
    private Set<Person> persons;

}
