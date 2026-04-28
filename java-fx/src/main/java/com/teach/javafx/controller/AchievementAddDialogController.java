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

import java.util.HashMap;
import java.util.Map;

public class AchievementAddDialogController {
    @FXML private ComboBox<OptionItem> typeComboBox;
    @FXML private TextField nameField;
    @FXML private ComboBox<OptionItem> levelComboBox;
    @FXML private TextField awardDateField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField certificateUrlField;

    private StudentAchievementController parentController;
    private Map<String, Object> editData;

    public void setParentController(StudentAchievementController controller) {
        this.parentController = controller;
    }

    public void setEditData(Map<String, Object> data) {
        this.editData = data;
    }

    @FXML
    public void initialize() {
        // 成就类型
        typeComboBox.getItems().addAll(
            new OptionItem(1, "COMPETITION", "竞赛获奖"),
            new OptionItem(2, "PUBLICATION", "科研论文"),
            new OptionItem(3, "PATENT", "专利"),
            new OptionItem(4, "PROJECT", "项目经历")
        );

        // 级别
        levelComboBox.getItems().addAll(
            new OptionItem(1, "国家级", "国家级"),
            new OptionItem(2, "省级", "省级"),
            new OptionItem(3, "校级", "校级"),
            new OptionItem(4, "院级", "院级")
        );

        if (editData != null) {
            // 填充编辑数据
            String type = (String) editData.get("type");
            if (type != null) {
                for (OptionItem item : typeComboBox.getItems()) {
                    if (item.getValue().equals(type)) {
                        typeComboBox.getSelectionModel().select(item);
                        break;
                    }
                }
            }
            nameField.setText((String) editData.get("name"));

            String level = (String) editData.get("level");
            if (level != null) {
                for (OptionItem item : levelComboBox.getItems()) {
                    if (item.getValue().equals(level)) {
                        levelComboBox.getSelectionModel().select(item);
                        break;
                    }
                }
            }

            awardDateField.setText(editData.get("awardDate") != null ? editData.get("awardDate").toString() : "");
            descriptionArea.setText((String) editData.get("description"));
            certificateUrlField.setText((String) editData.get("certificateUrl"));
        }
    }

    @FXML
    public void onOkButtonClick() {
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            MessageDialog.showDialog("成果名称不能为空");
            return;
        }

        OptionItem typeItem = typeComboBox.getValue();
        if (typeItem == null) {
            MessageDialog.showDialog("请选择成就类型");
            return;
        }

        Map<String, Object> form = new HashMap<>();
        if (editData != null && editData.get("achievementId") != null) {
            form.put("achievementId", editData.get("achievementId"));
        }
        form.put("type", typeItem.getValue());
        form.put("name", nameField.getText());

        OptionItem levelItem = levelComboBox.getValue();
        if (levelItem != null) {
            form.put("level", levelItem.getValue());
        }

        form.put("awardDate", awardDateField.getText());
        form.put("description", descriptionArea.getText());
        form.put("certificateUrl", certificateUrlField.getText());

        // 获取当前登录学生的personId，移动到form内部
        com.teach.javafx.request.JwtResponse jwt = com.teach.javafx.AppStore.getJwt();
        if (jwt == null || jwt.getId() == null) {
            MessageDialog.showDialog("用户未登录或会话已过期");
            return;
        }
        Integer personId = jwt.getId();
        form.put("studentId", personId);

        DataRequest req = new DataRequest();
        req.add("form", form);

        DataResponse res = HttpRequestUtil.request("/api/student/achievement/save", req);
        if (res != null && res.getCode() == 0) {
            parentController.doClose("ok", null);
        } else {
            MessageDialog.showDialog("保存失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }

    @FXML
    public void onCancelButtonClick() {
        parentController.doClose("cancel", null);
    }
}
