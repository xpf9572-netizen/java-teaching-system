package com.teach.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.*;

public class ClassPanelController {

    @FXML private TableView<Map> dataTableView;
    @FXML private TableColumn<Map, String> classNumColumn;
    @FXML private TableColumn<Map, String> classNameColumn;
    @FXML private TableColumn<Map, String> departmentColumn;
    @FXML private TableColumn<Map, String> majorColumn;
    @FXML private TableColumn<Map, String> counselorColumn;
    @FXML private TableColumn<Map, String> phoneColumn;
    @FXML private TableColumn<Map, Number> studentCountColumn;

    @FXML
    private void initialize() {
        classNumColumn.setCellValueFactory(new PropertyValueFactory<>("classNum"));
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        counselorColumn.setCellValueFactory(new PropertyValueFactory<>("counselor"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        studentCountColumn.setCellValueFactory(new PropertyValueFactory<>("studentCount"));
        loadData();
    }

    private void loadData() {
        try {
            Map<String, Object> response = com.teach.javafx.request.HttpRequestUtil.getForMap("/api/classes?page=1&size=100");
            if (Boolean.TRUE.equals(response.get("success"))) {
                List<Map> content = (List<Map>) response.get("content");
                dataTableView.getItems().clear();
                if (content != null) {
                    dataTableView.getItems().addAll(content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void onQueryButtonClick() { loadData(); }
    @FXML private void onAddButtonClick() { showAddDialog(); }
    @FXML private void onDeleteButtonClick() { }
    @FXML private void onSaveButtonClick() { }

    private void showAddDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("添加班级");
        dialog.setHeaderText("请输入班级名称");
        dialog.setContentText("班级名称：");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String className = result.get();
            Map<String, Object> data = new HashMap<>();
            data.put("className", className);
            data.put("classNum", "");
            data.put("department", "");
            data.put("major", "");
            data.put("counselor", "");
            data.put("phone", "");
            data.put("studentCount", 0);
            data.put("grade", "");
            data.put("status", "正常");
            com.teach.javafx.request.HttpRequestUtil.postForMap("/api/classes", data);
            showAlert("添加成功");
            loadData();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }
}