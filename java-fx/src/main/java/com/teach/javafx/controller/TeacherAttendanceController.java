package com.teach.javafx.controller;

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
 * TeacherAttendanceController 教师考勤管理控制器
 * 教师管理课程学生考勤
 */
public class TeacherAttendanceController {

    @FXML
    private ComboBox<OptionItem> courseComboBox;

    @FXML
    private DatePicker attendanceDatePicker;

    @FXML
    private TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map, String> studentNumColumn;

    @FXML
    private TableColumn<Map, String> studentNameColumn;

    @FXML
    private TableColumn<Map, String> classNameColumn;

    @FXML
    private TableColumn<Map, String> statusColumn;

    @FXML
    private TableColumn<Map, String> remarkColumn;

    @FXML
    private ComboBox<OptionItem> statusComboBox;

    @FXML
    private Label totalStudentsLabel;

    private List<OptionItem> courseList = new ArrayList<>();
    private ArrayList<Map> attendanceList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 设置表格列
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));
        remarkColumn.setCellValueFactory(new MapValueFactory<>("remark"));

        // 可编辑备注列
        remarkColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        remarkColumn.setOnEditCommit(event -> {
            Map<String, Object> row = event.getRowValue();
            row.put("remark", event.getNewValue());
            row.put("modified", true);
        });

        // 考勤状态下拉框
        statusComboBox.getItems().addAll(
                new OptionItem(1, "PRESENT", "出勤"),
                new OptionItem(2, "ABSENT", "缺勤"),
                new OptionItem(3, "LATE", "迟到"),
                new OptionItem(4, "LEAVE", "请假")
        );

        // 加载课程列表
        loadCourses();
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
            loadAttendanceData();
        }
    }

    private void loadAttendanceData() {
        try {
            DataRequest req = new DataRequest();
            OptionItem selectedCourse = courseComboBox.getValue();
            if (selectedCourse != null) {
                req.add("courseId", Integer.parseInt(selectedCourse.getValue()));
            }

            DataResponse res = HttpRequestUtil.request("/api/attendance/getAttendanceList", req);

            if (res != null && res.getCode() == 0) {
                attendanceList = (ArrayList<Map>) res.getData();
                updateTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 如果没有考勤数据，显示提示
            dataTableView.getItems().clear();
            totalStudentsLabel.setText("0 人");
        }
    }

    private void updateTable() {
        observableList.clear();
        for (Map<String, Object> attendance : attendanceList) {
            Map<String, Object> displayMap = new HashMap<>(attendance);
            displayMap.put("modified", false);
            observableList.add(displayMap);
        }
        dataTableView.setItems(observableList);
        totalStudentsLabel.setText(attendanceList.size() + " 人");
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
                try {
                    DataRequest req = new DataRequest();
                    req.add("courseId", Integer.parseInt(selectedCourse.getValue()));
                    req.add("studentId", ((Number) row.get("studentId")).intValue());
                    if (attendanceDatePicker.getValue() != null) {
                        req.add("attendanceDate", attendanceDatePicker.getValue().toString());
                    }
                    if (statusComboBox.getValue() != null) {
                        req.add("status", statusComboBox.getValue().getValue());
                    }
                    req.add("remark", row.get("remark"));

                    DataResponse res = HttpRequestUtil.request("/api/attendance/saveAttendance", req);
                    if (res != null && res.getCode() == 0) {
                        savedCount++;
                        row.put("modified", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        MessageDialog.showDialog("保存成功！已保存 " + savedCount + " 条记录");
    }

    @FXML
    protected void onRefreshButtonClick() {
        loadAttendanceData();
    }
}
