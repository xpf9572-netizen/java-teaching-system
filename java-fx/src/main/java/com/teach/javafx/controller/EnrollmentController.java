package com.teach.javafx.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.models.Enrollment;
import com.teach.javafx.models.Course;
import com.teach.javafx.models.Student;
import com.teach.javafx.request.ApiResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableView.TableViewSelectionModel;
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

public class EnrollmentController extends ToolController {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML private TableView<Map> dataTableView;
    @FXML private TableColumn<Map, String> studentNameColumn;
    @FXML private TableColumn<Map, String> courseNameColumn;
    @FXML private TableColumn<Map, String> scoreColumn;
    @FXML private TableColumn<Map, String> semesterColumn;
    @FXML private TableColumn<Map, String> statusColumn;

    @FXML private ComboBox<OptionItem> studentComboBox;
    @FXML private ComboBox<OptionItem> courseComboBox;
    @FXML private TextField scoreField;
    @FXML private ComboBox<OptionItem> semesterComboBox;
    @FXML private ComboBox<OptionItem> statusComboBox;

    @FXML private TextField queryStudentNameField;

    private Long currentId = null;
    private ArrayList<Map> enrollmentList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private List<Student> studentList = new ArrayList<>();
    private List<Course> courseList = new ArrayList<>();

    @FXML
    public void initialize() {
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        scoreColumn.setCellValueFactory(new MapValueFactory<>("score"));
        semesterColumn.setCellValueFactory(new MapValueFactory<>("semester"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));

        semesterComboBox.getItems().addAll(
                new OptionItem(1, "2024-1", "2024学年第一学期"),
                new OptionItem(2, "2024-2", "2024学年第二学期"),
                new OptionItem(3, "2025-1", "2025学年第一学期")
        );
        statusComboBox.getItems().addAll(
                new OptionItem(1, "ENROLLED", "已选课"),
                new OptionItem(2, "COMPLETED", "已完成"),
                new OptionItem(3, "FAILED", "不及格")
        );

        loadStudentAndCourseData();
        onQueryButtonClick();

        // 添加表格选择监听器
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
    }

    private void loadStudentAndCourseData() {
        try {
            HttpRequest studentReq = HttpRequest.newBuilder()
                    .uri(URI.create(HttpRequestUtil.serverUrl + "/api/student/all"))
                    .GET()
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();
            HttpResponse<String> studentResp = client.send(studentReq, HttpResponse.BodyHandlers.ofString());
            Type studentType = new TypeToken<ApiResponse<List<Student>>>(){}.getType();
            ApiResponse<List<Student>> studentApiResp = gson.fromJson(studentResp.body(), studentType);
            if (studentApiResp != null && studentApiResp.isSuccess()) {
                studentList = studentApiResp.getData();
                studentComboBox.getItems().clear();
                for (Student s : studentList) {
                    studentComboBox.getItems().add(new OptionItem(s.getPersonId().intValue(), s.getPersonId().toString(), s.getName() + "(" + s.getNum() + ")"));
                }
            }

            HttpRequest courseReq = HttpRequest.newBuilder()
                    .uri(URI.create(HttpRequestUtil.serverUrl + "/api/course/all"))
                    .GET()
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();
            HttpResponse<String> courseResp = client.send(courseReq, HttpResponse.BodyHandlers.ofString());
            Type courseType = new TypeToken<ApiResponse<List<Course>>>(){}.getType();
            ApiResponse<List<Course>> courseApiResp = gson.fromJson(courseResp.body(), courseType);
            if (courseApiResp != null && courseApiResp.isSuccess()) {
                courseList = courseApiResp.getData();
                courseComboBox.getItems().clear();
                for (Course c : courseList) {
                    courseComboBox.getItems().add(new OptionItem(c.getId().intValue(), c.getId().toString(), c.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTableViewData() {
        observableList.clear();
        observableList.addAll(enrollmentList);
        dataTableView.setItems(observableList);
    }

    @FXML
    protected void onQueryButtonClick() {
        try {
            String url = HttpRequestUtil.serverUrl + "/api/enrollments?page=1&size=100";
            if (queryStudentNameField.getText() != null && !queryStudentNameField.getText().isEmpty()) {
                url += "&studentName=" + queryStudentNameField.getText();
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type type = new TypeToken<ApiResponse<ApiResponse.PageData<Enrollment>>>(){}.getType();
            ApiResponse<ApiResponse.PageData<Enrollment>> apiResponse = gson.fromJson(response.body(), type);
            if (apiResponse.isSuccess()) {
                enrollmentList.clear();
                for (Enrollment e : apiResponse.getData().getContent()) {
                    enrollmentList.add(entityToMap(e));
                }
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
        Map selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("请选择要删除的选课记录");
            return;
        }
        if (MessageDialog.choiceDialog("确认要删除吗?") != MessageDialog.CHOICE_YES) return;
        try {
            Long id = getLong(selected, "id");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HttpRequestUtil.serverUrl + "/api/enrollments/" + id))
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
        if (studentComboBox.getValue() == null || courseComboBox.getValue() == null) {
            MessageDialog.showDialog("学生和课程不能为空");
            return;
        }
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("studentId", Long.parseLong(studentComboBox.getValue().getValue()));
            data.put("courseId", Long.parseLong(courseComboBox.getValue().getValue()));
            if (!scoreField.getText().isEmpty()) {
                try {
                    double score = Double.parseDouble(scoreField.getText());
                    if (score < 0 || score > 100) {
                        MessageDialog.showDialog("成绩必须在0-100之间");
                        return;
                    }
                    data.put("score", score);
                } catch (NumberFormatException e) {
                    MessageDialog.showDialog("请输入有效的数字");
                    return;
                }
            }
            if (semesterComboBox.getValue() != null) {
                data.put("semester", semesterComboBox.getValue().getValue());
            }
            if (statusComboBox.getValue() != null) {
                data.put("status", statusComboBox.getValue().getValue());
            }

            String json = gson.toJson(data);
            String url = currentId == null ? HttpRequestUtil.serverUrl + "/api/enrollments"
                    : HttpRequestUtil.serverUrl + "/api/enrollments/" + currentId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method(currentId == null ? "POST" : "PUT", HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type type = new TypeToken<ApiResponse<Enrollment>>(){}.getType();
            ApiResponse<Enrollment> apiResponse = gson.fromJson(resp.body(), type);
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

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeStudentInfo();
    }

    protected void changeStudentInfo() {
        Map<String, Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        currentId = getLong(form, "id");
        // 填充表单数据
        Long studentId = getLong(form, "studentId");
        Long courseId = getLong(form, "courseId");
        Double score = (Double) form.get("score");
        String semester = (String) form.get("semester");
        String status = (String) form.get("status");

        // 设置学生下拉框
        for (int i = 0; i < studentComboBox.getItems().size(); i++) {
            OptionItem item = studentComboBox.getItems().get(i);
            if (item.getValue().equals(String.valueOf(studentId))) {
                studentComboBox.getSelectionModel().select(i);
                break;
            }
        }
        // 设置课程下拉框
        for (int i = 0; i < courseComboBox.getItems().size(); i++) {
            OptionItem item = courseComboBox.getItems().get(i);
            if (item.getValue().equals(String.valueOf(courseId))) {
                courseComboBox.getSelectionModel().select(i);
                break;
            }
        }
        // 设置成绩
        if (score != null) {
            scoreField.setText(String.valueOf(score));
        } else {
            scoreField.setText("");
        }
        // 设置学期
        if (semester != null) {
            for (int i = 0; i < semesterComboBox.getItems().size(); i++) {
                OptionItem item = semesterComboBox.getItems().get(i);
                if (item.getValue().equals(semester)) {
                    semesterComboBox.getSelectionModel().select(i);
                    break;
                }
            }
        }
        // 设置状态
        if (status != null) {
            for (int i = 0; i < statusComboBox.getItems().size(); i++) {
                OptionItem item = statusComboBox.getItems().get(i);
                if (item.getValue().equals(status)) {
                    statusComboBox.getSelectionModel().select(i);
                    break;
                }
            }
        }
    }

    private void clearPanel() {
        currentId = null;
        studentComboBox.getSelectionModel().select(-1);
        courseComboBox.getSelectionModel().select(-1);
        scoreField.setText("");
        semesterComboBox.getSelectionModel().select(-1);
        statusComboBox.getSelectionModel().select(-1);
    }

    private Map<String, Object> entityToMap(Enrollment e) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", e.getId());
        map.put("studentId", e.getStudentId());
        map.put("courseId", e.getCourseId());
        map.put("studentName", e.getStudentName());
        map.put("courseName", e.getCourseName());
        map.put("score", e.getScore());
        map.put("semester", e.getSemester());
        map.put("status", e.getStatus());
        return map;
    }

    private Long getLong(Map map, String key) {
        Object v = map.get(key);
        if (v == null) return null;
        if (v instanceof Long) return (Long) v;
        if (v instanceof Integer) return ((Integer) v).longValue();
        return Long.parseLong(v.toString());
    }
}
