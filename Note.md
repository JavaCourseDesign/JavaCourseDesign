@ -1,25 +0,0 @@
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

# 学生/教师-课程-科目
学生/教师与课程、科目分别是多对多关系，课程与科目是一对多关系，课程是事件的子类

# 多对多/多对一关系保存
先保存属性再保存本体

```fxml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.shape.*?>

<AnchorPane prefHeight="540" prefWidth="600" stylesheets="@week_time_table.css" xmlns="http://javafx.com/javafx/17.0.2-ea" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.management.front.controller.WeekTimeTableController">
   <VBox fx:id="background" prefHeight="540.0" prefWidth="100.0">
       <Rectangle fill="#373737" height="30.0" opacity="0.0" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="15.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="30.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
       <Rectangle fill="#373737" height="15.0" opacity="0.3" stroke="BLACK" strokeType="INSIDE" width="600.0" />
   </VBox>
   <HBox>
       <Rectangle fill="#101010" height="540.0" opacity="0.6" stroke="BLACK" strokeType="INSIDE" width="75.0" />
       <Rectangle fill="#101010" height="540.0" opacity="0.6" stroke="BLACK" strokeType="INSIDE" width="75.0" />
       <Rectangle fill="#101010" height="540.0" opacity="0.6" stroke="BLACK" strokeType="INSIDE" width="75.0" />
       <Rectangle fill="#101010" height="540.0" opacity="0.6" stroke="BLACK" strokeType="INSIDE" width="75.0" />
       <Rectangle fill="#101010" height="540.0" opacity="0.6" stroke="BLACK" strokeType="INSIDE" width="75.0" />
       <Rectangle fill="#101010" height="540.0" opacity="0.6" stroke="BLACK" strokeType="INSIDE" width="75.0" />
       <Rectangle fill="#101010" height="540.0" opacity="0.6" stroke="BLACK" strokeType="INSIDE" width="75.0" />
       <Rectangle fill="#101010" height="540.0" opacity="0.6" stroke="BLACK" strokeType="INSIDE" width="75.0" />
   </HBox>
    <BorderPane fx:id="table" prefHeight="540" prefWidth="600">
        <top>
            <HBox prefHeight="30.0" prefWidth="600">
                <Label alignment="CENTER" prefHeight="30.0" prefWidth="75" style="-fx-text-fill: white" text="时间/星期" /> <!-- 占位符 -->
                <Label alignment="CENTER" prefHeight="30.0" prefWidth="75" style="-fx-text-fill: white" text="星期一" />
                <Label alignment="CENTER" prefHeight="30.0" prefWidth="75" style="-fx-text-fill: white" text="星期二" />
                <Label alignment="CENTER" prefHeight="30.0" prefWidth="75" style="-fx-text-fill: white" text="星期三" />
                <Label alignment="CENTER" prefHeight="30.0" prefWidth="75" style="-fx-text-fill: white" text="星期四" />
                <Label alignment="CENTER" prefHeight="30.0" prefWidth="75" style="-fx-text-fill: white" text="星期五" />
                <Label alignment="CENTER" prefHeight="30.0" prefWidth="75" style="-fx-text-fill: white" text="星期六" />
                <Label alignment="CENTER" prefHeight="30.0" prefWidth="75" style="-fx-text-fill: white" text="星期日" />
            </HBox>
        </top>
        <left>
            <VBox alignment="TOP_RIGHT" prefWidth="75">
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="06:00" /><!-- 以HOUR_HEIGHT为基准调整 -->
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="07:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="08:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="09:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="10:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="11:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="12:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="13:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="14:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="15:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="16:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="17:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="18:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="19:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="20:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="21:00" />
                <Label alignment="CENTER" prefHeight="30" prefWidth="75" style="-fx-text-fill: white" text="22:00" />
                <!-- 添加更多时间标签 -->
            </VBox>
        </left>
        <center>
            <GridPane fx:id="scheduleGrid">
                <padding>
                    <Insets bottom="15" top="15" />
                </padding>
                <AnchorPane fx:id="day1" prefHeight="15" prefWidth="75.0" GridPane.columnIndex="0" />
                <AnchorPane fx:id="day2" prefHeight="15" prefWidth="75.0" GridPane.columnIndex="1" />
                <AnchorPane fx:id="day3" prefHeight="15" prefWidth="75.0" GridPane.columnIndex="2" />
                <AnchorPane fx:id="day4" prefHeight="15" prefWidth="75.0" GridPane.columnIndex="3" />
                <AnchorPane fx:id="day5" prefHeight="15" prefWidth="75.0" GridPane.columnIndex="4" />
                <AnchorPane fx:id="day6" prefHeight="15" prefWidth="75.0" GridPane.columnIndex="5" />
                <AnchorPane fx:id="day7" prefHeight="15" prefWidth="75.0" GridPane.columnIndex="6" />
            <columnConstraints>
               <ColumnConstraints />
               <ColumnConstraints />
               <ColumnConstraints />
               <ColumnConstraints />
               <ColumnConstraints />
               <ColumnConstraints />
               <ColumnConstraints />
            </columnConstraints>
            <rowConstraints>
               <RowConstraints />
            </rowConstraints>
            </GridPane>
        </center>
    </BorderPane>
</AnchorPane>
```