package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * StudentLeaveController 学生请假控制器
 * 学生查看自己的请假历史列表，添加新请假申请
 */
public class StudentLeaveController {

    @FXML
    private TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map, String> leaveDateColumn;

    @FXML
    private TableColumn<Map, String> reasonColumn;

    @FXML
    private TableColumn<Map, String> stateNameColumn;

    @FXML
    private TableColumn<Map, String> teacherCommentColumn;

    @FXML
    private TableColumn<Map, String> adminCommentColumn;

    @FXML
    private TableColumn<Map, String> createTimeColumn;

    @FXML
    private Button addButton;

    private ArrayList<Map> leaveList = new ArrayList<>();

    private StudentLeaveAddDialogController leaveAddDialogController = null;
    private Stage dialogStage = null;

    /**
     * 将请假数据集合设置到面板上显示
     */
    private void setTableViewData() {
        dataTableView.getItems().clear();
        for (Map<String, Object> leave : leaveList) {
            dataTableView.getItems().add((Map) leave);
        }
    }

    /**
     * 页面加载对象创建完成初始化方法
     */
    @FXML
    public void initialize() {
        leaveDateColumn.setCellValueFactory(new MapValueFactory<>("leaveDate"));
        reasonColumn.setCellValueFactory(new MapValueFactory<>("reason"));
        stateNameColumn.setCellValueFactory(new MapValueFactory<>("stateName"));
        teacherCommentColumn.setCellValueFactory(new MapValueFactory<>("teacherComment"));
        adminCommentColumn.setCellValueFactory(new MapValueFactory<>("adminComment"));
        createTimeColumn.setCellValueFactory(new MapValueFactory<>("createTime"));

        onRefreshButtonClick();
    }

    /**
     * 查询请假列表
     */
    @FXML
    protected void onQueryButtonClick() {
        try {
            DataRequest req = new DataRequest();
            DataResponse res = HttpRequestUtil.request("/api/studentLeave/getStudentLeaveList", req);

            if (res != null && res.getCode() == 0) {
                leaveList = (ArrayList<Map>) res.getData();
                setTableViewData();
            } else {
                MessageDialog.showDialog("获取请假列表失败: " + (res != null ? res.getMsg() : "未知错误"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("查询失败: " + e.getMessage());
        }
    }

    /**
     * 刷新按钮点击事件
     */
    @FXML
    protected void onRefreshButtonClick() {
        onQueryButtonClick();
    }

    /**
     * 新增请假按钮点击事件
     */
    @FXML
    protected void onAddButtonClick() {
        initDialog();
        MainApplication.setCanClose(false);
        dialogStage.showAndWait();
    }

    /**
     * 初始化对话框
     */
    private void initDialog() {
        if (dialogStage != null)
            return;
        FXMLLoader fxmlLoader;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("student-leave-add-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 450, 350);
            dialogStage = new Stage();
            dialogStage.initOwner(MainApplication.getMainStage());
            dialogStage.initModality(Modality.NONE);
            dialogStage.setAlwaysOnTop(true);
            dialogStage.setScene(scene);
            dialogStage.setTitle("新增请假");
            dialogStage.setOnCloseRequest(event -> {
                MainApplication.setCanClose(true);
            });
            leaveAddDialogController = fxmlLoader.getController();
            leaveAddDialogController.setParentController(this);
            leaveAddDialogController.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭对话框后的回调
     */
    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        dialogStage.close();
        if (!"ok".equals(cmd))
            return;
        onQueryButtonClick();
    }

    /**
     * 对话框关闭时调用，清理对话框状态
     */
    public void onDialogClosed() {
        dialogStage = null;
        leaveAddDialogController = null;
    }
}
