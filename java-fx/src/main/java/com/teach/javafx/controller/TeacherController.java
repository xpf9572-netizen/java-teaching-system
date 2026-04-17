package com.teach.javafx.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.models.Teacher;
import com.teach.javafx.request.ApiResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleObjectProperty;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeacherController extends ToolController {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML private TableView<Map<String, Object>> dataTableView;
    @FXML private TableColumn<Map<String, Object>, String> teacherNumColumn;
    @FXML private TableColumn<Map<String, Object>, String> nameColumn;
    @FXML private TableColumn<Map<String, Object>, String> genderColumn;
    @FXML private TableColumn<Map<String, Object>, String> titleColumn;
    @FXML private TableColumn<Map<String, Object>, String> departmentColumn;
    @FXML private TableColumn<Map<String, Object>, String> phoneColumn;
    @FXML private TableColumn<Map<String, Object>, String> emailColumn;

    @FXML private TextField teacherNumField;
    @FXML private TextField nameField;
    @FXML private ComboBox<OptionItem> genderComboBox;
    @FXML private TextField titleField;
    @FXML private TextField departmentField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea introduceArea;
    @FXML private ComboBox<OptionItem> statusComboBox;

    @FXML private TextField queryNameField;
    @FXML private TextField queryTeacherNumField;

    private Long currentId = null;
    private ArrayList<Map<String, Object>> teacherList = new ArrayList<>();
    private ObservableList<Map<String, Object>> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        teacherNumColumn.setCellValueFactory(col -> new SimpleObjectProperty<>((String) col.getValue().get("teacherNum")));
        nameColumn.setCellValueFactory(col -> new SimpleObjectProperty<>((String) col.getValue().get("name")));
        genderColumn.setCellValueFactory(col -> new SimpleObjectProperty<>((String) col.getValue().get("gender")));
        titleColumn.setCellValueFactory(col -> new SimpleObjectProperty<>((String) col.getValue().get("title")));
        departmentColumn.setCellValueFactory(col -> new SimpleObjectProperty<>((String) col.getValue().get("department")));
        phoneColumn.setCellValueFactory(col -> new SimpleObjectProperty<>((String) col.getValue().get("phone")));
        emailColumn.setCellValueFactory(col -> new SimpleObjectProperty<>((String) col.getValue().get("email")));

        genderComboBox.getItems().addAll(
                new OptionItem(1, "M", "男"),
                new OptionItem(2, "F", "女")
        );
        statusComboBox.getItems().addAll(
                new OptionItem(1, "ACTIVE", "在职"),
                new OptionItem(2, "LEAVE", "离职")
        );

        onQueryButtonClick();
    }

    private void setTableViewData() {
        observableList.clear();
        observableList.addAll(teacherList);
        dataTableView.setItems(observableList);
    }

    @FXML
    protected void onQueryButtonClick() {
        try {
            String url = HttpRequestUtil.serverUrl + "/api/teachers?page=1&size=100";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Type type = new TypeToken<ApiResponse<ApiResponse.PageData<Teacher>>>(){}.getType();
                ApiResponse<ApiResponse.PageData<Teacher>> apiResponse = gson.fromJson(response.body(), type);
                if (apiResponse.isSuccess()) {
                    teacherList.clear();
                    for (Teacher t : apiResponse.getData().getContent()) {
                        teacherList.add(entityToMap(t));
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
        Map<String, Object> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("请选择要删除的教师");
            return;
        }
        if (MessageDialog.choiceDialog("确认要删除吗?") != MessageDialog.CHOICE_YES) return;

        try {
            Long id = getLong(selected, "id");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HttpRequestUtil.serverUrl + "/api/teachers/" + id))
                    .DELETE()
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            ApiResponse<Void> apiResponse = gson.fromJson(resp.body(), ApiResponse.class);
            if (apiResponse.isSuccess()) {
                MessageDialog.showDialog("删除成功!");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(apiResponse.getMsg());
            }
        } catch (Exception e) {
            MessageDialog.showDialog("删除失败: " + e.getMessage());
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        if (teacherNumField.getText().isEmpty() || nameField.getText().isEmpty()) {
            MessageDialog.showDialog("工号和姓名不能为空");
            return;
        }
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("teacherNum", teacherNumField.getText());
            data.put("name", nameField.getText());
            if (genderComboBox.getValue() != null) data.put("gender", genderComboBox.getValue().getValue());
            data.put("title", titleField.getText());
            data.put("department", departmentField.getText());
            data.put("phone", phoneField.getText());
            data.put("email", emailField.getText());
            data.put("introduce", introduceArea.getText());
            if (statusComboBox.getValue() != null) data.put("status", statusComboBox.getValue().getValue());

            String json = gson.toJson(data);
            String url = currentId == null ? HttpRequestUtil.serverUrl + "/api/teachers"
                    : HttpRequestUtil.serverUrl + "/api/teachers/" + currentId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method(currentId == null ? "POST" : "PUT", HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();

            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type type = new TypeToken<ApiResponse<Teacher>>(){}.getType();
            ApiResponse<Teacher> apiResponse = gson.fromJson(resp.body(), type);
            if (apiResponse.isSuccess()) {
                MessageDialog.showDialog("保存成功!");
                currentId = apiResponse.getData().getId();
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(apiResponse.getMsg());
            }
        } catch (Exception e) {
            MessageDialog.showDialog("保存失败: " + e.getMessage());
        }
    }

    public void doNew() { onAddButtonClick(); }
    public void doSave() { onSaveButtonClick(); }
    public void doDelete() { onDeleteButtonClick(); }
    public void doRefresh() { onQueryButtonClick(); }

    private void clearPanel() {
        currentId = null;
        teacherNumField.setText(""); nameField.setText("");
        genderComboBox.getSelectionModel().select(-1); titleField.setText("");
        departmentField.setText(""); phoneField.setText(""); emailField.setText("");
        introduceArea.setText(""); statusComboBox.getSelectionModel().select(-1);
    }

    private Map<String, Object> entityToMap(Teacher t) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", t.getId()); map.put("teacherNum", t.getTeacherNum());
        map.put("name", t.getName()); map.put("gender", t.getGender());
        map.put("title", t.getTitle()); map.put("department", t.getDepartment());
        map.put("phone", t.getPhone()); map.put("email", t.getEmail());
        map.put("introduce", t.getIntroduce()); map.put("status", t.getStatus());
        return map;
    }

    private Long getLong(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return null;
        if (v instanceof Long) return (Long) v;
        if (v instanceof Integer) return ((Integer) v).longValue();
        return Long.parseLong(v.toString());
    }
}
