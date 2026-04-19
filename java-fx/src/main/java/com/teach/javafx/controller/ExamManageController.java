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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.controller.base.MessageDialog;

import java.util.*;

public class ExamManageController {
    @FXML
    private TableView<Map<String, Object>> dataTableView;
    @FXML
    private TableColumn<Map, String> courseColumn;
    @FXML
    private TableColumn<Map, String> semesterColumn;
    @FXML
    private TableColumn<Map, String> examDateColumn;
    @FXML
    private TableColumn<Map, String> examTimeColumn;
    @FXML
    private TableColumn<Map, String> examLocationColumn;
    @FXML
    private TableColumn<Map, String> invigilatorColumn;
    @FXML
    private TableColumn<Map, String> examTypeColumn;
    @FXML
    private TableColumn<Map, FlowPane> operateColumn;

    @FXML
    private ComboBox<String> semesterComboBox;

    private List<Map<String, Object>> examList = new ArrayList<>();
    private final ObservableList<Map<String, Object>> observableList = FXCollections.observableArrayList();
    private Map<String, String> courseMap = new HashMap<>();
    private Map<String, String> teacherMap = new HashMap<>();
    private javafx.stage.Stage dialogStage = null;

    @FXML
    private void onQueryButtonClick() {
        DataResponse res;
        DataRequest req = new DataRequest();
        String semester = semesterComboBox.getValue();
        if (semester != null && !semester.isEmpty() && !semester.contains("全部")) {
            req.add("semester", semester);
        }
        res = HttpRequestUtil.request("/api/exam/getExamList", req);
        if (res != null && res.getCode() == 0) {
            examList = (List<Map<String, Object>>) res.getData();
        }
        setTableViewData();
    }

    private void setTableViewData() {
        observableList.clear();
        Map<String, Object> map;
        FlowPane flowPane;
        Button saveButton, deleteButton, violationButton;
        for (int j = 0; j < examList.size(); j++) {
            map = examList.get(j);
            flowPane = new FlowPane();
            flowPane.setHgap(10);
            flowPane.setAlignment(Pos.CENTER);
            saveButton = new Button("修改");
            saveButton.setId("save" + j);
            saveButton.setOnAction(e -> editItem(((Button) e.getSource()).getId()));
            violationButton = new Button("违纪");
            violationButton.setId("violation" + j);
            violationButton.setOnAction(e -> showViolationDialog(((Button) e.getSource()).getId()));
            deleteButton = new Button("删除");
            deleteButton.setId("delete" + j);
            deleteButton.setOnAction(e -> deleteItem(((Button) e.getSource()).getId()));
            flowPane.getChildren().addAll(saveButton, violationButton, deleteButton);
            map.put("operate", flowPane);
            observableList.add(map);
        }
        dataTableView.setItems(observableList);
    }

    private void editItem(String name) {
        if (name == null) return;
        int j = Integer.parseInt(name.substring(4));
        Map<String, Object> data = examList.get(j);
        showEditDialog(data);
    }

    public void deleteItem(String name) {
        if (name == null) return;
        int j = Integer.parseInt(name.substring(6));
        Map<String, Object> data = examList.get(j);
        DataRequest req = new DataRequest();
        if (data.get("examId") != null) {
            req.add("examId", data.get("examId"));
        }
        DataResponse res = HttpRequestUtil.request("/api/exam/examDelete", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("删除成功!");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog("删除失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }

    private void showViolationDialog(String name) {
        if (name == null) return;
        int j = Integer.parseInt(name.substring(9));
        Map<String, Object> data = examList.get(j);
        Integer examId = (Integer) data.get("examId");
        if (examId != null) {
            MessageDialog.showDialog("违纪记录功能开发中，考试ID: " + examId);
        }
    }

    @FXML
    private void onAddButtonClick() {
        showEditDialog(null);
    }

    private void showEditDialog(Map<String, Object> data) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("exam-edit-dialog.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 500, 450);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        dialogStage = new javafx.stage.Stage();
        dialogStage.initOwner(MainApplication.getMainStage());
        dialogStage.setScene(scene);
        dialogStage.setTitle(data == null ? "添加考试安排" : "修改考试安排");
        ExamEditController controller = fxmlLoader.getController();
        controller.setParentController(this);
        controller.initDialog(data);
        dialogStage.showAndWait();
    }

    public void doClose(String cmd, Map<String, Object> resultData) {
        dialogStage.close();
        if ("ok".equals(cmd)) {
            onQueryButtonClick();
        }
    }

    public Map<String, String> getCourseMap() { return courseMap; }
    public Map<String, String> getTeacherMap() { return teacherMap; }

    @FXML
    public void initialize() {
        courseColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        semesterColumn.setCellValueFactory(new MapValueFactory<>("semester"));
        examDateColumn.setCellValueFactory(new MapValueFactory<>("examDate"));
        examTimeColumn.setCellValueFactory(new MapValueFactory<>("examTime"));
        examLocationColumn.setCellValueFactory(new MapValueFactory<>("examLocation"));
        invigilatorColumn.setCellValueFactory(new MapValueFactory<>("invigilatorName"));
        examTypeColumn.setCellValueFactory(new MapValueFactory<>("examType"));
        operateColumn.setCellValueFactory(new MapValueFactory<>("operate"));

        semesterComboBox.getItems().addAll("全部", "2024-1", "2024-2", "2025-1", "2025-2");
        onQueryButtonClick();
    }
}
