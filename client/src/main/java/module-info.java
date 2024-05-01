module com.management.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires java.net.http;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires java.desktop;
    requires itextpdf;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires com.jfoenix;
    requires com.calendarfx.view;
    requires org.burningwave.core;
    requires org.kordamp.ikonli.materialdesign2;
    requires cn.hutool;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens com.management.client to javafx.fxml;
    exports com.management.client;

    /*// 以下是手动添加的模块声明
    exports com.management.client.request to com.google.gson;
    opens com.management.client.request to com.google.gson;
    exports com.management.client.page;
    opens com.management.client.page to javafx.fxml;
    exports com.management.client.util;
    opens com.management.client.util to com.google.gson;
    exports com.management.client.page.admin;
    opens com.management.client.page.admin to javafx.fxml;
    exports com.management.client.page.student;
    opens com.management.client.page.student to javafx.fxml;
    exports com.management.client.page.teacher;
    opens com.management.client.page.teacher to javafx.fxml;
    exports com.management.client.customComponents;
    opens com.management.client.customComponents to javafx.fxml;*/
}
