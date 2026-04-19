package com.teach.javafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.OptionItem;

import java.util.*;

public class ScoreAnalysisController {
    @FXML
    private ComboBox<String> courseComboBox;
    @FXML
    private ComboBox<String> analysisTypeComboBox;

    @FXML
    private TableView<Map<String, Object>> dataTableView;
    @FXML
    private TableColumn<Map, String> nameColumn;
    @FXML
    private TableColumn<Map, String> valueColumn;

    @FXML
    private TableView<Map<String, Object>> distributionTableView;
    @FXML
    private TableColumn<Map, String> rangeColumn;
    @FXML
    private TableColumn<Map, String> countColumn;

    @FXML
    private TableView<Map<String, Object>> warningTableView;
    @FXML
    private TableColumn<Map, String> studentColumn;
    @FXML
    private TableColumn<Map, String> courseNameColumn;
    @FXML
    private TableColumn<Map, String> markColumn;
    @FXML
    private TableColumn<Map, String> gapColumn;

    private Map<String, String> courseMap = new HashMap<>();
    private List<Map<String, Object>> warningList = new ArrayList<>();
    private final ObservableList<Map<String, Object>> observableList = FXCollections.observableArrayList();
    private final ObservableList<Map<String, Object>> distObservableList = FXCollections.observableArrayList();
    private final ObservableList<Map<String, Object>> warningObservableList = FXCollections.observableArrayList();

    @FXML
    private void onQueryButtonClick() {
        String analysisType = analysisTypeComboBox.getValue();
        if (analysisType == null) return;

        if ("课程分析".equals(analysisType)) {
            queryCourseAnalysis();
        } else if ("学生分析".equals(analysisType)) {
            queryStudentAnalysis();
        } else if ("预警学生".equals(analysisType)) {
            queryWarningStudents();
        } else if ("总体统计".equals(analysisType)) {
            queryOverallStatistics();
        }
    }

    private void queryCourseAnalysis() {
        String courseName = courseComboBox.getValue();
        if (courseName == null) return;

        DataRequest req = new DataRequest();
        req.add("courseId", courseMap.get(courseName));
        DataResponse res = HttpRequestUtil.request("/api/scoreAnalysis/getCourseAnalysis", req);
        if (res != null && res.getCode() == 0) {
            Map<String, Object> data = (Map<String, Object>) res.getData();
            displayAnalysisData(data);
            displayDistribution((Map<String, Integer>) data.get("distribution"));
        }
    }

    private void queryStudentAnalysis() {
        String courseName = courseComboBox.getValue();
        DataRequest req = new DataRequest();
        if (courseName != null) {
            req.add("courseId", courseMap.get(courseName));
        }
        DataResponse res = HttpRequestUtil.request("/api/scoreAnalysis/getStudentAnalysis", req);
        if (res != null && res.getCode() == 0) {
            Map<String, Object> data = (Map<String, Object>) res.getData();
            displayStudentAnalysisData(data);
        }
    }

    private void queryWarningStudents() {
        String courseName = courseComboBox.getValue();
        DataRequest req = new DataRequest();
        if (courseName != null) {
            req.add("courseId", courseMap.get(courseName));
        }
        req.add("threshold", 60);
        DataResponse res = HttpRequestUtil.request("/api/scoreAnalysis/getWarningStudents", req);
        if (res != null && res.getCode() == 0) {
            warningList = (List<Map<String, Object>>) res.getData();
            displayWarningData();
        }
    }

    private void queryOverallStatistics() {
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/scoreAnalysis/getOverallStatistics", req);
        if (res != null && res.getCode() == 0) {
            Map<String, Object> data = (Map<String, Object>) res.getData();
            displayOverallData(data);
        }
    }

    private void displayAnalysisData(Map<String, Object> data) {
        observableList.clear();
        if (data != null) {
            addAnalysisRow("课程ID", data.get("courseId") != null ? data.get("courseId").toString() : "");
            addAnalysisRow("总人数", data.get("totalStudents") != null ? data.get("totalStudents").toString() : "");
            addAnalysisRow("平均分", data.get("avgScore") != null ? data.get("avgScore").toString() : "");
            addAnalysisRow("最高分", data.get("maxScore") != null ? data.get("maxScore").toString() : "");
            addAnalysisRow("最低分", data.get("minScore") != null ? data.get("minScore").toString() : "");
            addAnalysisRow("及格人数", data.get("passCount") != null ? data.get("passCount").toString() : "");
            addAnalysisRow("及格率", data.get("passRate") != null ? data.get("passRate").toString() + "%" : "");
        }
        dataTableView.setItems(observableList);
    }

    private void displayStudentAnalysisData(Map<String, Object> data) {
        observableList.clear();
        if (data != null) {
            addAnalysisRow("学生ID", data.get("studentId") != null ? data.get("studentId").toString() : "");
            addAnalysisRow("课程数", data.get("totalCourses") != null ? data.get("totalCourses").toString() : "");
            addAnalysisRow("平均分", data.get("avgScore") != null ? data.get("avgScore").toString() : "");
            addAnalysisRow("总学分", data.get("totalCredits") != null ? data.get("totalCredits").toString() : "");
            addAnalysisRow("通过课程", data.get("passedCourses") != null ? data.get("passedCourses").toString() : "");
            addAnalysisRow("未通过课程", data.get("failedCourses") != null ? data.get("failedCourses").toString() : "");
        }
        dataTableView.setItems(observableList);
    }

    private void displayOverallData(Map<String, Object> data) {
        observableList.clear();
        if (data != null) {
            addAnalysisRow("总记录数", data.get("totalRecords") != null ? data.get("totalRecords").toString() : "");
            addAnalysisRow("平均分", data.get("avgScore") != null ? data.get("avgScore").toString() : "");
            addAnalysisRow("最高分", data.get("maxScore") != null ? data.get("maxScore").toString() : "");
            addAnalysisRow("最低分", data.get("minScore") != null ? data.get("minScore").toString() : "");
            addAnalysisRow("及格率", data.get("passRate") != null ? data.get("passRate").toString() + "%" : "");
        }
        dataTableView.setItems(observableList);
    }

    private void displayDistribution(Map<String, Integer> distribution) {
        distObservableList.clear();
        if (distribution != null) {
            for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
                Map<String, Object> row = new HashMap<>();
                row.put("range", entry.getKey());
                row.put("count", entry.getValue().toString());
                distObservableList.add(row);
            }
        }
        distributionTableView.setItems(distObservableList);
    }

    private void displayWarningData() {
        warningObservableList.clear();
        for (Map<String, Object> w : warningList) {
            Map<String, Object> row = new HashMap<>();
            row.put("student", w.get("studentName") != null ? w.get("studentName") : "");
            row.put("course", w.get("courseName") != null ? w.get("courseName") : "");
            row.put("mark", w.get("mark") != null ? w.get("mark").toString() : "");
            row.put("gap", w.get("gap") != null ? w.get("gap").toString() : "");
            warningObservableList.add(row);
        }
        warningTableView.setItems(warningObservableList);
    }

    private void addAnalysisRow(String name, String value) {
        Map<String, Object> row = new HashMap<>();
        row.put("name", name);
        row.put("value", value);
        observableList.add(row);
    }

    private void initComboBox() {
        List<OptionItem> courseOptions = HttpRequestUtil.requestOptionItemList("/api/course/getCourseOptionList", new DataRequest());
        if (courseOptions != null) {
            courseComboBox.getItems().clear();
            courseMap.clear();
            for (OptionItem item : courseOptions) {
                courseComboBox.getItems().add(item.getTitle());
                courseMap.put(item.getTitle(), item.getValue());
            }
        }

        analysisTypeComboBox.getItems().clear();
        analysisTypeComboBox.getItems().addAll("课程分析", "学生分析", "预警学生", "总体统计");
        analysisTypeComboBox.setValue("课程分析");
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        valueColumn.setCellValueFactory(new MapValueFactory<>("value"));
        rangeColumn.setCellValueFactory(new MapValueFactory<>("range"));
        countColumn.setCellValueFactory(new MapValueFactory<>("count"));
        studentColumn.setCellValueFactory(new MapValueFactory<>("student"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("course"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        gapColumn.setCellValueFactory(new MapValueFactory<>("gap"));

        initComboBox();
    }
}
