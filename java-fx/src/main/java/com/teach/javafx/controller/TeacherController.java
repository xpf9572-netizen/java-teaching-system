package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeacherController extends ToolController {

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

        // 添加表格选择监听器
        TableView.TableViewSelectionModel<Map<String, Object>> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
    }

    private void setTableViewData() {
        observableList.clear();
        observableList.addAll(teacherList);
        dataTableView.setItems(observableList);
    }

    @FXML
    protected void onQueryButtonClick() {
        try {
            String numName = "";
            if (queryTeacherNumField.getText() != null && !queryTeacherNumField.getText().isEmpty()) {
                numName = queryTeacherNumField.getText();
            } else if (queryNameField.getText() != null && !queryNameField.getText().isEmpty()) {
                numName = queryNameField.getText();
            }
            DataRequest req = new DataRequest();
            req.add("numName", numName);
            DataResponse res = HttpRequestUtil.request("/api/teachers/getTeacherList", req);
            if (res != null && res.getCode() == 0) {
                teacherList = (ArrayList<Map<String, Object>>) res.getData();
                setTableViewData();
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

        currentId = CommonMethod.getInteger(selected, "personId").longValue();
        DataRequest req = new DataRequest();
        req.add("personId", currentId);
        DataResponse res = HttpRequestUtil.request("/api/teachers/teacherDelete", req);
        if (res != null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功!");
                currentId = null;
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        } else {
            MessageDialog.showDialog("删除失败");
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        if (teacherNumField.getText() == null || teacherNumField.getText().isEmpty()) {
            MessageDialog.showDialog("工号不能为空");
            return;
        }
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            MessageDialog.showDialog("姓名不能为空");
            return;
        }
        // 邮箱格式验证
        String email = emailField.getText();
        if (email != null && !email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            MessageDialog.showDialog("请输入有效的邮箱地址");
            return;
        }

        Map<String, Object> form = new HashMap<>();
        form.put("teacherNum", teacherNumField.getText());
        form.put("name", nameField.getText());
        if (genderComboBox.getValue() != null) {
            form.put("gender", genderComboBox.getValue().getValue());
        }
        form.put("title", titleField.getText());
        form.put("dept", departmentField.getText());
        form.put("phone", phoneField.getText());
        form.put("email", emailField.getText());
        form.put("introduce", introduceArea.getText());
        if (statusComboBox.getValue() != null) {
            form.put("status", statusComboBox.getValue().getValue());
        }

        DataRequest req = new DataRequest();
        req.add("personId", currentId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/teachers/teacherEditSave", req);
        if (res.getCode() == 0) {
            currentId = CommonMethod.getIntegerFromObject(res.getData()).longValue();
            MessageDialog.showDialog("保存成功!");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void doNew() { onAddButtonClick(); }
    public void doSave() { onSaveButtonClick(); }
    public void doDelete() { onDeleteButtonClick(); }
    public void doRefresh() { onQueryButtonClick(); }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeTeacherInfo();
    }

    protected void changeTeacherInfo() {
        Map<String, Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        currentId = CommonMethod.getInteger(form, "personId").longValue();

        // 填充表单数据
        teacherNumField.setText(CommonMethod.getString(form, "teacherNum"));
        nameField.setText(CommonMethod.getString(form, "name"));
        titleField.setText(CommonMethod.getString(form, "title"));
        departmentField.setText(CommonMethod.getString(form, "dept"));
        phoneField.setText(CommonMethod.getString(form, "phone"));
        emailField.setText(CommonMethod.getString(form, "email"));
        introduceArea.setText(CommonMethod.getString(form, "introduce"));

        // 设置性别下拉框
        String gender = CommonMethod.getString(form, "gender");
        if (gender != null && !gender.isEmpty()) {
            for (int i = 0; i < genderComboBox.getItems().size(); i++) {
                OptionItem item = genderComboBox.getItems().get(i);
                if (item.getValue().equals(gender)) {
                    genderComboBox.getSelectionModel().select(i);
                    break;
                }
            }
        } else {
            genderComboBox.getSelectionModel().select(-1);
        }

        // 设置状态下拉框
        String status = CommonMethod.getString(form, "status");
        if (status != null && !status.isEmpty()) {
            for (int i = 0; i < statusComboBox.getItems().size(); i++) {
                OptionItem item = statusComboBox.getItems().get(i);
                if (item.getValue().equals(status)) {
                    statusComboBox.getSelectionModel().select(i);
                    break;
                }
            }
        } else {
            statusComboBox.getSelectionModel().select(-1);
        }
    }

    private void clearPanel() {
        currentId = null;
        teacherNumField.setText("");
        nameField.setText("");
        genderComboBox.getSelectionModel().select(-1);
        titleField.setText("");
        departmentField.setText("");
        phoneField.setText("");
        emailField.setText("");
        introduceArea.setText("");
        statusComboBox.getSelectionModel().select(-1);
    }
}
