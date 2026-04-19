package com.teach.javafx.controller;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.controller.base.MessageDialog;

import java.util.Map;

public class CourseScheduleEditController {
    public ComboBox<String> courseComboBox;
    public ComboBox<String> teacherComboBox;
    public ComboBox<String> classComboBox;
    public TextField classroomField;
    public ComboBox<String> dayOfWeekComboBox;
    public ComboBox<String> classPeriodComboBox;
    public TextField weekRangeField;
    public ComboBox<String> semesterComboBox;
    public TextField remarkField;

    private CourseScheduleController parentController;
    private Map<String, Object> editData;
    private Map<String, String> courseMap;
    private Map<String, String> teacherMap;
    private Map<String, String> classMap;

    public void setParentController(CourseScheduleController parentController) {
        this.parentController = parentController;
        this.courseMap = parentController.getCourseMap();
        this.teacherMap = parentController.getTeacherMap();
        this.classMap = parentController.getClassMap();
    }

    public void initDialog(Map<String, Object> data) {
        this.editData = data;

        courseComboBox.getItems().clear();
        teacherComboBox.getItems().clear();
        classComboBox.getItems().clear();
        dayOfWeekComboBox.getItems().clear();
        classPeriodComboBox.getItems().clear();
        semesterComboBox.getItems().clear();

        courseComboBox.getItems().addAll(courseMap.keySet());
        teacherComboBox.getItems().addAll(teacherMap.keySet());
        classComboBox.getItems().addAll(classMap.keySet());

        dayOfWeekComboBox.getItems().addAll("周一", "周二", "周三", "周四", "周五", "周六", "周日");
        classPeriodComboBox.getItems().addAll("1-2节", "3-4节", "5-6节", "7-8节", "9-10节");
        semesterComboBox.getItems().addAll("2024-1", "2024-2", "2025-1", "2025-2");

        if (data != null) {
            String courseName = (String) data.get("courseName");
            String teacherName = (String) data.get("teacherName");
            String className = (String) data.get("className");

            if (courseName != null) courseComboBox.setValue(courseName);
            if (teacherName != null) teacherComboBox.setValue(teacherName);
            if (className != null) classComboBox.setValue(className);
            classroomField.setText((String) data.get("classroom"));
            dayOfWeekComboBox.setValue((String) data.get("dayOfWeek"));
            classPeriodComboBox.setValue((String) data.get("classPeriod"));
            weekRangeField.setText((String) data.get("weekRange"));
            semesterComboBox.setValue((String) data.get("semester"));
            remarkField.setText((String) data.get("remark"));
        } else {
            semesterComboBox.setValue("2024-1");
        }
    }

    public void onSaveButtonClick() {
        String courseName = courseComboBox.getValue();
        String teacherName = teacherComboBox.getValue();
        String className = classComboBox.getValue();
        String classroom = classroomField.getText();
        String dayOfWeek = dayOfWeekComboBox.getValue();
        String classPeriod = classPeriodComboBox.getValue();
        String weekRange = weekRangeField.getText();
        String semester = semesterComboBox.getValue();
        String remark = remarkField.getText();

        if (courseName == null || teacherName == null || className == null ||
            classroom == null || dayOfWeek == null || classPeriod == null || semester == null) {
            MessageDialog.showDialog("请填写所有必填项");
            return;
        }

        DataRequest req = new DataRequest();
        if (editData != null && editData.get("scheduleId") != null) {
            req.add("scheduleId", editData.get("scheduleId"));
        }
        req.add("courseId", courseMap.get(courseName));
        req.add("teacherId", teacherMap.get(teacherName));
        req.add("classId", classMap.get(className));
        req.add("classroom", classroom);
        req.add("dayOfWeek", dayOfWeek);
        req.add("classPeriod", classPeriod);
        req.add("weekRange", weekRange);
        req.add("semester", semester);
        req.add("remark", remark);

        DataResponse res = HttpRequestUtil.request("/api/courseSchedule/scheduleSave", req);
        if (res != null && res.getCode() == 0) {
            parentController.doClose("ok", null);
        } else {
            MessageDialog.showDialog("保存失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }

    public void onCancelButtonClick() {
        parentController.doClose("cancel", null);
    }
}
