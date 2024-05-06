package com.management.client.page.student;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.management.client.ClientApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.management.client.util.HttpClientUtil.request;

public class StudentHomePage extends SplitPane {
    @FXML
    private VBox homePage;
    @FXML
    private CalendarView calendarView;

    private final CalendarSource calendarSource = new CalendarSource();
    public StudentHomePage() {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/studentFxml/StudentHomePage.fxml")); // 确保路径正确
        fxmlLoader.setController(this);
        try {
            homePage=fxmlLoader.load();
            this.getItems().add(homePage);
            //this.getStylesheets().add("dark-theme.css");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initializeCalendarView();
        putCalendars();
    }

    private void initializeCalendarView()
    {
        calendarView.setShowAddCalendarButton(false);
        calendarView.setPrefSize(850,700);
    }

    private void putCalendars()
    {
        List<Map> events = (List<Map>) request("/getStudentEvents",null).getData();
        Calendar calendar = new Calendar("My Calendar");
        calendarSource.getCalendars().add(calendar);
        setEvents(calendar, events);
        calendarView.getCalendarSources().clear();
        calendarView.getCalendarSources().add(calendarSource);
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
