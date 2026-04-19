package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentScheduleController 学生课表查看控制器
 * 学生查看自己的课程表
 */
public class StudentScheduleController {

    @FXML
    private TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map, String> dayOfWeekColumn;

    @FXML
    private TableColumn<Map, String> classPeriodColumn;

    @FXML
    private TableColumn<Map, String> courseNameColumn;

    @FXML
    private TableColumn<Map, String> courseNumColumn;

    @FXML
    private TableColumn<Map, String> teacherNameColumn;

    @FXML
    private TableColumn<Map, String> locationColumn;

    @FXML
    private TableColumn<Map, String> creditColumn;

    @FXML
    private ComboBox<String> semesterComboBox;

    private ArrayList<Map> scheduleList = new ArrayList<>();
    private ArrayList<String> semesterList = new ArrayList<>();

    @FXML
    public void initialize() {
        dayOfWeekColumn.setCellValueFactory(new MapValueFactory<>("dayOfWeek"));
        classPeriodColumn.setCellValueFactory(new MapValueFactory<>("classPeriod"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        teacherNameColumn.setCellValueFactory(new MapValueFactory<>("teacherName"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));

        onQueryButtonClick();
    }

    @FXML
    protected void onQueryButtonClick() {
        try {
            DataRequest req = new DataRequest();
            DataResponse res = HttpRequestUtil.request("/api/schedule/getStudentSchedule", req);

            if (res != null && res.getCode() == 0) {
                Map<String, Object> data = (Map<String, Object>) res.getData();

                // 更新学期下拉框
                List<String> semesters = (List<String>) data.get("semesterList");
                if (semesters != null) {
                    semesterList.clear();
                    semesterList.addAll(semesters);
                    semesterComboBox.getItems().clear();
                    semesterComboBox.getItems().addAll(semesters);
                    if (!semesters.isEmpty()) {
                        semesterComboBox.getSelectionModel().select(0);
                    }
                }

                // 更新课表数据
                List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("scheduleList");
                if (list != null) {
                    scheduleList.clear();
                    scheduleList.addAll(list);
                }

                setTableViewData();
            } else {
                MessageDialog.showDialog("获取课表失败: " + (res != null ? res.getMsg() : "未知错误"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("查询失败: " + e.getMessage());
        }
    }

    @FXML
    protected void onSemesterSelected() {
        String selectedSemester = semesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null || "全部学期".equals(selectedSemester)) {
            setTableViewData();
            return;
        }

        try {
            DataRequest req = new DataRequest();
            req.add("semester", selectedSemester);
            DataResponse res = HttpRequestUtil.request("/api/schedule/getScheduleBySemester", req);

            if (res != null && res.getCode() == 0) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) res.getData();
                if (list != null) {
                    scheduleList.clear();
                    for (Map<String, Object> item : list) {
                        scheduleList.add(new HashMap<>(item));
                    }
                }
                setTableViewData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("筛选失败: " + e.getMessage());
        }
    }

    private void setTableViewData() {
        dataTableView.getItems().clear();
        for (Map<String, Object> schedule : scheduleList) {
            Map<String, Object> displayMap = new HashMap<>();

            String dayOfWeek = (String) schedule.get("dayOfWeek");
            displayMap.put("dayOfWeek", dayOfWeek != null && !dayOfWeek.isEmpty() ? dayOfWeek : "--");

            String classPeriod = (String) schedule.get("classPeriod");
            displayMap.put("classPeriod", classPeriod != null && !classPeriod.isEmpty() ? classPeriod : "--");

            displayMap.put("courseName", schedule.get("courseName"));
            displayMap.put("courseNum", schedule.get("courseNum"));
            displayMap.put("teacherName", schedule.get("teacherName"));

            String location = (String) schedule.get("location");
            displayMap.put("location", location != null && !location.isEmpty() ? location : "--");

            Object creditObj = schedule.get("credit");
            String creditStr = creditObj != null ? String.valueOf(creditObj) : "--";
            displayMap.put("credit", creditStr);

            dataTableView.getItems().add(displayMap);
        }
    }

    public void doRefresh() {
        onQueryButtonClick();
    }
}
