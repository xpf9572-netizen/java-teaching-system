package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentLeaveAddDialogController 新增请假对话框控制器
 */
public class StudentLeaveAddDialogController {

    @FXML
    private ComboBox<OptionItem> teacherComboBox;

    @FXML
    private TextField leaveDateField;

    @FXML
    private TextArea reasonArea;

    private StudentLeaveController parentController;
    private List<OptionItem> teacherList;

    /**
     * 设置父控制器
     */
    public void setParentController(StudentLeaveController controller) {
        this.parentController = controller;
    }

    /**
     * 初始化对话框数据
     */
    public void init() {
        DataRequest req = new DataRequest();
        teacherList = HttpRequestUtil.requestOptionItemList("/api/studentLeave/getTeacherItemOptionList", req);
        if (teacherList != null) {
            teacherComboBox.getItems().clear();
            teacherComboBox.getItems().addAll(teacherList);
        }
        // 设置默认日期为今天
        leaveDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @FXML
    public void initialize() {
    }

    /**
     * 确定按钮点击事件
     */
    @FXML
    public void onOkButtonClick() {
        // 验证教师选择
        OptionItem teacherItem = teacherComboBox.getValue();
        if (teacherItem == null) {
            MessageDialog.showDialog("请选择指导老师");
            return;
        }

        // 验证日期
        String leaveDate = leaveDateField.getText();
        if (leaveDate == null || leaveDate.trim().isEmpty()) {
            MessageDialog.showDialog("请输入请假日期");
            return;
        }
        // 简单日期格式验证
        if (!leaveDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            MessageDialog.showDialog("日期格式不正确，请使用yyyy-MM-dd格式");
            return;
        }

        // 验证原因
        String reason = reasonArea.getText();
        if (reason == null || reason.trim().isEmpty()) {
            MessageDialog.showDialog("请输入请假原因");
            return;
        }

        // 构建请求数据 - 直接放在顶层，不使用form嵌套
        DataRequest req = new DataRequest();
        req.add("teacherId", Integer.parseInt(teacherItem.getValue()));
        req.add("leaveDate", leaveDate);
        req.add("reason", reason);

        // 发送保存请求
        DataResponse res = HttpRequestUtil.request("/api/studentLeave/studentLeaveSave", req);
        if (res != null && res.getCode() == 0) {
            parentController.doClose("ok", null);
        } else {
            MessageDialog.showDialog("保存失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }

    /**
     * 取消按钮点击事件
     */
    @FXML
    public void onCancelButtonClick() {
        parentController.doClose("cancel", null);
    }
}
