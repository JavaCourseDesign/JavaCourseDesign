package com.management.front.customComponents;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.page.WeekPage;
import com.calendarfx.view.popover.EntryDetailsView;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WeekTimeTable extends WeekPage {
    private final Calendar calendar;
    private final Calendar preViewCalendar;

    public WeekTimeTable() {
        this.setPrefHeight(600);

        this.calendar = new Calendar("Week Calendar");
        this.calendar.setStyle(Calendar.Style.STYLE1);
        CalendarSource calendarSource = new CalendarSource("My Calendar Source");
        calendarSource.getCalendars().add(this.calendar);

        this.preViewCalendar = new Calendar("Preview Calendar");
        this.preViewCalendar.setStyle(Calendar.Style.STYLE2);
        CalendarSource preViewCalendarSource = new CalendarSource("My Preview Calendar Source");
        preViewCalendarSource.getCalendars().add(this.preViewCalendar);

        this.getCalendarSources().setAll(calendarSource, preViewCalendarSource);
    }

    public void setCourse(Map<String, Object> course) {
        setEntryFactory(param -> {
            Entry<?> newEntry = new Entry<>(course.get("name") + "");
            newEntry.setInterval(param.getZonedDateTime());
            newEntry.setRecurrenceRule("RRULE:FREQ=WEEKLY;COUNT=20");//默认重复20次，每周一次
            //System.out.println(newEntry.recurrenceRuleProperty());

            newEntry.setCalendar(calendar);
            return newEntry;
        });
    }

    public void setEvents(List<Map<String,Object>> events) {
        this.calendar.clear();
        this.calendar.addEntries(events.stream().map(this::convertMapToEntry).collect(Collectors.toList()));
    }

    public void setPreViewEvents(List<Map<String,Object>> events) {
        this.preViewCalendar.clear();
        this.preViewCalendar.addEntries(events.stream().map(this::convertMapToEntry).collect(Collectors.toList()));
    }

    public List<Map> getEvents() {
        //return this.calendar.findEntries("").stream().map(this::convertEntryToMap).collect(Collectors.toList());
        List<Map> events = new ArrayList<>();
        System.out.println(this.calendar.findEntries(""));
        for (Entry<?> entry : (List<Entry>)this.calendar.findEntries("")) {
            events.add(convertEntryToMap(entry));
        }
        return events;
    }

    public Entry<String> convertMapToEntry(Map<String, Object> map) {
        Entry<String> entry = new Entry<>((String) map.get("name"));
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
                "startDate", startDate.toString(),
                "endDate", endDate.toString(),
                "startTime", startTime.toString(),
                "endTime", endTime.toString(),
                "location", location
        );
    }
}