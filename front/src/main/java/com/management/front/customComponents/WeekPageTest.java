package com.management.front.customComponents;

import com.calendarfx.view.page.WeekPage;

public class WeekPageTest extends WeekPage {
    public WeekPageTest() {
        super();
        this.setOnMouseClicked(event -> {
            System.out.println("WeekPageTest: " + event);
            System.out.println(this.getPropertySheetItems());
        });
    }
}
