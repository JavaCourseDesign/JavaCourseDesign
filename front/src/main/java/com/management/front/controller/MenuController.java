package com.management.front.controller;

import com.management.front.TestApplication;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MenuController {
    @FXML
    BorderPane rootPane;


    public void initialize()
   {
       //Creating tree items
       TreeItem<String> childNode1 = new TreeItem<>("学生管理");//创建树形结构选项组件
       TreeItem<String> childNode2 = new TreeItem<>("课程管理");//创建树形结构选项组件
       TreeItem<String> childNode3 = new TreeItem<>("教师管理");//创建树形结构选项组件
       TreeItem<String> childNode4 = new TreeItem<>("图片测试");//创建树形结构选项组件
       //Creating the root element
       final TreeItem<String> root = new TreeItem<>("根节点");
       root.setExpanded(true);//默认设置根节点可展开
       //Adding tree items to the root
       root.getChildren().setAll(childNode1, childNode2, childNode3,childNode4);//根节点添加3个叶子节点
       //Creating a column
       TreeTableColumn<String,String> column = new TreeTableColumn<>("列名");//创建一个表格列
       column.setPrefWidth(150);//列宽设置
       //Defining cell content
       column.setCellValueFactory((
               TreeTableColumn.CellDataFeatures<String, String> p)
               -> new ReadOnlyStringWrapper(p.getValue().getValue()));//定义所选cell组件的内容格式
       //Creating a tree table view
       final TreeTableView<String> treeTableView = new TreeTableView<>(root);//创建一个表格树组件并添加根选项节点到树形表格当中
       treeTableView.getColumns().add(column);//表格添加列
       treeTableView.setPrefWidth(152);//树表格添加宽度
       treeTableView.setShowRoot(true);//树表格显示根节点
       rootPane.setLeft(treeTableView);//将TreeTableView添加到BorderPane的左边
       treeTableView.setOnMouseClicked(event -> {
           TreeItem<String> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
           if (selectedItem == childNode1) {
               FXMLLoader fxmlLoader=new FXMLLoader(TestApplication.class.getResource("adminFxml/student.fxml"));
               Parent ro = null;
               try {
                   ro = fxmlLoader.load();
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
               rootPane.setCenter(ro);
               ro.setLayoutX(100);
               ro.setLayoutY(200);
               // 将从student.fxml加载的界面添加到BorderPane的中间
           } else if (selectedItem == childNode2) {
               FXMLLoader fxmlLoader=new FXMLLoader(TestApplication.class.getResource("adminFxml/course_panel.fxml"));
               Parent ro = null;
               try {
                   ro = fxmlLoader.load();
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
               rootPane.setCenter(ro);
               ro.setLayoutX(100);
               ro.setLayoutY(200);
           }
           else if(selectedItem==childNode3)
           {
               rootPane.setCenter(new TeacherManagementPage());
           }
           else if(selectedItem==childNode4) {
               FXMLLoader fxmlLoader = new FXMLLoader(TestApplication.class.getResource("adminFxml/photoDemo.fxml"));
               Parent ro = null;
               try {
                   ro = fxmlLoader.load();
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
               rootPane.setCenter(ro);
               ro.setLayoutX(100);
               ro.setLayoutY(200);

           }
       });
   }
}
