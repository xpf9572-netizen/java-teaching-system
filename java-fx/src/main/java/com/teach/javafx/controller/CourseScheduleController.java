package com.teach.javafx.controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.request.OptionItemList;
import com.teach.javafx.controller.base.MessageDialog;

import java.io.IOException;
import java.util.*;

public class CourseScheduleController {
    @FXML
    private TableView<Map<String, Object>> dataTableView;
    @FXML
    private TableColumn<Map, String> courseColumn;
    @FXML
    private TableColumn<Map, String> teacherColumn;
    @FXML
    private TableColumn<Map, String> classColumn;
    @FXML
    private TableColumn<Map, String> classroomColumn;
    @FXML
    private TableColumn<Map, String> dayOfWeekColumn;
    @FXML
    private TableColumn<Map, String> classPeriodColumn;
    @FXML
    private TableColumn<Map, String> semesterColumn;
    @FXML
    private TableColumn<Map, FlowPane> operateColumn;

    @FXML
    private ComboBox<String> semesterComboBox;
    @FXML
    private ComboBox<String> courseComboBox;
    @FXML
    private ComboBox<String> teacherComboBox;
    @FXML
    private ComboBox<String> classComboBox;

    private List<Map<String, Object>> scheduleList = new ArrayList<>();
    private final ObservableList<Map<String, Object>> observableList = FXCollections.observableArrayList();
    private Map<String, String> courseMap = new HashMap<>();
    private Map<String, String> teacherMap = new HashMap<>();
    private Map<String, String> classMap = new HashMap<>();
    private Map<String, String> semesterMap = new HashMap<>();

    private javafx.stage.Stage dialogStage = null;

    @FXML
    private void onQueryButtonClick() {
        DataResponse res;
        DataRequest req = new DataRequest();
        String semester = semesterComboBox.getValue();
        if (semester != null && !semester.isEmpty() && !semester.contains("全部")) {
            req.add("semester", semester.split("-")[0]);
        }
        res = HttpRequestUtil.request("/api/courseSchedule/getScheduleList", req);
        if (res != null && res.getCode() == 0) {
            scheduleList = (List<Map<String, Object>>) res.getData();
        }
        setTableViewData();
    }

    private void setTableViewData() {
        observableList.clear();
        Map<String, Object> map;
        FlowPane flowPane;
        Button saveButton, deleteButton;
        for (int j = 0; j < scheduleList.size(); j++) {
            map = scheduleList.get(j);
            flowPane = new FlowPane();
            flowPane.setHgap(10);
            flowPane.setAlignment(Pos.CENTER);
            saveButton = new Button("修改");
            saveButton.setId("save" + j);
            saveButton.setOnAction(e -> editItem(((Button) e.getSource()).getId()));
            deleteButton = new Button("删除");
            deleteButton.setId("delete" + j);
            deleteButton.setOnAction(e -> deleteItem(((Button) e.getSource()).getId()));
            flowPane.getChildren().addAll(saveButton, deleteButton);
            map.put("operate", flowPane);
            observableList.add(map);
        }
        dataTableView.setItems(observableList);
    }

    private void editItem(String name) {
        if (name == null) return;
        int j = Integer.parseInt(name.substring(4));
        Map<String, Object> data = scheduleList.get(j);
        showEditDialog(data);
    }

    public void deleteItem(String name) {
        if (name == null) return;
        int j = Integer.parseInt(name.substring(6));
        Map<String, Object> data = scheduleList.get(j);
        DataRequest req = new DataRequest();
        if (data.get("scheduleId") != null) {
            req.add("scheduleId", data.get("scheduleId"));
        }
        DataResponse res = HttpRequestUtil.request("/api/courseSchedule/scheduleDelete", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("删除成功!");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog("删除失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }


    @FXML
    private void onAddButtonClick() {
        System.out.println("添加按钮被点击了");  // 加这一行
        showEditDialog(null);
    }

    private void showEditDialog(Object data) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/teach/javafx/courseSchedule-edit-dialog.fxml"));
            javafx.scene.layout.GridPane gridPane = loader.load();  // 改成 GridPane
            javafx.scene.control.Dialog<javafx.scene.control.ButtonType> dialog = new javafx.scene.control.Dialog<>();
            dialog.getDialogPane().setContent(gridPane);  // 把 GridPane 放进去
            dialog.setTitle("添加课程安排");
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "打开窗口失败：" + e.getMessage()).showAndWait();
        }
    }

    public void doClose(String cmd, Map<String, Object> resultData) {
        dialogStage.close();
        if (!"ok".equals(cmd)) return;
        onQueryButtonClick();
    }

    private void initComboBox() {
        List<OptionItem> courseOptions = HttpRequestUtil.requestOptionItemList("/api/course/getCourseOptionList", new DataRequest());
        if (courseOptions != null) {
            courseComboBox.getItems().clear();
            courseMap.clear();
            for (OptionItem item : courseOptions) {
                courseComboBox.getItems().add(item.getTitle());
                courseMap.put(item.getTitle(), item.getValue());
            }
        }

        List<OptionItem> teacherOptions = HttpRequestUtil.requestOptionItemList("/api/teachers/getTeacherOptionList", new DataRequest());
        if (teacherOptions != null) {
            teacherComboBox.getItems().clear();
            teacherMap.clear();
            for (OptionItem item : teacherOptions) {
                teacherComboBox.getItems().add(item.getTitle());
                teacherMap.put(item.getTitle(), item.getValue());
            }
        }

        List<OptionItem> classOptions = HttpRequestUtil.requestOptionItemList("/api/classes/getClassOptionList", new DataRequest());
        if (classOptions != null) {
            classComboBox.getItems().clear();
            classMap.clear();
            for (OptionItem item : classOptions) {
                classComboBox.getItems().add(item.getTitle());
                classMap.put(item.getTitle(), item.getValue());
            }
        }

        semesterComboBox.getItems().clear();
        semesterComboBox.getItems().addAll("全部", "2024-1", "2024-2", "2025-1", "2025-2");
    }

    @FXML
    public void initialize() {
        courseColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        teacherColumn.setCellValueFactory(new MapValueFactory<>("teacherName"));
        classColumn.setCellValueFactory(new MapValueFactory<>("className"));
        classroomColumn.setCellValueFactory(new MapValueFactory<>("classroom"));
        dayOfWeekColumn.setCellValueFactory(new MapValueFactory<>("dayOfWeek"));
        classPeriodColumn.setCellValueFactory(new MapValueFactory<>("classPeriod"));
        semesterColumn.setCellValueFactory(new MapValueFactory<>("semester"));
        operateColumn.setCellValueFactory(new MapValueFactory<>("operate"));

        initComboBox();
        onQueryButtonClick();
    }

    public Map<String, String> getCourseMap() { return courseMap; }
    public Map<String, String> getTeacherMap() { return teacherMap; }
    public Map<String, String> getClassMap() { return classMap; }
}
