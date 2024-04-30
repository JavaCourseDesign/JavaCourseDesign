package com.management.front.customComponents;

import cn.hutool.json.JSONObject;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.page.WeekPage;
import com.calendarfx.view.popover.EntryPopOverContentPane;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.beanToMap;
import static cn.hutool.core.bean.BeanUtil.toBean;

public class WeekTimeTable extends WeekPage {
    public final Calendar calendar;
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public WeekTimeTable() {
        this.setPrefHeight(600);

        this.calendar = new Calendar("Week Calendar");
        this.calendar.setStyle(Calendar.Style.STYLE1);
        CalendarSource calendarSource = new CalendarSource("My Calendar Source");
        calendarSource.getCalendars().add(this.calendar);

        this.getCalendarSources().setAll(calendarSource);

        this.setEntryDetailsPopOverContentCallback(param -> {
            System.out.println(param.getEntry());

            return new EntryPopOverContentPane(param.getPopOver(), param.getDateControl(), param.getEntry());
        });

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

    public List<Map> getEvents() {
        //return this.calendar.findEntries("").stream().map(this::convertEntryToMap).collect(Collectors.toList());
        List<Map> events = new ArrayList<>();
        System.out.println(this.calendar.findEntries(""));
        for (Entry<Map<String, Object>> entry : (List<Entry>)this.calendar.findEntries("")) {
            events.add(convertEntryToMap(entry));
        }
        return events;
    }

    public Entry convertMapToEntry(Map<String, Object> map) {
        System.out.println("entryStr: "+map.get("entry"));
        Entry entry;
        try {
            entry = objectMapper.readValue((String) map.get("entry"), Entry.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("entry: "+entry);
        entry.setUserObject(map);
        return entry;
    }

    public Map<String, Object> convertEntryToMap(Entry<Map<String, Object>> entry) {
        Map<String, Object> userObject = entry.getUserObject();
        if (userObject == null) {
            userObject = new HashMap<>();
        }
        userObject.put("name", entry.getTitle());
        userObject.put("startDate", entry.getStartDate());
        userObject.put("startTime", entry.getStartTime());
        userObject.put("endDate", entry.getEndDate());
        userObject.put("endTime", entry.getEndTime());
        userObject.put("location", entry.getLocation());
        try {
            userObject.put("entry", objectMapper.writeValueAsString(entry));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return userObject;
    }
}
