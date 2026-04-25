package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.util.List;
import java.util.Map;

public class ViolationEditController {
    @FXML
    private Label examInfoLabel;
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    @FXML
    private ComboBox<String> violationTypeComboBox;
    @FXML
    private TextField violationDescField;
    @FXML
    private TextField punishmentField;
    @FXML
    private TextField remarkField;

    private ExamManageController parentController;
    private Integer examId;

    public void setParentController(ExamManageController parentController) {
        this.parentController = parentController;
    }

    public void initDialog(Integer examId, String examInfo) {
        this.examId = examId;
        examInfoLabel.setText("考试: " + examInfo);

        // 加载学生列表
        DataRequest req = new DataRequest();
        List<OptionItem> studentList = HttpRequestUtil.requestOptionItemList("/api/score/getStudentItemOptionList", req);
        if (studentList != null) {
            studentComboBox.getItems().addAll(studentList);
        }

        violationTypeComboBox.getItems().addAll("作弊", "抄袭", "携带通讯设备", "交头接耳", "未按时交卷", "其他");
        violationTypeComboBox.setValue("作弊");
    }

    @FXML
    private void onSaveButtonClick() {
        OptionItem studentItem = studentComboBox.getValue();
        String violationType = violationTypeComboBox.getValue();

        if (studentItem == null) {
            MessageDialog.showDialog("请选择学生");
            return;
        }
        if (violationType == null || violationType.isEmpty()) {
            MessageDialog.showDialog("请选择违纪类型");
            return;
        }

        DataRequest req = new DataRequest();
        req.add("examId", examId);
        req.add("studentId", Integer.parseInt(studentItem.getValue()));
        req.add("violationType", violationType);
        req.add("violationDesc", violationDescField.getText());
        req.add("punishment", punishmentField.getText());
        req.add("remark", remarkField.getText());

        DataResponse res = HttpRequestUtil.request("/api/exam/violationSave", req);
        if (res != null && res.getCode() == 0) {
            parentController.doClose("ok", null);
        } else {
            MessageDialog.showDialog("保存失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }

    @FXML
    private void onCancelButtonClick() {
        parentController.doClose("cancel", null);
    }
}
