package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TeacherScoreController 教师成绩录入控制器
 * 教师查看课程学生列表并录入成绩
 */
public class TeacherScoreController {

    @FXML
    private ComboBox<OptionItem> courseComboBox;

    @FXML
    private TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map, String> studentNumColumn;

    @FXML
    private TableColumn<Map, String> studentNameColumn;

    @FXML
    private TableColumn<Map, String> classNameColumn;

    @FXML
    private TableColumn<Map, String> markColumn;

    @FXML
    private Label courseNameLabel;

    @FXML
    private Label avgScoreLabel;

    @FXML
    private Label totalStudentsLabel;

    private List<OptionItem> courseList = new ArrayList<>();
    private ArrayList<Map> scoreList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 设置表格列
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));

        // 可编辑成绩列
        markColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        markColumn.setOnEditCommit(event -> {
            Map<String, Object> row = event.getRowValue();
            String newMark = event.getNewValue();
            row.put("mark", newMark);
            row.put("modified", true);
        });

        // 加载课程列表
        loadCourses();

        // 表格选择监听
        dataTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // 可以在这里处理行选择
            }
        });
    }

    private void loadCourses() {
        try {
            DataRequest req = new DataRequest();
            DataResponse res = HttpRequestUtil.request("/api/course/getCourseList", req);

            if (res != null && res.getCode() == 0) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) res.getData();
                courseComboBox.getItems().clear();

                for (Map<String, Object> course : list) {
                    String courseId = (String) course.get("courseId");
                    String courseName = (String) course.get("name");
                    courseComboBox.getItems().add(new OptionItem(
                            Integer.parseInt(courseId),
                            courseId,
                            courseName
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("加载课程列表失败: " + e.getMessage());
        }
    }

    @FXML
    protected void onCourseSelected() {
        OptionItem selectedCourse = courseComboBox.getValue();
        if (selectedCourse != null) {
            loadStudentsByCourse(selectedCourse.getValue());
        }
    }

    private void loadStudentsByCourse(String courseId) {
        try {
            // 这里需要后端API支持按课程ID查询学生成绩
            // 暂时显示提示
            DataRequest req = new DataRequest();
            req.add("courseId", Integer.parseInt(courseId));
            DataResponse res = HttpRequestUtil.request("/api/score/getScoreList", req);

            if (res != null && res.getCode() == 0) {
                scoreList = (ArrayList<Map>) res.getData();
                updateTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("加载学生列表失败: " + e.getMessage());
        }
    }

    private void updateTable() {
        observableList.clear();
        for (Map<String, Object> score : scoreList) {
            Map<String, Object> displayMap = new HashMap<>(score);
            Object markObj = score.get("mark");
            displayMap.put("mark", markObj != null ? String.valueOf(((Number) markObj).intValue()) : "");
            displayMap.put("modified", false);
            observableList.add(displayMap);
        }
        dataTableView.setItems(observableList);

        // 更新统计
        totalStudentsLabel.setText(scoreList.size() + " 人");
        calculateAverage();
    }

    private void calculateAverage() {
        double total = 0;
        int count = 0;
        for (Map<String, Object> score : scoreList) {
            Object markObj = score.get("mark");
            if (markObj != null) {
                try {
                    total += ((Number) markObj).doubleValue();
                    count++;
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        if (count > 0) {
            avgScoreLabel.setText(String.format("%.1f", total / count));
        } else {
            avgScoreLabel.setText("--");
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        OptionItem selectedCourse = courseComboBox.getValue();
        if (selectedCourse == null) {
            MessageDialog.showDialog("请先选择课程");
            return;
        }

        int savedCount = 0;
        for (Map<String, Object> row : observableList) {
            if (Boolean.TRUE.equals(row.get("modified"))) {
                // 保存修改的成绩
                try {
                    DataRequest req = new DataRequest();
                    req.add("courseId", Integer.parseInt(selectedCourse.getValue()));
                    req.add("personId", ((Number) row.get("personId")).intValue());
                    req.add("mark", Integer.parseInt((String) row.get("mark")));

                    DataResponse res = HttpRequestUtil.request("/api/score/scoreSave", req);
                    if (res != null && res.getCode() == 0) {
                        savedCount++;
                        row.put("modified", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        MessageDialog.showDialog("保存成功！已保存 " + savedCount + " 条成绩");
    }

    @FXML
    protected void onRefreshButtonClick() {
        onCourseSelected();
    }
}
