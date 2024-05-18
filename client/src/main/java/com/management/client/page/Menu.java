package com.management.client.page;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTreeView;
import com.management.client.ClientApplication;
import com.management.client.page.admin.*;
import com.management.client.page.student.*;
import com.management.client.page.admin.AdminHomePage;
import com.management.client.page.teacher.TeacherCourseMenuPage;
import com.management.client.page.teacher.TeacherHomePage;
import com.management.client.page.teacher.TeacherPersonalInfoPage;
import com.management.client.request.DataResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;
import static org.kordamp.ikonli.fontawesome.FontAwesome.ID_CARD;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignA.*;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignB.BOOK_OPEN_PAGE_VARIANT;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignB.BOOK_PLUS_MULTIPLE;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignF.FILE_DOCUMENT;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignF.FORMAT_ANNOTATION_PLUS;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignH.HOME;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignL.LIGHTBULB_ON_OUTLINE;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignM.MEDAL;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignM.MENU;
import static org.kordamp.ikonli.materialdesign2.MaterialDesignR.REFRESH;
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
    private MenuButton userMenu;
    @FXML
    private Text roleLabel;
    @FXML
    private MenuItem modifyPasswordMenuItem;
    @FXML
    private MenuItem logOutMenuItem;
    @FXML
    private Button refreshButton;

    private TreeItem<String> root = new TreeItem<>("菜单");

    private TreeItem<String> stuMngItm = new TreeItem<>("学生管理");
    private TreeItem<String> tchMngItm = new TreeItem<>("教师管理");
    private TreeItem<String> crsMngItm = new TreeItem<>("课程管理");
    private TreeItem<String> clzMngItm = new TreeItem<>("班级管理");
    private TreeItem<String> invMngItm = new TreeItem<>("创新实践管理");
    private TreeItem<String> dlaMngItm = new TreeItem<>("日常活动管理");
    private TreeItem<String> hnrMngItm = new TreeItem<>("荣誉管理");
    private TreeItem<String> logMngItm = new TreeItem<>("日志管理");

    private TreeItem<String> tchPerItm = new TreeItem<>("个人信息");
    private TreeItem<String> tchCrsItm = new TreeItem<>("课程信息");

    private TreeItem<String> stuHmpItm = new TreeItem<>("主界面");
    private TreeItem<String> tchHmpItm = new TreeItem<>("主界面");
    private TreeItem<String> admHmpItm = new TreeItem<>("主界面");
    private TreeItem<String> crsAppItm = new TreeItem<>("课程申请");
    private TreeItem<String> stuPerItm = new TreeItem<>("个人信息");
    private TreeItem<String> stuCrsItm = new TreeItem<>("课程信息");
    private TreeItem<String> stuAbsItm = new TreeItem<>("学生请假");
    private TreeItem<String> stuInoItm = new TreeItem<>("创新实践");
    private TreeItem<String> stuDlaItm = new TreeItem<>("日常活动");
    private TreeItem<String> stuScoItm = new TreeItem<>("成绩查询");

    public Menu(){
        //this.setStyle("-fx-background-color: red;");
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/menu.fxml")); // 确保路径正确
        fxmlLoader.setController(this);
        try {
            borderPane=fxmlLoader.load();
            AnchorPane.setTopAnchor(borderPane, 0.0);
            AnchorPane.setBottomAnchor(borderPane, 0.0);
            AnchorPane.setLeftAnchor(borderPane, 0.0);
            AnchorPane.setRightAnchor(borderPane, 0.0);
            this.getChildren().add(borderPane);
            //this.getStylesheets().add("dark-theme.css");
            this.getStylesheets().add("menu.css");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*modifyPasswordMenuItem.setStyle("-fx-background-color:#2196F3;-fx-text-fill: white;");
        logOutMenuItem.setStyle("-fx-background-color: #2196F3;-fx-text-fill: white;");
        refreshButton.setStyle("-fx-background-color: #2196F3;-fx-text-fill: white;");*/
        initializeLeftMenu();
        initializeIcons();
    }

    @FXML
    public void refresh()
    {
        Class<?> clazz = borderPane.getCenter().getClass();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            if(instance instanceof Node)
            {
                borderPane.setCenter((Node) instance);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void modifyPassword()
    {
        Stage modifyPasswordStage=new Stage();
        GridPane gridPane=new GridPane();
        //gridPane.getStylesheets().add("dark-theme.css");
        gridPane.setAlignment(javafx.geometry.Pos.CENTER);
        TextField oldPassword = new TextField();
        JFXPasswordField newPassword = new JFXPasswordField();
        JFXPasswordField confirmPassword = new JFXPasswordField();
        JFXButton confirmButton = new JFXButton("确认");
        JFXButton cancelButton = new JFXButton("取消");
        confirmButton.setOnAction(event1 -> {
            if(oldPassword.getText().equals("")||newPassword.getText().equals("")||confirmPassword.getText().equals(""))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "密码不能为空");
                alert.showAndWait();
                return;
            }
            if (!newPassword.getText().equals(confirmPassword.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "两次输入的密码不一致");
                alert.showAndWait();
                return;
            }
            if (oldPassword.getText().equals(newPassword.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "新密码不能与旧密码相同");
                alert.showAndWait();
                return;
            }
            Map<String, String> map = new HashMap<>();
            map.put("oldPassword", oldPassword.getText());
            map.put("newPassword", newPassword.getText());
            DataResponse response = request("/modifyPassword", map);
            if (response.getCode() == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, response.getMsg());
                alert.showAndWait();
                modifyPasswordStage.close();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, response.getMsg());
                alert.showAndWait();
            }
        });
        cancelButton.setOnAction(event1 -> modifyPasswordStage.close());
        gridPane.addColumn(0,new Label("旧密码"),new Label("新密码"),new Label("确认密码"),confirmButton);
        gridPane.addColumn(1,oldPassword,newPassword,confirmPassword,cancelButton);
        Scene scene=new Scene(gridPane, 300, 200);
        modifyPasswordStage.setScene(scene);
        modifyPasswordStage.show();
    }
    @FXML
    public void logOut()
    {
        Stage stage = (Stage) refreshButton.getScene().getWindow();
        stage.close();
        Stage loginStage = new Stage();
        Scene scene = new Scene(new LoginPage(), 300, 400);
        loginStage.setScene(scene);
        loginStage.show();
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
                borderPane.setCenter(new AdminHomePage());
                leftMenu.getRoot().getChildren().addAll(admHmpItm,stuMngItm,tchMngItm,crsMngItm,clzMngItm,invMngItm,dlaMngItm,hnrMngItm,logMngItm);
                break;
            case "ROLE_STUDENT":
                roleLabel.setText("学生菜单");
                borderPane.setCenter(new StudentHomePage());
                leftMenu.getRoot().getChildren().addAll(stuHmpItm,stuCrsItm,crsAppItm,stuPerItm,stuAbsItm,stuInoItm,stuDlaItm,stuScoItm);
                break;
            case "ROLE_TEACHER":
                roleLabel.setText("教师菜单");
                borderPane.setCenter(new TeacherHomePage());
                leftMenu.getRoot().getChildren().addAll(tchHmpItm,tchCrsItm,tchPerItm);
                break;
            default:
                //System.out.println("Error: Unknown role"+response.getMsg());
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
                borderPane.setCenter(new StudentCourseMenuPage());
                //borderPane.setCenter(new StudentHomeworkPage());
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
            else if(newValue == admHmpItm)
            {
                borderPane.setCenter(new AdminHomePage());
            }
            else if(newValue == stuHmpItm)
            {
                borderPane.setCenter(new StudentHomePage());
            }
            else if(newValue == tchHmpItm)
            {
                borderPane.setCenter(new TeacherHomePage());
            }
            else if(newValue == stuScoItm)
            {
                borderPane.setCenter(new StudentScorePage());
            }
        });


    }

    private void initializeIcons()
    {
        //leftMenu.setStyle("-fx-background-color: red;-fx-font-size: 15px;");
        root.setGraphic(new FontIcon(MENU));
        //tchCrsItm.setGraphic(new ImageView("/icons/teacher.png"));

        tchCrsItm.setGraphic(new FontIcon("mdi2b-book-open-page-variant"));
        admHmpItm.setGraphic(new FontIcon(HOME));
        stuHmpItm.setGraphic(new FontIcon(HOME));
        tchHmpItm.setGraphic(new FontIcon(HOME));
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
        stuScoItm.setGraphic(new FontIcon(FORMAT_ANNOTATION_PLUS));

        FontIcon acc=new FontIcon(ACCOUNT);
        acc.setIconSize(25);
        userMenu.setGraphic(acc);
        //userMenu.setStyle("-fx-background-color: transparent");

        FontIcon ref=new FontIcon(REFRESH);
        ref.setIconSize(25);
        refreshButton.setGraphic(ref);
    }
}
