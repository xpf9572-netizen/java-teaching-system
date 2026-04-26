package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoticeController {
    @FXML
    private TableView<Map<String, Object>> dataTableView;
    @FXML
    private TableColumn<Map, String> titleColumn;
    @FXML
    private TableColumn<Map, String> publisherColumn;
    @FXML
    private TableColumn<Map, String> publishTimeColumn;
    @FXML
    private TableColumn<Map, String> typeColumn;

    private List<Map<String, Object>> noticeList = new ArrayList<>();

    private NoticeEditDialogController noticeEditDialogController;
    private Stage noticeEditDialogStage;

    @FXML
    private void onRefreshButtonClick() {
        DataResponse res;
        DataRequest req = new DataRequest();

        JwtResponse jwt = AppStore.getJwt();
        if (jwt != null) {
            String role = jwt.getRole();
            if (role != null && (role.contains("STUDENT") || role.contains("ADMIN") || role.contains("TEACHER"))) {
                if (role.contains("STUDENT")) {
                    req.add("audience", "STUDENT");
                } else if (role.contains("TEACHER")) {
                    req.add("audience", "TEACHER");
                } else {
                    // ADMIN sees all, don't add audience filter
                }
            }
        }

        res = HttpRequestUtil.request("/api/notice/list", req);
        if (res != null && res.getCode() == 0 && res.getData() instanceof List) {
            noticeList = (List<Map<String, Object>>) res.getData();
            setTableViewData();
        } else {
            MessageDialog.showDialog("获取通知列表失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }

    private void setTableViewData() {
        dataTableView.getItems().clear();
        for (Map<String, Object> map : noticeList) {
            dataTableView.getItems().add(map);
        }
    }

    @FXML
    private void onTableRowClick(javafx.scene.input.MouseEvent event) {
        if (event.getClickCount() == 2) {
            Map<String, Object> selectedItem = dataTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                showNoticeDetail(selectedItem);
            }
        }
    }

    private void showNoticeDetail(Map<String, Object> notice) {
        String title = notice.get("title") != null ? notice.get("title").toString() : "";
        String publisher = notice.get("publisher") != null ? notice.get("publisher").toString() : "";
        String publishTime = notice.get("publishTime") != null ? notice.get("publishTime").toString() : "";
        String type = notice.get("type") != null ? notice.get("type").toString() : "";
        String targetAudience = notice.get("targetAudience") != null ? notice.get("targetAudience").toString() : "";
        String content = notice.get("content") != null ? notice.get("content").toString() : "";

        String audienceLabel = switch (targetAudience) {
            case "STUDENT" -> "学生";
            case "TEACHER" -> "教师";
            case "ALL" -> "全部";
            default -> targetAudience;
        };

        String detail = "标题: " + title + "\n\n" +
                "发布人: " + publisher + "\n\n" +
                "发布时间: " + publishTime + "\n\n" +
                "类型: " + type + "\n\n" +
                "目标受众: " + audienceLabel + "\n\n" +
                "内容:\n" + content;

        MessageDialog.showDialog(detail);
    }

    @FXML
    private void onAddButtonClick() {
        if (!isAdmin()) {
            MessageDialog.showDialog("无权限：仅管理员可添加通知");
            return;
        }
        initEditDialog();
        noticeEditDialogController.init(null);
        MainApplication.setCanClose(false);
        noticeEditDialogStage.showAndWait();
    }

    @FXML
    private void onEditButtonClick() {
        if (!isAdmin()) {
            MessageDialog.showDialog("无权限：仅管理员可修改通知");
            return;
        }
        Map<String, Object> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("没有选择，不能修改");
            return;
        }
        initEditDialog();
        noticeEditDialogController.init(selected);
        MainApplication.setCanClose(false);
        noticeEditDialogStage.showAndWait();
    }

    @FXML
    private void onDeleteButtonClick() {
        if (!isAdmin()) {
            MessageDialog.showDialog("无权限：仅管理员可删除通知");
            return;
        }
        Map<String, Object> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        DataRequest req = new DataRequest();
        req.add("noticeId", selected.get("noticeId"));
        DataResponse res = HttpRequestUtil.request("/api/notice/delete", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("删除成功");
            onRefreshButtonClick();
        } else {
            MessageDialog.showDialog("删除失败: " + (res != null ? res.getMsg() : "未知错误"));
        }
    }

    private void initEditDialog() {
        if (noticeEditDialogStage != null)
            return;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("notice-edit-dialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 420, 380);
            noticeEditDialogStage = new Stage();
            noticeEditDialogStage.initOwner(MainApplication.getMainStage());
            noticeEditDialogStage.initModality(Modality.NONE);
            noticeEditDialogStage.setAlwaysOnTop(true);
            noticeEditDialogStage.setScene(scene);
            noticeEditDialogStage.setTitle("编辑通知");
            noticeEditDialogStage.setOnCloseRequest(event -> MainApplication.setCanClose(true));
            noticeEditDialogController = fxmlLoader.getController();
            noticeEditDialogController.setNoticeController(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        noticeEditDialogStage.close();
        if ("ok".equals(cmd)) {
            onRefreshButtonClick();
        }
    }

    private boolean isAdmin() {
        JwtResponse jwt = AppStore.getJwt();
        if (jwt == null) return false;
        String role = jwt.getRole();
        return "ROLE_ADMIN".equals(role) || "ADMIN".equals(role);
    }

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new MapValueFactory<>("title"));
        publisherColumn.setCellValueFactory(new MapValueFactory<>("publisher"));
        publishTimeColumn.setCellValueFactory(new MapValueFactory<>("publishTime"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        onRefreshButtonClick();
    }
}
