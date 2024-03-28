/*

package com.management.front;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TestApplication extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage)  {
        stage.setTitle("TreeTableViewTest");
        AnchorPane rootPane = new AnchorPane(); // 创建一个新的AnchorPane
        Scene scene = new Scene(rootPane, 500, 400); // 使用新的AnchorPane创建场景
        //Creating tree items
        final TreeItem<String> childNode1 = new TreeItem<>("选项节点1");//创建树形结构选项组件
        final TreeItem<String> childNode2 = new TreeItem<>("选项节点2");//创建树形结构选项组件
        final TreeItem<String> childNode3 = new TreeItem<>("选项节点3");//创建树形结构选项组件
        //Creating the root element
        final TreeItem<String> root = new TreeItem<>("根节点");
        root.setExpanded(true);//默认设置根节点可展开
        //Adding tree items to the root
        root.getChildren().setAll(childNode1, childNode2, childNode3);//根节点添加3个叶子节点
        //Creating a column
        TreeTableColumn<String,String> column = new TreeTableColumn<>("列名");//创建一个表格列
        column.setPrefWidth(150);//列宽设置
        //Defining cell content
        column.setCellValueFactory((CellDataFeatures<String, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue()));//定义所选cell组件的内容格式
        //Creating a tree table view
        final TreeTableView<String> treeTableView = new TreeTableView<>(root);//创建一个表格树组件并添加根选项节点到树形表格当中
        treeTableView.getColumns().add(column);//表格添加列
        treeTableView.setPrefWidth(152);//树表格添加宽度
        treeTableView.setShowRoot(true);//树表格显示根节点
        rootPane.getChildren().add(treeTableView);//将TreeTableView添加到AnchorPane中
        stage.setScene(scene);//舞台设置场景
        stage.show();//舞台展现
        treeTableView.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
            if (selectedItem == childNode1) {
                FXMLLoader fxmlLoader=new FXMLLoader(TestApplication.class.getResource("student.fxml"));
                Parent ro = null;
                try {
                    ro = fxmlLoader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                rootPane.getChildren().add(ro);// 将从student.fxml加载的界面添加到AnchorPane中// 将从student.fxml加载的界面添加到AnchorPane中
            } else if (selectedItem == childNode2) {

            }
        });
    }
}



*/
package com.management.front;

import com.management.front.controller.MenuController;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TestApplication extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

   /* @Override
    public void start(Stage stage)  throws IOException{
        stage.setTitle("TreeTableViewTest");
        FXMLLoader fxmlLoader=new FXMLLoader(TestApplication.class.getResource("adminFxml/menu.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800, 400);
        stage.setScene(scene);//舞台设置场景
        stage.show();//舞台展现
    }*/
    @Override
    public void start(Stage stage)  throws IOException{
        FXMLLoader fxmlLoader=new FXMLLoader(TestApplication.class.getResource("adminFxml/login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800, 400);
        stage.setScene(scene);//舞台设置场景
        stage.show();//舞台展现
    }
}

