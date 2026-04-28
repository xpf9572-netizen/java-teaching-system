package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.*;

public class StudentAchievementController {
    @FXML private TableView<Map<String, Object>> dataTableView;
    @FXML private ComboBox<OptionItem> typeComboBox;
    @FXML private TableColumn<Map<String, Object>, String> typeColumn;
    @FXML private TableColumn<Map<String, Object>, String> nameColumn;
    @FXML private TableColumn<Map<String, Object>, String> levelColumn;
    @FXML private TableColumn<Map<String, Object>, String> awardDateColumn;
    @FXML private TableColumn<Map<String, Object>, String> statusColumn;
    @FXML private TableColumn<Map<String, Object>, String> descriptionColumn;

    private ArrayList<Map<String, Object>> achievementList = new ArrayList<>();
    private ObservableList<Map<String, Object>> observableList = FXCollections.observableArrayList();

    private AchievementAddDialogController achievementAddDialogController = null;
    private javafx.stage.Stage dialogStage = null;

    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll(
            new OptionItem(1, "", "全部"),
            new OptionItem(2, "COMPETITION", "竞赛获奖"),
            new OptionItem(3, "PUBLICATION", "科研论文"),
            new OptionItem(4, "PATENT", "专利"),
            new OptionItem(5, "PROJECT", "项目经历")
        );
        typeComboBox.getSelectionModel().select(0);

        typeColumn.setCellValueFactory(col -> new SimpleObjectProperty<>(getTypeName((String) col.getValue().get("type"))));
        nameColumn.setCellValueFactory(col -> new SimpleObjectProperty<>(str(col.getValue(), "name")));
        levelColumn.setCellValueFactory(col -> new SimpleObjectProperty<>(str(col.getValue(), "level")));
        awardDateColumn.setCellValueFactory(col -> new SimpleObjectProperty<>(str(col.getValue(), "awardDate")));
        statusColumn.setCellValueFactory(col -> new SimpleObjectProperty<>(getStatusName((String) col.getValue().get("status"))));
        descriptionColumn.setCellValueFactory(col -> new SimpleObjectProperty<>(str(col.getValue(), "description")));

        onQueryButtonClick();
    }

    @FXML
    protected void onQueryButtonClick() {
        try {
            com.teach.javafx.request.JwtResponse jwt = com.teach.javafx.AppStore.getJwt();
            if (jwt == null || jwt.getId() == null) {
                MessageDialog.showDialog("用户未登录或会话已过期");
                return;
            }
            DataRequest req = new DataRequest();
            req.add("personId", jwt.getId());
            OptionItem type = typeComboBox.getValue();
            if (type != null && !type.getValue().isEmpty()) {
                req.add("type", type.getValue());
            }
            DataResponse res = HttpRequestUtil.request("/api/student/achievement/list", req);
            if (res != null && res.getCode() == 0) {
                achievementList = (ArrayList<Map<String, Object>>) res.getData();
                observableList.clear();
                observableList.addAll(achievementList);
                dataTableView.setItems(observableList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("查询失败: " + e.getMessage());
        }
    }

    @FXML
    protected void onAddButtonClick() {
        initDialog();
        MainApplication.setCanClose(false);
        dialogStage.showAndWait();
    }

    private void initDialog() {
        if (dialogStage != null)
            return;
        FXMLLoader fxmlLoader;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("achievement-add-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 500, 450);
            dialogStage = new javafx.stage.Stage();
            dialogStage.initOwner(MainApplication.getMainStage());
            dialogStage.initModality(javafx.stage.Modality.NONE);
            dialogStage.setAlwaysOnTop(true);
            dialogStage.setScene(scene);
            dialogStage.setTitle("添加学生成就");
            dialogStage.setOnCloseRequest(event -> {
                MainApplication.setCanClose(true);
            });
            achievementAddDialogController = fxmlLoader.getController();
            achievementAddDialogController.setParentController(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        dialogStage.close();
        if (!"ok".equals(cmd))
            return;
        onQueryButtonClick();
    }

    @FXML
    protected void onDeleteButtonClick() {
        Map<String, Object> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("请选择要删除的记录");
            return;
        }
        if (MessageDialog.choiceDialog("确认要删除吗?") != MessageDialog.CHOICE_YES) return;

        try {
            DataRequest req = new DataRequest();
            req.add("achievementId", selected.get("achievementId"));
            DataResponse res = HttpRequestUtil.request("/api/student/achievement/delete", req);
            if (res != null && res.getCode() == 0) {
                MessageDialog.showDialog("删除成功!");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        } catch (Exception e) {
            MessageDialog.showDialog("删除失败: " + e.getMessage());
        }
    }

    private String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : "";
    }

    private String getTypeName(String type) {
        if (type == null) return "";
        return switch (type) {
            case "COMPETITION" -> "竞赛获奖";
            case "PUBLICATION" -> "科研论文";
            case "PATENT" -> "专利";
            case "PROJECT" -> "项目经历";
            default -> type;
        };
    }

    private String getStatusName(String status) {
        if (status == null) return "";
        return switch (status) {
            case "PENDING" -> "待审核";
            case "APPROVED" -> "已审核";
            case "REJECTED" -> "已拒绝";
            default -> status;
        };
    }
}
