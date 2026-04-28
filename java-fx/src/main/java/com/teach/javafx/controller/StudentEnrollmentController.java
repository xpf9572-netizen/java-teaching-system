package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentEnrollmentController 学生选课控制器
 * 学生查看可选课程、已选课程，并进行选课和退选操作
 */
public class StudentEnrollmentController {

    @FXML
    private TableView<Map> availableCourseTableView;

    @FXML
    private TableView<Map> myEnrollmentTableView;

    @FXML
    private TableColumn<Map, String> courseNumColumn;

    @FXML
    private TableColumn<Map, String> courseNameColumn;

    @FXML
    private TableColumn<Map, String> creditColumn;

    @FXML
    private TableColumn<Map, String> teacherNameColumn;

    @FXML
    private TableColumn<Map, String> enrollCourseNameColumn;

    @FXML
    private TableColumn<Map, String> semesterColumn;

    @FXML
    private TableColumn<Map, String> enrollCreditColumn;

    @FXML
    private TableColumn<Map, String> gradeColumn;

    @FXML
    private Button enrollButton;

    @FXML
    private Button dropButton;

    private ArrayList<Map> availableCourseList = new ArrayList<>();
    private ArrayList<Map> myEnrollmentList = new ArrayList<>();

    @FXML
    public void initialize() {
        // Set cell value factories for available courses table
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        teacherNameColumn.setCellValueFactory(new MapValueFactory<>("teacherName"));

        // Set cell value factories for my enrollments table
        enrollCourseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        semesterColumn.setCellValueFactory(new MapValueFactory<>("semester"));
        enrollCreditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        gradeColumn.setCellValueFactory(new MapValueFactory<>("mark"));

        loadData();
    }

    private void loadData() {
        loadAvailableCourses();
        loadMyEnrollments();
    }

    /**
     * Query available courses from the server
     */
    private void loadAvailableCourses() {
        try {
            DataRequest req = new DataRequest();
            DataResponse res = HttpRequestUtil.request("/api/course/getCourseList", req);

            if (res != null && res.getCode() == 0) {
                availableCourseList.clear();
                ArrayList<Map> courseList = (ArrayList<Map>) res.getData();
                if (courseList != null) {
                    for (Map<String, Object> course : courseList) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("courseId", course.get("courseId"));
                        map.put("courseNum", course.get("courseNum"));
                        map.put("courseName", course.get("courseName"));
                        map.put("credit", course.get("credit"));
                        map.put("teacherName", course.get("teacherName"));
                        availableCourseList.add(map);
                    }
                }
                setAvailableCourseTableData();
            } else {
                MessageDialog.showDialog("获取可选课程失败: " + (res != null ? res.getMsg() : "未知错误"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("获取可选课程失败: " + e.getMessage());
        }
    }

    /**
     * Query my enrollments from the server
     */
    private void loadMyEnrollments() {
        try {
            Integer studentId = AppStore.getJwt().getId();
            DataRequest req = new DataRequest();
            req.add("studentId", studentId);
            DataResponse res = HttpRequestUtil.request("/api/enrollments/my", req);

            if (res != null && res.getCode() == 0) {
                myEnrollmentList.clear();
                ArrayList<Map> enrollmentList = (ArrayList<Map>) res.getData();
                if (enrollmentList != null) {
                    for (Map<String, Object> enrollment : enrollmentList) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("enrollmentId", enrollment.get("enrollmentId"));
                        map.put("courseId", enrollment.get("courseId"));
                        map.put("courseName", enrollment.get("courseName"));
                        map.put("semester", enrollment.get("semester"));
                        map.put("credit", enrollment.get("credit"));

                        Object scoreObj = enrollment.get("score");
                        String scoreStr = scoreObj != null ? String.valueOf(((Number) scoreObj).intValue()) : "--";
                        map.put("mark", scoreStr);

                        myEnrollmentList.add(map);
                    }
                }
                setMyEnrollmentTableData();
            } else {
                MessageDialog.showDialog("获取已选课程失败: " + (res != null ? res.getMsg() : "未知错误"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("获取已选课程失败: " + e.getMessage());
        }
    }

    private void setAvailableCourseTableData() {
        availableCourseTableView.getItems().clear();
        availableCourseTableView.getItems().addAll(availableCourseList);
    }

    private void setMyEnrollmentTableData() {
        myEnrollmentTableView.getItems().clear();
        myEnrollmentTableView.getItems().addAll(myEnrollmentList);
    }

    /**
     * Handle enroll button click - enroll in selected course
     */
    @FXML
    protected void onEnrollButtonClick() {
        Map selected = availableCourseTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("请选择要选修的课程");
            return;
        }

        try {
            Integer studentId = AppStore.getJwt().getId();
            Integer courseId = (Integer) selected.get("courseId");
            String semester = "2025-1"; // Default semester

            DataRequest req = new DataRequest();
            req.add("studentId", studentId);
            req.add("courseId", courseId);
            req.add("semester", semester);

            DataResponse res = HttpRequestUtil.request("/api/enrollments/save", req);

            if (res != null && res.getCode() == 0) {
                MessageDialog.showDialog("选课成功!");
                loadData(); // Refresh both tables
            } else {
                MessageDialog.showDialog("选课失败: " + (res != null ? res.getMsg() : "未知错误"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("选课失败: " + e.getMessage());
        }
    }

    /**
     * Handle drop button click - drop selected enrollment
     */
    @FXML
    protected void onDropButtonClick() {
        Map selected = myEnrollmentTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("请选择要退选的课程");
            return;
        }

        if (MessageDialog.choiceDialog("确认要退选该课程吗?") != MessageDialog.CHOICE_YES) {
            return;
        }

        try {
            Integer enrollmentId = (Integer) selected.get("enrollmentId");
            DataRequest req = new DataRequest();
            req.add("enrollmentId", enrollmentId);

            DataResponse res = HttpRequestUtil.request("/api/enrollments/delete", req);

            if (res != null && res.getCode() == 0) {
                MessageDialog.showDialog("退选成功!");
                loadData(); // Refresh both tables
            } else {
                MessageDialog.showDialog("退选失败: " + (res != null ? res.getMsg() : "未知错误"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("退选失败: " + e.getMessage());
        }
    }

    /**
     * Handle refresh button click - reload all data
     */
    @FXML
    protected void onRefreshButtonClick() {
        loadData();
    }

    public void doRefresh() {
        loadData();
    }
}
