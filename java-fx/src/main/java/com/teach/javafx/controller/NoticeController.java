package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * NoticeController 通知公告交互控制类 对应 notice-panel.fxml
 * @FXML 属性 对应fxml文件中的
 * @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
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

    @FXML
    private void onRefreshButtonClick() {
        DataResponse res;
        DataRequest req = new DataRequest();
        res = HttpRequestUtil.request("/api/notice/list", req);
        if (res != null && res.getCode() == 0) {
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
        if (event.getClickCount() == 1) {
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
        String content = notice.get("content") != null ? notice.get("content").toString() : "";

        String detail = "标题: " + title + "\n\n" +
                "发布人: " + publisher + "\n\n" +
                "发布时间: " + publishTime + "\n\n" +
                "类型: " + type + "\n\n" +
                "内容:\n" + content;

        MessageDialog.showDialog(detail);
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
