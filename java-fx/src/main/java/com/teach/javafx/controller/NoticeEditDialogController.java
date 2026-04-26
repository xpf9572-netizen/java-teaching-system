package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class NoticeEditDialogController {
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<OptionItem> typeComboBox;
    @FXML
    private ComboBox<OptionItem> targetAudienceComboBox;
    @FXML
    private TextField publisherField;
    @FXML
    private TextArea contentArea;

    private NoticeController noticeController;
    private Map<String, Object> editData;

    public void setNoticeController(NoticeController controller) {
        this.noticeController = controller;
    }

    public void init(Map<String, Object> data) {
        OptionItem[] typeOptions = {
                new OptionItem(null, "通知", "通知"),
                new OptionItem(null, "公告", "公告"),
                new OptionItem(null, "活动", "活动")
        };
        typeComboBox.setItems(FXCollections.observableArrayList(typeOptions));

        OptionItem[] audienceOptions = {
                new OptionItem(null, "ALL", "全部"),
                new OptionItem(null, "STUDENT", "学生"),
                new OptionItem(null, "TEACHER", "教师")
        };
        targetAudienceComboBox.setItems(FXCollections.observableArrayList(audienceOptions));

        String perName = AppStore.getJwt() != null ? AppStore.getJwt().getPerName() : "";
        publisherField.setText(perName);

        if (data != null) {
            editData = data;
            titleField.setText(data.get("title") != null ? data.get("title").toString() : "");
            contentArea.setText(data.get("content") != null ? data.get("content").toString() : "");

            String type = data.get("type") != null ? data.get("type").toString() : "";
            for (OptionItem item : typeOptions) {
                if (item.getValue().equals(type)) {
                    typeComboBox.getSelectionModel().select(item);
                    break;
                }
            }

            String audience = data.get("targetAudience") != null ? data.get("targetAudience").toString() : "";
            for (OptionItem item : audienceOptions) {
                if (item.getValue().equals(audience)) {
                    targetAudienceComboBox.getSelectionModel().select(item);
                    break;
                }
            }

            if (data.get("publisher") != null) {
                publisherField.setText(data.get("publisher").toString());
            }
        } else {
            editData = null;
            titleField.setText("");
            contentArea.setText("");
            typeComboBox.getSelectionModel().clearSelection();
            targetAudienceComboBox.getSelectionModel().clearSelection();
        }
    }

    @FXML
    public void okButtonClick() {
        String title = titleField.getText();
        if (title == null || title.trim().isEmpty()) {
            MessageDialog.showDialog("标题不能为空");
            return;
        }
        String content = contentArea.getText();
        if (content == null || content.trim().isEmpty()) {
            MessageDialog.showDialog("内容不能为空");
            return;
        }

        DataRequest req = new DataRequest();
        if (editData != null && editData.get("noticeId") != null) {
            req.add("noticeId", editData.get("noticeId"));
        }
        req.add("title", title);
        req.add("content", content);
        req.add("publisher", publisherField.getText());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        req.add("publishTime", sdf.format(new Date()));

        OptionItem typeItem = typeComboBox.getSelectionModel().getSelectedItem();
        req.add("type", typeItem != null ? typeItem.getValue() : "通知");

        OptionItem audienceItem = targetAudienceComboBox.getSelectionModel().getSelectedItem();
        req.add("targetAudience", audienceItem != null ? audienceItem.getValue() : "ALL");

        DataResponse res = HttpRequestUtil.request("/api/notice/save", req);
        if (res != null && res.getCode() == 0) {
            if (noticeController != null) {
                noticeController.doClose("ok", null);
            }
        } else {
            MessageDialog.showDialog("保存失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }

    @FXML
    public void cancelButtonClick() {
        if (noticeController != null) {
            noticeController.doClose("cancel", null);
        }
    }
}
