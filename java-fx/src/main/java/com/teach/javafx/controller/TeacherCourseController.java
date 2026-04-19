package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
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
import java.util.List;
import java.util.Map;

/**
 * TeacherCourseController 教师我的课程控制器
 * 显示教师教授的课程列表
 */
public class TeacherCourseController {

    @FXML
    private TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map, String> courseNumColumn;

    @FXML
    private TableColumn<Map, String> courseNameColumn;

    @FXML
    private TableColumn<Map, String> creditColumn;

    @FXML
    private TableColumn<Map, String> studentCountColumn;

    @FXML
    private TableColumn<Map, String> semesterColumn;

    @FXML
    private Label totalCoursesLabel;

    private ArrayList<Map> courseList = new ArrayList<>();

    @FXML
    public void initialize() {
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("num"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        studentCountColumn.setCellValueFactory(new MapValueFactory<>("studentCount"));
        semesterColumn.setCellValueFactory(new MapValueFactory<>("semester"));

        loadCourseData();
    }

    private void loadCourseData() {
        try {
            DataRequest req = new DataRequest();
            DataResponse res = HttpRequestUtil.request("/api/course/getCourseList", req);

            if (res != null && res.getCode() == 0) {
                courseList = (ArrayList<Map>) res.getData();
                updateTable();
            } else {
                MessageDialog.showDialog("加载课程列表失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("加载数据失败: " + e.getMessage());
        }
    }

    private void updateTable() {
        dataTableView.getItems().clear();
        for (Map<String, Object> course : courseList) {
            // 添加默认选课人数
            if (!course.containsKey("studentCount")) {
                course.put("studentCount", "--");
            }
            // 添加默认学期
            if (!course.containsKey("semester")) {
                course.put("semester", "2024-1");
            }
            dataTableView.getItems().add(course);
        }
        totalCoursesLabel.setText(courseList.size() + " 门课程");
    }

    @FXML
    protected void onRefreshButtonClick() {
        loadCourseData();
    }

    public void doRefresh() {
        loadCourseData();
    }
}
