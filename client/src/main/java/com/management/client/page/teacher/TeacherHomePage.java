package com.management.client.page.teacher;


import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DayEntryView;
import com.calendarfx.view.DayView;
import com.calendarfx.view.DayViewBase;
import com.calendarfx.view.popover.EntryPopOverContentPane;
import com.management.client.ClientApplication;
import com.management.client.request.DataResponse;
import impl.com.calendarfx.view.DayEntryViewSkin;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static com.management.client.util.HttpClientUtil.request;
import static com.management.client.util.HttpClientUtil.uploadFile;

public class TeacherHomePage extends SplitPane {
    @FXML
    private SplitPane homePage;
    @FXML
    private CalendarView calendarView;


    @FXML
    private TextField eventNameField;
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField endTimeField;
    @FXML
    private TextField locationField;
    @FXML
    private TextArea noteArea;

    private final CalendarSource calendarSource = new CalendarSource();
    public TeacherHomePage() {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/teacherFxml/TeacherHomePage.fxml")); // 确保路径正确
        fxmlLoader.setController(this);
        try {
            homePage=fxmlLoader.load();
            this.getItems().add(homePage);
            //homePage.setDividerPosition(0,0.6);
            //this.getStylesheets().add("dark-theme.css");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initializeCalendarView();
        //initializeToDoTable();
    }

    private void initializeCalendarView()
    {
        calendarView.setShowAddCalendarButton(false);
        calendarView.setPrefSize(1000,800);
        calendarView.getDayPage().getDetailedDayView().setEarlyLateHoursStrategy(DayViewBase.EarlyLateHoursStrategy.SHOW_COMPRESSED);
        calendarView.getDayPage().getDetailedDayView().setHoursLayoutStrategy(DayViewBase.HoursLayoutStrategy.FIXED_HOUR_COUNT);
        calendarView.getDayPage().getDetailedDayView().setVisibleHours(18);
        calendarView.getWeekPage().getDetailedWeekView().setEarlyLateHoursStrategy(DayViewBase.EarlyLateHoursStrategy.SHOW_COMPRESSED);
        calendarView.getWeekPage().getDetailedWeekView().setHoursLayoutStrategy(DayViewBase.HoursLayoutStrategy.FIXED_HOUR_COUNT);
        calendarView.getWeekPage().getDetailedWeekView().setVisibleHours(18);

        //System.out.println(calendarView.getDayPage().getDetailedDayView().getDayView().getEntryViewFactory());
        calendarView.getDayPage().getDetailedDayView().getDayView().setEntryViewFactory(entry -> {
            DayEntryView entryView = new DayEntryView(entry);
            entryView.addNode(Pos.TOP_RIGHT,new Label("地点："+entry.getLocation()));
            entryView.setOnMouseClicked(event -> {
                boolean readOnly = entryView.getEntry().getCalendar().isReadOnly();
                eventNameField.setDisable(readOnly);
                startTimeField.setDisable(readOnly);
                endTimeField.setDisable(readOnly);
                locationField.setDisable(readOnly);
                noteArea.setDisable(readOnly);

                eventNameField.setText(entryView.getEntry().getTitle());
                startTimeField.setText(entryView.getEntry().getStartTime().toString());
                endTimeField.setText(entryView.getEntry().getEndTime().toString());
                locationField.setText(entryView.getEntry().getLocation());
                noteArea.setText(((Map)(entryView.getEntry().getUserObject())).get("introduction")==null?"":((Map)(entryView.getEntry().getUserObject())).get("introduction").toString());
            });
            return entryView;
        });

        calendarView.getWeekPage().getSelections().addListener((SetChangeListener<? super Entry<?>>) change -> {
            if (calendarView.getWeekPage().getSelections().size() == 1) {
                List<Entry<?>> selectedEntries = new ArrayList<>(calendarView.getWeekPage().getSelections());
                Entry<?> entry = selectedEntries.get(0);

                boolean readOnly = entry.getCalendar().isReadOnly();
                eventNameField.setDisable(readOnly);
                startTimeField.setDisable(readOnly);
                endTimeField.setDisable(readOnly);
                locationField.setDisable(readOnly);
                noteArea.setDisable(readOnly);

                eventNameField.setText(entry.getTitle());
                startTimeField.setText(entry.getStartTime().toString());
                endTimeField.setText(entry.getEndTime().toString());
                locationField.setText(entry.getLocation());
                noteArea.setText(((Map)(entry.getUserObject())).get("introduction")==null?"":((Map)(entry.getUserObject())).get("introduction").toString());
            }
        });

        calendarView.getMonthPage().getSelections().addListener((SetChangeListener<? super Entry<?>>) change -> {
            if (calendarView.getMonthPage().getSelections().size() == 1) {
                List<Entry<?>> selectedEntries = new ArrayList<>(calendarView.getMonthPage().getSelections());
                Entry<?> entry = selectedEntries.get(0);

                boolean readOnly = entry.getCalendar().isReadOnly();
                eventNameField.setDisable(readOnly);
                startTimeField.setDisable(readOnly);
                endTimeField.setDisable(readOnly);
                locationField.setDisable(readOnly);
                noteArea.setDisable(readOnly);

                eventNameField.setText(entry.getTitle());
                startTimeField.setText(entry.getStartTime().toString());
                endTimeField.setText(entry.getEndTime().toString());
                locationField.setText(entry.getLocation());
                noteArea.setText(((Map)(entry.getUserObject())).get("introduction")==null?"":((Map)(entry.getUserObject())).get("introduction").toString());
            }
        });

        calendarView.getYearPage().getSelections().addListener((SetChangeListener<? super Entry<?>>) change -> {
            if (calendarView.getYearPage().getSelections().size() == 1) {
                List<Entry<?>> selectedEntries = new ArrayList<>(calendarView.getYearPage().getSelections());
                Entry<?> entry = selectedEntries.get(0);

                boolean readOnly = entry.getCalendar().isReadOnly();
                eventNameField.setDisable(readOnly);
                startTimeField.setDisable(readOnly);
                endTimeField.setDisable(readOnly);
                locationField.setDisable(readOnly);
                noteArea.setDisable(readOnly);

                eventNameField.setText(entry.getTitle());
                startTimeField.setText(entry.getStartTime().toString());
                endTimeField.setText(entry.getEndTime().toString());
                locationField.setText(entry.getLocation());
                noteArea.setText(((Map)(entry.getUserObject())).get("introduction")==null?"":((Map)(entry.getUserObject())).get("introduction").toString());
            }
        });

        //calendarView.getDayPage().getDetailedDayView().setMaxWidth(300);
        putCalendars();
    }

    /*private void initializeToDoTable()
    {
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        homeworkContentColumn.setCellValueFactory(new MapValueFactory<>("homeworkContent"));
        deadlineColumn.setCellValueFactory(new MapValueFactory<>("deadline"));
        submitColumn.setCellFactory(param->{
            final Button button=new Button("提交");
            TableCell<Map,String> cell=new TableCell<Map,String>(){
                @Override
                protected void updateItem(String item,boolean empty)
                {
                    super.updateItem(item,empty);
                    if(empty)
                    {
                        setGraphic(null);
                        setText(null);
                    }
                    else
                    {
                        button.setOnAction(event->{
                            Map m=getTableView().getItems().get(getIndex());
                            System.out.println(m);
                            uploadHomeWork(m);
                        });
                        setGraphic(button);
                        setText(null);
                    }
                }
            };
            return cell;
        });

        displayToDoTable();
    }*/

    /*private void displayToDoTable()
    {
        ArrayList<Map> list=(ArrayList<Map>) request("/getTeacherHomework", null).getData();
        for(Map m:list)
        {
            if(m.get("course")!=null)
            {
                Map course=(Map) m.get("course");
                m.put("courseName",course.get("name"));
            }
        }
        toDoTable.setItems(FXCollections.observableArrayList(list));
    }*/

    /*private void uploadHomeWork(Map m) {
        String submitTime = LocalDate.now().toString();  // set submit time to today
        String deadline =(String)m.get("deadline");  // get deadline from selected item
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate submitDate = LocalDate.parse(submitTime, formatter);
        LocalDate deadlineDate = LocalDate.parse(deadline, formatter);
        if (submitDate.isAfter(deadlineDate)) {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("提交时间已经超过截止日期");
            alert.show();
            return;
        }
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("选择作业文件");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        fileDialog.getExtensionFilters().add(extFilter);

        File file = fileDialog.showOpenDialog(null);
        if(file==null)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("未选择文件");
            alert.show();
            return;
        }
        DataResponse r=uploadFile("/uploadHomework",file.getPath(), file.getName(),(String) m.get("homeworkId"));
        if(r.getCode()==0)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("上传成功");
            alert.show();
            //displayToDoTable();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("上传失败");
            alert.show();
        }
    }*/

    private void putCalendars()
    {
        List<Map> events = (List<Map>) request("/getTeacherEvents",null).getData();
        Map<String,List<Map>> sortedEvents = new HashMap<>();
        for (Map event : events) {
            String key = event.get("name") + "";
            if (!sortedEvents.containsKey(key)) {
                sortedEvents.put(key, new ArrayList<>());
            }
            sortedEvents.get(key).add(event);
        }
        System.out.println(sortedEvents);
        ArrayList<Calendar> calendars = new ArrayList<>();
        int i=1;
        for (String key : sortedEvents.keySet()) {
            Calendar calendar = new Calendar(key);
            setEvents(calendar, sortedEvents.get(key));
            System.out.println(sortedEvents.get(key));
            calendar.setReadOnly(true);
            calendar.setStyle("style"+i);
            calendars.add(calendar);
            i++;
            if(i==8) i=1;
        }

        /*Calendar calendar = new Calendar("我的日程");
        calendar.setStyle("style7");
        calendars.add(calendar);*/ //砍了

        //System.out.println(calendars);

        calendarView.getCalendarSources().clear();
        calendarSource.getCalendars().addAll(calendars);
        calendarView.getCalendarSources().add(calendarSource);
    }

    public List<Map> getEvents(Calendar calendar) {
        List<Map> events = new ArrayList<>();
        for (Entry<Map> entry : (List<Entry>)calendar.findEntries("")) {
            events.add(convertEntryToMap(entry));
        }
        return events;
    }

    public void setEvents(Calendar calendar, List<Map> events) {
        calendar.clear();
        calendar.addEntries(events.stream().map(this::convertMapToEntry).collect(Collectors.toList()));
    }

    public Entry<Map> convertMapToEntry(Map<String, Object> map) {
        Entry<Map> entry = new Entry<>((String) map.get("name"));
        entry.setUserObject(map);
        entry.setInterval(
                LocalDate.parse(map.get("startDate")+""),
                LocalTime.parse(map.get("startTime")+"") ,
                LocalDate.parse(map.get("endDate")+""),
                LocalTime.parse(map.get("endTime")+"")
        );
        entry.setLocation((String) map.get("location"));

        return entry;
    }

    public Map<String, Object> convertEntryToMap(Entry<Map> entry) {
        String name = entry.getTitle() != null ? entry.getTitle() : "";
        String eventId = entry.getUserObject() != null ? (String) entry.getUserObject().get("eventId") : "";
        LocalDate startDate = entry.getStartDate() != null ? entry.getStartDate() : LocalDate.now();
        LocalDate endDate = entry.getEndDate() != null ? entry.getEndDate() : LocalDate.now();
        LocalTime startTime = entry.getStartTime() != null ? entry.getStartTime() : LocalTime.now();
        LocalTime endTime = entry.getEndTime() != null ? entry.getEndTime() : LocalTime.now();
        String location = entry.getLocation() != null ? entry.getLocation() : "";

        return Map.of(
                "name", name,
                "eventId", eventId,
                "startDate", startDate.toString(),
                "endDate", endDate.toString(),
                "startTime", startTime.toString(),
                "endTime", endTime.toString(),
                "location", location
        );
    }

    /*@FXML
    private void saveEvent() {
        String msg = request("/saveEvent",m).getMsg();
        if (msg.equals("保存成功")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("保存成功");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("保存失败");
            alert.show();
        }
    }*/
}


