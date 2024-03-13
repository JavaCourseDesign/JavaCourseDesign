# 建表失败解决
删除依赖
```xml
<dependency>
<groupId>jakarta.transaction</groupId>
<artifactId>jakarta.transaction-api</artifactId>
<version>1.3.3</version>
</dependency>
```
删除@springbootapplication(exclude)

# 多对多关系
```java
@ManyToMany(mappedBy = "students")
private List<Event> events;
```
```java
@ManyToMany
    @JoinTable(name = "student_event")
    private List<Student> students;
```
主控方问题：在Student类中，@ManyToMany注解的mappedBy属性指定了关系的被控方是Event类中的students属性，所以在数据库中，student_event表中的外键是student_id，而不是event_id。这样，当我们在Student类中添加了一个Event对象时，Hibernate会先将Student对象保存到student表中，然后再将student_event表中的外键student_id设置为student表中的id。这样，我们就可以通过Student对象来获取它参加的所有Event对象了。