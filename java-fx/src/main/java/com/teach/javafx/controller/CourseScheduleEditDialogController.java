package com.teach.javafx.controller;

import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.util.List;
import java.util.Map;

public class CourseScheduleEditDialogController {

    @FXML private ComboBox<String> courseComboBox;
    @FXML private ComboBox<String> teacherComboBox;
    @FXML private ComboBox<String> classComboBox;
    @FXML private TextField classroomField;
    @FXML private ComboBox<String> dayOfWeekComboBox;
    @FXML private ComboBox<String> classPeriodComboBox;
    @FXML private TextField weekRangeField;
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private TextField remarkField;

    @FXML
    public void initialize() {
        loadTeachers();
        loadCourses();
    }

    private void loadTeachers() {
        try {
            DataResponse response = HttpRequestUtil.get("/api/courseSchedule/teacherList");
            if (response.getCode() == 0) {
                List<Map<String, Object>> teachers = (List<Map<String, Object>>) response.getData();
                for (Map<String, Object> teacher : teachers) {
                    // 老师名字在 person 对象里面
                    Map<String, Object> person = (Map<String, Object>) teacher.get("person");
                    String name = (String) person.get("name");
                    teacherComboBox.getItems().add(name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadCourses() {
        try {
            DataResponse response = HttpRequestUtil.get("/api/courseSchedule/courseList");
            if (response.getCode() == 0) {
                List<Map<String, Object>> courses = (List<Map<String, Object>>) response.getData();
                for (Map<String, Object> course : courses) {
                    String name = (String) course.get("name");
                    courseComboBox.getItems().add(name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSaveButtonClick() {
        System.out.println("保存");
        closeWindow();
    }

    @FXML
    private void onCancelButtonClick() {
        System.out.println("取消");
        closeWindow();
    }

    private void closeWindow() {
        teacherComboBox.getScene().getWindow().hide();
    }
}