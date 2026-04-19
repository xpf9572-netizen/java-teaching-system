package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentAttendanceController 学生考勤查询控制器
 * 学生查看自己的考勤记录列表
 */
public class StudentAttendanceController {

    @FXML
    private TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map, String> dateColumn;

    @FXML
    private TableColumn<Map, String> courseNameColumn;

    @FXML
    private TableColumn<Map, String> statusColumn;

    @FXML
    private TableColumn<Map, String> remarkColumn;

    @FXML
    private javafx.scene.control.DatePicker startDatePicker;

    @FXML
    private javafx.scene.control.DatePicker endDatePicker;

    @FXML
    private Label presentCountLabel;

    @FXML
    private Label abnormalCountLabel;

    private ArrayList<Map> attendanceList = new ArrayList<>();

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new MapValueFactory<>("attendanceDate"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));
        remarkColumn.setCellValueFactory(new MapValueFactory<>("remark"));

        onQueryButtonClick();
    }

    @FXML
    protected void onQueryButtonClick() {
        try {
            DataRequest req = new DataRequest();

            if (startDatePicker.getValue() != null) {
                req.add("startDate", startDatePicker.getValue().toString());
            }
            if (endDatePicker.getValue() != null) {
                req.add("endDate", endDatePicker.getValue().toString());
            }

            DataResponse res = HttpRequestUtil.request("/api/attendance/getStudentAttendanceList", req);

            if (res != null && res.getCode() == 0) {
                attendanceList = (ArrayList<Map>) res.getData();

                // Update statistics
                updateStatistics();

                // Update table
                setTableViewData();
            } else {
                MessageDialog.showDialog("获取考勤记录失败: " + res.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("查询失败: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        int presentCount = 0;
        int abnormalCount = 0;

        if (attendanceList != null) {
            for (Map<String, Object> attendance : attendanceList) {
                String status = (String) attendance.get("status");
                if (status != null) {
                    if ("PRESENT".equals(status) || "正常".equals(status)) {
                        presentCount++;
                    } else {
                        abnormalCount++;
                    }
                }
            }
        }

        presentCountLabel.setText(String.valueOf(presentCount));
        abnormalCountLabel.setText(String.valueOf(abnormalCount));
    }

    private void setTableViewData() {
        dataTableView.getItems().clear();
        for (Map<String, Object> attendance : attendanceList) {
            Map<String, Object> displayMap = new HashMap<>();
            displayMap.put("attendanceDate", attendance.get("attendanceDate"));

            String courseName = (String) attendance.get("courseName");
            displayMap.put("courseName", courseName != null ? courseName : "");

            // Convert status to Chinese
            String status = (String) attendance.get("status");
            String statusChinese = convertStatusToChinese(status);
            displayMap.put("status", statusChinese);

            displayMap.put("remark", attendance.get("remark"));
            dataTableView.getItems().add(displayMap);
        }
    }

    private String convertStatusToChinese(String status) {
        if (status == null) return "";
        return switch (status) {
            case "PRESENT", "正常" -> "正常";
            case "ABSENT", "缺勤" -> "缺勤";
            case "LATE", "迟到" -> "迟到";
            case "LEAVE", "请假" -> "请假";
            default -> status;
        };
    }

    @FXML
    protected void onRefreshButtonClick() {
        onQueryButtonClick();
    }

    public void doRefresh() {
        onQueryButtonClick();
    }
}
