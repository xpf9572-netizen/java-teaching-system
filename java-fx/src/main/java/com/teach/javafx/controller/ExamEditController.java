package com.teach.javafx.controller;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.controller.base.MessageDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExamEditController {
    public ComboBox<String> courseComboBox;
    public TextField examDateField;
    public TextField examTimeField;
    public TextField examLocationField;
    public ComboBox<String> invigilatorComboBox;
    public ComboBox<String> examTypeComboBox;
    public TextField totalStudentsField;
    public TextField remarkField;
    public ComboBox<String> semesterComboBox;

    private ExamManageController parentController;
    private Map<String, Object> editData;
    private Map<String, String> courseMap = new java.util.HashMap<>();
    private Map<String, String> teacherMap = new java.util.HashMap<>();

    public void setParentController(ExamManageController parentController) {
        this.parentController = parentController;
    }

    public void initDialog(Map<String, Object> data) {
        this.editData = data;

        courseComboBox.getItems().clear();
        invigilatorComboBox.getItems().clear();
        examTypeComboBox.getItems().clear();
        semesterComboBox.getItems().clear();

        List<OptionItem> courseOptions = HttpRequestUtil.requestOptionItemList("/api/course/getCourseOptionList", new DataRequest());
        if (courseOptions != null) {
            for (OptionItem item : courseOptions) {
                courseComboBox.getItems().add(item.getTitle());
                courseMap.put(item.getTitle(), item.getValue());
            }
        }

        List<OptionItem> teacherOptions = HttpRequestUtil.requestOptionItemList("/api/teachers/getTeacherOptionList", new DataRequest());
        if (teacherOptions != null) {
            for (OptionItem item : teacherOptions) {
                invigilatorComboBox.getItems().add(item.getTitle());
                teacherMap.put(item.getTitle(), item.getValue());
            }
        }

        examTypeComboBox.getItems().addAll("FINAL", "MIDTERM", "MAKEUP");
        semesterComboBox.getItems().addAll("2024-1", "2024-2", "2025-1", "2025-2");

        if (data != null) {
            String courseName = (String) data.get("courseName");
            String invigilatorName = (String) data.get("invigilatorName");
            String examType = (String) data.get("examType");
            String semester = (String) data.get("semester");

            if (courseName != null) courseComboBox.setValue(courseName);
            if (invigilatorName != null) invigilatorComboBox.setValue(invigilatorName);
            if (examType != null) examTypeComboBox.setValue(examType);
            if (semester != null) semesterComboBox.setValue(semester);

            examDateField.setText(data.get("examDate") != null ? data.get("examDate").toString().split("T")[0] : "");
            examTimeField.setText((String) data.get("examTime"));
            examLocationField.setText((String) data.get("examLocation"));
            totalStudentsField.setText(data.get("totalStudents") != null ? data.get("totalStudents").toString() : "");
            remarkField.setText((String) data.get("remark"));
        } else {
            semesterComboBox.setValue("2024-1");
            examDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }
    }

    public void onSaveButtonClick() {
        String courseName = courseComboBox.getValue();
        String examDate = examDateField.getText();
        String examTime = examTimeField.getText();
        String examLocation = examLocationField.getText();
        String invigilatorName = invigilatorComboBox.getValue();
        String examType = examTypeComboBox.getValue();
        String totalStudentsStr = totalStudentsField.getText();
        String semester = semesterComboBox.getValue();
        String remark = remarkField.getText();

        if (courseName == null || examDate == null || examTime == null || examLocation == null || semester == null) {
            MessageDialog.showDialog("请填写所有必填项");
            return;
        }

        DataRequest req = new DataRequest();
        if (editData != null && editData.get("examId") != null) {
            req.add("examId", editData.get("examId"));
        }
        req.add("courseId", courseMap.get(courseName));
        req.add("examDate", examDate);
        req.add("examTime", examTime);
        req.add("examLocation", examLocation);
        req.add("invigilatorId", teacherMap.get(invigilatorName));
        req.add("examType", examType != null ? examType : "FINAL");
        req.add("semester", semester);
        req.add("totalStudents", totalStudentsStr != null && !totalStudentsStr.isEmpty() ? Integer.parseInt(totalStudentsStr) : 0);
        req.add("remark", remark);

        DataResponse res = HttpRequestUtil.request("/api/exam/examSave", req);
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
