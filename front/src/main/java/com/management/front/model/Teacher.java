package com.management.front.model;

public class Teacher extends Person{
    private Integer teacherId;
    private String major;
    private String className;

    public Teacher(){
        super();
    }

    public Teacher(Integer teacherId,Integer personId,String num, String name){
        super(personId,num,name);
        this.teacherId = teacherId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
