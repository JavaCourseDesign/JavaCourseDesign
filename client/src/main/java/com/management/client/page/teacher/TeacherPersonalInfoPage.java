package com.management.client.page.teacher;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;

import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;

/**
 * @author 23993
 */
public class TeacherPersonalInfoPage extends TabPane {
    public TeacherPersonalInfoPage() {
        Map teacher = (Map) request("/getTeacher", null).getData();
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Tab teacherBasicInfoTab = new TeacherBasicInfoTab(teacher);
        this.getTabs().add(teacherBasicInfoTab);
    }
}
