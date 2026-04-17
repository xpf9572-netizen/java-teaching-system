package com.teach.javafx.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.models.Attendance;
import com.teach.javafx.models.Course;
import com.teach.javafx.models.Student;
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

public class AttendanceController extends ToolController {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML private TableView<Map> dataTableView;
    @FXML private TableColumn<Map, String> studentNameColumn;
    @FXML private TableColumn<Map, String> courseNameColumn;
    @FXML private TableColumn<Map, String> attendanceDateColumn;
    @FXML private TableColumn<Map, String> statusColumn;
    @FXML private TableColumn<Map, String> remarkColumn;

    @FXML private ComboBox<OptionItem> studentComboBox;
    @FXML private ComboBox<OptionItem> courseComboBox;
    @FXML private DatePicker attendanceDatePicker;
    @FXML private ComboBox<OptionItem> statusComboBox;
    @FXML private TextField remarkField;

    @FXML private TextField queryStudentNameField;

    private Long currentId = null;
    private ArrayList<Map> attendanceList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private List<Student> studentList = new ArrayList<>();
    private List<Course> courseList = new ArrayList<>();

    @FXML
    public void initialize() {
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        attendanceDateColumn.setCellValueFactory(new MapValueFactory<>("attendanceDate"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));
        remarkColumn.setCellValueFactory(new MapValueFactory<>("remark"));

        statusComboBox.getItems().addAll(
                new OptionItem(1, "PRESENT", "出勤"),
                new OptionItem(2, "ABSENT", "缺勤"),
                new OptionItem(3, "LATE", "迟到"),
                new OptionItem(4, "LEAVE", "请假")
        );

        loadStudentAndCourseData();
        onQueryButtonClick();
    }

    private void loadStudentAndCourseData() {
        try {
            HttpRequest studentReq = HttpRequest.newBuilder()
                    .uri(URI.create(HttpRequestUtil.serverUrl + "/api/students/all"))
                    .GET()
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();
            HttpResponse<String> studentResp = client.send(studentReq, HttpResponse.BodyHandlers.ofString());
            Type studentType = new TypeToken<ApiResponse<List<Student>>>(){}.getType();
            ApiResponse<List<Student>> studentApiResp = gson.fromJson(studentResp.body(), studentType);
            if (studentApiResp.isSuccess()) {
                studentList = studentApiResp.getData();
                studentComboBox.getItems().clear();
                for (Student s : studentList) {
                    studentComboBox.getItems().add(new OptionItem(s.getPersonId().intValue(), s.getPersonId().toString(), s.getName()));
                }
            }

            HttpRequest courseReq = HttpRequest.newBuilder()
                    .uri(URI.create(HttpRequestUtil.serverUrl + "/api/courses/all"))
                    .GET()
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();
            HttpResponse<String> courseResp = client.send(courseReq, HttpResponse.BodyHandlers.ofString());
            Type courseType = new TypeToken<ApiResponse<List<Course>>>(){}.getType();
            ApiResponse<List<Course>> courseApiResp = gson.fromJson(courseResp.body(), courseType);
            if (courseApiResp.isSuccess()) {
                courseList = courseApiResp.getData();
                courseComboBox.getItems().clear();
                for (Course c : courseList) {
                    courseComboBox.getItems().add(new OptionItem(c.getId().intValue(), c.getId().toString(), c.getCourseName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTableViewData() {
        observableList.clear();
        observableList.addAll(attendanceList);
        dataTableView.setItems(observableList);
    }

    @FXML
    protected void onQueryButtonClick() {
        try {
            String url = HttpRequestUtil.serverUrl + "/api/attendances?page=1&size=100";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type type = new TypeToken<ApiResponse<ApiResponse.PageData<Attendance>>>(){}.getType();
            ApiResponse<ApiResponse.PageData<Attendance>> apiResponse = gson.fromJson(response.body(), type);
            if (apiResponse.isSuccess()) {
                attendanceList.clear();
                for (Attendance a : apiResponse.getData().getContent()) {
                    attendanceList.add(entityToMap(a));
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
            MessageDialog.showDialog("请选择要删除的考勤记录");
            return;
        }
        if (MessageDialog.choiceDialog("确认要删除吗?") != MessageDialog.CHOICE_YES) return;
        try {
            Long id = getLong(selected, "id");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HttpRequestUtil.serverUrl + "/api/attendances/" + id))
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
            if (attendanceDatePicker.getValue() != null) {
                data.put("attendanceDate", attendanceDatePicker.getValue().toString());
            }
            if (statusComboBox.getValue() != null) {
                data.put("status", statusComboBox.getValue().getValue());
            }
            data.put("remark", remarkField.getText());

            String json = gson.toJson(data);
            String url = currentId == null ? HttpRequestUtil.serverUrl + "/api/attendances"
                    : HttpRequestUtil.serverUrl + "/api/attendances/" + currentId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method(currentId == null ? "POST" : "PUT", HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + com.teach.javafx.AppStore.getJwt().getToken())
                    .build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type type = new TypeToken<ApiResponse<Attendance>>(){}.getType();
            ApiResponse<Attendance> apiResponse = gson.fromJson(resp.body(), type);
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
        studentComboBox.getSelectionModel().select(-1);
        courseComboBox.getSelectionModel().select(-1);
        attendanceDatePicker.setValue(null);
        statusComboBox.getSelectionModel().select(-1);
        remarkField.setText("");
    }

    private Map<String, Object> entityToMap(Attendance a) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", a.getId());
        map.put("studentId", a.getStudentId());
        map.put("courseId", a.getCourseId());
        map.put("studentName", a.getStudentName());
        map.put("courseName", a.getCourseName());
        map.put("attendanceDate", a.getAttendanceDate());
        map.put("status", a.getStatus());
        map.put("remark", a.getRemark());
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
