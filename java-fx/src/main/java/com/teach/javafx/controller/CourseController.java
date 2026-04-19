package com.teach.javafx.controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.cell.MapValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.MainApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.controller.base.MessageDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CourseController 登录交互控制类 对应 course-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class CourseController {
    @FXML
    private TableView<Map<String, Object>> dataTableView;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> creditColumn;
    @FXML
    private TableColumn<Map,String> preCourseColumn;
    @FXML
    private TableColumn<Map,FlowPane> operateColumn;

    private List<Map<String,Object>> courseList = new ArrayList<>();  // 学生信息列表数据
    private final ObservableList<Map<String,Object>> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    private com.teach.javafx.controller.CourseAddDialogController courseAddDialogController = null;
    private javafx.stage.Stage dialogStage = null;

    @FXML
    private void onQueryButtonClick(){
        DataResponse res;
        DataRequest req =new DataRequest();
        res = HttpRequestUtil.request("/api/course/getCourseList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            courseList = (List<Map<String, Object>>) res.getData();
        }
        setTableViewData();
    }

    private void setTableViewData() {
       observableList.clear();
       Map<String,Object> map;
        FlowPane flowPane;
        Button saveButton,deleteButton;
            for (int j = 0; j < courseList.size(); j++) {
                map = courseList.get(j);
                flowPane = new FlowPane();
                flowPane.setHgap(10);
                flowPane.setAlignment(Pos.CENTER);
                saveButton = new Button("修改保存");
                saveButton.setId("save"+j);
                saveButton.setOnAction(e->{
                    saveItem(((Button)e.getSource()).getId());
                });
                deleteButton = new Button("删除");
                deleteButton.setId("delete"+j);
                deleteButton.setOnAction(e->{
                    deleteItem(((Button)e.getSource()).getId());
                });
                flowPane.getChildren().addAll(saveButton,deleteButton);
                map.put("operate",flowPane);
                observableList.addAll(FXCollections.observableArrayList(map));
            }
            dataTableView.setItems(observableList);
    }
    public void saveItem(String name){
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(4));
        Map<String,Object> data = courseList.get(j);
        DataRequest req = new DataRequest();
        if(data.get("courseId") != null) {
            req.add("courseId", data.get("courseId"));
        }
        req.add("num", data.get("num"));
        req.add("name", data.get("name"));
        req.add("credit", data.get("credit"));
        req.add("coursePath", data.get("coursePath"));
        if(data.get("preCourseId") != null) {
            req.add("preCourseId", data.get("preCourseId"));
        }
        DataResponse res = HttpRequestUtil.request("/api/course/courseSave", req);
        if(res != null && res.getCode() == 0) {
            System.out.println("保存成功");
            onQueryButtonClick();
        } else {
            System.out.println("保存失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }
    public void deleteItem(String name){
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(6));
        Map<String,Object> data = courseList.get(j);
        DataRequest req = new DataRequest();
        if(data.get("courseId") != null) {
            req.add("courseId", data.get("courseId"));
        }
        DataResponse res = HttpRequestUtil.request("/api/course/courseDelete", req);
        if(res != null && res.getCode() == 0) {
            System.out.println("删除成功");
            onQueryButtonClick();
        } else {
            System.out.println("删除失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }

    @FXML
    private void onAddButtonClick() {
        initDialog();
        courseAddDialogController.init();
        MainApplication.setCanClose(false);
        dialogStage.showAndWait();
    }

    private void initDialog() {
        if (dialogStage != null)
            return;
        FXMLLoader fxmlLoader;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("course-add-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 300, 200);
            dialogStage = new javafx.stage.Stage();
            dialogStage.initOwner(MainApplication.getMainStage());
            dialogStage.initModality(javafx.stage.Modality.NONE);
            dialogStage.setAlwaysOnTop(true);
            dialogStage.setScene(scene);
            dialogStage.setTitle("添加课程对话框");
            dialogStage.setOnCloseRequest(event -> {
                MainApplication.setCanClose(true);
            });
            courseAddDialogController = (com.teach.javafx.controller.CourseAddDialogController) fxmlLoader.getController();
            courseAddDialogController.setCourseController(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        dialogStage.close();
        if (!"ok".equals(cmd))
            return;

        DataRequest req = new DataRequest();
        req.add("num", data.get("num"));
        req.add("name", data.get("name"));
        req.add("credit", data.get("credit"));
        req.add("coursePath", data.get("coursePath"));
        if (data.get("preCourseId") != null) {
            req.add("preCourseId", data.get("preCourseId"));
        }

        DataResponse res = HttpRequestUtil.request("/api/course/courseSave", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("保存成功!");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog("保存失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }

    @FXML
    public void initialize() {
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        numColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        numColumn.setOnEditCommit(event -> {
            Map<String,Object> map = event.getRowValue();
            map.put("num", event.getNewValue());
        });
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("name", event.getNewValue());
        });
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        creditColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        creditColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("credit", event.getNewValue());
        });
        preCourseColumn.setCellValueFactory(new MapValueFactory<>("preCourse"));
        preCourseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        preCourseColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("preCourse", event.getNewValue());
        });
        operateColumn.setCellValueFactory(new MapValueFactory<>("operate"));
        dataTableView.setEditable(true);
        onQueryButtonClick();
    }


}
