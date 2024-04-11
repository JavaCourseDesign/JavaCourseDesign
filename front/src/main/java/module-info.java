module com.management.front {
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

    opens com.management.front to javafx.fxml;
    exports com.management.front;

    //following are manually added
    exports com.management.front.request to com.google.gson;
    opens com.management.front.request to com.google.gson;
    exports com.management.front.controller;
    opens com.management.front.controller to javafx.fxml;
    exports com.management.front.util;
    opens com.management.front.util to com.google.gson;
}