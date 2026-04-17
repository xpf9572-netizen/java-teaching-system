package com.teach.javafx.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.models.ClassEntity;
import com.teach.javafx.request.ApiResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassController extends ToolController {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    private TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map, String> classNumColumn;

    @FXML
    private TableColumn<Map, String> classNameColumn;

    @FXML
    private TableColumn<Map, String> departmentColumn;

    @FXML
    private TableColumn<Map, String> majorColumn;

    @FXML
    private TableColumn<Map, String> counselorColumn;

    @FXML
    private TableColumn<Map, String> phoneColumn;

    @FXML
    private TableColumn<Map, String> studentCountColumn;

    @FXML
    private TableColumn<Map, String> gradeColumn;

    @FXML
    private TextField classNumField;

    @FXML
    private TextField classNameField;

    @FXML
    private TextField departmentField;

    @FXML
    private TextField majorField;

    @FXML
    private TextField counselorField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField studentCountField;

    @FXML
    private TextField gradeField;

    @FXML
    private ComboBox<OptionItem> statusComboBox;

    @FXML
    private TextField queryClassNameField;

    @FXML
    private TextField queryClassNumField;

    private Long currentId = null;
    private ArrayList<Map> classList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        classNumColumn.setCellValueFactory(new MapValueFactory<>("classNum"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        departmentColumn.setCellValueFactory(new MapValueFactory<>("department"));
        majorColumn.setCellValueFactory(new MapValueFactory<>("major"));
        counselorColumn.setCellValueFactory(new MapValueFactory<>("counselor"));
        phoneColumn.setCellValueFactory(new MapValueFactory<>("phone"));
        studentCountColumn.setCellValueFactory(new MapValueFactory<>("studentCount"));
        gradeColumn.setCellValueFactory(new MapValueFactory<>("grade"));

        statusComboBox.getItems().addAll(
                new OptionItem(1, "ACTIVE", "在读"),
                new OptionItem(2, "GRADUATED", "已毕业"),
                new OptionItem(3, "SUSPENDED", "休学")
        );

        onQueryButtonClick();
    }

    private void setTableViewData() {
        observableList.clear();
        for (Map<String, Object> map : classList) {
            observableList.add(map);
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    protected void onQueryButtonClick() {
        try {
            String url = HttpRequestUtil.serverUrl + "/api/classes?page=1&size=100";
            if (!queryClassNumField.getText().isEmpty()) {
                url += "&classNum=" + queryClassNumField.getText();
            }
            if (!queryClassNameField.getText().isEmpty()) {
                url += "&className=" + queryClassNameField.getText();
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ApiResponse<ApiResponse.PageData<ClassEntity>> apiResponse = gson.fromJson(
                        response.body(),
                        new TypeToken<ApiResponse<ApiResponse.PageData<ClassEntity>>>(){}.getType()
                );
                if (apiResponse.isSuccess()) {
                    classList.clear();
                    for (ClassEntity c : apiResponse.getData().getContent()) {
                        classList.add(entityToMap(c));
                    }
                    setTableViewData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("查询失败: " + e.getMessage());
        }
    }

    @FXML
    protected void onAddButtonClick() {
        currentId = null;
        clearPanel();
    }

    @FXML
    protected void onDeleteButtonClick() {
        Map selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("请选择要删除的班级");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        try {
            Long id = getLongFromMap(selected, "id");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HttpRequestUtil.serverUrl + "/api/classes/" + id))
                    .DELETE()
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ApiResponse<Void> apiResponse = gson.fromJson(response.body(), ApiResponse.class);
            if (apiResponse.isSuccess()) {
                MessageDialog.showDialog("删除成功!");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(apiResponse.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("删除失败: " + e.getMessage());
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        if (classNumField.getText().isEmpty() || classNameField.getText().isEmpty()) {
            MessageDialog.showDialog("班级编号和名称不能为空");
            return;
        }
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("classNum", classNumField.getText());
            data.put("className", classNameField.getText());
            data.put("department", departmentField.getText());
            data.put("major", majorField.getText());
            data.put("counselor", counselorField.getText());
            data.put("phone", phoneField.getText());
            if (!studentCountField.getText().isEmpty()) {
                data.put("studentCount", Integer.parseInt(studentCountField.getText()));
            }
            data.put("grade", gradeField.getText());
            if (statusComboBox.getValue() != null) {
                data.put("status", statusComboBox.getValue().getValue());
            }

            String json = gson.toJson(data);
            String url = currentId == null
                    ? HttpRequestUtil.serverUrl + "/api/classes"
                    : HttpRequestUtil.serverUrl + "/api/classes/" + currentId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method(currentId == null ? "POST" : "PUT", HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ApiResponse<ClassEntity> apiResponse = gson.fromJson(response.body(),
                    new TypeToken<ApiResponse<ClassEntity>>(){}.getType());
            if (apiResponse.isSuccess()) {
                MessageDialog.showDialog("保存成功!");
                currentId = apiResponse.getData().getId();
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(apiResponse.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("保存失败: " + e.getMessage());
        }
    }

    public void doNew() {
        onAddButtonClick();
    }

    public void doSave() {
        onSaveButtonClick();
    }

    public void doDelete() {
        onDeleteButtonClick();
    }

    public void doRefresh() {
        onQueryButtonClick();
    }

    private void clearPanel() {
        currentId = null;
        classNumField.setText("");
        classNameField.setText("");
        departmentField.setText("");
        majorField.setText("");
        counselorField.setText("");
        phoneField.setText("");
        studentCountField.setText("");
        gradeField.setText("");
        statusComboBox.getSelectionModel().select(-1);
    }

    private Map<String, Object> entityToMap(ClassEntity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", entity.getId());
        map.put("classNum", entity.getClassNum());
        map.put("className", entity.getClassName());
        map.put("department", entity.getDepartment());
        map.put("major", entity.getMajor());
        map.put("counselor", entity.getCounselor());
        map.put("phone", entity.getPhone());
        map.put("studentCount", entity.getStudentCount());
        map.put("grade", entity.getGrade());
        map.put("status", entity.getStatus());
        map.put("createTime", entity.getCreateTime());
        return map;
    }

    private Long getLongFromMap(Map map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        return Long.parseLong(value.toString());
    }
}
