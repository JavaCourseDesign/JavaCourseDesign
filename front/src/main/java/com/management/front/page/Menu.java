package com.management.front.page;

import com.management.front.HelloApplication;
import com.management.front.page.admin.*;
import com.management.front.page.student.CourseApplyPage;
import com.management.front.page.student.StudentHomeworkPage;
import com.management.front.page.student.StudentPersonalInfoPage;
import com.management.front.page.teacher.TeacherCourseMenuPage;
import com.management.front.request.DataResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

import static com.management.front.util.HttpClientUtil.request;
import static org.kordamp.ikonli.fontawesome.FontAwesome.*;

public class Menu extends AnchorPane {
    //@FXML
    //private JFXTreeView<String> leftMenu;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TreeView<String> leftMenu;
    @FXML
    private Label roleLabel;

    private TreeItem<String> root = new TreeItem<>("菜单");

    private TreeItem<String> stuMngItm = new TreeItem<>("学生管理");
    private TreeItem<String> tchMngItm = new TreeItem<>("教师管理");
    private TreeItem<String> crsMngItm = new TreeItem<>("课程管理");
    private TreeItem<String> clzMngItm = new TreeItem<>("班级管理");
    private TreeItem<String> logMngItm = new TreeItem<>("日志管理");
    private TreeItem<String> crsAppItm = new TreeItem<>("课程申请");
    private TreeItem<String> stuPerItm = new TreeItem<>("个人信息");
    private TreeItem<String> tchPerItm = new TreeItem<>("个人信息");
    private TreeItem<String> tchCrsItm = new TreeItem<>("课程信息");
    private TreeItem<String> stuCrsItm = new TreeItem<>("作业信息");

    public Menu(){
        //this.setStyle("-fx-background-color: red;");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/menu.fxml")); // 确保路径正确
        fxmlLoader.setController(this);
        try {
            borderPane=fxmlLoader.load();
            AnchorPane.setTopAnchor(borderPane, 0.0);
            AnchorPane.setBottomAnchor(borderPane, 0.0);
            AnchorPane.setLeftAnchor(borderPane, 0.0);
            AnchorPane.setRightAnchor(borderPane, 0.0);
            this.getChildren().add(borderPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initializeLeftMenu();
        initializeIcons();
    }

    public void initializeLeftMenu()
    {
        leftMenu.setRoot(root);
        //leftMenu.setShowRoot(false);
        root.setExpanded(true);

        DataResponse response = request("/getRole",null);
        switch (response.getMsg())
        {
            case "ROLE_ADMIN":
                roleLabel.setText("管理员菜单");
                leftMenu.getRoot().getChildren().addAll(stuMngItm,tchMngItm,crsMngItm,clzMngItm,logMngItm);
                break;
            case "ROLE_STUDENT":
                roleLabel.setText("学生菜单");
                leftMenu.getRoot().getChildren().addAll(stuCrsItm,crsAppItm,stuPerItm);
                break;
            case "ROLE_TEACHER":
                roleLabel.setText("教师菜单");
                leftMenu.getRoot().getChildren().addAll(tchCrsItm,tchPerItm);
                break;
            default:
                System.out.println("Error: Unknown role"+response.getMsg());
        }

        leftMenu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == stuMngItm)
            {
                borderPane.setCenter(new StudentManagementPage());
            }
            else if(newValue == tchMngItm)
            {
                borderPane.setCenter(new TeacherManagementPage());
            }
            else if(newValue == crsMngItm)
            {
                borderPane.setCenter(new CourseManagementPage());
            }
            else if(newValue == clzMngItm)
            {
                borderPane.setCenter(new ClazzManagementPage());
            }
            else if(newValue == logMngItm)
            {
                borderPane.setCenter(new StudentLogManagementPage());
            }
            else if(newValue == crsAppItm)
            {
                borderPane.setCenter(new CourseApplyPage());
            }
            else if(newValue == stuPerItm)
            {
                borderPane.setCenter(new StudentPersonalInfoPage());
            }
            else if(newValue == tchPerItm)
            {
                //borderPane.setCenter(new TeacherPersonalInfoPage());
            }
            else if(newValue == tchCrsItm)
            {
                borderPane.setCenter(new TeacherCourseMenuPage());
            }
            else if(newValue == stuCrsItm)
            {
                borderPane.setCenter(new StudentHomeworkPage());
            }
        });
    }

    private void initializeIcons()
    {

        //leftMenu.setStyle("-fx-background-color: red;-fx-font-size: 15px;");

        root.setGraphic(new FontIcon("mdi-menu"));
        //tchCrsItm.setGraphic(new ImageView("/icons/teacher.png"));

        tchCrsItm.setGraphic(new FontIcon("mdi-book-open-page-variant"));

        tchPerItm.setGraphic(new FontIcon(ID_CARD));
        stuPerItm.setGraphic(new FontIcon(ID_CARD));
        stuCrsItm.setGraphic(new FontIcon("mdi-book-open-page-variant"));
        stuMngItm.setGraphic(new FontIcon("mdi-account-multiple"));
        tchMngItm.setGraphic(new FontIcon("mdi-account-multiple"));
        crsMngItm.setGraphic(new FontIcon("mdi-book-open-page-variant"));
        clzMngItm.setGraphic(new FontIcon("mdi-school"));
        logMngItm.setGraphic(new FontIcon("mdi-file-document"));
        crsAppItm.setGraphic(new FontIcon("mdi-book-open-page-variant"));








    }
}
