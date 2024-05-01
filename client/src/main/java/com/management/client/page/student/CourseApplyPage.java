package com.management.client.page.student;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.page.WeekPage;
import com.management.client.customComponents.SearchableTableView;
import com.management.client.page.LoginPage;
import com.management.client.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.management.client.util.HttpClientUtil.request;

public class CourseApplyPage extends SplitPane {
    private SearchableTableView courseTable;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private WeekPage weekTimeTable = new WeekPage();
    private Label creditCount = new Label();
    private Label requiredCreditCount = new Label();
    private Label optionalCreditCount = new Label();
    private Label selectiveCreditCount = new Label();
    private ToggleButton filterConflict = new ToggleButton();
    private ToggleButton filterAvailable = new ToggleButton();
    private ToggleButton filterChosen = new ToggleButton();
    private Calendar chosenCalendar = new Calendar("已中课程");
    private Calendar selectedCalendar = new Calendar("已选课程");
    private Calendar preViewCalendar = new Calendar("预览课程");
    private CalendarSource calendarSource = new CalendarSource("My Calendar Source");
    public CourseApplyPage() {
        this.setWidth(1000);
        this.setDividerPosition(0, 0.7);
        this.setOrientation(Orientation.VERTICAL);
        initializeWeekTimeTable();
        initializeTable();
        displayCourses();
    }
    private void initializeWeekTimeTable() {
        Label time=new Label(LocalDate.now().toString());
        creditCount.setFont(javafx.scene.text.Font.font(30));
        VBox infoBox = new VBox(time,creditCount,requiredCreditCount,optionalCreditCount,selectiveCreditCount);
        infoBox.setAlignment(Pos.CENTER);

        chosenCalendar.setStyle(Calendar.Style.STYLE1);
        selectedCalendar.setStyle(Calendar.Style.STYLE2);
        preViewCalendar.setStyle(Calendar.Style.STYLE3);
        calendarSource.getCalendars().addAll(chosenCalendar,selectedCalendar,preViewCalendar);
        weekTimeTable.getCalendarSources().add(calendarSource);

        chosenCalendar.setReadOnly(true);
        selectedCalendar.setReadOnly(true);
        preViewCalendar.setReadOnly(true);

        setEvents(chosenCalendar, (List<Map>) request("/getStudentEvents",null).getData());

        SplitPane splitPane = new SplitPane(weekTimeTable, infoBox);
        this.getItems().add(splitPane);
    }

    private void initializeTable() {
        TableColumn<Map, String> courseIdColumn = new TableColumn<>("课程号");
        TableColumn<Map, String> courseNameColumn = new TableColumn<>("课程名");
        TableColumn<Map, String> courseCreditColumn = new TableColumn<>("学分");
        TableColumn<Map, String> courseTeacherColumn = new TableColumn<>("教师");
        TableColumn<Map, String> courseAvailableColumn = new TableColumn<>("可选");
        TableColumn<Map, String> courseChosenColumn = new TableColumn<>("已选");
        TableColumn<Map, String> courseChosenStateColumn = new TableColumn<>("抽签状态");
        TableColumn<Map, Void> courseApplyColumn = new TableColumn<>("选课");

        courseIdColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        courseCreditColumn.setCellValueFactory(new MapValueFactory<>("credit"));

        courseTeacherColumn.setCellValueFactory(data -> {
            List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("persons");
            String personNames = persons.stream()
                    .filter(person -> person.containsKey("teacherId"))
                    .map(person -> (String) person.get("name"))
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(personNames);
        });

        courseAvailableColumn.setCellValueFactory(data -> {
            boolean available = (boolean) data.getValue().get("available");
            return new SimpleStringProperty(available ? "是" : "否");
        });

        courseChosenColumn.setCellValueFactory(data -> {
            //boolean chosen = (boolean) data.getValue().get("chosen");
            List<Map> willingStudents = (List<Map>) data.getValue().get("willingStudents");
            boolean chosen = willingStudents.stream().anyMatch(student -> student.get("studentId").equals(LoginPage.username));
            return new SimpleStringProperty(chosen ? "是" : "否");
        });

        courseChosenStateColumn.setCellValueFactory(data -> {
            Boolean chosen = (Boolean) data.getValue().get("chosen");
            if(chosen==null) return new SimpleStringProperty("未抽签");
            return new SimpleStringProperty(chosen ? "已抽中" : "未抽中");
        });

        courseApplyColumn.setCellFactory(param -> {
            final Button applyButton = new Button("选课");
            TableCell<Map, Void> cell = new TableCell<>() {
                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        applyButton.setOnAction(event -> applyCourse(getTableView().getItems().get(getIndex())));
                        //if(!(boolean) getTableView().getItems().get(getIndex()).get("available")) applyButton.setDisable(true);
                        setGraphic(applyButton);
                    }
                }
            };
            return cell;
        });

        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.addAll(List.of(courseIdColumn, courseNameColumn, courseCreditColumn, courseTeacherColumn, courseAvailableColumn, courseChosenColumn,courseChosenStateColumn,courseApplyColumn));
        courseTable = new SearchableTableView(observableList, List.of("courseId", "name"), columns);

        HBox filterPanel = new HBox();
        filterPanel.setSpacing(10);

        filterConflict.setText("过滤冲突");
        filterConflict.setOnAction(event -> displayCourses());

        filterAvailable.setText("过滤可选");
        filterAvailable.setOnAction(event -> displayCourses());

        filterChosen.setText("过滤已选");
        filterChosen.setOnAction(event -> displayCourses());

        filterPanel.getChildren().addAll(filterConflict, filterAvailable, filterChosen);
        courseTable.setFilterPanel(filterPanel);

        courseTable.setOnItemClick(course -> {setEvents(preViewCalendar,(List<Map>) request("/getLessonsByCourse",course).getData());});



        this.getItems().add(courseTable);
    }

    private void displayCourses() {
        List<Map> courses = (ArrayList) request("/getWantedCourses", null).getData();
        //System.out.println("wantedCourses"+courses);

        List<Map> allEvents = new ArrayList<>();
        double credit = 0;
        double requiredCredit = 0;
        double optionalCredit = 0;
        double selectiveCredit = 0;
        for (Map course : courses) {
            credit += course.get("credit") == null ? 0 : (double) course.get("credit");
            if(course.get("type")!=null&&course.get("type").equals("0")) requiredCredit += course.get("credit") == null ? 0 : (double) course.get("credit");
            if(course.get("type")!=null&&course.get("type").equals("1")) optionalCredit += course.get("credit") == null ? 0 : (double) course.get("credit");
            if(course.get("type")!=null&&course.get("type").equals("2")) selectiveCredit += course.get("credit") == null ? 0 : (double) course.get("credit");
            List<Map> events = (List<Map>) request("/getLessonsByCourse", Map.of("courseId", course.get("courseId"))).getData();
            //if(events==null) continue;
            allEvents.addAll(events);
        }
        creditCount.setText("已选学分：" + credit);
        requiredCreditCount.setText("必修学分：" + requiredCredit);
        optionalCreditCount.setText("选修学分：" + optionalCredit);
        selectiveCreditCount.setText("选修学分：" + selectiveCredit);


        //weekTimeTable.setEvents((List)allEvents);
        setEvents(selectedCalendar,allEvents);

        Map filter = new HashMap();
        filter.put("filterConflict", filterConflict.isSelected());
        filter.put("filterAvailable", filterAvailable.isSelected());
        filter.put("filterChosen", filterChosen.isSelected());

        filter.put("getChosenState", "true");

        List<Map> courseList = (ArrayList) request("/getAllCoursesForStudent", filter).getData();
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList(courseList));
        courseTable.setData(observableList);
    }

    private void applyCourse(Map selectedCourse) {
        //Map selectedCourse = courseTable.getSelectedItem();
        if (selectedCourse == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法申请");
            alert.showAndWait();
            return;
        }

        DataResponse response = request("/applyCourse", Map.of("courseId", (String) selectedCourse.get("courseId")));

        if (response.getCode() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("申请成功");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("申请失败: " + response.getMsg());
            alert.showAndWait();
        }
        displayCourses();
    }

    public List<Map> getEvents(Calendar calendar) {
        List<Map> events = new ArrayList<>();
        for (Entry<?> entry : (List<Entry>)calendar.findEntries("")) {
            events.add(convertEntryToMap(entry));
        }
        return events;
    }

    public void setEvents(Calendar calendar, List<Map> events) {
        calendar.clear();
        calendar.addEntries(events.stream().map(this::convertMapToEntry).collect(Collectors.toList()));
    }

    public Entry<String> convertMapToEntry(Map<String, Object> map) {
        Entry<String> entry = new Entry<>((String) map.get("name"));
        entry.setId((String) map.get("eventId"));
        entry.setInterval(
                LocalDate.parse(map.get("startDate")+""),
                LocalTime.parse(map.get("startTime")+"") ,
                LocalDate.parse(map.get("endDate")+""),
                LocalTime.parse(map.get("endTime")+"")
        );
        entry.setLocation((String) map.get("location"));

        return entry;
    }

    public Map<String, Object> convertEntryToMap(Entry entry) {
        String name = entry.getTitle() != null ? entry.getTitle() : "";
        LocalDate startDate = entry.getStartDate() != null ? entry.getStartDate() : LocalDate.now();
        LocalDate endDate = entry.getEndDate() != null ? entry.getEndDate() : LocalDate.now();
        LocalTime startTime = entry.getStartTime() != null ? entry.getStartTime() : LocalTime.now();
        LocalTime endTime = entry.getEndTime() != null ? entry.getEndTime() : LocalTime.now();
        String location = entry.getLocation() != null ? entry.getLocation() : "";

        return Map.of(
                "name", name,
                "eventId", entry.getId(),
                "startDate", startDate.toString(),
                "endDate", endDate.toString(),
                "startTime", startTime.toString(),
                "endTime", endTime.toString(),
                "location", location
        );
    }
}