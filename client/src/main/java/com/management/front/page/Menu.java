package com.management.front.page;

import com.jfoenix.controls.JFXTreeView;
import com.management.front.ClientApplication;
import com.management.front.page.admin.*;
import com.management.front.page.student.*;
import com.management.front.page.teacher.TeacherCourseMenuPage;
import com.management.front.page.teacher.TeacherPersonalInfoPage;
import com.management.front.request.DataResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

import static com.management.front.util.HttpClientUtil.request;
import static org.kordamp.ikonli.fontawesome.FontAwesome.*;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignA.*;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignB.BOOK_OPEN_PAGE_VARIANT;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignB.BOOK_PLUS_MULTIPLE;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignF.FILE_DOCUMENT;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignL.LIGHTBULB_ON_OUTLINE;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignM.*;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignR.RUN;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignS.SCHOOL;

public class Menu extends AnchorPane {
    //@FXML
    //private JFXTreeView<String> leftMenu;
    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXTreeView<String> leftMenu;
    @FXML
    private Text roleLabel;

    private TreeItem<String> root = new TreeItem<>("菜单");

    private TreeItem<String> stuMngItm = new TreeItem<>("学生管理");
    private TreeItem<String> tchMngItm = new TreeItem<>("教师管理");
    private TreeItem<String> crsMngItm = new TreeItem<>("课程管理");
    private TreeItem<String> clzMngItm = new TreeItem<>("班级管理");
    private TreeItem<String> invMngItm = new TreeItem<>("创新实践管理");
    private TreeItem<String> dlaMngItm = new TreeItem<>("日常活动管理");
    private TreeItem<String> hnrMngItm = new TreeItem<>("荣誉管理");
    private TreeItem<String> logMngItm = new TreeItem<>("日志管理");
    private TreeItem<String> crsAppItm = new TreeItem<>("课程申请");
    private TreeItem<String> stuPerItm = new TreeItem<>("个人信息");
    private TreeItem<String> tchPerItm = new TreeItem<>("个人信息");
    private TreeItem<String> tchCrsItm = new TreeItem<>("课程信息");
    private TreeItem<String> stuCrsItm = new TreeItem<>("课程信息");
    private TreeItem<String> stuAbsItm = new TreeItem<>("学生请假");
    private TreeItem<String> stuInoItm = new TreeItem<>("创新实践");
    private TreeItem<String> stuDlaItm = new TreeItem<>("日常活动");

    public Menu(){
        //this.setStyle("-fx-background-color: red;");
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/com/management/front/menu.fxml")); // 确保路径正确
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
                leftMenu.getRoot().getChildren().addAll(stuMngItm,tchMngItm,crsMngItm,clzMngItm,invMngItm,dlaMngItm,hnrMngItm,logMngItm);
                break;
            case "ROLE_STUDENT":
                roleLabel.setText("学生菜单");
                leftMenu.getRoot().getChildren().addAll(stuCrsItm,crsAppItm,stuPerItm,stuAbsItm,stuInoItm,stuDlaItm);
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
            else if(newValue == invMngItm)
            {
                borderPane.setCenter(new InnovationManagementPage());
            }
            else if(newValue == dlaMngItm)
            {
                borderPane.setCenter(new DailyActivityManagementPage());
            }
            else if(newValue == hnrMngItm)
            {
                borderPane.setCenter(new HonorManagementPage());
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
                borderPane.setCenter(new TeacherPersonalInfoPage());
            }
            else if(newValue == tchCrsItm)
            {
                borderPane.setCenter(new TeacherCourseMenuPage());
            }
            else if(newValue == stuCrsItm)
            {
                //borderPane.setCenter(new StudentCourseMenuPage());
                borderPane.setCenter(new StudentHomeworkPage());
            }
            else if(newValue == stuAbsItm)
            {
                borderPane.setCenter(new AbsencePage());
            }
            else if(newValue == stuInoItm)
            {
                borderPane.setCenter(new InnovationPage());
            }
            else if(newValue == stuDlaItm)
            {
                borderPane.setCenter(new DailyActivityPage());
            }
        });


    }

    private void initializeIcons()
    {

        //leftMenu.setStyle("-fx-background-color: red;-fx-font-size: 15px;");

        root.setGraphic(new FontIcon(MENU));
        //tchCrsItm.setGraphic(new ImageView("/icons/teacher.png"));

        tchCrsItm.setGraphic(new FontIcon("mdi2b-book-open-page-variant"));

        tchPerItm.setGraphic(new FontIcon(ID_CARD));
        stuPerItm.setGraphic(new FontIcon(ID_CARD));
        stuCrsItm.setGraphic(new FontIcon(BOOK_OPEN_PAGE_VARIANT));
        stuMngItm.setGraphic(new FontIcon(ACCOUNT_MULTIPLE));
        tchMngItm.setGraphic(new FontIcon(ACCOUNT_MULTIPLE_OUTLINE));
        crsMngItm.setGraphic(new FontIcon(BOOK_OPEN_PAGE_VARIANT));
        clzMngItm.setGraphic(new FontIcon(SCHOOL));
        logMngItm.setGraphic(new FontIcon(FILE_DOCUMENT));
        crsAppItm.setGraphic(new FontIcon(BOOK_PLUS_MULTIPLE));
        stuAbsItm.setGraphic(new FontIcon(AIRPORT));
        stuInoItm.setGraphic(new FontIcon(LIGHTBULB_ON_OUTLINE));
        invMngItm.setGraphic(new FontIcon(LIGHTBULB_ON_OUTLINE));
        stuDlaItm.setGraphic(new FontIcon(RUN));
        dlaMngItm.setGraphic(new FontIcon(RUN));
        hnrMngItm.setGraphic(new FontIcon(MEDAL));







    }
}
